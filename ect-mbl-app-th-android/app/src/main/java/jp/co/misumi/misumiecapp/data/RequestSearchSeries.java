package jp.co.misumi.misumiecapp.data;

import jp.co.misumi.misumiecapp.AppConst;

/**
 *
 */
public class RequestSearchSeries extends RequestDataContainer {

    public String categoryCode;
    public Integer page;
    public Integer pageSize;
    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    public String seriesCode;
    public String innerCode;
    public String brandCode = "";
    //--ADD NT-LWL 17/09/08 BrandSearch FR -

    public RequestSearchSeries() {
        categoryCode = "";
        page = 1;
        pageSize = AppConst.SERIES_LIST_REQUEST_COUNT;
    }
}
