package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * カート取得API用データ（レスポンス）2015/08/28版
 */
public class GetCart extends DataContainer {

//    public static final String API003200 = "API003200";
//    public static final String API003201 = "API003201";

    // List cartItemList			//○カートリスト
    public ArrayList<Product> mProductList = new ArrayList<>();

    public ErrorList errorList;

    // List cartItemList			//○カートリスト
    public static class Product extends DataContainer {

        public String cartId;                    //○カートID
        public String partNumber;                //○型番
        public String productName;                //○商品名
        public String seriesCode;                //○シリーズコード
        public String innerCode;                //○インナーコード
        public String productPageUrl;            //　商品ページURL
        public String productImageUrl;            //　商品画像URL
        public String brandCode;                //○ブランドコード
        public String brandName;                //○ブランド名
        public Integer quantity;                //○数量
        public Double unitPrice;                //　単価
        public Double standardUnitPrice;        //　標準単価
        public Double totalPrice;                //　合計金額
        public Double totalPriceWithTax;        //　税込合計金額
        public String currencyCode;                //　通貨コード
        public Integer daysToShip;                //　出荷日数
        public String shipType;                    //　出荷区分
        public String expressType;                //　ストーク
        public Integer piecesPerPackage;        //　パック品入数
        public String productType;                //○商品タイプ
        public String campaignEndDate;            //　キャンペーン終了日
        public String campaignEndFlag;            //　キャンペーン終了フラグ
        public String updateDateTime;            //○カート更新日時
        public String errorMessage;                //　エラーメッセージ
/*
エラー情報	30		エラーメッセージ		errorMessage	String			○	エラーメッセージ	この商品は10個単位でのご利用となります。	最大1件のみ
*/

        public boolean checked;                        //UI操作用
        //        public boolean	expanded;						//UI操作用
        public String editedQuantity;                //UI数量

        // List volumeDiscountList				//○スライド情報リスト
        public ArrayList<VolumeDiscount> volumeDiscountList = new ArrayList<>();


        //-- ADD NT-SLJ 17/07/13 3小时必达 FR –
        public ArrayList<String> expressList = new ArrayList<>();
        //-- ADD NT-SLJ 17/07/13 3小时必达 TO –


        boolean setData(JSONObject json) {
            try {

                //○カートID
                cartId = getJsonString(json, "cartId");

                //○型番
                partNumber = getJsonString(json, "partNumber");

                //○商品名
                productName = getJsonString(json, "productName");

                //○シリーズコード
                seriesCode = getJsonString(json, "seriesCode");

                //○インナーコード
                innerCode = getJsonString(json, "innerCode");

                //　商品ページURL
                productPageUrl = getJsonString(json, "productPageUrl");

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

                //　合計金額
                totalPrice = getJsonDouble(json, "totalPrice");

                //　税込合計金額
                totalPriceWithTax = getJsonDouble(json, "totalPriceWithTax");

                //　通貨コード
                currencyCode = getJsonString(json, "currencyCode");

                //　出荷日数
                daysToShip = getJsonInteger(json, "daysToShip");

                //　出荷区分
                shipType = getJsonString(json, "shipType");

                //　ストーク
                expressType = getJsonString(json, "expressType");

                //　パック品入数
                piecesPerPackage = getJsonInteger(json, "piecesPerPackage");

                //○商品タイプ
                productType = getJsonString(json, "productType");

                //　キャンペーン終了日
                campaignEndDate = getJsonString(json, "campaignEndDate");

                //　キャンペーン終了フラグ
                campaignEndFlag = getJsonString(json, "campaignEndFlag");

                //○カート更新日時
                updateDateTime = getJsonString(json, "updateDateTime");

                //　エラーメッセージ
                errorMessage = getJsonString(json, "errorMessage");

                //UI変数
                checked = false;
//        		expanded = false;

                //UI数量
                if (quantity == null) {
                    quantity = 1;    //デフォルト数量は 1にする（決定済み仕様）
                }

                editedQuantity = "" + quantity;


                // List volumeDiscountList			//○スライド情報リスト
                if (json.has("volumeDiscountList")) {
                    JSONArray discountList = getJsonArray(json, "volumeDiscountList");
                    for (int ii = 0; ii < discountList.length(); ii++) {
                        JSONObject discountinfo = discountList.getJSONObject(ii);

                        VolumeDiscount vol = new VolumeDiscount();
                        if (vol.setData(discountinfo)) {
                            volumeDiscountList.add(vol);
                        } else {
                            return false;
                        }
                    }
                }


                //-- ADD NT-SLJ 17/07/13 3小时必达 FR –
                if (json.has("expressList")) {
                    JSONArray discountList = getJsonArray(json, "expressList");
                    for (int ii = 0; ii < discountList.length(); ii++) {
                        JSONObject expressObj = discountList.getJSONObject(ii);

                        String express = getJsonString(expressObj, "expressType");
                        expressList.add(express);
                    }
                }
                //-- ADD NT-SLJ 17/07/13 3小时必达 TO –

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
    }


    //モジュラは無し


    public boolean setData(String src) {

        if (!mProductList.isEmpty()) {
            mProductList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            JSONArray productList = getJsonArray(json, "cartItemList");

            for (int ii = 0; ii < productList.length(); ii++) {
                Product product = new Product();
                if (!product.setData(productList.getJSONObject(ii))) {
                    return false;
                } else {
                    mProductList.add(product);
                }
            }
        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }

}
