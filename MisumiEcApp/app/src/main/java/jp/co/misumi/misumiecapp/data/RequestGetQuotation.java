package jp.co.misumi.misumiecapp.data;


/**
 * 見積履歴検索API用データ（リクエスト）
 */
public class RequestGetQuotation extends RequestDataContainer {

    public String quotationSlipNo;				//見積伝票番号


    public RequestGetQuotation() {

		quotationSlipNo			= "";
    }


}
