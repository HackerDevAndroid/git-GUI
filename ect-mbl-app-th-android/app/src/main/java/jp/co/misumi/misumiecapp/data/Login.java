package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ログインデータ
 */
public class Login extends DataContainer {

    public static final String API001100 = "API001100";


    public String sessionId;
    public String customerCode;
    public String customerName;
    public String userCode;
    public String userName;
    public String currencyCode;
    public String paymentType;
    public String settlementType;
    public Integer quotationUnfitCount;
    public Integer orderUnfitCount;
    public String[] permissionList;
    public ErrorList errorList;
    //--ADD NT-SLJ 17/07/14 3小时必达 FR -
    // 3小时必达对象标识 1：对象内 0：对象外
    public String immediateDeliveryFlag;
    //--ADD NT-SLJ 17/07/14 3小时必达 TO -

    /**
     * setData
     *
     * @param src
     * @return
     */
    public boolean setData(String src) {

        if (src == null || src.length() == 0) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);

            sessionId = getJsonString(json, "sessionId");
            customerCode = getJsonString(json, "customerCode");
            customerName = getJsonString(json, "customerName");
            userCode = getJsonString(json, "userCode");
            userName = getJsonString(json, "userName");
            currencyCode = getJsonString(json, "currencyCode");
            paymentType = getJsonString(json, "paymentType");
            settlementType = getJsonString(json, "settlementType");
            quotationUnfitCount = getJsonInteger(json, "quotationUnfitCount");
            orderUnfitCount = getJsonInteger(json, "orderUnfitCount");
            immediateDeliveryFlag = getJsonString(json, "immediateDeliveryFlag");

            if (json.has("permissionList")) {
                JSONArray array = json.getJSONArray("permissionList");
                permissionList = new String[array.length()];
                for (int ii = 0; ii < array.length(); ii++) {
                    permissionList[ii] = (String) array.get(ii);
                }
            } else {
                permissionList = null;
            }

            // エラーリスト
            errorList = getErrorList(json);

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

}
