package jp.co.misumi.misumiecapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.LoginState;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.GetCartCount;
import jp.co.misumi.misumiecapp.data.Login;
import jp.co.misumi.misumiecapp.data.UrlList;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Browser;
import jp.co.misumi.misumiecapp.util.Format;


/**
 * ログイン（ダイアログ風）
 */
public class LoginActivity extends Activity implements View.OnClickListener, TextWatcher {

    private static final String PARAM_MODE = "param_mode";
    public static final int MODE_NORMAL = 1;
    public static final int MODE_REQUIRED = 2;
    public static final int MODE_LOST_SESSION = 3;
    //--ADD NT-LWL 17/05/19 Share FR -
    // 登录后询价
    public static final int MODE_CHECK_PRICE = 4;
    // 动作key
    public static final String ACTION = "action_key";
    // 关闭登录框
    public static final String ACTION_CLOSE_LOGIN = "close_loginActivity";
    //--ADD NT-LWL 17/05/19 Share TO -

    public int launchMode;
    boolean isOtherUser = false;

    private EditText mUserId;
    private EditText mPassWord;
    private FrameLayout mButtonLogin;
    private CheckBox mCheckPassword;

    private AppConfig config;

    private LoginApi mLoginApi;
    private CartCountApi mCartCountApi;
    private TextView textViewError;
    private ViewGroup errorInfoArea;

