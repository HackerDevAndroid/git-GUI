package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 注文確認 (from注文確認) API用データ（リクエスト）2015/10/07版
 */
public class RequestCheckOrderFromOrder extends RequestDataContainer {


	public String receptionCode;			//○受付番号
	public String billingUserName;			//　請求先担当名
	public String billingDepartmentName;	//　請求先部課名
	public String receiverCode;				//　直送先コード
	public String receiverUserName;			//　直送先担当名
	public String receiverDepartmentName;	//　直送先部課名
	public String shipOption;					//　出荷オプション
	public String resolveOutstockFlag;			//　在庫切れ解消フラグ
	public String resolveErrorPassOnFlag;		//　素通しエラー解消フラグ
	//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
	public String paymentGroup; //決済グループ
	// -- ADD NT-LWL 16/12/05 AliPay Payment TO -

	// List orderItemList			//○注文明細リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestCheckOrderFromOrder() {
    }


	// List orderItemList			//○注文商品リスト
	public static class ItemInfo extends RequestDataContainer {

		public Integer orderItemNo;			//○注文明細番号
		public String todayShipFlag;			//　当日出荷フラグ
		public String expressType;				//　ストーク

		public ItemInfo() {
		}
	}

}
