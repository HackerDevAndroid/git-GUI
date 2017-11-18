package jp.co.misumi.misumiecapp.data;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;


/**
 * 見積履歴検索API用データ（リクエスト）2015/08/28版
 */
public class RequestSearchQuotation extends RequestDataContainer {

    //YYYY/MM/DD
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public String userCode = null;                        //担当者コード
    public String quotationSlipNo = null;                //見積伝票番号
    public String dateFrom = null;                        //期間(From)
    public String dateTo = null;                        //期間(To)
    public Integer page = null;                        //ページ
    public Integer pageSize = null;                    //ページサイズ
    public String sort = null;                            //ソート順


    public RequestSearchQuotation() {

        //3ヶ月前の月初
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DATE, 1);
//        cal.add(Calendar.MONTH, -3);

        userCode = AppConfig.getInstance().getUserCode();
        //quotationSlipNo = "";
        //dateFrom = sdf.format(cal.getTime());
        //dateTo = "";
        page = 1;
        pageSize = AppConst.HISTORY_LIST_REQUEST_COUNT;
        sort = "0";
    }


}
