package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 注文確定API用データ（レスポンス）2015/10/23版
 */
public class ResponseConfirmOrder extends ResponseConfirm {

    public static final String API003200 = "API003200";
    public static final String API003201 = "API003201";


    public boolean setData(String src) {

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            //受注伝票番号
            infoSlipNo = getJsonString(json, "orderSlipNo");
            itemCount = getJsonInteger(json, "itemCount");
            itemCountInChecking = getJsonInteger(json, "itemCountInChecking");
            totalPrice = getJsonDouble(json, "totalPrice");

            //○税込合計金額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            infoDatetime = getJsonString(json, "orderDateTime");


            //-- ADD NT-LWL 16/11/15 AliPay Payment FR -
            paymentGroup = getJsonString(json, "paymentGroup");
            paymentGroupName = getJsonString(json, "paymentGroupName");
            paymentDeadlineDateTime = getJsonString(json, "paymentDeadlineDateTime");
            //-- ADD NT-LWL 16/11/15 AliPay Payment TO -

        } catch (JSONException e) {
            AppLog.e(e);
            return false;
        }
        return true;
    }

}
