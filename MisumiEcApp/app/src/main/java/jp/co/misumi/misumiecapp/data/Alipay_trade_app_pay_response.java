package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by Administrator on 2016/11/28.
 */
public class Alipay_trade_app_pay_response extends DataContainer {
    public String code;
    public String msg;
    public String app_id;
    public String out_trade_no;
    public String trade_no;
    public String total_amount;
    public String seller_id;
    public String charset;
    public String timestamp;

    boolean setData(JSONObject json) {
        try{
            code = getJsonString(json,"code");
            msg = getJsonString(json,"msg");
            app_id = getJsonString(json,"app_id");
            out_trade_no = getJsonString(json,"out_trade_no");
            trade_no = getJsonString(json,"trade_no");
            total_amount = getJsonString(json,"total_amount");
            seller_id = getJsonString(json,"seller_id");
            charset = getJsonString(json,"charset");
            timestamp = getJsonString(json,"timestamp");
        }catch (JSONException e) {
            AppLog.e(e);
            return false;
        }
        return true;
    }
}
