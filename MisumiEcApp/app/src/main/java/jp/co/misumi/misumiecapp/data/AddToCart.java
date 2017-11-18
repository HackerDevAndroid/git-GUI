package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AddCart カート追加API用データ
 */
public class AddToCart extends DataContainer {

    public ErrorList errorList;

    public boolean setData(String src){

        if (src.length() == 0){
            return true;
        }

        try {
            JSONObject json = new JSONObject(src);
            errorList = getErrorList(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
