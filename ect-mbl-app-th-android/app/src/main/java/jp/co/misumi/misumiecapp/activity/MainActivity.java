package jp.co.misumi.misumiecapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.HashMap;
import java.util.Objects;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLifecycle;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.SearchBar;
import jp.co.misumi.misumiecapp.SessionRequiredDialog;
import jp.co.misumi.misumiecapp.SideMenu;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.api.SchemeApi;
import jp.co.misumi.misumiecapp.data.AliPaymentInfo;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.data.RequestSearchOrder;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseSearchOrder;
import jp.co.misumi.misumiecapp.fragment.BaseFragment;
import jp.co.misumi.misumiecapp.fragment.CartFragment;
import jp.co.misumi.misumiecapp.fragment.LaunchFragment;
import jp.co.misumi.misumiecapp.fragment.MyPartsListFragment;
import jp.co.misumi.misumiecapp.fragment.OrderListFragment;
import jp.co.misumi.misumiecapp.fragment.OrderPayFragment;
import jp.co.misumi.misumiecapp.fragment.SearchResultKeywordFragment;
import jp.co.misumi.misumiecapp.fragment.SplashFragment;
import jp.co.misumi.misumiecapp.fragment.TopFragment;
import jp.co.misumi.misumiecapp.fragment.WebViewFragment;
import jp.co.misumi.misumiecapp.header.HeaderView;
import jp.co.misumi.misumiecapp.header.MainHeader;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

import static jp.co.misumi.misumiecapp.activity.ScanActivity.REQUEST_CODE;


/**
 * メイン
 */
public class MainActivity extends AppActivity {

    private SideMenu mSideMenu = null;
    private SearchBar mSearchBar = null;

    private CartApi mCartApi;
    private MyPartsApi mMyPartsApi;
    private AppSchemeApi mAppSchemeApi;

    //-- ADD NT-SLJ 16/11/11 AliPay Payment FR -
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private LoginBroadcast loginBroadcast;

    //-- ADD NT-SLJ 16/11/11 AliPay Payment TO -
    //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
    private OrderHistoryApi mOrderHistoryApi;

    public boolean isRefreshCart = false;//刷新购物车标志

    public String paymentfromWhere;
    //-- ADD NT-LWL 16/11/17 AliPay Payment TO -

    //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
    protected Tracker mTracker;
    protected static final int PERMISSIONS_REQUEST_CAMERA = 5;

    //-- ADD NT-LWL 17/03/22 AliPay Payment TO -

    private Uri getIntentUri(Intent intent) {

        if (intent == null) {
            return null;
        }

        if (intent.getExtras() == null) {
            return null;
        }

        return (Uri) intent.getExtras().getParcelable("Uri");
    }


    protected void setFragment(Bundle savedInstanceState) {

        if (savedInstanceState == null) {

            //スキーマ起動のデータをスプラッシュに受け渡す
            Uri uri = getIntentUri(getIntent());

            BaseFragment baseFragment = new SplashFragment();
            baseFragment.setParameterUri(uri);
            getFragmentController().replaceFragment(baseFragment, FragmentController.ANIMATION_NON);

        }
    }


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
        String flag = intent.getStringExtra("flag");
        if (flag != null && flag.equals("OrderComplete")) {
            isRefreshCart = true;
            AliPaymentInfo mRequestOnlinePayment = (AliPaymentInfo) intent.getExtras().getSerializable("online_payment");
            getFragmentController().stackFragment(new OrderPayFragment(), FragmentController.ANIMATION_NON, mRequestOnlinePayment);
            return;
        }
        //loading画面调用订单一览接口成功
        if (flag != null && flag.equals("loading")) {
            ResponseSearchOrder responseSearchOrder = (ResponseSearchOrder) intent.getExtras().getSerializable("order_list");
//            getFragmentController().clearAndReplace();
            getFragmentController().clearAndReplace(false);
            getFragmentController().stackFragment(new OrderListFragment(), FragmentController.ANIMATION_NON, responseSearchOrder);
            return;
        }
        //loading画面调用订单一览接口失败
        if (flag != null && flag.equals("top")) {
//            getFragmentController().clearAndReplace();
            getFragmentController().clearAndReplace(true);
            return;
        }
        //-- ADD NT-LWL 16/11/17 AliPay Payment TO -

