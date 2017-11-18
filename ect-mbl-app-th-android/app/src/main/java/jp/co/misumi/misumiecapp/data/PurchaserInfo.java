package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 発注者
 */
public class PurchaserInfo extends DataContainer {

    public String customerCode;                //○得意先コード
    public String customerName;                //○得意先名(現地語)
    public String userCode;                    //○担当者コード
    public String userName;                    //○請求先担当名
    public String userDepartmentName;        //○請求先部課名


    boolean setData(JSONObject json) {

        try {

            //○得意先コード
            customerCode = getJsonString(json, "customerCode");

            //○得意先名(現地語)
            customerName = getJsonString(json, "customerName");

            //○担当者コード
            userCode = getJsonString(json, "userCode");

            //○請求先担当名
            userName = getJsonString(json, "userName");

            //○請求先部課名
            userDepartmentName = getJsonString(json, "userDepartmentName");

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }

}
