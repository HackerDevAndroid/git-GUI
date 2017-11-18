package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ストーク情報
 */
public class PriceInfo extends DataContainer {

    // List priceList			//　価格リスト
    public String partNumber;                //○型番
    public String catalogPartNumber;        //　カタログ型番
    public String brandCode;                //　ブランドコード略称
    public String seriesCode;    //追加系API用
    public String productName;                //　商品名
    public Double unitPrice;                //　単価
    public Double standardUnitPrice;        //　標準単価
    public Integer quantity;                //　数量
    public Double totalPrice;                //　合計金額
    public Double totalPriceIncludingTax;    //　税込合計金額
    public String currencyCode;                //　通貨コード
    public String expressType;                //　ストーク
    public Integer daysToShip;                //　出荷日数
    public String shipDate;                    //　出荷日
    public String deliveryDate;                //　顧客到着日
    public String shipType;                    //　出荷区分
    public Integer longLeadTimeThreshold;    //　長納期品出荷日数閾値
    public Integer minQuantity;                //　最低発注数量
    public Integer orderUnit;                //　発注単位数量
    public Integer piecesPerPackage;        //　パック品入数
    public String orderDeadline;            //　受注〆時刻
    public Integer largeOrderMinQuantity;    //　大口下限数量
    public Integer largeOrderMaxQuantity;    //　大口上限数量

    public String editedQuantity;            //　受注〆時刻

    // List volumeDiscountList				//○スライド情報リスト
    public ArrayList<VolumeDiscount> mVolumeDiscountList = new ArrayList<>();

    // List expressList				//　ストーク情報リスト
    public ArrayList<ExpressInfo> mExpressList = new ArrayList<>();

    public ErrorList errorList;

    boolean setData(JSONObject json) {
        try {

            // エラーリスト
            errorList = getErrorList(json);

            //○型番
            partNumber = getJsonString(json, "partNumber");

            //　カタログ型番
            catalogPartNumber = getJsonString(json, "catalogPartNumber");

            //　ブランドコード略称
            brandCode = getJsonString(json, "brandCode");

            //　商品名
            productName = getJsonString(json, "productName");

            //　単価
            unitPrice = getJsonDouble(json, "unitPrice");

            //　標準単価
            standardUnitPrice = getJsonDouble(json, "standardUnitPrice");

            //　数量
            quantity = getJsonInteger(json, "quantity");

            //　合計金額
            totalPrice = getJsonDouble(json, "totalPrice");

            //　税込合計金額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            //　通貨コード
            currencyCode = getJsonString(json, "currencyCode");

            //　ストーク
            expressType = getJsonString(json, "expressType");

            //　出荷日数
            daysToShip = getJsonInteger(json, "daysToShip");

            //　出荷日
            shipDate = getJsonString(json, "shipDate");

            //　顧客到着日
            deliveryDate = getJsonString(json, "deliveryDate");

            //　出荷区分
            shipType = getJsonString(json, "shipType");

            //　長納期品出荷日数閾値
            longLeadTimeThreshold = getJsonInteger(json, "longLeadTimeThreshold");

            //　最低発注数量
            minQuantity = getJsonInteger(json, "minQuantity");

            //　発注単位数量
            orderUnit = getJsonInteger(json, "orderUnit");

            //　パック品入数
            piecesPerPackage = getJsonInteger(json, "piecesPerPackage");

            //　受注〆時刻
            orderDeadline = getJsonString(json, "orderDeadline");

            //　大口下限数量
            largeOrderMinQuantity = getJsonInteger(json, "largeOrderMinQuantity");

            //　大口上限数量
            largeOrderMaxQuantity = getJsonInteger(json, "largeOrderMaxQuantity");

            //UI数量
            if (quantity == null) {
                quantity = 1;    //デフォルト数量は 1にする（決定済み仕様）
            }

            editedQuantity = "" + quantity;

            // List volumeDiscountList			//○スライド情報リスト
            mVolumeDiscountList.clear();
            if (json.has("volumeDiscountList")) {
                JSONArray discountList = getJsonArray(json, "volumeDiscountList");
                for (int ii = 0; ii < discountList.length(); ii++) {
                    JSONObject discountinfo = discountList.getJSONObject(ii);

                    VolumeDiscount vol = new VolumeDiscount();
                    if (vol.setData(discountinfo)) {
                        mVolumeDiscountList.add(vol);
                    } else {
                        return false;
                    }
                }
            }

            // List expressList			//　ストーク情報リスト
            mExpressList.clear();
            JSONArray itemList = getJsonArray(json, "expressList");
            for (int ii = 0; ii < itemList.length(); ii++) {
                ExpressInfo itemInfo = new ExpressInfo();
                if (!itemInfo.setData(itemList.getJSONObject(ii))) {
                    return false;
                } else {
                    mExpressList.add(itemInfo);
                }
            }

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }
}
