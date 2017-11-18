package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 注文履歴詳細API用データ
 */
public class ResponseGetOrderDetail extends DataContainer {

    public String orderSlipNo;
    public String headerOrderNo;
    public String orderDateTime;
    public String paymentDeadlineDateTime;
    public String orderType;
    public String userName;
    public Double totalPrice;
    public Double totalPriceIncludingTax;
	public Double cashOnDeliveryChargeIncludingTax;		//　代引き
    public String registerDateTime;
    public ArrayList<ItemInfo> mItemList = new ArrayList<>();
    public ErrorList errorList;

    //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
    public String settlementType;
    public String paymentGroup;
    public String paymentGroupName;
    public String paymentType;
    public String paymentTypeName;
    //-- ADD NT-LWL 16/11/13 AliPay Payment TO -


    public static class ItemInfo extends DataContainer {

        public String	orderItemNo;
        public String   partNumber;
        public String   productName;
        public String   seriesCode;
        public String   innerCode;
        public String   productImageUrl;
        public String   brandCode;
        public String   brandName;
        public Integer  quantity;
        public Double   unitPrice;
        public Double   unitPriceWithTax;
        public Double   standardUnitPrice;
        public Double   totalPrice;
        //public Double   tax;
        public Double   totalPriceWithTax;
        public Integer  piecesPerPackage;
        public String   campaignFlag;
        public String   campaignEndDate;
        public String   couponCode;
        public String   couponFlag;
        public String   misumiNo;
        public String invoiceNo;
        public String deliveryCompanyName;
        public String deliveryCompanyAbbrName;
        public String deliveryCompanyCode;
        public String deliveryStatusUrl;
        public String shipDateTime;
        //public String shipType;
        public String expressType;
        public String status;
        public Integer piecesPerPakage;
        public String campainEndDate;

        public boolean	checked;

        public ErrorList errorList;



        boolean setData(JSONObject itemInfo) {
            try {
                orderItemNo = getJsonString(itemInfo, "orderItemNo");
                partNumber = getJsonString(itemInfo, "partNumber");
                productName = getJsonString(itemInfo, "productName");
                seriesCode = getJsonString(itemInfo, "seriesCode");
                innerCode = getJsonString(itemInfo, "innerCode");
                productImageUrl = getJsonString(itemInfo, "productImageUrl");
                brandCode = getJsonString(itemInfo, "brandCode");
                brandName = getJsonString(itemInfo, "brandName");
                quantity = getJsonInteger(itemInfo, "quantity");
                unitPrice = getJsonDouble(itemInfo, "unitPrice");
                unitPriceWithTax = getJsonDouble(itemInfo, "unitPriceWithTax");
                standardUnitPrice = getJsonDouble(itemInfo, "standardUnitPrice");
                totalPrice = getJsonDouble(itemInfo, "totalPrice");
                //tax = getJsonDouble(itemInfo, "tax");
                totalPriceWithTax = getJsonDouble(itemInfo, "totalPriceIncludingTax");
                piecesPerPackage = getJsonInteger(itemInfo, "piecesPerPackage");
                campaignFlag = getJsonString(itemInfo, "campaignFlag");
                campaignEndDate = getJsonString(itemInfo, "campaignEndDate");
                couponCode = getJsonString(itemInfo, "couponCode");
                couponFlag = getJsonString(itemInfo, "couponFlag");
                misumiNo = getJsonString(itemInfo, "misumiNo");
                invoiceNo = getJsonString(itemInfo, "invoiceNo");
                deliveryCompanyName = getJsonString(itemInfo, "deliveryCompanyName");
                deliveryCompanyAbbrName = getJsonString(itemInfo, "deliveryCompanyAbbrName");
                deliveryCompanyCode = getJsonString(itemInfo, "deliveryCompanyCode");
                deliveryStatusUrl = getJsonString(itemInfo, "deliveryStatusUrl");
                shipDateTime = getJsonString(itemInfo, "shipDateTime");
                //shipType = getJsonString(itemInfo, "shipType");
                expressType = getJsonString(itemInfo, "expressType");
                status = getJsonString(itemInfo, "status");

                //　パック数量
                piecesPerPakage = getJsonInteger(itemInfo, "piecesPerPackage");

                //キャンペーン終了日
                campainEndDate = getJsonString(itemInfo, "campaignEndDate");

                checked = false;

                errorList = getErrorList(itemInfo);

            } catch (JSONException e) {
                e.printStackTrace();
                AppLog.e(e.getMessage());
                return false;
            }
            return true;
        }
    }


    public boolean setData(String src){

        if (!mItemList.isEmpty()){
            mItemList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            errorList = getErrorList(json);

            orderSlipNo = getJsonString(json, "orderSlipNo");
            headerOrderNo = getJsonString(json, "headerOrderNo");
            orderDateTime = getJsonString(json, "orderDateTime");
            paymentDeadlineDateTime = getJsonString(json, "paymentDeadlineDateTime");
            orderType = getJsonString(json, "orderType");
            userName = getJsonString(json, "userName");
            totalPrice = getJsonDouble(json, "totalPrice");
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");
            cashOnDeliveryChargeIncludingTax = getJsonDouble(json, "cashOnDeliveryChargeIncludingTax");
            registerDateTime = getJsonString(json, "registerDateTime");

            //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
            settlementType = getJsonString(json,"settlementType");
            paymentGroup = getJsonString(json,"paymentGroup");
            paymentGroupName = getJsonString(json,"paymentGroupName");
            paymentType = getJsonString(json,"paymentType");
            paymentTypeName = getJsonString(json,"paymentTypeName");
            //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

            JSONArray itemList = getJsonArray(json, "orderItemList");

            for (int ii = 0; ii < itemList.length(); ii++) {
                ItemInfo itemInfo = new ItemInfo();
                if (!itemInfo.setData(itemList.getJSONObject(ii))){
                    return false;
                } else {
                    mItemList.add(itemInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }

}
