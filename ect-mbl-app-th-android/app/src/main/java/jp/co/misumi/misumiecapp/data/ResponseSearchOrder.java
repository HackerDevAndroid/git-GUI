package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 注文履歴検索API用データ（レスポンス）2015/08/28版
 */
public class ResponseSearchOrder extends ResponseSearch {

    //注文履歴リスト
    public static class ListInfo extends ResponseSearchInfo {

        public String orderType;                    //注文方法
        public String paymentDeadlineDateTime;        //支払期限
        public String registerDateTime;                //登録日時

        @Override
        public boolean setData(JSONObject json) {

            if (!super.setData(json)) {
                return false;
            }

            try {
                //伝票番号
                infoSlipNo = getJsonString(json, "orderSlipNo");
                infoDateTime = getJsonString(json, "orderDateTime");

                orderType = getJsonString(json, "orderType");
                paymentDeadlineDateTime = getJsonString(json, "paymentDeadlineDateTime");
                registerDateTime = getJsonString(json, "registerDateTime");

                //quotationItemList
                JSONArray itemList = getJsonArray(json, "orderItemList");

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

        public String approvalStatus;                    //承認ステータス


        @Override
        public boolean setData(JSONObject json) {

            if (!super.setData(json)) {
                return false;
            }

            try {
                //明細番号
                infoItemNo = getJsonString(json, "orderItemNo");

                //承認ステータス
                approvalStatus = getJsonString(json, "approvalStatus");

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

            JSONArray itemList = getJsonArray(json, "orderList");

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
