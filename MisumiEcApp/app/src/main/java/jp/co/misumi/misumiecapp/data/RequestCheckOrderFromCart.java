package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 注文確認 (fromカート) API用データ（リクエスト）2015/10/23版
 */
public class RequestCheckOrderFromCart extends RequestDataContainer {

	// List orderItemList			//○注文商品リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestCheckOrderFromCart() {
    }


	// List orderItemList			//○注文商品リスト
	public static class ItemInfo extends RequestDataContainer {

		public String cartId;				//○カートID
		public Integer quantity;			//　数量

		public ItemInfo() {

		}

	}

}
