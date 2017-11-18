package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * バージョン情報
 */
public class VersionInfo extends DataContainer {

    public String subsidiaryCode;
    public Integer version_android;
    public Integer required_version_android;
    public String market_url_android;

    public boolean setData(String src) {

        if (src == null || src.length() == 0) {
            return false;
        }

        try {

            JSONObject json = new JSONObject(src);
            String result;

            result = getJsonString(json, "version_android");
            if (result != null){
                version_android = Integer.valueOf(result);
            }else{
                version_android = null;
            }

            result = getJsonString(json, "required_version_android");
            if (result != null){
                required_version_android = Integer.valueOf(result);
            }else{
                required_version_android = null;
            }
            market_url_android = getJsonString(json, "market_url_android");


            if (version_android == null || market_url_android == null || required_version_android == null){
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }
}
