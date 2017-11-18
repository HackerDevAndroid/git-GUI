package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * InputSuggest
 */
public class SearchSuggest extends DataContainer {

    // キーワード情報リスト
    public ArrayList<String> keywordList;
    // 型番リスト
    public ArrayList<PartNumber> partNumberList;

    public ErrorList errorList;


    /**
     * partNumberList
     */
    public class PartNumber{
        // 型番
        public String partNumber;
        // ブランド名称
        public String brandName;
        // 複雑品フラグ
        public String complexFlag;
        // シリーズコード
        public String seriesCode;
        // インナーコード
        public String innerCode;
        // 確定タイプ
        public String completeType;
        // 掲載タイプ
        public String publishType;

        /**
         * setData
         * @param json
         * @return
         */
        public boolean setData(JSONObject json){
            if (json == null){
                return false;
            }

            try {
                partNumber = getJsonString(json,"partNumber");
                brandName = getJsonString(json,"brandName");
                complexFlag = getJsonString(json,"complexFlag");
                seriesCode = getJsonString(json,"seriesCode");
                innerCode = getJsonString(json,"innerCode");
                completeType = getJsonString(json, "completeType");
                publishType = getJsonString(json, "publishType");

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }
    /**
     * setData
     * @param src
     * @return
     */
    public boolean setData(String src){

        if (src == null || src.length() == 0){
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);

            if (json.has("keywordList")){
                keywordList = new ArrayList<>();
                JSONArray array = json.getJSONArray("keywordList");

                for (int ii = 0 ; ii < array.length() ; ii++){
                    keywordList.add(array.getString(ii));
                }
            }else{
                keywordList = null;
            }

            if (json.has("partNumberList")){
                partNumberList = new ArrayList<>();

                JSONArray array = json.getJSONArray("partNumberList");
                for (int ii = 0 ; ii < array.length() ; ii++){
                    PartNumber partNumber = new PartNumber();
                    partNumber.setData(array.getJSONObject(ii));

					/*
					NPF_SP-485 サジェスト検索API結果から画面に表示する対象の明確化
					https://misumi-imj.backlog.jp/view/NPF_SP-485

					掲載タイプ		publishType
					タイプの具体的な値ですが、現状では
					1: 掲載中
					2: 未掲載
					となります。

					「2:未掲載」のサジェストは除いて表示して頂く必要があります。

					よって、
					アプリ側では掲載タイプが
					1: 掲載中
					のみを表示する仕様と思われる。
					*/
					if (!"1".equals(partNumber.publishType)) {
						continue;
					}

                    partNumberList.add(partNumber);
                }
            }else{
                partNumberList = null;
            }

            // エラーリスト
            errorList = getErrorList(json);

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

}
