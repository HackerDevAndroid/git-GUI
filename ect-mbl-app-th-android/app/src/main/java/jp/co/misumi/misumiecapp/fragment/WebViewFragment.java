package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.data.Live800Data;
import jp.co.misumi.misumiecapp.data.WebViewData;
import jp.co.misumi.misumiecapp.header.HeaderView;
import jp.co.misumi.misumiecapp.header.MainHeader;
import jp.co.misumi.misumiecapp.observer.ApiAccessObserver;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * WebViewFragment
 */
public class WebViewFragment extends BaseFragment {


    WebView mWebView;
    private DataContainer mDataContainer;

    public WebViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataContainer = getParameterFromActivity();

        //-- ADD NT-LWL 17/08/04 Depo FR -
        if (mDataContainer == null) {
            mDataContainer = getParameter();
        }
        //-- ADD NT-LWL 17/08/04 Depo TO -
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflateLayout(inflater, R.layout.fragment_webview, container, false);

        String url;
        WebViewData webViewData = (WebViewData) mDataContainer;
        if (webViewData != null) {
            url = webViewData.url;

            //サイカタ
            if (!url.contains("?")) {
                url += "?";
            } else {
                url += "&";
            }
            //-- UDP NT-SLJ 16/11/12 Live800 FR -
//            url += MisumiEcApp.getWebUrlStrApp();
            if (!SubsidiaryCode.isJapan() && webViewData.question != null) {
                url = appendUrl(url, webViewData.question);
            } else {

                if (SubsidiaryCode.isJapan()) {
                    url += MisumiEcApp.getWebUrlStrApp();
                } else {
                    url = url.substring(0, url.length() - 1);
                }

            }
            //--  UDP NT-SLJ 16/11/12 Live800 TO -
        } else {
            url = "";
        }

        AppLog.d("url=" + url);


        mWebView = (WebView) view.findViewById(R.id.webContents);
        //-- ADD NT-LWL 17/08/04 Depo FR -
        // 设置JS调用对象
//        mWebView.addJavascriptInterface(new JsObject(), "JsBridge");
        //-- ADD NT-LWL 17/08/04 Depo TO -
        mWebView.clearHistory();
        mWebView.setWebViewClient(new WebViewClientEx());
        mWebView.loadUrl(url);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);


        return view;
    }

    //--  ADD NT-SLJ 16/11/12 Live800 FR -
    @NonNull
    public String appendUrl(String url, Live800Data.Question question) {
        StringBuilder sb = new StringBuilder();
        sb.append("companyID=");
        sb.append(question.companyID);
        sb.append("&configID=");
        sb.append(question.configID);
        sb.append("&codeType=");
        sb.append(question.codeType);
        sb.append("&name=");
        String sbName = getStringName(question);
        sb.append(sbName);
        sb.append("&live800_ud_CellCD=");
        sb.append(question.live800_ud_CellCD);
        url += sb.toString();
        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800, question.question);
//        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null) {
//            AppConfig config = AppConfig.getInstance();
//            String userCode = "";
//
//            if (config.hasSessionId()) {
//                userCode = config.getUserCode();
//                if (android.text.TextUtils.isEmpty(userCode)) {
//                    userCode = "";
//                }
//            }
//            mTracker.set("&uid", userCode);
//            mTracker.send(new HitBuilders.EventBuilder()
//                                  .setCategory(GoogleAnalytics.CATEGORY_LIVE800)
//                                  .setAction(GoogleAnalytics.ACTION_VIEW_CLICK)
//                                  .setLabel(question.question)
//                                  .build());
//        }
        return url;
    }

    @NonNull
    private String getStringName(Live800Data.Question question) {
        String url = "";
        StringBuilder sbName = new StringBuilder();
        sbName.append(AppConfig.getInstance().getCustomerCode());
        sbName.append("_");
        sbName.append(AppConfig.getInstance().getCustomerName());
        sbName.append("_");
        sbName.append(AppConfig.getInstance().getUserName());
        sbName.append("_");
        sbName.append(question.live800_ud_CellCD);
        try {
            url = URLEncoder.encode(sbName.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
        }
        return url;
    }
    //-- ADD NT-SLJ 16/11/12 Live800 TO -

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.WebView;
    }


    @Override
    public void onHeaderEvent(int event, Objects objects) {
        super.onHeaderEvent(event, objects);
        mParent.finish();
    }

    /**
     * WebViewClientEx
     */
    private class WebViewClientEx extends WebViewClient {

        Handler mTimeOutHandler;

        public WebViewClientEx() {
            mTimeOutHandler = new Handler(callback);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mTimeOutHandler.sendEmptyMessageDelayed(1, AppConst.ConnectTimeout);
            showProgress(R.string.message_progress_transmitting);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mTimeOutHandler.removeMessages(1);
            super.onPageFinished(view, url);
            hideProgress();
        }

        //-- ADD NT-LWL 17/08/03 Depo FR -
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("misumi://addcart")) {
                Uri uri = Uri.parse(url);
                String cartList = uri.getQueryParameter("list");
                AppLog.d("url = " + url);
                AppLog.d("cartList = " + cartList);
                if (mAddToCartApi == null) {
                    mAddToCartApi = new AddToCartApi();
                }
                if (!TextUtils.isEmpty(cartList)) {
                    mAddToCartApi.setParameter(cartList);
                    mAddToCartApi.connect(mParent);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
        //-- ADD NT-LWL 17/08/03 Depo TO -

        private Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mWebView.loadUrl("file:///android_asset/web_timeout.html");
                hideProgress();
                return false;
            }
        };

