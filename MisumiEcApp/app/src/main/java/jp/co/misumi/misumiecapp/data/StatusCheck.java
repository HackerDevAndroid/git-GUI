package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ステータスチェック
 */
public class StatusCheck extends DataContainer {


    public static final int ESTIMATE_MAINTENANCE = 0;
    public static final int ESTIMATE_ACCEPT = 1;
    public static final int ORDER_MAINTENANCE = 0;
    public static final int ORDER_ACCEPT = 1;
    public static final int ORDER_PROVISIONAL = 2;
    public static final int UNSET = -1;

    public String quotationStatus;
    public String orderStatus;
    public String currentDateTime;
    public String currentDayOfWeek;
    public ErrorList errorList;

    public StatusCheck(){
        quotationStatus = null;
        orderStatus = null;
        currentDateTime = null;
        errorList = null;
    }

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

            quotationStatus = getJsonString(json,"quotationStatus");
            orderStatus = getJsonString(json, "orderStatus");
            currentDateTime = getJsonString(json, "currentDateTime");
            currentDayOfWeek = getJsonString(json, "currentDayOfWeek");

            // エラーリスト
            errorList = getErrorList(json);

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * getQuotationStatus
     * @return
     */
    public int getQuotationStatus(){

        if (quotationStatus == null){
            return UNSET;
        }
        if (quotationStatus.compareTo("0") == 0) {
            return ESTIMATE_MAINTENANCE;
        }
        if (quotationStatus.compareTo("1") == 0) {
            return ESTIMATE_ACCEPT;
        }
        return UNSET;
    }

    /**
     * getOrderStatus
     * @return
     */
    public int getOrderStatus(){
        if (orderStatus == null){
            return UNSET;
        }
        if (orderStatus.compareTo("0") == 0) {
            return ORDER_MAINTENANCE;
        }
        if (orderStatus.compareTo("1") == 0) {
            return ORDER_ACCEPT;
        }
        if (orderStatus.compareTo("2") == 0) {
            return ORDER_PROVISIONAL;
        }
        return UNSET;
    }

}
