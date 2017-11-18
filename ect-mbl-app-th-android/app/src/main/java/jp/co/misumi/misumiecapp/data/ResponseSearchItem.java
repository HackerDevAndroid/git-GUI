package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 履歴検索API用データ（明細共通）
 */
public class ResponseSearchItem extends DataContainer {

    public ErrorList errorList;

    public String infoItemNo;                        //明細番号

    public String partNumber;                        //型番
    public String productName;                    //商品名
    public String seriesCode;                        //シリーズコード
    public String innerCode;                        //インナーコード
    public String productImageUrl;                //商品画像URL
    public String brandCode;                        //ブランドコード
    public String brandName;                        //ブランド名
    public Double totalPrice;                    //合計金額
    public Double totalPriceIncludingTax;            //税込合計金額
    public String daysToShip;                       // 出荷日数
    public String shipDateTime;                        //出荷日時
    public String shipType;                        //出荷区分
    public String expressType;                    //ストーク
    public String status;                            //ステータス


    public boolean setData(JSONObject json) {

        try {

            // エラーリスト
            errorList = getErrorList(json);

            //型番
            partNumber = getJsonString(json, "partNumber");

            //商品名
            productName = getJsonString(json, "productName");

            //シリーズコード
            seriesCode = getJsonString(json, "seriesCode");

            //インナーコード
            innerCode = getJsonString(json, "innerCode");

            //商品画像URL
            productImageUrl = getJsonString(json, "productImageUrl");

            //ブランドコード
            brandCode = getJsonString(json, "brandCode");

            //ブランド名
            brandName = getJsonString(json, "brandName");

            // 合計金額
            totalPrice = getJsonDouble(json, "totalPrice");

            // 税込合計金額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            // 出荷日数
            daysToShip = getJsonString(json, "daysToShip");

            //出荷日時
            shipDateTime = getJsonString(json, "shipDateTime");

            //出荷区分
            shipType = getJsonString(json, "shipType");

            //ストーク
            expressType = getJsonString(json, "expressType");

            //ステータス
            status = getJsonString(json, "status");

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }

}