        if (mAppSchemeApi == null) {
            // 避免重复创建
            mAppSchemeApi = new AppSchemeApi(this);
        }

        BaseFragment baseFragment = getFragmentController().getCurrentFragment();
        if (baseFragment instanceof SplashFragment) {

            //スプラッシュ中にスキームされた場合対応
            //前回のインスタンスを破棄
            finish();

            //再度アプリを起動する
            startActivity(intent);

            return;
        }

        //スキーマ起動のデータを取得
        Uri uri = getIntentUri(intent);
//        Uri uri = intent.getData();
        if (uri != null) {
            //-- ADD NT-LWL 17/06/26 Share FR -
            // 分享点击统计api参数获取
            String uuid = uri.getQueryParameter("UUID");
            // 设置参数
            mAppSchemeApi.setOpenShareApiUuid(uuid);
            String scode = uri.getQueryParameter("scode");
            // 判断来自购物车直接调用
            if (TextUtils.isEmpty(scode)) {
                mAppSchemeApi.openShareApi();
            }
            //-- ADD NT-LWL 17/06/26 Share TO -
            //ここでスキーマ起動の通信処理
            mAppSchemeApi.onCreate(uri);
        }

        //それ以外は何もしない（単にホーム画面から戻って来た等）
    }


    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mAppSchemeApi.onPause();

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan()) {
            MisumiEcApp application = (MisumiEcApp) getApplication();
            mTracker = application.getDefaultTracker();
        }
        //-- ADD NT-LWL 17/03/22 AliPay Payment TO -

        //-- ADD NT-SLJ 16/11/11 AliPay Payment FR –
        if (!SubsidiaryCode.isJapan()) {
            loginBroadcast = new LoginBroadcast();
            intentFilter = new IntentFilter(LoginActivity.LONGIN_SUCCESS_ACTION);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(loginBroadcast, intentFilter);

        }
        //-- ADD NT-SLJ 16/11/11 AliPay Payment TO –

        mAppSchemeApi = new AppSchemeApi(this);

        //2015/10/22 Appクラスに移動
//        AppConfig.createInstance(this).loadConfig();
//        AppLog.Config();

        mSideMenu = new SideMenu(this);
        mSearchBar = new SearchBar(this);

        if (savedInstanceState == null) {
            getHeaderView().hideHeader();
        } else {
            getHeaderView().showHeader();
        }
        mSearchBar.hideBar();

        // フラグメント変更監視
        getFragmentController().addFragmentChangeListener(mFragmentChangeStateListener);
        AppNotifier.getInstance().addListener(mAppStateListener, AppNotifier.USER_NEW_LOGIN);

        // フォアグランド・バックグラウンド監視
        getApplication().registerActivityLifecycleCallbacks(new AppLifecycle(appLifecycleCallback));

        mCartApi = new CartApi();
        mMyPartsApi = new MyPartsApi();
        //-- ADD NT-LWL 16/11/17 AliPay Payment FR -

        mOrderHistoryApi = new OrderHistoryApi();
        //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
        if (AppConfig.getInstance().hasSessionId()) {
            AppNotifier.getInstance().setCartCount(AppConfig.getInstance().getCartCount());
        }
