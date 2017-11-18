package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;


/**
 * API005 見積確定API用データ（リクエスト）2015/10/23版
 */
public class RequestConfirmQuotation extends RequestDataContainer {


    public String receptionCode;            //○受付番号
    public String billingUserName;            //○請求先担当名
    public String billingDepartmentName;    //　請求先部課名
    public String receiverCode;                //　直送先コード
    public String receiverUserName;            //○直送先担当名
    public String receiverDepartmentName;    //　直送先部課名

    // List quotationItemList			//○見積明細リスト
    public List<ItemInfo> mItemList = new ArrayList<>();

    public RequestConfirmQuotation() {

    }

    // List quotationItemList			//○見積明細リスト
    public static class ItemInfo extends RequestDataContainer {

        public Integer quotationItemNo;            //○見積明細番号
        public String expressType;            //　ストーク

        public ItemInfo() {

        }

    }

}
