package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 注文確認API用データ共通（レスポンス）2015/10/06版を必要な物だけ反映
 */
public class ResponseCheckOrder extends ResponseCheck {

    public boolean isFromCart;
    public boolean isFromQuote;

    public String receptionCode;            //○受付番号
    public String quotationSlipNo;        //Q	//　見積伝票番号
    public String customerOrderNo;            //　顧客注文番号

    public String deliveryType;                //　配送指定
    public String paymentType;                //　支払手段
    public String paymentTerms;                //　支払条件
    public String shipOption;                    //○出荷オプション
    public Double standardDeliveryCharge;    //  標準配送料（伝票単位）
    public Double deliveryCharge;            //　配送料(伝票単位)
    public Double deliveryChargeDiscount;    //　配送料値引(伝票単位)
    public Double tax;                        //　税金(伝票単位)
    public Double cashOnDeliveryChargeIncludingTax;        //　代引き

    public Double totalPrice;                //　合計金額
    public Double totalPriceIncludingTax;    //　税込合計税額
    //	public String unfitConfirmType;			//  アンフィット確認タイプ
    public String orderStatus;                //○注文ステータス
//	public String orderableType;


    public List<String> expressConfirmTypeList = new ArrayList<>();    // ストーク確認タイプリスト
    public String stockoutConfirmFlag;        // 在庫切れ確認フラグ


    // Map purchaser						//○発注者
    public boolean hasPurchaser;
    public PurchaserInfo purchaser;

    // Map receiver			//　直送先
    public boolean hasReceiver;
    public ReceiverInfo receiver;

    // List receiverList			//　
    public List<ReceiverInfo> mReceiverList = new ArrayList<>();

    // List orderItemList			//　注文明細リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public ErrorList errorList;

    //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
    public List<PaymentGroup> paymentGroupList = new ArrayList<>();

    public static class PaymentGroup extends DataContainer {
        public String paymentGroup;            //決済グループ
        public String paymentGroupName;        //決済グループ名
        public String selectedFlag;            //選択中フラグ

        public boolean setData(JSONObject json) {

            try {

                paymentGroup = getJsonString(json, "paymentGroup");
                paymentGroupName = getJsonString(json, "paymentGroupName");
                selectedFlag = getJsonString(json, "selectedFlag");

            } catch (JSONException e) {
                e.printStackTrace();
                AppLog.e(e);
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "PaymentGroup{" +
                    "paymentGroup='" + paymentGroup + '\'' +
                    ", paymentGroupName='" + paymentGroupName + '\'' +
                    ", selectedFlag='" + selectedFlag + '\'' +
                    '}';
        }
    }
    //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

    // List orderItemList			//　注文明細リスト
    public static class ItemInfo extends ResponseCheck.ItemInfo {

        public Integer orderItemNo;                //○注文明細番号
        public String cartId;                //C	//　カートID
        public String quotationSlipNo;        //Q	//○見積伝票番号

        public String customerOrderItemNo;        //　注文番号(親)
        public String customerOrderItemSubNo;    //　注文番号(子)

        public String brandCode;                //○ブランドコード
        //		public String brandName;
//		public String partNumber;				//○型番
        public String innerCode;                //○インナーコード

        //		public String productName;					//○商品名
        public List<String> productImageUrlList;    //○画像URL
        //		public Integer quantity;					//○受注数量
        public String unfitFlag;                //　アンフィットフラグ
        //		public String orderableFlag;			//○注文可能フラグ
        public String orderDate;                //○注文日
        //		public String shipType;					//　出荷区分
//		public Integer daysToShip;				//　出荷日数
        public String arrivalDate;                //　入荷日
        public String shipDate;                    //　出荷日
        public String earliestShipDate;            //　最短出荷日
        public String deliveryDate;                //　顧客到着日
        public String nextArrivalDate;
        public String couponApplyFlag;            //　クーポン適用可否フラグ
        public Integer longLeadTimeThreshold;    //　長納期品出荷日数閾値
        public Integer minQuantity;                //　最低発注数量
        public Integer orderUnit;                //　発注単位数量
//		public Integer piecesPerPackage;		//　パック品入数