//        setFragment() 方法中已经有了
//        Uri uri = getIntentUri(getIntent());
//        if (uri != null) {
//
//            //ここでスキーマ起動の通信処理
//            mAppSchemeApi.onCreate(uri);
//        }
    }

    //-- ADD NT-SLJ 16/11/11 AliPay Payment FR–
    @Override
    protected void onDestroy() {
        if (null != localBroadcastManager && loginBroadcast != null) {
            localBroadcastManager.unregisterReceiver(loginBroadcast);
        }
        super.onDestroy();
    }
    //-- ADD NT-SLJ 16/11/11 AliPay Payment TO -

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected HeaderView createHeaderView() {
        HeaderView view = new MainHeader(this);
        view.addHeaderEventListener(mHeaderEvent);

        return view;
    }


    @Override
    public void onBackPressed() {
//        AppLog.d("onBackPressed");
        if (mSideMenu.isOpened()) {
            mSideMenu.closeDrawer();
            return;
        }

        if (mSearchBar.isOpened()) {
            mSearchBar.closeBar();
        }


        AppLog.v("fragment count = " + getFragmentController().getFragmentCount());
        if (getFragmentController().getFragmentCount() <= 1) {
            new MessageDialog(MainActivity.this, new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        MainActivity.this.finish();
                    }
                }
            }).show(R.string.message_apllication_finish, R.string.dialog_button_yes, R.string.dialog_button_no);
        } else {
            //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
            if (paymentfromWhere != null && !paymentfromWhere.isEmpty() && paymentfromWhere.equals("1")) {
                BaseFragment baseFragment = getFragmentController().getCurrentFragment();
                boolean isOrderPayFragment = false;
                if (baseFragment instanceof OrderPayFragment) {
                    isOrderPayFragment = true;
                }
                if (isOrderPayFragment) {
                    mOrderHistoryApi.connect(MainActivity.this);
                    return;
                }
            }
            //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
            getFragmentController().onBackKey();
        }
    }

    /**
     * onForeground
     */
    private void onForeground() {
        BaseFragment baseFragment = getFragmentController().getCurrentFragment();
        if (baseFragment != null) {
            baseFragment.onApplicationForeground();
        }
    }

    /**
     * onBackground
     */
    private void onBackground() {
        BaseFragment baseFragment = getFragmentController().getCurrentFragment();
        if (baseFragment != null) {
            baseFragment.onApplicationBackground();
        }
    }

    public SideMenu getSlideMenu() {
        return mSideMenu;
    }

    public SearchBar getSearchBar() {
        return mSearchBar;
    }

    /**
     * ヘッダからのイベント受信
     */
    HeaderView.HeaderEventListener mHeaderEvent = new HeaderView.HeaderEventListener() {

        @Override
        public void onHeaderEvent(int event, Objects objects) {
            //-- ADD NT-LWL 17/03/28 Live800 FR -
            MisumiEcApp misumiEcApp = (MisumiEcApp) getApplication();
            //-- ADD NT-LWL 17/03/28 Live800 TO -
            switch (event) {
                case MainHeader.BACK_BUTTON:
                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }
                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    }
                    //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
                    BaseFragment baseFragment = getFragmentController().getCurrentFragment();
                    if (paymentfromWhere != null && !paymentfromWhere.isEmpty() && paymentfromWhere.equals("1")) {
//                        BaseFragment baseFragment = getFragmentController().getCurrentFragment();
                        boolean isOrderPayFragment = false;
                        if (baseFragment instanceof OrderPayFragment) {
                            isOrderPayFragment = true;
                        }
                        if (isOrderPayFragment) {
                            mOrderHistoryApi.connect(MainActivity.this);
                            return;
                        }
                    }

                    // 控制H5画面返回
                    if (baseFragment instanceof WebViewFragment) {
                        WebViewFragment webViewFragment = (WebViewFragment) baseFragment;
                        if (webViewFragment.canGoBack()) {
                            webViewFragment.goBack();
                            return;
                        }
                    }

                    //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
                    getFragmentController().onBackKey();
                    break;

                case MainHeader.SEARCH_BUTTON:
                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }

                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    } else {
                        getSearchBar().openBar();
                    }
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_search);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    break;

                case MainHeader.MENU_BUTTON:
                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                        return;
                    }

                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                    } else {
                        getSlideMenu().openDrawer();
                    }
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_menu);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    break;

                case MainHeader.CART_BUTTON:

                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }

                    //右上のカートボタン
                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    }

                    if (!(getFragmentController().getCurrentFragment() instanceof CartFragment)) {
                        if (new SessionRequiredDialog().judgeLaunchRestriction(MainActivity.this)) {
                            mCartApi.connect(MainActivity.this);
                        }
                    }
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_cart);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    break;

                case MainHeader.MY_PARTS_BUTTON:

                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }

                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    }


                    if (!(getFragmentController().getCurrentFragment() instanceof MyPartsListFragment)) {
                        if (new SessionRequiredDialog().judgeLaunchRestriction(MainActivity.this)) {
                            mMyPartsApi.connect(MainActivity.this);
                        }
                    }
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_myParts);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    break;
                case MainHeader.LOGO_AREA:
                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }
                    break;
                //-- ADD NT-SLJ 16/11/11 Live800 FR -
                case MainHeader.CONSULTATION_BUTTON:
                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }
                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    }
                    SubActivity.launchActivity(getFragmentController().getCurrentFragment(), SubActivity.SUB_TYPE_CONSULTATION,
                            SubActivity.REQUEST_CODE_CONSULTION_VIEW, null);
                    //-- UDP NT-LWL 17/03/28 Live800 FR -
