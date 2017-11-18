package jp.co.misumi.misumiecapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by ost000422 on 2015/08/03.
 */
public class ErrorMessageManager {

    public ArrayList<MessageInfo> MessageInfoList = new ArrayList<>();
    public ArrayList<ScreenWordInfo> ScreenWordInfoList = new ArrayList<>();
    private Context mContext;
    public String jsonData;

    private static ErrorMessageManager _managerInstance = null;

    public ErrorMessageManager(Context context) {
        mContext = context;
    }

    //インスタンスの作成
    public static ErrorMessageManager createInstance(Context context){
        if (_managerInstance == null){
            _managerInstance = new ErrorMessageManager(context);
        }
        return _managerInstance;
    }

    public static ErrorMessageManager getManagerInstance(){
        return _managerInstance;
    }

    private final String mFileName = "errorconfig";

    public class MessageInfo {
        public String errorCode;
        public String errorMessage;

        boolean setData(JSONObject errormessageinfo) throws JSONException {

            if (errormessageinfo.has("errorCode")) {
                errorCode = errormessageinfo.getString("errorCode");
            } else {
                AppLog.v("errorCode is null");
                errorCode = null;
            }
            if (errormessageinfo.has("errorMessage")) {
                errorMessage = errormessageinfo.getString("errorMessage");
            } else {
                AppLog.v("errorMessage is null");
                errorMessage = null;
            }
            return true;
        }
    }

    public class ScreenWordInfo {
        public String screenId;
        public String wordParam;
        public String errorWord;

        boolean setData(JSONObject screewordninfo) throws JSONException {

            if (screewordninfo.has("screenId")) {
                screenId = screewordninfo.getString("pageId");
            } else {
                AppLog.v("pageId is null");
                screenId = null;
            }
            if (screewordninfo.has("wordParam")) {
                wordParam = screewordninfo.getString("wordParam");
            } else {
                AppLog.v("wordParam is null");
                wordParam = null;
            }
            if (screewordninfo.has("errorWord")) {
                errorWord = screewordninfo.getString("errorWord");
            } else {
                AppLog.v("errorWord is null");
                errorWord = null;
            }
            return true;
        }
    }

    public boolean setData(String src) {

        SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = pref.edit();

        if (!MessageInfoList.isEmpty()) {
            MessageInfoList.clear();
        }
        if (!ScreenWordInfoList.isEmpty()) {
            ScreenWordInfoList.clear();
        }

        jsonData = src;
        JSONObject json;

        //メッセージ格納
        try {
            json = new JSONObject(src);
            JSONObject messageObj = json.getJSONObject("errorMessageList");
            Iterator<String> keys = messageObj.keys();
            while (keys.hasNext()) {
                String messageCode = keys.next();
                String message = messageObj.getString(messageCode);

                edit.putString(messageCode, message);
            }
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }

        //ワード格納
        try {
            json = new JSONObject(src);
            JSONObject screenObj = json.getJSONObject("screenWordList");
            Iterator<String> screenKeys = screenObj.keys();
            //画面ID
            while (screenKeys.hasNext()) {
                String screenId = screenKeys.next();
                JSONObject paramObj = screenObj.getJSONObject(screenId);
                Iterator<String> paramKeys = paramObj.keys();
                //パラメータ
                while (paramKeys.hasNext()) {
                    String wordParam = paramKeys.next();
                    String word = paramObj.getString(wordParam);

                    edit.putString(screenId + "+" + wordParam, word);
                }
            }
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }

    //getの処理
    public String getMessage(String errorMessageCode, String message) {
        String errorMessage;
        try {
            SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            errorMessage = pref.getString(errorMessageCode, message);
        } catch (Exception e){
            errorMessage = message;
        }
        return errorMessage;
    }

    public String getWord(String screenId, Object field) {
        String word;
        try{
            SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);

            // データ確認用ログ
//            Map<String, ?> data = pref.getAll();
//            for (String key : data.keySet()) {
//                AppLog.d(key + data.get(key));
//            }

            word = pref.getString(screenId + "+" + field, "");
            if (word.equals("")){
                word = pref.getString("Common+" + field, "");
            }
            if (word.equals("")){
                word = (String) field;
            }
        } catch (Exception e){
            word = "";
        }
        return word;
    }
}
