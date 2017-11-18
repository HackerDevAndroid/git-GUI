package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ost000422 on 2015/09/10.
 */
public class RequestAddToCart extends RequestDataContainer {

    public List<cartItemList> mItemList = new ArrayList<>();

    public static class cartItemList extends RequestDataContainer {

        //ブランドコード
        public String brandCode;
        //型番
        public String partNumber;
        //数量
        public Integer quantity;

        public cartItemList() {
            brandCode = "";
            partNumber = "";
            quantity = 0;
        }
    }
}