//                    MisumiEcApp misumiEcApp= (MisumiEcApp) getApplication();
//                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(),GoogleAnalytics.CATEGORY_LIVE800,getString(R.string.click_live800));
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_live800);
                    //-- UDP NT-LWL 17/03/28 Live800 TO -
                    break;
                //-- ADD NT-SLJ 16/11/11 Live800 TO -
                //--ADD NT-LWL 17/05/18 QR scan FR -
                //扫码
                case MainHeader.SCAN_BUTTON:
                    if (getSlideMenu().isOpened()) {
                        getSlideMenu().closeDrawer();
                        return;
                    }
                    if (getSearchBar().isOpened()) {
                        getSearchBar().closeBar();
                    }
                    openScanCheckSelfPermission();
                    // 追加 QR扫码 GA事件
                    GoogleAnalytics.sendAction(misumiEcApp.getDefaultTracker(), GoogleAnalytics.CATEGORY_TOPBAR, GoogleAnalytics.lable_QRscan);
                    break;
                //--ADD NT-LWL 17/05/18 QR scan TO -
                default:
                    getFragmentController().onHeaderEvent(event, objects);
                    break;
            }
        }
    };


    /**
     * カート
     */
    class CartApi extends ApiAccessWrapper {

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCart();
        }

        @Override
        public void onResult(int responseCode, String result) {
            GetCart cart = new GetCart();
            boolean pars = cart.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    MainActivity.this.getFragmentController().stackFragment(new CartFragment(), FragmentController.ANIMATION_SLIDE_IN, cart);
                    break;

                default:
                    showErrorMessage(cart.errorList);
                    break;
            }
        }

        @Override
        protected String getScreenId() {
            return getFragmentController().getCurrentFragment().getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }

    /**
     * My部品表
     */
    private class MyPartsApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return getFragmentController().getCurrentFragment().getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            String folderId = "0";
            String sort = "0";
            return ApiBuilder.createGetMyComponents(folderId, sort);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseGetMyComponents response = new ResponseGetMyComponents();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    MainActivity.this.getFragmentController().stackFragment(new MyPartsListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }
    //-- ADD NT-LWL 16/11/11 AliPay Payment FR -

    /**
     * 注文履歴
     */
    private class OrderHistoryApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchOrder request = new RequestSearchOrder();
            return ApiBuilder.createSearchOrder(request);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseSearchOrder response = new ResponseSearchOrder();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    MainActivity.this.getFragmentController().onBackKey();
                    MainActivity.this.getFragmentController().stackFragment(new OrderListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }
    //-- ADD NT-LWL 16/11/11 AliPay Payment TO -


    FragmentController.FragmentChangeStateListener mFragmentChangeStateListener = new FragmentController.FragmentChangeStateListener() {
        @Override
        public void changeFragmentState(int event) {
            FragmentController fragmentController = getFragmentController();
            MainHeader header = (MainHeader) getHeaderView();
            int count = fragmentController.getFragmentCount();

            if (count <= 1) {
                header.showBackButton(false);
            } else {
                header.showBackButton(true);
            }

            if ((fragmentController.getCurrentFragment() instanceof TopFragment)
                    || (fragmentController.getCurrentFragment() instanceof SearchResultKeywordFragment)) {
                header.showSearchButton(false);
            } else {
                header.showSearchButton(true);
            }

            if (fragmentController.getCurrentFragment() instanceof CartFragment) {
                header.disableCartButton(false);
            } else {
                header.disableCartButton(true);
            }

            if (fragmentController.getCurrentFragment() instanceof MyPartsListFragment) {
                header.disableMyPartButton(false);
            } else {
                header.disableMyPartButton(true);
            }

        }
    };

    //-- ADD NT-SLJ 16/11/11 AliPay Payment FR -
    // 登录成功后接收广播
    class LoginBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainHeader mainHeader = (MainHeader) getHeaderView();
