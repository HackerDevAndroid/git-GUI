package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * DataContainer
 */
public class DataContainer implements Serializable {

    /**
     * メンテナンスエラー
     */
    public static final String API000002 = "API000002";

    /**
     * setErrorList
     * @param json
     * @return
     */
    protected final ErrorList getErrorList(JSONObject json) throws JSONException {
        ErrorList errorList = null;
        // エラーリスト
        if (!json.has("errorList")){
//            AppLog.d("errorList is non");
        }else {
            errorList = new ErrorList();
            errorList.setData(json.getJSONArray("errorList"));
        }
        return errorList;
    }


    /**
     * getJsonString
     * @param jsonObj
     * @param tag
     * @return
     * @throws JSONException
     */
	protected final String getJsonString(JSONObject jsonObj, String tag) throws JSONException {

        if (jsonObj.isNull(tag)){
//            AppLog.d(tag +" is null");
            return null;
        }

        if (jsonObj.has(tag)) {
            return jsonObj.getString(tag);
        } else {
//            AppLog.d(tag +" is non");
            return null;
        }
	}

    /**
     * getJsonStringToUpperCase
     * @param jsonObj
     * @param tag
     * @return
     * @throws JSONException
     */
	protected final String getJsonStringToUpperCase(JSONObject jsonObj, String tag) throws JSONException {

        if (jsonObj.isNull(tag)){
            //AppLog.d(tag +" is null");
            return null;
        }

        if (jsonObj.has(tag)) {
            return jsonObj.getString(tag).toUpperCase();
        } else {
//            AppLog.d(tag +" is non");
            return null;
        }
	}

    /**
     * getJsonDouble
     * @param jsonObj
     * @param tag
     * @return
     * @throws JSONException
     */
	protected final Double getJsonDouble(JSONObject jsonObj, String tag) throws JSONException {

        if (jsonObj.isNull(tag)){
            //AppLog.d(tag +" is null");
            return null;
        }

        if (jsonObj.has(tag)) {
            return jsonObj.getDouble(tag);
        } else {
//            AppLog.d(tag +" is non");
            return null;
        }
	}

    /**
     * getJsonInteger
     * @param jsonObj
     * @param tag
     * @return
     * @throws JSONException
     */
	protected final Integer getJsonInteger(JSONObject jsonObj, String tag) throws JSONException {

        if (jsonObj.isNull(tag)){
            //AppLog.d(tag +" is null");
            return null;
        }

        if (jsonObj.has(tag)) {
            return jsonObj.getInt(tag);
        } else {
//            AppLog.d(tag +" is non");
            return null;
        }
	}

    /**
     * getJsonArray
     * @param jsonObj
     * @param tag
     * @return
     * @throws JSONException
     */
	protected final JSONArray getJsonArray(JSONObject jsonObj, String tag) throws JSONException {

        if (jsonObj.isNull(tag)){
            //AppLog.d(tag +" is null");
            return new JSONArray();
        }

        if (jsonObj.has(tag)) {
            return jsonObj.getJSONArray(tag);
        } else {
//            AppLog.d(tag +" is non");
            return new JSONArray();
        }
	}

}
