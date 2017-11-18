package jp.co.misumi.misumiecapp.data;

/**
 * WebViewData
 */
public class WebViewData extends DataContainer {
    public String url;
    //-- ADD NT-SLJ 16/11/11 Live800 FR -
    public Live800Data.Question question;
    //-- ADD NT-SLJ 16/11/11 Live800 TO -

    public WebViewData(String seturl){
        url = seturl;
    }
}