//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            new MessageDialog(getContext(), null).show(R.string.message_network_error, R.string.dialog_button_ok);
//        }
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.WebView;
    }

    //-- ADD NT-LWL 17/08/03 Depo FR -
    // H5点击加入购物车，调用本地方法

    private AddToCartApi mAddToCartApi;
    private RefreshCartApi mRefreshCartApi;
    private MessageDialog mMessageDialogGoCart;

    // Js调用的java对象
//    public class JsObject {
//        @JavascriptInterface
//        public void addToCart(String brandCode, String seriesCode, String partNumber) {
//            AppLog.d("addToCart: brandCode= " + brandCode + ",seriesCode=" + seriesCode + ",partNumber=" + partNumber);
//            if (mAddToCartApi == null) {
//                mAddToCartApi = new AddToCartApi();
//            }
//            mAddToCartApi.setParameter(brandCode,seriesCode,partNumber);
//            mAddToCartApi.connect(mParent);
//        }
//    }

    private void doGoCart() {
        if (mRefreshCartApi == null) {
            mRefreshCartApi = new RefreshCartApi();
        }
        mRefreshCartApi.connect(mParent, mMessageDialogGoCart);
    }

    // 加入购物车Api
    private class AddToCartApi extends ApiAccessWrapper {

        private String cartList;

        @Override
        protected String getScreenId() {
            return null;
        }

        public void setParameter(String cartList) {
            this.cartList = cartList;
        }

        public HashMap<String, String> getParameter() {

            return ApiBuilder.createAddToCart(cartList);
        }

        public void onResult(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    mMessageDialogGoCart = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                doGoCart();
                            } else {
                                //閉じる
                                mMessageDialogGoCart.hide();
                            }
                        }
                    }).setAutoClose(false);

                    int count = 1;
                    try {
                        JSONArray list = new JSONArray(cartList);
                        count = list.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String string = "";
                    HeaderView h = getHeader();
                    if (h instanceof MainHeader) {
                        MainHeader mainHeader = (MainHeader) h;
                        Integer integer = mainHeader.addCartCount(count);
                        AppNotifier.getInstance().updateCartCount();
//                        string = String.format(getString(R.string.item_detail_dialog_added_cart), integer.toString());
                    } else {
                        Integer integer = AppConfig.getInstance().getCartCount() + count;
                        AppNotifier.getInstance().addCartCount(count);
//                        string = String.format(getString(R.string.item_detail_dialog_added_cart), integer.toString());
                    }
                    Toast.makeText(mParent, getString(R.string.item_detail_dialog_added_cart_title), Toast.LENGTH_SHORT).show();
//                    mMessageDialogGoCart.showCart(getString(R.string.item_detail_dialog_added_cart_title), string,
//                                                  R.string.dialog_button_go_cart, R.string.dialog_button_close);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }

    // 刷新购物车Api
    private class RefreshCartApi extends ApiAccessWrapper {

        private MessageDialog mMessageDialog;

        @Override
        protected String getScreenId() {
            return null;
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCart();
        }


        public void connect(Context context, MessageDialog messageDialog) {
            mMessageDialog = messageDialog;
            super.connect(context);
        }

        @Override
        protected boolean getMethod() {
            return ApiAccessObserver.API_GET;
        }

        @Override
        public void onResult(int responseCode, String result) {

            GetCart response = new GetCart();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }
                    getFragmentController().stackFragment(new CartFragment(), FragmentController.ANIMATION_SLIDE_IN, response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }

    // 是否能返回
    public boolean canGoBack() {
        if (mWebView != null) {
            return mWebView.canGoBack();
        }
        return false;
    }

    // 返回上个画面
    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }
    //-- ADD NT-LWL 17/08/03 Depo TO -
}
