package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 見積確認API用データ（レスポンス）2015/08/28版
 */
public class ResponseSearchQuotation extends ResponseSearch {

    //見積明細リスト
    public static class ListInfo extends ResponseSearchInfo {

        public String quotationExpireDateTime;        //見積有効期限

        @Override
        public boolean setData(JSONObject json) {

            if (!super.setData(json)) {
                return false;
            }

            try {
                //伝票番号
                infoSlipNo = getJsonString(json, "quotationSlipNo");
                infoDateTime = getJsonString(json, "quotationDateTime");

                quotationExpireDateTime = getJsonString(json, "quotationExpireDateTime");

                //quotationItemList
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


    //明細リスト
    public static class ItemInfo extends ResponseSearchItem {

        public Integer daysToShip;                        //出荷日数


        @Override
        public boolean setData(JSONObject json) {

            if (!super.setData(json)) {
                return false;
            }

            try {
                //明細番号
                infoItemNo = getJsonString(json, "quotationItemNo");

                //出荷日数
                daysToShip = getJsonInteger(json, "daysToShip");

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
    }


    @Override
    public boolean setData(String src) {

        if (!super.setData(src)) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);

            JSONArray itemList = getJsonArray(json, "quotationList");

            for (int ii = 0; ii < itemList.length(); ii++) {
                ListInfo info = new ListInfo();
                if (!info.setData(itemList.getJSONObject(ii))) {
                    return false;
                } else {
                    mList.add(info);
                }
            }
        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }

}
