package jp.co.misumi.misumiecapp.data;

import jp.co.misumi.misumiecapp.AppConst;

/**
 * 注文履歴検索API用データ（リクエスト）2015/08/28版
 */
public class RequestKeywordSearch extends RequestDataContainer {

    public String keyword;
    public Integer page;                        //ページ
    public Integer pageSize;                    //ページサイズ
    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    public String brandCode = "";   // 品牌code
    public String categoryCode;  // 分类code
    //--ADD NT-LWL 17/09/08 BrandSearch TO -

    public RequestKeywordSearch() {

        keyword = "";
        page = 1;
        //--UDP NT-LWL 17/09/28 Series TO -
        //pageSize = AppConst.HISTORY_LIST_REQUEST_COUNT;
        pageSize = AppConst.KEYWORD_LIST_REQUEST_COUNT;
        //--UDP NT-LWL 17/09/28 Series TO -
    }


}
