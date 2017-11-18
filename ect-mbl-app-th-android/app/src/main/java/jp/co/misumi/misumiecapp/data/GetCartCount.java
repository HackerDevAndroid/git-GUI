package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * API009 カート件数取得API用データ
 */
public class GetCartCount extends DataContainer {

    public Integer count;
    public ErrorList errorList;

    public boolean setData(String src) {

        try {
            JSONObject json = new JSONObject(src);
            count = getJsonInteger(json, "count");

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
