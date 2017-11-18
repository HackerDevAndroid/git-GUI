//-- ADD NT-LWL 16/11/16 AliPay Payment FR -
package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created by Administrator on 2016/11/16.
 */
public class AliPaymentInfo extends DataContainer {
    public ErrorList errorList;

    public String userName;//担当者名(現地語)

    public String address1;

    public String address2;

    public String address3;

    public String address4;

    public String tel;//電話番号

    public Double totalPriceIncludingTax;//税込合計金額 (元単位)

    public List<String> onlinePaymentTypeList; //オンラインペイメントプラットフォームのリスト 60 : Alipay

    public String paymentDeadlineDateTime;

    public Alipay mAlipayInfo;

    public String orderSlipNo;//订单号


    public boolean isFromList;
    public boolean isFromDetail;
    public boolean isFromComplete;

    public boolean isFromList() {
        return isFromList;
    }
    public void setFromList() {
        isFromList = true;
    }

    public boolean isFromDetail() {
        return isFromDetail;
    }
    public void setFromDetail() {
        isFromDetail = true;
    }

    public boolean isFromComplete() {
        return isFromComplete;
    }
    public void setFromComplete() {
        isFromComplete = true;
    }


    public boolean setData(String src) {
        try{
            JSONObject json = new JSONObject(src);
            errorList = getErrorList(json);
            userName =  getJsonString(json, "userName");
            address1 =  getJsonString(json, "address1");
            address2 =  getJsonString(json, "address2");
            address3 =  getJsonString(json, "address3");
            address4 =  getJsonString(json, "address4");
            tel = getJsonString(json,"tel");
            totalPriceIncludingTax = getJsonDouble(json,"totalPriceIncludingTax");
            onlinePaymentTypeList = new ArrayList<>();
            JSONArray onlinePaymentTypeArrayList = getJsonArray(json,"onlinePaymentTypeList");
            for (int i = 0 ; i <onlinePaymentTypeArrayList.length(); i++){
                if(onlinePaymentTypeArrayList.get(i)!=null&&!onlinePaymentTypeArrayList.get(i).toString().isEmpty()){
                    onlinePaymentTypeList.add(onlinePaymentTypeArrayList.get(i).toString());
                }
            }
            paymentDeadlineDateTime = getJsonString(json,"paymentDeadlineDateTime");
            mAlipayInfo = new Alipay();
            if(json.has("alipay")){
                JSONObject jsonMap = json.getJSONObject("alipay");
                mAlipayInfo.setData(jsonMap);
            }


        }catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }
}
//-- ADD NT-LWL 16/11/16 AliPay Payment TO -
