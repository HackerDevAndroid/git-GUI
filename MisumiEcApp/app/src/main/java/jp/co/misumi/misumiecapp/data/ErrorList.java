package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.Serializable;


/**
 * エラーリストデータ
 */
public class ErrorList extends DataContainer{

    public ArrayList<ErrorInfo> ErrorInfoList = new ArrayList<>();

    public static class ErrorInfo implements Serializable{
        public String errorCode;
        public String errorType;
        public String errorLevel;
        public String errorMessage;
        public ArrayList<Object> errorParameterList;
        public ArrayList<Object> fieldList;

        boolean setData(JSONObject errorinfo) throws JSONException {

            if (errorinfo.has("errorCode")) {
                errorCode = errorinfo.getString("errorCode");
            }else{
                //AppLog.v("errorCode is null");
                errorCode = null;
            }
            if (errorinfo.has("errorType")) {
                errorType = errorinfo.getString("errorType");
            }else{
                //AppLog.v("errorType is null");
                errorType = null;
            }
            if (errorinfo.has("errorLevel")) {
                errorLevel = errorinfo.getString("errorLevel");
            }else{
                //AppLog.v("errorLevel is null");
                errorLevel = null;
            }
            if (errorinfo.has("errorMessage")) {
                errorMessage = errorinfo.getString("errorMessage");
            }else{
                //AppLog.v("errorMessage is null");
                errorMessage = null;
            }

            //パラメータ設定
            errorParameterList = new ArrayList<>();
            Object obj;
            //errorParameterList
            if (errorinfo.has("errorParameterList")){

				//TODO:暫定不正JSON対応
				if (errorinfo.get("errorParameterList") instanceof JSONArray) {

                    JSONArray errorParameterListArray = errorinfo.getJSONArray("errorParameterList");
                    for (int num=0; num<errorParameterListArray.length(); num++){
                        obj = errorParameterListArray.get(num);

                        //2015/10/23 サーバ側レスポンス不正null避け処理
                        if (obj == JSONObject.NULL) {

                            if (errorParameterListArray.length() == 1) {
                                break;
                            }

                        }else if (obj instanceof JSONObject) {
                        }else if (obj instanceof JSONArray){

                            JSONArray array = (JSONArray) obj;
                            if (array.length() > 0 ){
                                String addstring;
                                Object o = array.get(0);

                                if (o instanceof String){
                                    addstring = (String) o;
                                }else{
                                    addstring = o.toString();
                                }
                                ArrayList<String> data = new ArrayList<>();
                                data.add(addstring);
                                errorParameterList.add(data);
                            }
                        }else{
                            errorParameterList.add(obj);
                        }

                    }

                }
            } else {
                //AppLog.v("errorParameterList is null");
                errorParameterList = null;
            }



            //fieldList
            fieldList = new ArrayList<>();
            Object str;
            if (errorinfo.has("fieldList")){
                JSONArray fieldListArray = errorinfo.getJSONArray("fieldList");
                for (int num=0; num<fieldListArray.length(); num++){
                    str = fieldListArray.get(num);

					//2015/10/23 サーバ側レスポンス不正null避け処理
					if (str == JSONObject.NULL) {

						if (fieldListArray.length() == 1) {
							break;
						}

						str = "";
					}

                    fieldList.add(str);
                }
            } else {
                //AppLog.v("fieldList is null");
                fieldList = null;
            }
            return true;
        }

        public String getErrorMessage(String screenId){

            String msg = ErrorMessageManager.getManagerInstance().getMessage(errorCode, errorMessage);
            try {

                if (errorCode == null || errorCode.isEmpty()) {

                    return errorMessage;
                } else {

                    Object[] words;

                    //errorParameterListがnullかどうか
                    if (errorParameterList == null || errorParameterList.isEmpty()) {
                        return msg;
                    } else {
                        //パラメータ要素取得
                        int paramNum = errorParameterList.size();
                        if (paramNum == 0){
                            return errorMessage;
                        } else {
                            words = new Object[paramNum];
                            Object param;
                            for (int i = 0; i < paramNum; i++) {
                                if (errorParameterList.get(i) instanceof ArrayList) {
                                    param = ((ArrayList) errorParameterList.get(i)).get(0);
                                    words[i] = ErrorMessageManager.getManagerInstance().getWord(screenId, param);
                                } else {
                                    words[i] = errorParameterList.get(i);
                                }
                            }
                            msg = String.format(msg, words);
                            return msg;
                        }
                    }
                }
            } catch (Exception e){
                return msg;
            }
        }
    }

    public boolean setData(JSONArray errorlist) throws JSONException {

        for (int ii = 0; ii < errorlist.length(); ii++) {
//            try {
                JSONObject errorinf = errorlist.getJSONObject(ii);
                ErrorInfo info = new ErrorInfo();
                if (!info.setData(errorinf)){
                    return false;
                }
                ErrorInfoList.add(info);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
        return true;
    }

//    public ErrorInfo getInfo(){
//        if (ErrorInfoList.size() == 0){
//            return null;
//        }
//        ErrorInfo result = null;
//
//        return result;
//    }

/*
    public ErrorInfo getError(){
        if (ErrorInfoList.size() == 0){
            return null;
        }
        String comp = "";
        ErrorInfo result = null;

        for (ErrorInfo info : ErrorInfoList) {
//            if (info.errorCode == null) {
//                continue;
                comp = info.errorCode;
                result = info;
//            }
//            if (comp.compareTo(info.errorCode) < 0) {
//                comp = info.errorCode;
//                result = info;
//            }
        }
        return result;
    }
    public String getErrorMessage(String screenId) {

        ErrorInfo errorInfo = this.getError();
        if (errorInfo == null){
            return null;
        }
        return errorInfo.getErrorMessage(screenId);
    }
*/

    public String getErrorMessage(String screenId) {

        if (ErrorInfoList == null) {
            return null;
        }

        String msgs = null;
        for (ErrorInfo errorInfo : ErrorInfoList) {

			String msg = errorInfo.getErrorMessage(screenId);
			if (android.text.TextUtils.isEmpty(msg)) {
				continue;
			}

			if (android.text.TextUtils.isEmpty(msgs)) {
	        	msgs = msg;
			} else {
	        	msgs += "\n";
	        	msgs += msg;
			}
        }

        return msgs;
    }


//    public boolean isMaintenance(){
//        if (ErrorInfoList.size() == 0){
//            return false;
//        }
//        for (ErrorInfo info : ErrorInfoList){
//            if (API000002.compareTo(info.errorCode) == 0){
//                return true;
//            }
//        }
//        return false;
//    }


    public boolean isEmpty(){
        return ErrorInfoList.isEmpty();
    }
}