//            mainHeader.showConsultButton(true);
            //--ADD NT-LWL 17/05/20 Share FR -
            if (!LoginActivity.ACTION_CLOSE_LOGIN.equals(intent.getStringExtra(LoginActivity.ACTION))) {
                // 用户未关闭登录框时 才显示live800
                mainHeader.showConsultButton(true);
            }
            // 判断是否需要登录后自动询价
            if (intent.getIntExtra("param_mode", LoginActivity.MODE_NORMAL) == LoginActivity.MODE_CHECK_PRICE) {
                // App未在后台运行时MainActivity中 mAppSchemeApi.scode为空
                if (TextUtils.isEmpty(mAppSchemeApi.scode)) {

                    // 用户直接关闭登录框 应该回到TOP画面
                    if (LoginActivity.ACTION_CLOSE_LOGIN.equals(intent.getStringExtra(LoginActivity.ACTION))) {
                        // 关闭登录框  清除session 进入详情画面
                        BaseFragment fragment = getFragmentController().getCurrentFragment();
                        if (fragment instanceof SplashFragment) {
                            SchemeApi schemeApi = ((SplashFragment) fragment).getSchemeApi();
                            // 清除session
                            schemeApi.setSessionId("");
                            schemeApi.checkPrice();
                        }
//                        getFragmentController().replaceFragmentAllowingStateLoss(new TopFragment(), FragmentController.ANIMATION_FADE_IN);
                    } else {
                        // 用户登录后自动询价

                        BaseFragment fragment = getFragmentController().getCurrentFragment();
                        if (fragment instanceof SplashFragment) {
                            ((SplashFragment) fragment).getSchemeApi().setSessionId(AppConfig.getInstance().getSessionId());
                            ((SplashFragment) fragment).getSchemeApi().checkPrice();
                        }
                    }
                } else {

                    // 用户登录后自动询价
                    if (!LoginActivity.ACTION_CLOSE_LOGIN.equals(intent.getStringExtra(LoginActivity.ACTION))) {
                        mAppSchemeApi.setSessionId(AppConfig.getInstance().getSessionId());
                        mAppSchemeApi.checkPrice();
                    } else {
                        // 关闭登录框  清除session 进入详情画面
                        mAppSchemeApi.setSessionId("");
                        mAppSchemeApi.checkPrice();
                    }
                }
            }
            //--ADD NT-LWL 17/05/20 Share TO -
        }
    }
    //-- ADD NT-SLJ 16/11/11 AliPay Payment TO -

    /**
     * フォアグランド・バックグラウンド監視
     */
    private AppLifecycle.AppLifecycleCallback appLifecycleCallback = new AppLifecycle.AppLifecycleCallback() {
        @Override
        public void onForeground() {
            MainActivity.this.onForeground();
        }

        @Override
        public void onBackground() {
            MainActivity.this.onBackground();
        }
    };

    AppNotifier.AppNoticeListener mAppStateListener = new AppNotifier.AppNoticeListener() {
        @Override
        public void appNotice(AppNotifier.AppNotice notice) {
            AppLog.d("AppState Changed(TopFragment) = " + notice.event);
            if (notice.event == AppNotifier.USER_NEW_LOGIN) {
                FragmentController fragmentController = getFragmentController();
                if (fragmentController != null) {
                    // 異なるユーザー
                    //BroadcastReceiverでダイアログを閉じる
                    {
                        Intent i = new Intent(SchemeActivity.BROADCAST_MESSAGE_DIALOG_DISMISS);
//						i.putExtra("key", "USER_NEW_LOGIN");
                        sendBroadcast(i);
                    }

                    fragmentController.clearStack(new TopFragment());
                }
            }
        }
    };

    //--ADD NT-LWL 17/05/18 QR scan FR -
    // Android6.0 检测是否有相机权限
    protected void openScanCheckSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限时 需要请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
            if (Build.VERSION.SDK_INT < 23) {
                // Android 6.0以下，弹出打开设置界面提示
                showMessage();
            }
        } else {
            openScan();
        }
    }

    // 打开扫码界面
    protected void openScan() {
        ScanActivity.launchActivity(getFragmentController().getCurrentFragment(), getHeaderView().getHeaderView().getHeight());
    }

    /**
     * 显示设置权限提示框
     */
    private void showMessage() {
        new MessageDialog(this, new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                switch (which) {
                    //立即设置
                    case DialogInterface.BUTTON_POSITIVE:
                        openSetting();
                        break;
                    //取消
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                }
            }
        }).show(R.string.open_permission, R.string.set, R.string.cancel);
    }

    /**
     * 打开设置界面
     */
    private void openSetting() {
        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            Toast.makeText(this, getString(R.string.open_error_tip), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意权限申请
                openScan();
            } else {
                // 用户拒绝权限申请
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // QQ分享回调
    private IUiListener shareListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            AppLog.e(o.toString());
        }

        @Override
        public void onError(UiError uiError) {
            AppLog.e("errorCode:" + uiError.errorCode + ",errorMsg:" + uiError.errorMessage);
            new MessageDialog(MainActivity.this, null).show(R.string.share_fail);
        }

        @Override
        public void onCancel() {
            AppLog.e("QQ share cancel");
        }
    };

    public IUiListener getShareListener() {
        return shareListener;
    }

    // 扫码结果 回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QQ分享结果回调
        Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    // 判断二维码格式是否正确
                    if (result.contains("ccode") || result.contains("scode")) {

                        Uri uri = Uri.parse(result);
                        String ccode = uri.getQueryParameter("ccode");
                        String scode = uri.getQueryParameter("scode");

                        if (TextUtils.isEmpty(ccode) && TextUtils.isEmpty(scode)) {
                            new MessageDialog(MainActivity.this, null).show(R.string.QR_error, R.string.dialog_button_close);
                        } else {
                            // 追加扫码成功GA
                            GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_QRSCAN_OK, result);
                            mAppSchemeApi.onCreate(uri);
                        }
                    } else {
                        new MessageDialog(MainActivity.this, null).show(R.string.QR_error, R.string.dialog_button_close);
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    //--ADD NT-LWL 17/05/18 QR scan TO -
    private class AppSchemeApi extends SchemeApi {

        public AppSchemeApi(Context context) {
            super(context);
        }

        @Override
        protected void doNextScreen(DataContainer dataContainer) {

            BaseFragment nextFragment = getNextFragment(dataContainer);
            //--UDP NT-LWL 17/08/30 Launch FR -
            //getFragmentController().stackFragment(nextFragment, FragmentController.ANIMATION_SLIDE_IN, dataContainer);
            if (getFragmentController().getCurrentFragment() instanceof LaunchFragment) {
                // 恢复侧滑菜单 可以滑动
                getSlideMenu().setEnable(true);
                getFragmentController().replaceFragment(nextFragment, FragmentController.ANIMATION_SLIDE_IN, dataContainer);
            } else {
                getFragmentController().stackFragment(nextFragment, FragmentController.ANIMATION_SLIDE_IN, dataContainer);
            }
            //--UDP NT-LWL 17/08/30 Launch TO -
        }

        @Override
        protected MessageDialog.MessageDialogListener getMessageDialogListener() {

            //閉じるだけ
            return null;
        }
    }


    public void closeSearchBar() {

        if (getSearchBar().isOpened()) {
            getSearchBar().closeBar();
        }
    }

    //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
    public Tracker getTracker() {
        return mTracker;
    }
    //-- ADD NT-LWL 17/03/22 AliPay Payment TO -


}