        public String orderDeadline;            //　受注〆時刻
        public Integer largeOrderMinQuantity;    //　大口下限数量
        public Integer largeOrderMaxQuantity;    //　大口上限数量

        public String discountType;                //　値引き区分
        public Double campaignDiscountAmount;    //　キャンペーン値引額
        public Double individualDiscountRate;    //　個別値引率
        public Double companyDiscountRate;        //　商社値引率
        public Double priorDiscountRate;        //　先行還元率
        public Double groupDiscountRate;        //　グループ値引率
        public Double campaignDiscountRate;        //　キャンペーン値引率
        public String campaignEndDate;            //　キャンペーン値引率
        public Double discountRate;                //　値引率
        public Double discountAmount;            //　値引額
        //		public Double unitPrice;				//　売単価
        public Integer standardUnitPrice;        //　標準単価
//		public Double totalPrice;				//　合計金額(明細単位)
//		public Double totalPriceIncludingTax;	//　税込合計税額(明細単位)


/*
        //ResponseCheckに移動
		public Integer standardDaysToShip;		//　標準出荷日数
		//当日出荷選択中フラグ
		public String todayShipSelectedFlag;
		public String expressType;				//　ストーク

		// List expressList				//　ストーク情報リスト
		public ArrayList<ExpressInfo> mExpressList = new ArrayList<>();

		//明細の数量がこの数量を超えていた場合は「数量がXX以上の場合はストーク利用不可となります。」というようなメッセージを出力して下さい
		public String expressMaxQuantity;		// ストーク上限数量
		public String todayShipFlag;			// 当日出荷可能フラグ
*/

        // List volumeDiscountList		//　スライド情報リスト
        // Map lowVolumeCharge			//　バラチャージ

//		// List errorList				//　エラーリスト
//        public ErrorList errorList;

