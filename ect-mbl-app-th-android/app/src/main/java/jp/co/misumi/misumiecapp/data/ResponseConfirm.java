package jp.co.misumi.misumiecapp.data;


/**
 * 見積注文共通確定API用データ（レスポンス）2015/10/23版
 */
public abstract class ResponseConfirm extends DataContainer {

    public boolean isFromCart;
    public boolean isFromQuote;

    public String infoSlipNo;                //○＊＊伝票番号
    public Integer itemCount;                //○商品件数
    public Integer itemCountInChecking;        //○ミスミ確認中商品件数
    public Double totalPrice;                //○合計金額
    public Double totalPriceIncludingTax;    //○税込合計金額
    public String infoDatetime;                //○＊＊日時

    //-- ADD NT-SLJ 16/10/13 AliPay Payment FR -
    public String paymentGroup;
    public String paymentGroupName;
    public String paymentDeadlineDateTime;
    //-- ADD NT-SLJ 16/10/13 AliPay Payment TO -

    public ErrorList errorList;

    public abstract boolean setData(String src);

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

    //画面遷移時にどっちかを呼んでセットする
    public void setFromCart() {

        isFromCart = true;
    }

    public void setFromQuote() {

        isFromQuote = true;
    }

}
