package jp.co.misumi.misumiecapp;

import android.app.Application;
import android.content.Context;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.ErrorMessageManager;
import jp.co.misumi.misumiecapp.observer.ApiAccessObserver;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.BlackListUtils;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * アプリケーションクラス.
 */
public class MisumiEcApp extends Application {
	//-- ADD NT-LWL 16/12/02 AliPay Payment FR –
	private Tracker mTracker;

	/**
	 * Gets the default {@link Tracker} for this {@link Application}.
	 * @return tracker
	 */
	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			//设置发送频率
			analytics.setLocalDispatchPeriod(20);
//			analytics.setDryRun(true); //不发送GA
			// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
			mTracker = analytics.newTracker(R.xml.global_tracker);
		}
		return mTracker;
	}
	//-- ADD NT-LWL 16/12/02 AliPay Payment TO –
    //private RefWatcher mRefWatcher;

    private CategoryList mTopCategoryList;

	/* (非 Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		AppLog.v("onCreate");

		super.onCreate();
		//-- ADD NT-LWL 17/05/18 QR scan FR -
		// QR scan初始化 中国环境下
		if (SubsidiaryCode.isChinese()) {
			ZXingLibrary.initDisplayOpinion(this);

			//-- ADD NT-LWL 17/09/25 Category FR -
			BlackListUtils.createBlackList(this);
			//-- ADD NT-LWL 17/09/25 Category TO -
		}
		//LeakCanary.install(this);
		//-- ADD NT-LWL 17/05/18 QR scan TO -
		//mRefWatcher = LeakCanary.install(this);

		// アプリケーションコンテキスト
		final Context	context	= getApplicationContext();

		if (BuildConfig.subsidiaryCode.equals("MJP")) {
			//サイカタ
			Config.setContext(context);
			Config.setDebugLogging(false);
		}

		//Utilクラスを初期化
		PicassoUtil.initialize(context);

		AppNotifier.createInstance(context);

		//
        AppConfig.createInstance(context).loadConfig();
        AppLog.Config();

        NetworkInterface.createInstance(this);
        ApiAccessObserver.createInstance();
        ErrorMessageManager.createInstance(this);
	}


	public CategoryList getTopCategoryList() {

		//トップで使用のカテゴリは大きいのでシングルトン化
		if (mTopCategoryList == null) {
	    	mTopCategoryList = new CategoryList();

			//ファイルから読む
            mTopCategoryList.importFile(getApplicationContext());
		}

		return mTopCategoryList;
	}

	public void setTopCategoryList(CategoryList categoryList) {

		//トップで使用のカテゴリは大きいのでシングルトン化
    	mTopCategoryList = null;
        categoryList.exportFile(getApplicationContext());
        AppConfig.getInstance().setCategoryUpdateTime(categoryList.updateDateTime);
    	mTopCategoryList = categoryList;
	}


	//サイカタ
	public static String getWebUrlStrApp() {
		return getWebUrlStr(AppConst.SaicataAppIDWebview);
	}

	public static String getWebUrlStrExt() {
		return getWebUrlStr(AppConst.SaicataAppIDBrowser);
	}

	private static String getWebUrlStr(String appid) {
		String str;
//		str = "?appid=android-jp-catalog-inapp&sc_vid=12345";

		/*
		■value1-1(webview)
		日本：android-jp-catalog-inapp
		中国：android-cn-catalog-inapp

		■value1-2(browser)
		日本：android-jp-catalog-brows
		中国：android-cn-catalog-brows
		*/
		str = "appid=";
//		str += "android-jp-catalog-inapp";
		str += appid;

		str += "&sc_vid=";
		str += retrieveVisitorIdentification();

		return str;
	}


	//サイカタ
	private static String retrieveVisitorIdentification() {

	    // check to see if a custom ID is set.
	    String visitorId = Config.getUserIdentifier();
	    if (visitorId == null || visitorId.length() <= 0) {
	        // if a custom ID was not set, get the default ID.
	        visitorId = Analytics.getTrackingIdentifier();
	    }

	    return visitorId;
	}
}