        boolean setData(JSONObject json) {
            try {

                // エラーリスト
                errorList = getErrorList(json);

                //errorMessageが無い場合はトルツメということになりました。
                if (errorList != null && !errorList.ErrorInfoList.isEmpty()) {
                    ArrayList<ErrorList.ErrorInfo> ErrorInfoList = new ArrayList<>();
                    for (ErrorList.ErrorInfo errorInfo : errorList.ErrorInfoList) {

                        if (errorInfo.errorMessage == null) {
                            continue;
                        }

                        ErrorInfoList.add(errorInfo);
                    }
                    errorList.ErrorInfoList = ErrorInfoList;
                }


                //当日出荷選択中フラグ
                todayShipSelectedFlag = getJsonString(json, "todayShipSelectedFlag");

                //○注文明細番号
                orderItemNo = getJsonInteger(json, "orderItemNo");


                //Q	//○見積伝票番号
                quotationSlipNo = getJsonString(json, "quotationSlipNo");

                //C	//　カートID
                cartId = getJsonString(json, "cartId");


                //　注文番号(親)
                customerOrderItemNo = getJsonString(json, "customerOrderItemNo");

                //　注文番号(子)
                customerOrderItemSubNo = getJsonString(json, "customerOrderItemSubNo");

                //○ブランドコード
                brandCode = getJsonString(json, "brandCode");

                //○ブランドコード
                brandName = getJsonString(json, "brandName");

                //○型番
                partNumber = getJsonString(json, "partNumber");

                //○インナーコード
                innerCode = getJsonString(json, "innerCode");

                //○商品名
                productName = getJsonString(json, "productName");

                //○画像URL
//				productImageUrlList = getJsonList<String>(json, "productImageUrlList");
/*
				productImageUrlList = (List<String>) json.get("productImageUrlList");
				if (productImageUrlList == null) {
					productImageUrlList = new ArrayList<>();
				}
*/

                Object photUrl;
                productImageUrlList = new ArrayList<>();
                JSONArray productImageUrlListArray = getJsonArray(json, "productImageUrlList");
                for (int i = 0; i < productImageUrlListArray.length(); i++) {
                    if (productImageUrlListArray.isNull(i)) {
                        continue;
                    }
                    photUrl = productImageUrlListArray.get(i);
                    productImageUrlList.add((String) photUrl);
                }


                //○受注数量
                quantity = getJsonInteger(json, "quantity");

                //　ストーク
                expressType = getJsonString(json, "expressType");

                //　アンフィットフラグ
                unfitFlag = getJsonString(json, "unfitFlag");

                //○注文可能フラグ
                orderableFlag = getJsonString(json, "orderableFlag");

                //○注文日
                orderDate = getJsonString(json, "orderDate");

                //　出荷区分
                shipType = getJsonString(json, "shipType");


                //　出荷日数
                daysToShip = getJsonInteger(json, "daysToShip");

                //　入荷日
                arrivalDate = getJsonString(json, "arrivalDate");

                //　出荷日
                shipDate = getJsonString(json, "shipDate");

                //　最短出荷日
                earliestShipDate = getJsonString(json, "earliestShipDate");

                //　顧客到着日
                deliveryDate = getJsonString(json, "deliveryDate");

                nextArrivalDate = getJsonString(json, "nextArrivalDate");

                //　クーポン適用可否フラグ
                couponApplyFlag = getJsonString(json, "couponApplyFlag");

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

                //　値引き区分
                discountType = getJsonString(json, "discountType");

                //　キャンペーン値引額
                campaignDiscountAmount = getJsonDouble(json, "campaignDiscountAmount");

                //　個別値引率
                individualDiscountRate = getJsonDouble(json, "individualDiscountRate");

                //　商社値引率
                companyDiscountRate = getJsonDouble(json, "companyDiscountRate");

                //　先行還元率
                priorDiscountRate = getJsonDouble(json, "priorDiscountRate");

                //　グループ値引率
                groupDiscountRate = getJsonDouble(json, "groupDiscountRate");

                //　キャンペーン値引率
                campaignDiscountRate = getJsonDouble(json, "campaignDiscountRate");

                campaignEndDate = getJsonString(json, "campaignEndDate");

                //　値引率
                discountRate = getJsonDouble(json, "discountRate");

                //　値引額
                discountAmount = getJsonDouble(json, "discountAmount");

                //　売単価
                unitPrice = getJsonDouble(json, "unitPrice");

                //　標準単価
                standardUnitPrice = getJsonInteger(json, "standardUnitPrice");

                //　合計金額(明細単位)
                totalPrice = getJsonDouble(json, "totalPrice");

                //　税込合計税額(明細単位)
                totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

                standardDaysToShip = getJsonInteger(json, "standardDaysToShip");

                // ストーク上限数量
                expressMaxQuantity = getJsonInteger(json, "expressMaxQuantity");

                //　当日出荷可能フラグ
                todayShipFlag = getJsonString(json, "todayShipFlag");

                todayShipDeadline = getJsonString(json, "todayShipDeadline");

                // List expressList			//　ストーク情報リスト
                JSONArray itemList = getJsonArray(json, "expressList");

                for (int ii = 0; ii < itemList.length(); ii++) {
                    ExpressInfo itemInfo = new ExpressInfo();
                    if (!itemInfo.setData(itemList.getJSONObject(ii))) {
                        return false;
                    } else {
                        mExpressList.add(itemInfo);
                    }
                }

                //UI数量
                if (quantity == null) {
                    //空欄時のデフォルト数量は 1にする（決定済み仕様）
                    quantity = 1;
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

            // エラーリスト
            errorList = getErrorList(json);


            //○受付番号
            receptionCode = getJsonString(json, "receptionCode");

            //Q	//○見積伝票番号
            quotationSlipNo = getJsonString(json, "quotationSlipNo");

            //○注文ステータス
            orderStatus = getJsonString(json, "orderStatus");

            //　注文可能タイプ
            orderableType = getJsonString(json, "orderableType");


            //　顧客注文番号
            customerOrderNo = getJsonString(json, "customerOrderNo");

            //　配送指定
            deliveryType = getJsonString(json, "deliveryType");

            //○支払手段
            paymentType = getJsonString(json, "paymentType");

            //○支払条件
            paymentTerms = getJsonString(json, "paymentTerms");

            //○出荷種別
            shipOption = getJsonString(json, "shipOption");

            //  標準配送料（伝票単位）
            standardDeliveryCharge = getJsonDouble(json, "standardDeliveryCharge");

            //　配送料(伝票単位)
            deliveryCharge = getJsonDouble(json, "deliveryCharge");

            //　税金(伝票単位)
            tax = getJsonDouble(json, "tax");

            //　配送料値引(伝票単位)
            deliveryChargeDiscount = getJsonDouble(json, "deliveryChargeDiscount");


            //　合計金額
            totalPrice = getJsonDouble(json, "totalPrice");

            //　税込合計税額
            totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

            cashOnDeliveryChargeIncludingTax = getJsonDouble(json, "cashOnDeliveryChargeIncludingTax");

            //  アンフィット確認タイプ
            unfitConfirmType = getJsonString(json, "unfitConfirmType");

            // ストーク確認タイプリスト
            JSONArray expresslist = getJsonArray(json, "expressConfirmTypeList");
            expressConfirmTypeList = new ArrayList<>();
            for (int ii = 0; ii < expresslist.length(); ii++) {
                Object data = expresslist.get(ii);
                if (data instanceof String) {
                    //重複を追加しない
                    if (!expressConfirmTypeList.contains(data)) {
                        expressConfirmTypeList.add((String) data);
                    }
                }
            }
            Collections.sort(expressConfirmTypeList);


            // 在庫切れ確認フラグ
            stockoutConfirmFlag = getJsonString(json, "stockoutConfirmFlag");
            // Map purchaser			//○発注者
            purchaser = new PurchaserInfo();
            if (json.has("purchaser")) {
                JSONObject jsonMap = json.getJSONObject("purchaser");

                if (purchaser.setData(jsonMap)) {
                    hasPurchaser = true;
                }

            }

            // Map receiver			//　直送先
            receiver = new ReceiverInfo();
            if (json.has("receiver")) {
                JSONObject jsonMap = json.getJSONObject("receiver");

                if (receiver.setData(jsonMap)) {
                    hasReceiver = true;
                }
            }


            // List receiverList			//　直送先リスト
            JSONArray receiverList = getJsonArray(json, "receiverList");

            for (int ii = 0; ii < receiverList.length(); ii++) {
                ReceiverInfo itemInfo = new ReceiverInfo();
                if (!itemInfo.setData(receiverList.getJSONObject(ii))) {
                    return false;
                } else {
                    mReceiverList.add(itemInfo);
                }
            }


            // List orderItemList			//　注文明細リスト
            JSONArray itemList = getJsonArray(json, "orderItemList");

            for (int ii = 0; ii < itemList.length(); ii++) {
                ItemInfo itemInfo = new ItemInfo();
                if (!itemInfo.setData(itemList.getJSONObject(ii))) {
                    return false;
                } else {
                    mItemList.add(itemInfo);
                }
            }

            //-- ADD NT-LWL 16/11/14 AliPay Payment FR -
            // List paymentGroupList	支付方式
            JSONArray groupList = getJsonArray(json, "paymentGroupList");

            for (int ii = 0; ii < groupList.length(); ii++) {
                PaymentGroup paymentGroup = new PaymentGroup();
                if (!paymentGroup.setData(groupList.getJSONObject(ii))) {
                    return false;
                } else {
                    paymentGroupList.add(paymentGroup);
                }
            }
            //-- ADD NT-LWL 16/11/14 AliPay Payment TO -
        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }


    /**
     * カートから遷移の時は true
     */
    public boolean isFromCart() {
        return isFromCart;
    }

    /**
     * 見積履歴から遷移の時は true
     */
    public boolean isFromQuote() {
        return isFromQuote;
    }


    //直送先変更とかの from無しレスポンスの時にどっちかを呼んでセットする
    public void setFromCart() {

        isFromCart = true;
    }

    public void setFromQuote() {

        isFromQuote = true;
    }

}
