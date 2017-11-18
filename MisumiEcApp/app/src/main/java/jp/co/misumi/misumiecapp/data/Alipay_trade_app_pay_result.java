package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by Administrator on 2016/11/28.
 */
public class Alipay_trade_app_pay_result extends DataContainer{
//    public Alipay_trade_app_pay_response mAlipay_trade_app_pay_response;
    public String response;
    public String sign;
    public String sign_type;

    public boolean setData(String src) {
        try {
            JSONObject json = new JSONObject(src);
//            AppLog.d("原始的result："+src);
//            mAlipay_trade_app_pay_response = new Alipay_trade_app_pay_response();
//            JSONObject jsonMap = new JSONObject();
//            if (json.has("alipay_trade_app_pay_response")) {
//                AppLog.d("alipay_trade_app_pay_response：" + getJsonString(json,"alipay_trade_app_pay_response"));
//                jsonMap = json.getJSONObject("alipay_trade_app_pay_response");
//                AppLog.d("alipay_trade_app_pay_response转json"+jsonMap.toString());
////                mAlipay_trade_app_pay_response.setData(jsonMap);
//            }
            response = src.split("\"alipay_trade_app_pay_response\":")[1].split(",\"sign\":")[0];
//            response = jsonMap.toString();
            sign = getJsonString(json,"sign");
            sign_type = getJsonString(json,"sign_type");
        }
        catch (JSONException e) {
            AppLog.e(e);
            return false;
        }
        return true;
    }

}