    //-- ADD NT-SLJ 16/11/11 AliPay Payment FR –
    public static final String LONGIN_SUCCESS_ACTION="com.misumi_ec.cn.misumi_ec.login_success";
    //-- ADD NT-SLJ 16/11/11 AliPay Payment TO –
    //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
    protected Tracker mTracker;
    //-- ADD NT-LWL 17/03/22 AliPay Payment TO -

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
       if (BuildConfig.subsidiaryCode.equals("CHN")) {
            MisumiEcApp application = (MisumiEcApp)getApplication();
            mTracker = application.getDefaultTracker();
        }
        //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
        mLoginApi = new LoginApi();
        mCartCountApi = new CartCountApi();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_login);


        Intent intent = getIntent();
        if (intent != null){
            launchMode = intent.getIntExtra(PARAM_MODE, MODE_NORMAL);
        }else{
            launchMode = MODE_NORMAL;
        }


        config = AppConfig.getInstance();
        config.loadConfig();


        mUserId = (EditText) findViewById(R.id.editUserId);
        mUserId.setInputType(InputType.TYPE_CLASS_TEXT);
        mPassWord = (EditText) findViewById(R.id.editPassword);
        mUserId.addTextChangedListener(this);
        mPassWord.addTextChangedListener(this);


        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (Format.isAsciiFormat(source.toString())) {
                    return source;
                } else {
                    return "";
                }
            }
        };
        InputFilter inputFilterLength = new InputFilter.LengthFilter(8);
        mPassWord.setFilters(new InputFilter[]{inputFilter, inputFilterLength});


        mCheckPassword = (CheckBox) findViewById(R.id.checkInputOmit);
        mCheckPassword.setChecked(config.getEnableIDandPassward());

        // ログインボタン
        mButtonLogin = (FrameLayout) findViewById(R.id.buttonLogin);
        mButtonLogin.setEnabled(judgeEnableLoginButton());
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                mLoginApi.connect(LoginActivity.this);
                //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
                GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_LOGIN, "-");
                //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
            }
        });

        if (config.getEnableIDandPassward()){
            mUserId.setText(config.getLoginId());
            mPassWord.setText(config.getLoginPassword());
        }

        //パスワード忘れ
        TextView link1 = (TextView) findViewById(R.id.textLinkPassword);
        UrlList urlList = AppConfig.getInstance().getUrlList();
        if (urlList != null && urlList.forgetPasswordUrl == null){
            link1.setVisibility(View.GONE);
        }else {
            link1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Browser.run(LoginActivity.this, AppConfig.getInstance().getUrlList().forgetPasswordUrl);
                }
            });
        }

        //閉じる
        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--ADD NT-LWL 17/05/20 Share FR -
                if (AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_CHN)) {

                    // QR 分享进入 询价标识
                    if (launchMode == MODE_CHECK_PRICE){

                        // 用户关闭登录框 给MainActivity发广播 跳转到TOP画面
                        Intent intent = new Intent(LONGIN_SUCCESS_ACTION);
                        // 询价参数
                        intent.putExtra(PARAM_MODE,MODE_CHECK_PRICE);
                        // 关闭参数
                        intent.putExtra(ACTION,ACTION_CLOSE_LOGIN);
                        LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(intent);
                    }
                }
                //--ADD NT-LWL 17/05/20 Share TO -
                finish();
            }
        });

        TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);
        switch (launchMode){
            case LoginActivity.MODE_NORMAL:
                textViewMessage.setVisibility(View.GONE);
                break;
            case LoginActivity.MODE_REQUIRED:
                textViewMessage.setText(getResources().getText(R.string.login_label_mode_require));
                break;
            case LoginActivity.MODE_LOST_SESSION:
                textViewMessage.setText(getResources().getText(R.string.login_label_mode_session));
                break;
            //--ADD NT-LWL 17/05/20 Share FR -
            case LoginActivity.MODE_CHECK_PRICE:
                // 设置提示文字
                if (config.hasSessionId()){
                    textViewMessage.setText(getResources().getText(R.string.login_label_mode_session));
                } else {
                    textViewMessage.setText(getResources().getText(R.string.login_label_mode_require));
                }
                break;
            //--ADD NT-LWL 17/05/20 Share TO -
        }


        textViewError = (TextView) findViewById(R.id.textViewError);
        errorInfoArea = (ViewGroup) findViewById(R.id.errorInfoArea);
        errorInfoArea.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonLogin:

                hideKeyboard();
                mLoginApi.connect(this);
                break;
            default:
                break;
        }
    }

    protected void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * judgeEnableLoginButton
     * @return
     */
    boolean judgeEnableLoginButton(){
        return mPassWord.getText().length() > 0 &&
                mUserId.getText().length() > 0;
    }

    private void onFinishLoginProcess(){

        finish();
    }


    /**
     * LoginApi
     */
    private class LoginApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return null;
        }

        @Override
        public HashMap<String, String> getParameter() {
            String loginid = mUserId.getText().toString();
            String loginpass = mPassWord.getText().toString();

            if (launchMode == MODE_LOST_SESSION && !loginid.equals(config.getLoginId())) {
                // 異なるユーザー
                isOtherUser = true;
            }

            return ApiBuilder.createLogin(loginid, loginpass);
        }

        @Override
        public void onResult(int responseCode, String result) {

            Login login = new Login();
            boolean pars = login.setData(result);
            if (!pars){
                showErrorMessage(null);
                return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    try {
                        new LoginState().login(login, mUserId.getText().toString(), mPassWord.getText().toString(), mCheckPassword.isChecked(), isOtherUser);
                        mCartCountApi.connect(getContext());
                        //-- ADD NT-SLJ 16/11/11 AliPay Payment FR –
                        if (AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_CHN)) {
                            Intent intent = new Intent(LONGIN_SUCCESS_ACTION);
                            //--ADD NT-LWL 17/05/20 Share FR -
                            if (launchMode == MODE_CHECK_PRICE){
                                // 询价标识
                                intent.putExtra(PARAM_MODE,MODE_CHECK_PRICE);
                            }
                            //--ADD NT-LWL 17/05/20 Share TO -
                            LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(intent);
                        }
                        //-- ADD NT-SLJ 16/11/11 AliPay Payment TO –
                    } catch (Exception e) {
                        String message;
                        message = getString(R.string.message_write_file_error);
                        message += "(" + e.toString() + ")";
                        new MessageDialog(getContext(), null).show(message);
                    }
                    break;
                default:
                    mPassWord.setText("");
                    if (login.errorList == null || login.errorList.ErrorInfoList.size() == 0) {
                        textViewError.setText(R.string.message_unknown_error);
                        errorInfoArea.setVisibility(View.VISIBLE);
                    }else{
                        String message = login.errorList.getErrorMessage(getScreenId());
                        textViewError.setText(message);
                        errorInfoArea.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
        }
    }


    /**
     * CartCountApi
     */
    private class CartCountApi extends ApiAccessWrapper{

        @Override
        protected String getScreenId() {
            return ScreenId.Login;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCartCount();
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            GetCartCount cartCount = new GetCartCount();
            if(!cartCount.setData(result)){
                onFinishLoginProcess();
                return;
            }

            if (responseCode == NetworkInterface.STATUS_OK){
                AppNotifier.getInstance().setCartCount(cartCount.count);

            }
            finish();
        }

        @Override
        protected void onTimeout() {
            finish();
        }

        @Override
        protected void onNetworkError(int responseCode) {
            finish();
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        mButtonLogin.setEnabled(judgeEnableLoginButton());
    }



    /**
     * launchActivity
     * @param context
     */
    public static void launchActivity(Context context, int mode){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(PARAM_MODE,mode);
        context.startActivity(intent);
    }

}
