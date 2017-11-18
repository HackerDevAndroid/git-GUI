package jp.co.misumi.misumiecapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.activity.LoginActivity;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.data.RequestSearchOrder;
import jp.co.misumi.misumiecapp.data.RequestSearchQuotation;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseSearchOrder;
import jp.co.misumi.misumiecapp.data.ResponseSearchQuotation;
import jp.co.misumi.misumiecapp.data.WebViewData;
import jp.co.misumi.misumiecapp.fragment.CartFragment;
import jp.co.misumi.misumiecapp.fragment.CategorySearchFragment;
import jp.co.misumi.misumiecapp.fragment.DebugFragment;
import jp.co.misumi.misumiecapp.fragment.DeviceInfoFragment;
import jp.co.misumi.misumiecapp.fragment.EstimateListFragment;
import jp.co.misumi.misumiecapp.fragment.MyPartsListFragment;
import jp.co.misumi.misumiecapp.fragment.OrderListFragment;
import jp.co.misumi.misumiecapp.fragment.TopFragment;
import jp.co.misumi.misumiecapp.header.MainHeader;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Browser;

/**
 * SideMenu
 */
public class SideMenu implements View.OnClickListener {

    DrawerLayout mDrawerLayout;
    View mDrawerView;
    FragmentController mFragmentController;
    MainActivity mainActivity;


    CategorySearchApi mCategorySearchApi;
    EstimateHistoryApi mEstimateHistoryApi;
    OrderHistoryApi mOrderHistoryApi;
    CartApi mCartApi;
    MyPartsApi mMyPartsApi;
    LogoutApi mLogoutApi;

    AppConfig mConfig;

    public SideMenu(Activity activity){

        mConfig = AppConfig.getInstance();
        mainActivity = (MainActivity) activity;
        mFragmentController = mainActivity.getFragmentController();

        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        mDrawerView = mDrawerLayout.findViewById(R.id.left_drawer);

        mCategorySearchApi = new CategorySearchApi();
        mEstimateHistoryApi = new EstimateHistoryApi();
        mOrderHistoryApi = new OrderHistoryApi();
        mCartApi = new CartApi();
        mLogoutApi = new LogoutApi();
        mMyPartsApi = new MyPartsApi();

        mDrawerView.findViewById(R.id.frameTop).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameLogin).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameLogout).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameCategoryList).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameCart).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameMyParts).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameEstimateHistory).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameOrderHistory).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameWorkingHours).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameGuide).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameAgreements).setOnClickListener(this);
        mDrawerView.findViewById(R.id.framePersonalInfo).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameRegister).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameContact).setOnClickListener(this);
        mDrawerView.findViewById(R.id.frameDeviceInfo).setOnClickListener(this);


        if (AppConfig.getInstance().hasSessionId()){
            setLogin(true);
        }else{
            setLogin(false);
        }

