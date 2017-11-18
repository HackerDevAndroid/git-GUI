package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ログインデータ
 */
public class Information extends DataContainer {

    public String serviceInformation;

    /**
     * setData
     * @param src
     * @return
     */
    public boolean setData(String src){

        if (src == null || src.length() == 0){
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);
            serviceInformation = getJsonString(json,"serviceInformation");

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

}
