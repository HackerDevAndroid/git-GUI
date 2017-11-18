package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 注文確定API用データ（リクエスト）2015/10/23版
 */
public class RequestConfirmOrder extends RequestDataContainer {

	public String receptionCode;				//○受付番号
	public String billingUserName;				//○請求先担当名
	public String billingDepartmentName;		//　請求先部課名
	public String receiverCode;					//　直送先コード
	public String receiverUserName;				//○直送先担当名
	public String receiverDepartmentName;		//　直送先部課名
	public String shipOption;					//　出荷オプション
	public String quotationConvertFlag;			//○見積変換フラグ

	public String unfitPassOnFlag;				//アンフィット素通しフラグ

	//-- ADD NT-LWL 16/11/13 AliPay Payment FR -
	public String paymentGroup;					//支付方式
	//-- ADD NT-LWL 16/11/13 AliPay Payment TO -

	// List orderItemList					//○注文明細リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestConfirmOrder() {

    }

	// List orderItemList					//○注文明細リスト
    public static class ItemInfo extends RequestDataContainer {

		public Integer orderItemNo;			//○注文明細番号
		public String expressType;			//　ストーク
		public String requestShipDate;		//　指定出荷日

	    public ItemInfo() {

	    }

	}

}