//        MISUMI_MOBILE_APP-583 【中国版】メニューの営業日・営業時間を削除したい
        if (BuildConfig.subsidiaryCode.equals("CHN")){
            workingHour(true);
        } else {
            workingHour(false);
        }

        AppNotifier.getInstance().addListener(mAppStateListener,
                AppNotifier.USER_LOGIN|AppNotifier.USER_LOGOUT);


        if (BuildConfig.DebugMode){
            mDrawerView.findViewById(R.id.frameDebug).setOnClickListener(this);
        }else{
            mDrawerView.findViewById(R.id.frameDebug).setVisibility(View.GONE);
        }

    }

    /**
     * updateDisableItem
     */
    public void updateDisableItem(){
        if (mConfig.getUrlList().userPolicyUrl == null){
            mDrawerView.findViewById(R.id.frameAgreements).setVisibility(View.GONE);
        }
        if (mConfig.getUrlList().personalInformationUrl == null){
            mDrawerView.findViewById(R.id.framePersonalInfo).setVisibility(View.GONE);
        }
        if (mConfig.getUrlList().newRegistUrl == null){
            mDrawerView.findViewById(R.id.frameRegister).setVisibility(View.GONE);
        }
        if (mConfig.getUrlList().workingHourUrl == null){
            mDrawerView.findViewById(R.id.frameWorkingHours).setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    AppNotifier.AppNoticeListener mAppStateListener = new AppNotifier.AppNoticeListener() {
        @Override
        public void appNotice(AppNotifier.AppNotice notice) {
//            AppLog.d("AppState Changed(SlideMenu) = " + notice.event);
            if (notice.event == AppNotifier.USER_LOGIN){
                setLogin(true);
            }else if(notice.event == AppNotifier.USER_LOGOUT){
                setLogin(false);
            }
        }

    };

    /**
     * setLogin
     * @param login
     */
    private void setLogin(boolean login){

        if (login) {
            mDrawerView.findViewById(R.id.frameLogin).setVisibility(View.GONE);
            mDrawerView.findViewById(R.id.frameLogout).setVisibility(View.VISIBLE);
        } else {
            mDrawerView.findViewById(R.id.frameLogin).setVisibility(View.VISIBLE);
            mDrawerView.findViewById(R.id.frameLogout).setVisibility(View.GONE);
        }
    }

//    MISUMI_MOBILE_APP-583 【中国版】メニューの営業日・営業時間を削除したい
    private void workingHour(boolean chn){

        if (chn){
            mDrawerView.findViewById(R.id.frameWorkingHours).setVisibility(View.GONE);
        } else {
            mDrawerView.findViewById(R.id.frameWorkingHours).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frameTop:
                // TOPフラグメントの場合は遷移しない
                if (!(mFragmentController.getCurrentFragment() instanceof TopFragment)) {
                    mFragmentController.stackFragment(new TopFragment(), FragmentController.ANIMATION_SLIDE_IN);
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_top);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameLogin:
                LoginActivity.launchActivity(mainActivity,LoginActivity.MODE_NORMAL);
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_login);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameLogout:
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_logout);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                new MessageDialog(mainActivity, new MessageDialog.MessageDialogListener() {
                    @Override
                    public void onDialogResult(Dialog dlg, View view, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            mLogoutApi.connect(mainActivity);
                            //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
                            GoogleAnalytics.sendAction(mainActivity.getTracker(),null,GoogleAnalytics.CATEGORY_LOGOUT,"-");
                            //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
                        }
                    }
                }).show(R.string.message_logout, R.string.dialog_button_yes, R.string.dialog_button_no);
                break;
            case R.id.frameCategoryList:
                if (!(mFragmentController.getCurrentFragment() instanceof CategorySearchFragment)) {
                    mCategorySearchApi.connect(mainActivity);
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_categorySearch);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameCart:
                if (!(mFragmentController.getCurrentFragment() instanceof CartFragment)) {
                    if (new SessionRequiredDialog().judgeLaunchRestriction(mainActivity)) {
                        mCartApi.connect(mainActivity);
                    }
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_cart);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameMyParts:
                if (!(mFragmentController.getCurrentFragment() instanceof MyPartsListFragment)) {
                    if (new SessionRequiredDialog().judgeLaunchRestriction(mainActivity)) {
                        mMyPartsApi.connect(mainActivity);
                    }
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_myParts);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameEstimateHistory:
                if (!(mFragmentController.getCurrentFragment() instanceof EstimateListFragment)) {
                    if (new SessionRequiredDialog().judgeLaunchRestriction(mainActivity)) {
                        mEstimateHistoryApi.connect(mainActivity);
                    }
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_QThistory);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameOrderHistory:
                if (!(mFragmentController.getCurrentFragment() instanceof OrderListFragment)) {
                    if (new SessionRequiredDialog().judgeLaunchRestriction(mainActivity)) {
                        mOrderHistoryApi.connect(mainActivity);
                    }
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_SOhistory);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameWorkingHours:
                // 営業時間
                Browser.run(mainActivity, mConfig.getUrlList().workingHourUrl);
                break;
            case R.id.frameGuide:
                //ユーザーガイド
                SubActivity.launchActivity(mFragmentController.getCurrentFragment(),SubActivity.SUB_TYPE_WEB_VIEW,
                        SubActivity.REQUEST_CODE_WEB_VIEW, new WebViewData(mConfig.getUrlList().userGuidUrl));
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_guide);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameAgreements:
                // 利用規約
                Browser.run(mainActivity, mConfig.getUrlList().userPolicyUrl);
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_terms);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.framePersonalInfo:
                // 個人情報
                Browser.run(mainActivity, mConfig.getUrlList().personalInformationUrl);
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_privacy);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameRegister:
                // 会員登録
                //MISUMI_MOBILE_APP-574 外部ブラウザ、アプリ内ブラウザ表示の精査 外部ブラウザ起動に変更
                // MISUMI_MOBILE_APP-664
                SubActivity.launchActivity(mFragmentController.getCurrentFragment(), SubActivity.SUB_TYPE_WEB_VIEW,
                        SubActivity.REQUEST_CODE_WEB_VIEW, new WebViewData(mConfig.getUrlList().newRegistUrl));
                //-- UDP NT-LWL 17/03/28 AliPay Payment FR -
