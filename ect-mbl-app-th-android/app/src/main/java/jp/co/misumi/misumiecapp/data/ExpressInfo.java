package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * ストーク情報
 */
public class ExpressInfo extends DataContainer {


    public String expressType;            //　ストーク
    public String chargeType;            //　料金計算方式
    public Double charge;                //　料金
    //追加料金の場合は正の金額、割引料金の場合は負の金額を返します
    public String expressDeadline;        //　ストーク〆時刻
    public String enableFlag;            //　選択可能フラグ
    //選択可能フラグ	新規	選択可能な場合は"1"、締め時間を超過している場合など、選択不可の場合は"0"を返します

    //出荷日数
    public Integer daysToShip;

    public ErrorList errorList;

    boolean setData(JSONObject json) {

        try {

            // エラーリスト
            errorList = getErrorList(json);

            //　ストーク
            expressType = getJsonString(json, "expressType");

            //　料金計算方式
            chargeType = getJsonString(json, "chargeType");

            //　料金
            charge = getJsonDouble(json, "charge");

            //　ストーク〆時刻
            expressDeadline = getJsonString(json, "expressDeadline");

            //　選択可能フラグ
            enableFlag = getJsonString(json, "enableFlag");

            //　出荷日数
            daysToShip = getJsonInteger(json, "daysToShip");

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }

}
