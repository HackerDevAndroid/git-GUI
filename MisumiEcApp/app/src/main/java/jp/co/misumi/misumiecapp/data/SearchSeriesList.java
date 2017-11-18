package jp.co.misumi.misumiecapp.data;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * シリーズ検索、ResponseKeywordSearchでも継承して使用
 */
public class SearchSeriesList extends DataContainer {

    //総件数
    public Integer totalCount;
    //シリーズ情報リスト
    public ArrayList<Series> mSeriesList = new ArrayList<>();
    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    public List<Brand> mSeriesBrandList = new ArrayList<>();
    //--ADD NT-LWL 17/09/08 BrandSearch TO -
    //--ADD NT-LWL 17/09/28 Series FR -
    // 复杂品数
    public int complexFlagCount = 0;
    //--ADD NT-LWL 17/09/28 Series TO -

	//TODO:カテゴリ名称を自前で代入
    public String getCategoryName;

    public ErrorList errorList;

    public static class Series extends DataContainer {

        //カテゴリーコード
        public String categoryCode;
        //カテゴリ名 2015/10/30対応
        public String categoryName;
        //シリーズコード
        public String seriesCode;
        //シリーズ名称
        public String seriesName;
        //ブランドコード
        public String brandCode;
        //ブランド名称
        public String brandName;
        //画像ＵＲＬ
        public ArrayList<String> productImageUrlList;
        //キャッチコピー
        public String catchCopy;
        //最小通常出荷日
        public Integer minStandardDaysToShip;
        //最大通常出荷日
        public Integer maxStandardDaysToShip;
        //最小通常価格
        public Double minStandardUnitPrice;
        //最大通常価格
        public Double maxStandardUnitPrice;
        //おすすめフラグ
        public String recommendFlag;
        //グレードタイプ
        public String gradeType;
        //スライド値引きフラグ
        public String volumeDiscountFlag;
        //C-Valuフラグ
        public String cValueFlag;
        //アイコンリスト
        public ArrayList<String> iconList;
        //ピクトリスト
        public ArrayList<String> pictList;
        //複雑品フラグ
        public String complexFlag;
        //キャンペーン終了日
        public String campainEndDate;
        //基本情報リスト
        public ArrayList<StandardSpecInfo> mStandardSpecList = new ArrayList<>();
        //問い合わせ情報
        public ArrayList<ContactInfo> mContactList = new ArrayList<>();

		//2015/10/21 下記の 3個はキーワード検索だけ存在する
        //型番
        public String partNumber;
		//2015/10/21 追加
        public String innerCode;
        public String completeType;

        public static class StandardSpecInfo extends DataContainer{

            //スペック名称
            public String specName;
            //スペック単位
            public String specUnit;
            //スペック値
            public String specValue;

            boolean setData (JSONObject json) {
                try {
                    specName = getJsonString(json, "specName");
                    specUnit = getJsonString(json, "specUnit");
                    specValue = getJsonString(json, "specValue");

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLog.e(e.getMessage());
                    return false;
                }
                return true;
            }
        }

        public static class ContactInfo extends DataContainer{

            //問い合わせ先名称
            public String contactName;
            //TEL
            public String tel;
            //FAX
            public String fax;
            //受付時間
            public String receptionTime;

            boolean setData (JSONObject json) {
                try {
                    contactName = getJsonString(json, "contactName");
                    tel = getJsonString(json, "tel");
                    fax = getJsonString(json, "fax");
                    receptionTime = getJsonString(json, "receptionTime");

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLog.e(e.getMessage());
                    return false;
                }
                return true;
            }
        }

