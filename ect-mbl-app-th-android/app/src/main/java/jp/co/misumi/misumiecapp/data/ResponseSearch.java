package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 履歴検索API用データ（共通クラス）
 */
public class ResponseSearch extends DataContainer {

    public static final String API000000 = "API000000";
    public static final String API000100 = "API000100";
    public static final String API000101 = "API000101";


    public Integer totalCount;       //履歴の総件数
    public List<ResponseSearchInfo> mList = new ArrayList<>();

    public ErrorList errorList;


    public boolean setData(String src) {

        if (!mList.isEmpty()) {
            mList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            //履歴の総件数
            totalCount = getJsonInteger(json, "totalCount");

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }

}
