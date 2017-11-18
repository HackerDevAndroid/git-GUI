package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 見積確認 (fromカート) API用データ（リクエスト）2015/10/23版
 */
public class RequestCheckQuotationFromCart extends RequestDataContainer {

    // List quotationItemList			//○見積商品リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestCheckQuotationFromCart() {
    }


    // List quotationItemList			//○見積商品リスト
    public static class ItemInfo extends RequestDataContainer {

        public String cartId;                //○カートID
        public Integer quantity;            //　数量

        public ItemInfo() {

        }

    }

}
