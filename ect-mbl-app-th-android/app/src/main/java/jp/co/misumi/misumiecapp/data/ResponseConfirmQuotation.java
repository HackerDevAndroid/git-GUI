package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 見積確定API用データ（レスポンス）2015/10/23版
 */
public class ResponseConfirmQuotation extends ResponseConfirm {


    public boolean setData(String src) {

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            //見積伝票番号
            infoSlipNo = getJsonString(json, "quotationSlipNo");
            itemCount = getJsonInteger(json, "itemCount");
            itemCountInChecking = getJsonInteger(json, "itemCountInChecking");
            totalPrice = getJsonDouble(json, "totalPrice");

            //○税込合計金額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            infoDatetime = getJsonString(json, "quotationDateTime");

        } catch (JSONException e) {
            AppLog.e(e);
            return false;
        }
        return true;
    }

}
