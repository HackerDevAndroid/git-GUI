package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 見積履歴詳細API用データ
 */
public class ResponseGetQuotation extends DataContainer {


    public String quotationSlipNo;            //○見積伝票番号
    public String headerOrderNo;            //　ヘッダー注文番号
    public String quotationDateTime;            //○見積日時
    public String quotationExpireDateTime;            //○見積有効期限
    public String userName;            //○担当者名(現地語)
    public String userDeptName;            //○担当部門(現地語)
    public String receiverCode;            //○直送先コード
    public String receiverUserName;            //○納入者氏名(現地語)
    public String receiverDeptName;            //○納入者部課(現地語)
    public Double totalPrice;            //　合計金額
    public Double totalPriceIncludingTax;            //　税込合計金額

    // List quotationItemList			//○見積明細リスト
    public ArrayList<ItemInfo> mItemList = new ArrayList<>();

    public ErrorList errorList;


    //見積明細リスト
    public static class ItemInfo extends DataContainer {

        public String quotationItemNo;            //○見積明細番号
        public String partNumber;            //○型番
        public String productName;            //○商品名
        public String seriesCode;            //○シリーズコード
        public String innerCode;            //○インナーコード
        public String productImageUrl;            //　商品画像URL
        public String brandCode;            //○ブランドコード
        public String brandName;            //○ブランド名
        public Integer quantity;            //○数量
        public Double unitPrice;            //　単価
        public Double standardUnitPrice;            //　標準単価
        public Double totalPrice;            //　合計金額(明細)
        public Double tax;            //　税額(明細)
        public Double totalPriceIncludingTax;            //　税込合計金額(明細)
        public String campaignFlag;            //　キャンペーン適用フラグ
        public String couponCode;            //　クーポンコード
        public String couponFlag;            //　クーポン適用フラグ
        //		public  specialType;			//　特注区分
        public Integer daysToShip;            //　出荷日数
        public String shipDateTime;            //　出荷日時
        public String shipType;            //　出荷区分
        public String expressType;            //　ストーク
        public String status;            //○ステータス
        public Integer piecesPerPakage;            //　パック数量
        //キャンペーン終了日
        public String campainEndDate;

        public ErrorList errorList;

        public boolean checked;                        //UI操作用


        boolean setData(JSONObject json) {
            try {

                //○見積明細番号
                quotationItemNo = getJsonString(json, "quotationItemNo");

                //○型番
                partNumber = getJsonString(json, "partNumber");

                //○商品名
                productName = getJsonString(json, "productName");

                //○シリーズコード
                seriesCode = getJsonString(json, "seriesCode");

                //○インナーコード
                innerCode = getJsonString(json, "innerCode");

                //　商品画像URL
                productImageUrl = getJsonString(json, "productImageUrl");

                //○ブランドコード
                brandCode = getJsonString(json, "brandCode");

                //○ブランド名
                brandName = getJsonString(json, "brandName");

                //○数量
                quantity = getJsonInteger(json, "quantity");

                //　単価
                unitPrice = getJsonDouble(json, "unitPrice");

                //　標準単価
                standardUnitPrice = getJsonDouble(json, "standardUnitPrice");

                //　合計金額(明細)
                totalPrice = getJsonDouble(json, "totalPrice");

                //　税額(明細)
                tax = getJsonDouble(json, "tax");

                //　税込合計金額(明細)
                totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

                //　キャンペーン適用フラグ
                campaignFlag = getJsonString(json, "campaignFlag");

                //　クーポンコード
                couponCode = getJsonString(json, "couponCode");

                //　クーポン適用フラグ
                couponFlag = getJsonString(json, "couponFlag");

//				//　特注区分
//				specialType = getJson(json, "specialType");

                //　出荷日数
                daysToShip = getJsonInteger(json, "daysToShip");

                //　出荷日時
                shipDateTime = getJsonString(json, "shipDateTime");

                //　出荷区分
                shipType = getJsonString(json, "shipType");

                //　ストーク
                expressType = getJsonString(json, "expressType");

                //○ステータス
                status = getJsonString(json, "status");

                //　パック数量
                piecesPerPakage = getJsonInteger(json, "piecesPerPackage");

                //キャンペーン終了日
                campainEndDate = getJsonString(json, "campaignEndDate");

                //エラーリスト
                errorList = getErrorList(json);


                //UI変数
                checked = false;

                //UI数量
                if (quantity == null) {
                    quantity = 1;    //デフォルト数量は 1にする（決定済み仕様）
                }


            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
    }


    public boolean setData(String src) {

        if (!mItemList.isEmpty()) {
            mItemList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            //○見積伝票番号
            quotationSlipNo = getJsonString(json, "quotationSlipNo");

            //　ヘッダー注文番号
            headerOrderNo = getJsonString(json, "headerOrderNo");

            //○見積日時
            quotationDateTime = getJsonString(json, "quotationDateTime");

            //○見積有効期限
            quotationExpireDateTime = getJsonString(json, "quotationExpireDateTime");

            //○担当者名(現地語)
            userName = getJsonString(json, "userName");

            //○担当部門(現地語)
            userDeptName = getJsonString(json, "userDeptName");

            //○直送先コード
            receiverCode = getJsonString(json, "receiverCode");

            //○納入者氏名(現地語)
            receiverUserName = getJsonString(json, "receiverUserName");

            //○納入者部課(現地語)
            receiverDeptName = getJsonString(json, "receiverDeptName");

            //　合計金額
            totalPrice = getJsonDouble(json, "totalPrice");

            //　税込合計金額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            //エラーリスト
            errorList = getErrorList(json);


            JSONArray itemList = getJsonArray(json, "quotationItemList");

            for (int ii = 0; ii < itemList.length(); ii++) {
                ItemInfo itemInfo = new ItemInfo();
                if (!itemInfo.setData(itemList.getJSONObject(ii))) {
                    return false;
                } else {
                    mItemList.add(itemInfo);
                }
            }
        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }

}
