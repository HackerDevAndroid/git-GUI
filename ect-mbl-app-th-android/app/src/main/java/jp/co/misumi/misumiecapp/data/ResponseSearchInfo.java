package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 履歴検索API用データ（リスト共通）
 */
public class ResponseSearchInfo extends DataContainer {

    public ErrorList errorList;

    public String infoSlipNo;                    //伝票番号
    public String infoDateTime;                    //日時

    public String headerOrderNo;                //ヘッダー注文番号
    public Double totalPrice;                    //合計金額
    public Double totalPriceIncludingTax;            //税込合計金額

    //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
    public String settlementType; //決済形態
    public String paymentGroup;//決済グループ
    public String paymentGroupName;//支付方法
    public String paymentType;
    //-- ADD NT-LWL 16/11/11 AliPay Payment TO -
    public List<ResponseSearchItem> mItemList = new ArrayList<>();

    public boolean setData(JSONObject json) {

        try {
            // エラーリスト
            errorList = getErrorList(json);

            headerOrderNo = getJsonString(json, "headerOrderNo");
            totalPrice = getJsonDouble(json, "totalPrice");
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");
            //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
//            settlementType = "ADV";
//            paymentGroup = "1";
            settlementType = getJsonString(json, "settlementType");
            paymentGroup = getJsonString(json, "paymentGroup");
            paymentGroupName = getJsonString(json, "paymentGroupName");
            paymentType = getJsonString(json, "paymentType");
            //-- ADD NT-LWL 16/11/11 AliPay Payment TO -

        } catch (JSONException e) {
            AppLog.e(e);
            return false;
        }

        return true;
    }

}