//                GoogleAnalytics.sendAction(mainActivity.getTracker(),null,GoogleAnalytics.CATEGORY_REGISTER,"-");
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_register);
                //-- UDP NT-LWL 17/03/28 AliPay Payment TO -
                break;
            case R.id.frameContact:
                // 問い合わせ
                //MISUMI_MOBILE_APP-574 外部ブラウザ、アプリ内ブラウザ表示の精査 アプリ内WebView起動に変更
                SubActivity.launchActivity(mFragmentController.getCurrentFragment(), SubActivity.SUB_TYPE_WEB_VIEW,
                        SubActivity.REQUEST_CODE_WEB_VIEW, new WebViewData(mConfig.getUrlList().contactUrl));
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_contact);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameDeviceInfo:
                // デバイス情報
                if (!(mFragmentController.getCurrentFragment() instanceof DeviceInfoFragment)) {
                    mFragmentController.stackFragment(new DeviceInfoFragment(), FragmentController.ANIMATION_SLIDE_IN);
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mainActivity.getTracker(), GoogleAnalytics.CATEGORY_MENU, GoogleAnalytics.lable_phoneInfo);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
                break;
            case R.id.frameDebug:
                // デバッグ
                mFragmentController.stackFragment(new DebugFragment(), FragmentController.ANIMATION_SLIDE_IN);
                break;
            default:
                break;
        }
        closeDrawer();
    }

    /**
     * openDrawer
     */
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerView);
    }

    /**
     * closeDrawer
     */
    public void closeDrawer(){
        mDrawerLayout.closeDrawer(mDrawerView);
    }

    public boolean isOpened(){
        return mDrawerLayout.isDrawerOpen(mDrawerView);
    }

    /**
     * ログアウト
     */
    private class LogoutApi extends ApiAccessWrapper{

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createLogout();
        }

        @Override
        public void onResult(int responseCode, String result) {
            onLogout();
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            onLogout();
        }
        private void onLogout(){
            new LoginState().logout();
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    mFragmentController.clearStack(new TopFragment());
                }
            }).show(R.string.message_logout_success);
            //-- ADD NT-SLJ 16/11/11 Live800 FR –
            MainHeader mainHeader= (MainHeader) mainActivity.getHeaderView();
            mainHeader.showConsultButton(false);
            //-- ADD NT-SLJ 16/11/11 Live800 TO –
        }
    }

    /**
     * カテゴリ検索
     */
    private class CategorySearchApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createSearchCategory(null, null);
        }

        @Override
        public void onResult(int responseCode, String result) {

            CategoryList categorylist = new CategoryList();
            if (!categorylist.setData(result)){
                showErrorMessage(null);
                return;
            }


            if (responseCode == NetworkInterface.STATUS_OK) {
                if (categorylist.isEmpty()){
					//ネットワーク読み込みして空の場合はアプリキャッシュ
                    categorylist = ((MisumiEcApp)mainActivity.getApplication()).getTopCategoryList();
                }else {
                    // カテゴリデータを読み込んだときは外部ファイルへ書き出しを行う
                    ((MisumiEcApp)mainActivity.getApplication()).setTopCategoryList(categorylist);
                }
                mFragmentController.stackFragment(new CategorySearchFragment(), FragmentController.ANIMATION_SLIDE_IN, categorylist);
            } else {
                showErrorMessage(categorylist.errorList);
            }
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }

    /**
     * 見積履歴
     */
    private class EstimateHistoryApi extends ApiAccessWrapper{

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchQuotation request	= new RequestSearchQuotation();
            return ApiBuilder.createSearchQuotation(request);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseSearchQuotation response = new ResponseSearchQuotation();
            boolean pars = response.setData(result);
            if (!pars){
                showErrorMessage(null);
                return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    mFragmentController.stackFragment(new EstimateListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
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

    /**
     * 注文履歴
     */
    private class OrderHistoryApi extends ApiAccessWrapper{

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchOrder request	= new RequestSearchOrder();
            return ApiBuilder.createSearchOrder(request);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseSearchOrder response = new ResponseSearchOrder();
            boolean pars = response.setData(result);
            if (!pars){
                showErrorMessage(null);
                return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    mFragmentController.stackFragment(new OrderListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
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

    /**
     * カート
     */
    private class CartApi extends ApiAccessWrapper{

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCart();
        }

        @Override
        public void onResult(int responseCode, String result) {
            GetCart cart = new GetCart();
            boolean pars = cart.setData(result);
            if (!pars){
                showErrorMessage(null);
				return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    mFragmentController.stackFragment(new CartFragment(), FragmentController.ANIMATION_SLIDE_IN, cart);
                    break;
                default:
                    showErrorMessage(cart.errorList);
                    break;
            }
        }

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }

    /**
     * My部品表
     */
    private class MyPartsApi extends ApiAccessWrapper{

        @Override
        public HashMap<String, String> getParameter() {
            String folderId	= "0";
            String sort		= "0";
            return  ApiBuilder.createGetMyComponents(folderId, sort);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseGetMyComponents response = new ResponseGetMyComponents();
            boolean pars = response.setData(result);
            if (!pars){
                showErrorMessage(null);
				return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    mFragmentController.stackFragment(new MyPartsListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected String getScreenId() {
            return ScreenId.Menu;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }


    public void setEnable(boolean enable){

		if (enable) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		} else {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}
    }


}
