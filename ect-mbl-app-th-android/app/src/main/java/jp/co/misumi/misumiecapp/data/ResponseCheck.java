package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;


/**
 * 確認API用データ共通（レスポンス）2015/11/30版
 */
public abstract class ResponseCheck extends DataContainer {


    //共通
    public String unfitConfirmType;            //  アンフィット確認タイプ
    public String orderableType;                    //　注文可能タイプ


    //共通
    public static class ItemInfo extends DataContainer {

        public String brandName;            //○ブランド名
        public String partNumber;            //○型番
        public String productName;            //○商品名

        public Integer quantity;                    //○受注数量
        public Integer piecesPerPackage;        //　パック品入数

        public Double unitPrice;                //　売単価
        public Double totalPrice;                //　合計金額(明細単位)
        public Double totalPriceIncludingTax;            //　税込合計税額(明細単位)

        public Integer standardDaysToShip;        //　標準出荷日数
        public String shipType;                    //　出荷区分
        public Integer daysToShip;                //　出荷日数

        public String orderableFlag;            //○注文可能フラグ

        //当日出荷選択中フラグ
        public String todayShipSelectedFlag;
        public String expressType;                //　ストーク

        // List expressList				//　ストーク情報リスト
        public ArrayList<ExpressInfo> mExpressList = new ArrayList<>();

        //
        public Integer expressMaxQuantity;        // ストーク上限数量
        public String todayShipFlag;            // 当日出荷可能フラグ
        public String todayShipDeadline;        // 当日出荷しめ


        // List errorList				//　エラーリスト
        public ErrorList errorList;

        //見積系データ判定
        public boolean isQuote() {

            return (this instanceof ResponseCheckQuotation.ItemInfo);
        }

    }

    //見積系データ判定
    public boolean isQuote() {

        return (this instanceof ResponseCheckQuotation);
    }

}
