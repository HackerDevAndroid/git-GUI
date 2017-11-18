package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 見積確認API用データ（レスポンス）2015/08/28版
 */
public class ResponseCheckQuotation extends ResponseCheck {

    public static final String API015200 = "API015200";
    public static final String API015201 = "API015201";
    public static final String API015202 = "API015202";


    public static final String API015300 = "API015300";
    public static final String API015301 = "API015301";
    public static final String API015302 = "API015302";
    public static final String API003303 = "API003303";
    public static final String API015304 = "API015304";
    public static final String API015305 = "API015305";
    public static final String API015306 = "API015306";
    public static final String API015307 = "API015307";
    public static final String API015308 = "API015308";
    public static final String API015309 = "API015309";


	public boolean isFromCart;


	public String receptionCode;				//○受付番号
	public String customerOrderNo;				//　顧客注文番号

	public String deliveryType;					//　配送指定
	public Double standardDeliveryCharge;		//　配送料（伝票単位）
	public Double deliveryChargeDiscount;		//　配送料値引（伝票単位）
	public Double deliveryCharge;		//　配送料（伝票単位）
	public Double tax;							//　税金（伝票単位）

	public Double totalPrice;					//　合計金額
	public Double totalPriceIncludingTax;		//　税込合計税額
//	public String unfitConfirmType;			//  アンフィット確認タイプ
//	public String orderableType;					//　注文可能タイプ

	//expressADiscountFlag (早割Aフラグ)
	public String expressADiscountSelectedFlag;	//(早割A選択中フラグ)

	// Map purchaser						//○発注者
	public boolean hasPurchaser;
	public PurchaserInfo purchaser;

	// Map receiver			//　直送先
	public boolean hasReceiver;
	public ReceiverInfo receiver;

	// List receiverList			//　
    public List<ReceiverInfo> mReceiverList = new ArrayList<>();


	// List quotationItemList			//　見積明細リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public ErrorList errorList;


	//見積明細リスト
    public static class ItemInfo extends ResponseCheck.ItemInfo {

		// List quotationItemList			//　見積明細リスト
		public Integer quotationItemNo;		//○見積明細番号
		public String customerOrderItemNo;	//　注文番号(親)
		public String customerOrderItemSubNo;	//　注文番号(子)

		public String brandCode;			//○ブランドコード
//		public String brandName;			//○ブランド名
//		public String partNumber;			//○型番
		public String innerCode;			//○インナーコード
		public String seriesCode;			//○シリーズコード
		public String cartId;				//　カートID

//		public String productName;			//○商品名
		public List<String> productImageUrlList;			//○画像URL
//		public Integer quantity;			//○見積数量
//		public Integer daysToShip;			//　出荷日数
//		public String shipType;				//○出荷区分
//		public String orderableFlag;			//○注文可能フラグ

		public String quotationDate;			//○見積日
		public String earliestShipDate;			//　最短出荷日
		public String nextArrivalDate;			//　nextArrivalDate
		public String campaignEndDate;		//　キャンペーン値引率
		public Integer longLeadTimeThreshold;			//　長納期品出荷日数閾値
		public Integer minQuantity;				//　最低発注数量
		public Integer orderUnit;				//　発注単位数量
//		public Integer piecesPerPackage;			//　パック品入数

		public String orderDeadline;			//　受注〆時刻
		public Integer largeOrderMinQuantity;			//　大口下限数量
		public Integer largeOrderMaxQuantity;			//　大口上限数量

		public String discountType;				//　値引区分
		public Double individualDiscountRate;			//　個別値引率
		public Double companyDiscountRate;			//　商社値引率
		public Double priorDiscountRate;			//　先行還元率
		public Double groupDiscountRate;			//　グループ値引率
		public Double campaignDiscountRate;			//　キャンペーン値引率
		public Integer campaignDiscountAmount;			//　キャンペーン値引額

		public Double discountRate;				//　値引率
		public Double discountAmount;			//　値引額

//		public Double unitPrice;				//　単価
		public Double standardUnitPrice;			//　標準単価
//		public Double totalPrice;				//　合計金額(明細単位)
//		public Double totalPriceIncludingTax;			//　税込合計税額(明細単位)

		public String unfitType;				//　アンフィット区分
		public String unfitFlag;				//


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

	        mExpressList.clear();

