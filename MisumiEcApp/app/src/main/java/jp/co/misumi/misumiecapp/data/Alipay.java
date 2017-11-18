//-- ADD NT-LWL 16/11/16 AliPay Payment FR -
package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by Administrator on 2016/11/16.
 */
public class Alipay extends DataContainer {
//    public String app_id; //開発者用のアプリID
//    public String method;//インターフェイス名称
//    public String format;//フォーマット
//    public String charset;//エンコード形式
//    public String sign_type;//サインアルゴリズム
//    public String sign;//サイン値
//    public String timestamp;//支払請求依頼時間
//    public String version;//インターフェイスバージョン
//    public String notify_url;//非同期通知URL
//    public AliBizContent mbizContent;
    public String query;//呼出支付宝参数

    boolean setData(JSONObject json) {
        try{
//            app_id = getJsonString(json,"app_id");
//            method = getJsonString(json,"method");
//            format = getJsonString(json,"format");
//            charset = getJsonString(json,"charset");
//            sign_type = getJsonString(json,"sign_type");
//            sign = getJsonString(json,"sign");
//            timestamp = getJsonString(json,"timestamp");
//            version = getJsonString(json,"version");
//            notify_url = getJsonString(json,"notify_url");
            query = getJsonString(json,"query");
//            mbizContent = new AliBizContent();
//            if(json.has("biz_content")){
//                JSONObject jsonMap = json.getJSONObject("biz_content");
//                mbizContent.setData(jsonMap);
//            }
        }catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }
}
//-- ADD NT-LWL 16/11/16 AliPay Payment TO -
