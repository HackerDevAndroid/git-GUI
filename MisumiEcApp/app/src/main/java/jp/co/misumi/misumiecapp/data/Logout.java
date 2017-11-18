package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ログインデータ
 */
public class Logout extends DataContainer {

    public static final String API002200 = "API002200";
    public ErrorList errorList;

    public boolean setData(String src){

        if (src.equals("")){
            return true;
        }

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }

}
