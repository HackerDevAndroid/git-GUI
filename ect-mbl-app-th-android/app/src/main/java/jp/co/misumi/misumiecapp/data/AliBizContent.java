//-- ADD NT-LWL 16/11/16 AliPay Payment FR -
package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by Administrator on 2016/11/16.
 */
public class AliBizContent extends DataContainer {
    public String body;
    public String subject;
    public String out_trade_no;
    public String timeout_express;
    public String total_amount;
    public String seller_id;
    public String product_code;

    boolean setData(JSONObject json) {
        try {
            body = getJsonString(json, "body");
            subject = getJsonString(json, "subject");
            out_trade_no = getJsonString(json, "out_trade_no");
            timeout_express = getJsonString(json, "timeout_express");
            total_amount = getJsonString(json, "total_amount");
            seller_id = getJsonString(json, "seller_id");
            product_code = getJsonString(json, "product_code");
        } catch (JSONException e) {
            AppLog.e(e);
            return false;
        }
        return true;
    }
}
//-- ADD NT-LWL 16/11/16 AliPay Payment TO -