package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 注文確認 (from見積履歴詳細) API用データ（リクエスト）2015/10/23版
 */
public class RequestCheckOrderFromQuote extends RequestDataContainer {

//https://mjp.misumi-ec.com/api/v1/order/check/fromQuotation

	public String quotationSlipNo;				//○見積伝票番号


	// List quotationItemList			//○注文商品リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestCheckOrderFromQuote() {
    }


	// List quotationItemList			//○注文商品リスト
	public static class ItemInfo extends RequestDataContainer {

		public String quotationItemNo;				//　見積明細番号

		public ItemInfo() {
		}

	}

}
