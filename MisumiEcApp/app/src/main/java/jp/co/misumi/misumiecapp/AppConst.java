package jp.co.misumi.misumiecapp;


/**
 * アプリの固定値管理
 */
public class AppConst {

	//WeChat SDKアプリID
    public static final String WECHAT_APP_ID = "wxc247c61f1c2933cf";
    //--ADD NT-LWL 17/05/20 Share FR -
    // 微信开放平台的appId
    public static final String WEIXIN_APP_ID = BuildConfig.weixinAppId;
    // 腾讯开放平台的appId
    public static final String QQ_APP_ID = BuildConfig.tencentAppId;
    //--ADD NT-LWL 17/05/20 Share TO -

    public static String EncryptKey = "JdEMmVHcUmnt";


    public static final String AppID = BuildConfig.AppIDforAPI;

    public static final String SaicataAppIDWebview = BuildConfig.SaicataAppIDWebview;
    public static final String SaicataAppIDBrowser = BuildConfig.SaicataAppIDBrowser;

    public static final String SUBSIDIARY_CODE_CHN = "CHN";
    public static final String SUBSIDIARY_CODE_MJP = "MJP";

    public static String subsidiaryCode = BuildConfig.subsidiaryCode;

	//通貨コード
    public static final String CURRENCY_CODE_JPY = "JPY";	// "JPY": 日本円
    public static final String CURRENCY_CODE_RMB = "RMB";	// "RMB": 人民元
    public static final String CURRENCY_CODE_USD = "USD";	// "USD": 米ドル

    public static final int ConnectTimeout = BuildConfig.ConnectTimeout;

    public static final String UnsetUrl = "file:///android_asset/url_unset.html";
    public static final String LicenseFile = "file:///android_asset/license.html";


	//履歴一覧で表示する履歴件数の最大数
    public static final int HISTORY_LIST_MAX_COUNT = BuildConfig.ListMaxCount;

	//履歴一覧で一度に取得する件数
    public static final int HISTORY_LIST_REQUEST_COUNT = 10;

    //シリーズ検索で一度に表示する件数
    public static final int SERIES_LIST_MAX_COUNT = BuildConfig.ListMaxCount;

    //シリーズ検索で一度に取得する件数
    //--UDP NT-LWL 17/09/28 Series FR -
    //public static final int SERIES_LIST_REQUEST_COUNT = 10;
    public static final int SERIES_LIST_REQUEST_COUNT = 100;
    //--UDP NT-LWL 17/09/28 Series TO -

    //キーワード検索で一度に表示する件数
    public static final int KEYWORD_LIST_MAX_COUNT = BuildConfig.ListMaxCount;

    //キーワード検索で一度に取得する件数
    //--UDP NT-LWL 17/09/28 Series FR -
    //public static final int KEYWORD_LIST_REQUEST_COUNT = 10;
    public static final int KEYWORD_LIST_REQUEST_COUNT = 100;
    //--UDP NT-LWL 17/09/28 Series TO -

    //型番検索で取得する件数（この件数より多い場合はパソコンで見てねUI）
    //-- UDP NT-LWL 17/05/04 Alipay FR -
//    public static final int PART_NUMBER_LIST_REQUEST_COUNT = 50;
    public static final int PART_NUMBER_LIST_REQUEST_COUNT = 300;
    //-- UDP NT-LWL 17/05/04 Alipay TO -
    //型番検索でスペック項目リスト最大件数を20件
    public static final int PART_NUMBER_SPEC_LIST_COUNT = 20;

    //サジェスト最大件数
    public static final int SUGGEST_MAX_COUNT = 10;

}

