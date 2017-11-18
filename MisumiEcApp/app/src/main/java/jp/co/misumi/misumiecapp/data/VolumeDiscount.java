package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * VolumeDiscount
 */
public class VolumeDiscount extends DataContainer{
    //volumeDiscountList

    //最小数量
    public Integer minQuantity;
    //最大数量
    public Integer maxQuantity;
    //単価
    public Double	unitPrice;
    //出荷日数
    public Integer daysToShip;

    boolean setData(JSONObject discountinfo) {
        try {
            //最小数量
            minQuantity = getJsonInteger(discountinfo, "minQuantity");
            //最大数量
            maxQuantity = getJsonInteger(discountinfo, "maxQuantity");
            //単価
            unitPrice = getJsonDouble(discountinfo, "unitPrice");
            //出荷日数
            daysToShip = getJsonInteger(discountinfo, "daysToShip");
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }
}
