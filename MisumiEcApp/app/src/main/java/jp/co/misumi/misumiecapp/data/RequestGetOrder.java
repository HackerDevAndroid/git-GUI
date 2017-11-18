package jp.co.misumi.misumiecapp.data;


/**
 * 注文履歴検索API用データ（リクエスト）
 */
public class RequestGetOrder extends RequestDataContainer {

    public String orderSlipNo;				//注文伝票番号


    public RequestGetOrder() {

		orderSlipNo			= "";
    }


}