        boolean setData(JSONObject json) {

            try {

				//2015/10/21 下記の 3個はキーワード検索だけ存在する
                //型番
                partNumber = getJsonString(json, "partNumber");

                //2015/10/21 追加
                innerCode = getJsonString(json, "innerCode");
                completeType = getJsonString(json, "completeType");


                //カテゴリーコード
                categoryCode = getJsonString(json, "categoryCode");
                //カテゴリ名 2015/10/30対応
                categoryName = getJsonString(json, "categoryName");
                //シリーズコード
                seriesCode = getJsonString(json, "seriesCode");
                //シリーズ名称
                seriesName = getJsonString(json, "seriesName");
                //ブランドコード
                brandCode = getJsonString(json, "brandCode");
                //ブランド名称
                brandName = getJsonString(json, "brandName");
                //画像ＵＲＬ

                productImageUrlList = new ArrayList<>();
                JSONArray productImageUrlListArray = getJsonArray(json, "productImageUrlList");
                for (int i=0; i<productImageUrlListArray.length(); i++){
                    if (!productImageUrlListArray.isNull(i)){
                        String photUrl = (String)productImageUrlListArray.get(i);
                        productImageUrlList.add(photUrl);
                    }
                }
                //キャッチコピー
                catchCopy = getJsonString(json, "catchCopy");
                //最小通常出荷日
                minStandardDaysToShip = getJsonInteger(json, "minStandardDaysToShip");
                //最大通常出荷日
                maxStandardDaysToShip = getJsonInteger(json, "maxStandardDaysToShip");
                //最小通常価格
                minStandardUnitPrice = getJsonDouble(json, "minStandardUnitPrice");
                //最大通常価格
                maxStandardUnitPrice = getJsonDouble(json, "maxStandardUnitPrice");
                //おすすめフラグ
                recommendFlag = getJsonString(json, "recommendFlag");
                //グレードタイプ
                gradeType = getJsonString(json, "gradeType");
                //スライド値引きフラグ
                volumeDiscountFlag = getJsonString(json, "volumeDiscountFlag");
                //C-Valuフラグ
                cValueFlag = getJsonString(json, "cValueFlag");
                //アイコンリスト
                Object icon;
                iconList = new ArrayList<>();
                JSONArray iconListArray = getJsonArray(json, "iconList");
                for (int i=0; i<iconListArray.length(); i++){
                    icon = iconListArray.get(i);
                    iconList.add((String)icon);
                }

                //ピクトリスト
                Object pict;
                pictList = new ArrayList<>();
                JSONArray pictListArray = getJsonArray(json, "pictList");
                for (int i=0; i<pictListArray.length(); i++){
                    pict = pictListArray.get(i);
                    pictList.add((String)pict);
                }

                //キャンペーン終了日
                campainEndDate = getJsonString(json, "campaignEndDate");
                //複雑品フラグ
                complexFlag = getJsonString(json, "complexFlag");
                //基本情報リスト
                mStandardSpecList = new ArrayList<>();
                JSONArray standardSpecList = getJsonArray(json, "standardSpecList");
                for (int i=0; i<standardSpecList.length(); i++){
                    StandardSpecInfo standardSpecInfo = new StandardSpecInfo();
                    if (!standardSpecInfo.setData(standardSpecList.getJSONObject(i))){
                        return false;
                    } else {
                        mStandardSpecList.add(standardSpecInfo);
                    }
                }
                //問い合わせ先情報
                mContactList = new ArrayList<>();
                JSONArray contactList = getJsonArray(json, "contactList");
                for (int i=0; i<contactList.length(); i++){
                    ContactInfo contactInfo = new ContactInfo();
                    if (!contactInfo.setData(contactList.getJSONObject(i))){
                        return false;
                    } else {
                        mContactList.add(contactInfo);
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
                AppLog.e(e.getMessage());
                return false;
            }
            return true;
        }
    }

    public boolean setData(String src){

        try {
            JSONObject json = new JSONObject(src);
            //商品件数
            totalCount = getJsonInteger(json, "totalCount");

            mSeriesList = new ArrayList<>();
            JSONArray serieses = getJsonArray(json, "seriesList");

            //--ADD NT-LWL 17/09/28 Series FR -
            complexFlagCount = 0;
            //--ADD NT-LWL 17/09/28 Series FR -
            for (int num=0; num<serieses.length(); num++){
                SearchSeriesList.Series series = new SearchSeriesList.Series();
                if (!series.setData(serieses.getJSONObject(num))){
                    return false;
                } else {
                    mSeriesList.add(series);
                    //--UDP NT-LWL 17/09/28 Series FR -
                    // 移除复杂品
                    if (!TextUtils.isEmpty(series.complexFlag) && series.complexFlag.equals("1")){
                        complexFlagCount++;
                        AppLog.d("复杂品名称 = "+series.seriesName);
                        mSeriesList.remove(series);
                    }
                    //--UDP NT-LWL 17/09/28 Series TO -
                }
            }

    //--ADD NT-LWL 17/09/08 BrandSearch FR -
            JSONArray brandList = getJsonArray(json, "seriesBrandList");
            for (int i=0; i<brandList.length();i++){
                Brand brand =new Brand();
                brand.position = i;
                if (!brand.setData(brandList.getJSONObject(i))){
                    return false;
                }else {
                    mSeriesBrandList.add(brand);
                }
            }
    //--ADD NT-LWL 17/09/08 BrandSearch TO -

            errorList = getErrorList(json);


        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }
}