            try {

	            // エラーリスト
	            errorList = getErrorList(json);

				//errorMessageが無い場合はトルツメということになりました。
				if (errorList != null && !errorList.ErrorInfoList.isEmpty()) {
    				ArrayList<ErrorList.ErrorInfo> ErrorInfoList = new ArrayList<>();
					for (ErrorList.ErrorInfo errorInfo: errorList.ErrorInfoList) {

						if (errorInfo.errorMessage == null) {
							continue;
						}

						ErrorInfoList.add(errorInfo);
					}
					errorList.ErrorInfoList = ErrorInfoList;
				}


				//当日出荷選択中フラグ
				todayShipSelectedFlag = getJsonString(json, "todayShipSelectedFlag");

				//○見積明細番号
				quotationItemNo = getJsonInteger(json, "quotationItemNo");

				//　注文番号(親)
				customerOrderItemNo = getJsonString(json, "customerOrderItemNo");

				//　注文番号(子)
				customerOrderItemSubNo = getJsonString(json, "customerOrderItemSubNo");

				//○ブランドコード
				brandCode = getJsonString(json, "brandCode");

				//○ブランド名
				brandName = getJsonString(json, "brandName");

				//○型番
				partNumber = getJsonString(json, "partNumber");

				//○インナーコード
				innerCode = getJsonString(json, "innerCode");

				//○シリーズコード
				seriesCode = getJsonString(json, "seriesCode");

				//　カートID
//				if (isFromCart()) {
					cartId = getJsonString(json, "cartId");
//				}

				//○商品名
				productName = getJsonString(json, "productName");

				//○画像URL
/*
				productImageUrlList = (List<String>) json.get("productImageUrlList");
				if (productImageUrlList == null) {
					productImageUrlList = new ArrayList<>();
				}
*/

                Object photUrl;
                productImageUrlList = new ArrayList<>();
                JSONArray productImageUrlListArray = getJsonArray(json, "productImageUrlList");
                for (int i=0; i<productImageUrlListArray.length(); i++){
					if (productImageUrlListArray.isNull(i)){
						continue;
					}
                    photUrl = productImageUrlListArray.get(i);
                    productImageUrlList.add((String)photUrl);
                }

				//○見積数量
				quantity = getJsonInteger(json, "quantity");

				//　出荷日数
				daysToShip = getJsonInteger(json, "daysToShip");

				//○出荷区分
				shipType = getJsonString(json, "shipType");

				//　アンフィット区分
				unfitType = getJsonString(json, "unfitType");

				//　アンフィット区分
				unfitFlag = getJsonString(json, "unfitFlag");

				//○注文可能フラグ
				orderableFlag = getJsonString(json, "orderableFlag");

				//○見積日
				quotationDate = getJsonString(json, "quotationDate");

				//　最短出荷日
				earliestShipDate = getJsonString(json, "earliestShipDate");

				//　nextArrivalDate
				nextArrivalDate = getJsonString(json, "nextArrivalDate");

				//　ストーク
				expressType = getJsonString(json, "expressType");

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

				//　値引区分
				discountType = getJsonString(json, "discountType");

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

				//　キャンペーン値引額
				campaignDiscountAmount = getJsonInteger(json, "campaignDiscountAmount");

				campaignEndDate = getJsonString(json, "campaignEndDate");

				//　値引率
				discountRate = getJsonDouble(json, "discountRate");

				//　値引額
				discountAmount = getJsonDouble(json, "discountAmount");

				//　単価
				unitPrice = getJsonDouble(json, "unitPrice");

				//　標準単価
				standardUnitPrice = getJsonDouble(json, "standardUnitPrice");

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
	                if (!itemInfo.setData(itemList.getJSONObject(ii))){
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


    public boolean setData(String src){

        mItemList.clear();

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);


			//○受付番号
			receptionCode = getJsonString(json, "receptionCode");

			//　顧客注文番号
			customerOrderNo = getJsonString(json, "customerOrderNo");

			expressADiscountSelectedFlag = getJsonString(json, "expressADiscountSelectedFlag");


			//　配送指定
			deliveryType = getJsonString(json, "deliveryType");

			//　配送料（伝票単位）
			standardDeliveryCharge = getJsonDouble(json, "standardDeliveryCharge");

			//　配送料値引（伝票単位）
			deliveryChargeDiscount = getJsonDouble(json, "deliveryChargeDiscount");

			//　配送料（伝票単位）
			deliveryCharge = getJsonDouble(json, "deliveryCharge");

			//　税金（伝票単位）
			tax = getJsonDouble(json, "tax");

			//　合計金額
			totalPrice = getJsonDouble(json, "totalPrice");

			//　税込合計税額(明細単位)
			totalPriceIncludingTax = getJsonDouble(json, "totalPriceIncludingTax");

			//  アンフィット確認タイプ
			unfitConfirmType = getJsonString(json, "unfitConfirmType");

			//　注文可能タイプ
			orderableType = getJsonString(json, "orderableType");

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
                if (!itemInfo.setData(receiverList.getJSONObject(ii))){
                    return false;
                } else {
                    mReceiverList.add(itemInfo);
                }
            }



			// List quotationItemList			//　見積明細リスト
            JSONArray itemList = getJsonArray(json, "quotationItemList");

            for (int ii = 0; ii < itemList.length(); ii++) {
                ItemInfo itemInfo = new ItemInfo();
                if (!itemInfo.setData(itemList.getJSONObject(ii))){
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


	/**
	 * カートから遷移の時は true
	 * 
	 */
	public boolean isFromCart() {
		return isFromCart;
	}

	//直送先変更とかの from無しレスポンスの時にどっちかを呼んでセットする
	public void setFromCart() {

		isFromCart = true;
	}

}
