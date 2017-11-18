package jp.co.misumi.misumiecapp.data;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;


/**
 * 注文履歴検索API用データ（リクエスト）2015/08/28版
 */
public class RequestSearchOrder extends RequestDataContainer {

    //YYYY/MM/DD
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public String userCode;                        //担当者コード
    public String orderSlipNo;                    //注文伝票番号
    public String dateFrom;                        //期間(From)
    public String dateTo;                        //期間(To)
    public Integer page;                        //ページ
    public Integer pageSize;                    //ページサイズ
    public String sort;                            //ソート順


    public RequestSearchOrder() {

        //3ヶ月前の月初
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DATE, 1);
//        cal.add(Calendar.MONTH, -3);

        userCode = AppConfig.getInstance().getUserCode();
//        orderSlipNo = "";
//        dateFrom = sdf.format(cal.getTime());
//        dateTo = "";
        page = 1;
        pageSize = AppConst.HISTORY_LIST_REQUEST_COUNT;
        sort = "0";
    }


}
