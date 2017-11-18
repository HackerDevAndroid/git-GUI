package jp.co.misumi.misumiecapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.co.misumi.misumiecapp.data.UrlList;
import jp.co.misumi.misumiecapp.util.Encryption;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * コンフィグ
 */
public class AppConfig {

    private static AppConfig mInstance = null;
    private Context mContext;

    public final String initApiBaseUrl = BuildConfig.ApiUrl;
    public final String initNasBaseUrl = BuildConfig.NasUrl;

    // ---
    private boolean enableIDandPassward;
    private String loginId;
    private String loginPassword;
    private String apiBaseUrl = initApiBaseUrl;
    private String nasBaseUrl = initNasBaseUrl;
    private String sessionId;
    private String customerCode;
    private String customerName;
    private String userCode;
    private String userName;
    private Integer versionNumber;
    //    private long finalVersionCheck;
    private String currencyCode;
    private String paymentType;
    private String settlementType;
    private Integer quotationUnfitCount;
    private Integer orderUnfitCount;
    private String[] permissionList;
    private UrlList urlList;
    private String categoryUpdateTime;
    private Integer cartCount;
    //--ADD NT-SLJ 17/07/14 3小时必达 FR -
    private String immediateDeliveryFlag;
    //--ADD NT-SLJ 17/07/14 3小时必达 TO -

    public int dp;

    private String selectedLanguage;
    // -------
    public static final String KEY_LOGIN_ID = "loginid";
    public static final String KEY_LOGIN_PASS = "loginpass";
    public static final String KEY_ENABLE_IDPASS = "enable_idpass";
    public static final String KEY_SESSION_ID = "sessionid";
    public static final String KEY_APIBASE = "apibase";
    public static final String KEY_NASBASE = "nasbase";
    public static final String KEY_USRNAME = "username";
    public static final String KEY_USRCODE = "usercode";
    public static final String KEY_CUSTOMER_NAME = "customer_name";
    public static final String KEY_CUSTOMER_CODE = "customer_code";
    public static final String KEY_CURRENCY_CODE = "currencyCode";

    public static final String KEY_SETTING_LANGUAGE = "language_setting";

    public static final String KEY_PAYMENT_TYPE = "paymentType";
    public static final String KEY_SETTLEMENT_TYPE = "settlementType";

    public static final String KEY_QUOTAION_UNFIT_COUNT = "quotationUnfitCount";
    public static final String KEY_ORDER_UNFIT_COUNT = "orderUnfitCount";
    public static final String KEY_PERMISSION_LIST = "permissionList";
    public static final String KEY_PASSWORD_URL = "passwordUrl";
    public static final String KEY_NEW_REGIST_URL = "registUrl";
    public static final String KEY_CALENDAR_TOP_URL = "calendarTopUrl";
    public static final String KEY_CALENDAR_FULL_URL = "calendarFullUrl";
    public static final String KEY_USER_GUIDE_URL = "guideUrl";
    public static final String KEY_USER_POLICY_URL = "policyUrl";
    public static final String KEY_OTHER_URL = "ohterUrl";
    public static final String KEY_PERSONAL_INFO_URL = "personalInfoUrl";
    public static final String KEY_CONTACT_URL = "contactUrl";
    public static final String KEY_WORKING_HOUR_URL = "workingHourUrl";
    public static final String KEY_CANCEL_ORDER = "cancelOrder";
    public static final String KEY_CATEGORY_UPDATE_TIME = "categoryUpdateTime";
    public static final String KEY_CART_COUNT = "cartCount";
    //--ADD NT-LWL 17/07/06 Category FR -
    public static final String KEY_CATEGORY_REPLACE_LIST = "categoryImgURLReplaceList";
    //--ADD NT-LWL 17/07/06 Category TO -
    //--ADD NT-SLJ 17/07/14 3小时必达 FR -
    public static final String KEY_IMMEDIATEDELIVERY_FLAG = "immediateDeliveryFlag";
    public static final String KEY_DEPO_BANNERURL_FLAG = "depoBannerUrl";
    public static final String KEY_DEPO_PRODUCTSPAGEURL_FLAG = "depoProductsPageUrl";
    //--ADD NT-SLJ 17/07/14 3小时必达 TO -


    private final String mFileName = "appconfig.xml";

    private AppConfig(Context context) {
        mContext = context;
    }

    public static AppConfig createInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppConfig(context);
        }
        return mInstance;
    }

    public static AppConfig getInstance() {
        return mInstance;
    }

    public void loadConfig() {
        SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);

        float scale = mContext.getResources().getDisplayMetrics().density; //画面のdensityを指定。
        dp = (int) (1 * scale + 0.5f);

        // ログイン情報
        enableIDandPassward = pref.getBoolean(KEY_ENABLE_IDPASS, false);
        selectedLanguage = pref.getString(KEY_SETTING_LANGUAGE, "");
        loginId = pref.getString(KEY_LOGIN_ID, "");
        loginPassword = pref.getString(KEY_LOGIN_PASS, "");
        if (!loginPassword.isEmpty()) {
            loginPassword = Encryption.fromAES128(AppConst.EncryptKey, loginPassword);
        }

        sessionId = pref.getString(KEY_SESSION_ID, "");

//        // バージョンチェック時間
//        finalVersionCheck = pref.getLong(KEY_VERSIONCHECK, -1);

        // バージョン文字列
        versionNumber = 0;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionNumber = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //　客情報
        customerName = pref.getString(KEY_CUSTOMER_NAME, "");
        userName = pref.getString(KEY_USRNAME, "");
        customerCode = pref.getString(KEY_CUSTOMER_CODE, "");
        userCode = pref.getString(KEY_USRCODE, "");
        currencyCode = pref.getString(KEY_CURRENCY_CODE, "");
        paymentType = pref.getString(KEY_PAYMENT_TYPE, "");
        settlementType = pref.getString(KEY_SETTLEMENT_TYPE, "");
        quotationUnfitCount = pref.getInt(KEY_QUOTAION_UNFIT_COUNT, -1);
        orderUnfitCount = pref.getInt(KEY_ORDER_UNFIT_COUNT, -1);
        String readdata = pref.getString(KEY_PERMISSION_LIST, "");
        permissionList = readdata.split(",");
        categoryUpdateTime = pref.getString(KEY_CATEGORY_UPDATE_TIME, "");
        cartCount = pref.getInt(KEY_CART_COUNT, 0);
        //--ADD NT-SLJ 17/07/14 3小时必达 FR -
        immediateDeliveryFlag = pref.getString(KEY_IMMEDIATEDELIVERY_FLAG, "");
        //--ADD NT-SLJ 17/07/14 3小时必达 TO -
        apiBaseUrl = pref.getString(KEY_APIBASE, initApiBaseUrl);
        nasBaseUrl = pref.getString(KEY_NASBASE, initNasBaseUrl);

        AppLog.v("----");
        AppLog.v("enableIDandPassward = " + enableIDandPassward);
        AppLog.v("loginId = " + loginId);
        AppLog.v("loginPassword = " + loginPassword);
        AppLog.v("apiBaseUrl = " + apiBaseUrl);
        AppLog.v("nasBaseUrl = " + nasBaseUrl);
        AppLog.v("sessionId = " + sessionId);
//        AppLog.v("userType = " + userType);
        AppLog.v("customerCode = " + customerCode);
        AppLog.v("customerName = " + customerName);
        AppLog.v("userCode = " + userCode);
        AppLog.v("userName = " + userName);
        AppLog.v("versionNumber = " + versionNumber);
//        AppLog.v("finalVersionCheck = " + finalVersionCheck);
        AppLog.v("currencyCode = " + currencyCode);
        AppLog.v("quotationUnfitCount = " + quotationUnfitCount);
        AppLog.v("orderUnfitCount = " + orderUnfitCount);
        for (String st : permissionList) {
            AppLog.v("permissionList = " + st);
        }
        AppLog.v("categoryUpdateTime" + categoryUpdateTime);
        AppLog.v("cartCount = " + cartCount);
        //--ADD NT-SLJ 17/07/14 3小时必达 FR -
        AppLog.v("immediateDeliveryFlag=" + immediateDeliveryFlag);
        //--ADD NT-SLJ 17/07/14 3小时必达 TO -
    }


    public void setConfigData(HashMap<String, Object> param) throws Exception {

        SharedPreferences.Editor edit = getSharedPreferencesEditor();

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();

            switch (key) {
                case KEY_LOGIN_ID:
                    loginId = (String) entry.getValue();
                    edit.putString(KEY_LOGIN_ID, loginId);
                    break;
                case KEY_LOGIN_PASS:
                    loginPassword = (String) entry.getValue();
                    String result = Encryption.toAES128(AppConst.EncryptKey, loginPassword);
                    edit.putString(KEY_LOGIN_PASS, result);
                    break;
                case KEY_ENABLE_IDPASS:
                    enableIDandPassward = (Boolean) entry.getValue();
                    edit.putBoolean(KEY_ENABLE_IDPASS, enableIDandPassward);
                    break;
                case KEY_SESSION_ID:
                    sessionId = (String) entry.getValue();
                    edit.putString(KEY_SESSION_ID, sessionId);
                    break;
                case KEY_APIBASE:
                    apiBaseUrl = (String) entry.getValue();
                    edit.putString(KEY_APIBASE, apiBaseUrl);
                    break;
                case KEY_NASBASE:
                    nasBaseUrl = (String) entry.getValue();
                    edit.putString(KEY_NASBASE, nasBaseUrl);
                    break;
//                case KEY_VERSIONCHECK:
//                    finalVersionCheck = (Long) entry.getValue();
//                    edit.putLong(KEY_VERSIONCHECK, finalVersionCheck);
//                    break;
                case KEY_USRNAME:
                    userName = (String) entry.getValue();
                    edit.putString(KEY_USRNAME, userName);
                    break;
                case KEY_USRCODE:
                    userCode = (String) entry.getValue();
                    edit.putString(KEY_USRCODE, userCode);
                    break;
//                case KEY_USER_TYPE:
//                    userType = (String) entry.getValue();
//                    edit.putString(KEY_USER_TYPE, userType);
//                    break;
                case KEY_CUSTOMER_NAME:
                    customerName = (String) entry.getValue();
                    edit.putString(KEY_CUSTOMER_NAME, customerName);
                    break;
                case KEY_CUSTOMER_CODE:
                    customerCode = (String) entry.getValue();
                    edit.putString(KEY_CUSTOMER_CODE, customerCode);
                    break;
                case KEY_CURRENCY_CODE:
                    currencyCode = (String) entry.getValue();
                    edit.putString(KEY_CURRENCY_CODE, currencyCode);
                    break;
                case KEY_PAYMENT_TYPE:
                    paymentType = (String) entry.getValue();
                    edit.putString(KEY_PAYMENT_TYPE, paymentType);
                    break;
                case KEY_SETTLEMENT_TYPE:
                    settlementType = (String) entry.getValue();
                    edit.putString(KEY_SETTLEMENT_TYPE, settlementType);
                    break;
                case KEY_QUOTAION_UNFIT_COUNT:
                    quotationUnfitCount = (Integer) entry.getValue();
                    edit.putInt(KEY_QUOTAION_UNFIT_COUNT, quotationUnfitCount);
                    break;
                case KEY_ORDER_UNFIT_COUNT:
                    orderUnfitCount = (Integer) entry.getValue();
                    edit.putInt(KEY_ORDER_UNFIT_COUNT, orderUnfitCount);
                    break;
                case KEY_PERMISSION_LIST:
                    permissionList = (String[]) entry.getValue();
                    String writedata = "";
                    for (String ss : permissionList) {
                        writedata += ss + ",";
                    }
                    edit.putString(KEY_PERMISSION_LIST, writedata);
                    break;
                case KEY_CATEGORY_UPDATE_TIME:
                    edit.putString(KEY_CATEGORY_UPDATE_TIME, (String) entry.getValue());
                    break;
                case KEY_CART_COUNT:
                    edit.putInt(KEY_CART_COUNT, (Integer) entry.getValue());
                    break;
                //--ADD NT-SLJ 17/07/14 3小时必达 FR -
                case KEY_IMMEDIATEDELIVERY_FLAG:
                    immediateDeliveryFlag = (String) entry.getValue();
                    edit.putString(KEY_IMMEDIATEDELIVERY_FLAG, (String) entry.getValue());
                    break;
                //--ADD NT-SLJ 17/07/14 3小时必达 FR -
                default:
                    throw new IllegalArgumentException(key);
            }
        }
        if (!edit.commit()) {
            throw new IOException("write error <config file>.");
        }
    }

    public boolean getEnableIDandPassward() {
        return enableIDandPassward;
    }

    public boolean setEnableIDandPassward(boolean value) {

        SharedPreferences.Editor edit = getSharedPreferencesEditor();
        edit.putBoolean(KEY_ENABLE_IDPASS, value);
        boolean result = edit.commit();
        enableIDandPassward = value;
        return result;
    }

    public String getSelectedLanguageSetting() {
        if (selectedLanguage == null) {
            SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            selectedLanguage = pref.getString(KEY_SETTING_LANGUAGE, "");
        }
        return selectedLanguage;
    }

    public boolean setSelectedLanguage(String value) {
        SharedPreferences.Editor edit = getSharedPreferencesEditor();
        edit.putString(KEY_SETTING_LANGUAGE, value);
        boolean result = edit.commit();
        if (result) {
            selectedLanguage = value;
        }
        return result;
    }

    public String getLoginId() {
        return loginId;
    }

    public boolean setLoginId(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_LOGIN_ID, value);
            result = edit.commit();
        }
        loginId = value;
        return result;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public boolean setLoginPassword(String value) {
        String result = Encryption.toAES128(AppConst.EncryptKey, value);

        SharedPreferences.Editor edit = getSharedPreferencesEditor();
        edit.putString(KEY_LOGIN_PASS, result);
        boolean ret = edit.commit();
        loginPassword = value;
        return ret;
    }


//    public void clearInfo(){
//
//        setSessionId("");
//        setCustomerCode("");
//        setCustomerName("");
//        setUserCode("");
//        setUserName("");
//        setLoginPassword("");
//	}

    public boolean hasSessionId() {

        return sessionId != null && sessionId.length() != 0;

    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean setSessionId(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_SESSION_ID, value);
            result = edit.commit();
        }
        sessionId = value;
        return result;
    }

    /**
     * getApiBaseUrl
     *
     * @return
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    /**
     * setApiBaseUrl
     *
     * @param value
     * @return
     */
    public boolean setApiBaseUrl(String value) {

        SharedPreferences.Editor edit = getSharedPreferencesEditor();
        edit.putString(KEY_APIBASE, value);
        boolean result = edit.commit();
        apiBaseUrl = value;
        return result;
    }

    /**
     * getNasBaseUrl
     *
     * @return
     */
    public String getNasBaseUrl() {
        return nasBaseUrl;
    }

    /**
     * setNasBaseUrl
     *
     * @param value
     * @return
     */
    public boolean setNasBaseUrl(String value) {

        SharedPreferences.Editor edit = getSharedPreferencesEditor();
        edit.putString(KEY_NASBASE, value);
        boolean result = edit.commit();
        nasBaseUrl = value;
        return result;
    }


    public Integer getVersionNumber() {
        return versionNumber;
    }

//    public long getFinalVersionCheck(){
//        return finalVersionCheck;
//    }
//    public boolean setFinalVersionCheck(long value){
//        SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = pref.edit();
//        edit.putLong(KEY_VERSIONCHECK, value);
//        boolean result = edit.commit();
//        finalVersionCheck = value;
//        return result;
//    }

    public String getCustomerName() {
        return customerName;
    }

    public boolean setCustomerName(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_CUSTOMER_NAME, value);
            result = edit.commit();
        }
        customerName = value;
        return result;
    }

    //--ADD NT-SLJ 17/07/14 3小时必达 FR -
    public String getImmediateDeliveryFlag() {
        return immediateDeliveryFlag;
    }

    public boolean setImmediateDeliveryFlag(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_IMMEDIATEDELIVERY_FLAG, value);
            result = edit.commit();
        }
        immediateDeliveryFlag = value;
        return result;
    }

    //--ADD NT-SLJ 17/07/14 3小时必达 TO -
    public String getCustomerCode() {
        return customerCode;
    }

    public boolean setCustomerCode(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_CUSTOMER_CODE, value);
            result = edit.commit();
        }
        customerCode = value;
        return result;
    }

    public String getUserCode() {
        return userCode;
    }

    public boolean setUserCode(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_USRCODE, value);
            result = edit.commit();
        }
        userCode = value;
        return result;
    }

    public String getUserName() {
        return userName;
    }

    public boolean setUserName(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_USRNAME, value);
            result = edit.commit();
        }
        userName = value;
        return result;
    }

    public String getCurrencyCode() {
        if (currencyCode != null && !currencyCode.isEmpty()) {
            return currencyCode;
        } else {
            if (!SubsidiaryCode.isJapan()) {
                return AppConst.CURRENCY_CODE_RMB;
            } else {
                return AppConst.CURRENCY_CODE_JPY;
            }
        }
    }

    public String getCurrencyString(String code) {
        switch (code) {
            case AppConst.CURRENCY_CODE_JPY:
                return mContext.getResources().getString(R.string.label_currency_jpy);
            case AppConst.CURRENCY_CODE_RMB:
                return mContext.getResources().getString(R.string.label_currency_rmb);
            case AppConst.CURRENCY_CODE_USD:
                return mContext.getResources().getString(R.string.label_currency_usd_pre);
        }
        return null;
    }

    public boolean isDollar() {
        return AppConst.CURRENCY_CODE_USD.equals(getCurrencyCode());
    }

    public boolean setCurrencyCode(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_CURRENCY_CODE, value);
            result = edit.commit();
        }
        currencyCode = value;
        return result;
    }


    public Integer getQuotationUnfitCount() {
        return quotationUnfitCount;
    }

    public boolean setQuotationUnfitCount(Integer value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putInt(KEY_QUOTAION_UNFIT_COUNT, value);
            result = edit.commit();
        }
        quotationUnfitCount = value;
        return result;
    }


    public Integer getOrderUnfitCount() {
        return orderUnfitCount;
    }

    public boolean setOrderUnfitCount(Integer value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putInt(KEY_ORDER_UNFIT_COUNT, value);
            result = edit.commit();
        }
        orderUnfitCount = value;
        return result;
    }

    public String[] getPermissionList() {
        return permissionList;
    }

    public boolean setPermissionList(String[] value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();

            String writedata = "";
            for (String ss : value) {
                writedata += ss + ",";
            }

            edit.putString(KEY_PERMISSION_LIST, writedata);
            result = edit.commit();
        }
        permissionList = value;
        return result;
    }

    public UrlList getUrlList() {
        return urlList;
    }

    public boolean setUrlList(UrlList value) throws IOException {
        urlList = value;

        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();

            String data;

            data = value.forgetPasswordUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_PASSWORD_URL, data);

            data = value.newRegistUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_NEW_REGIST_URL, data);

            data = value.calendarTopUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_CALENDAR_TOP_URL, data);

            data = value.calendarFullUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_CALENDAR_FULL_URL, data);

            data = value.userGuidUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_USER_GUIDE_URL, data);

            data = value.userPolicyUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_USER_POLICY_URL, data);

            data = value.othersUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_OTHER_URL, data);

            data = value.personalInformationUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_PERSONAL_INFO_URL, data);

            data = value.contactUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_CONTACT_URL, data);

            data = value.workingHourUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_WORKING_HOUR_URL, data);

            data = value.cancelOrderUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_CANCEL_ORDER, data);

            //--ADD NT-LWL 17/07/06 Category FR -
            data = value.categoryImgURLReplaceList;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_CATEGORY_REPLACE_LIST, data);
            //--ADD NT-LWL 17/07/06 Category TO -
            //--ADD NT-LWL 17/08/22 Depo FR -
            data = value.depoBannerUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_DEPO_BANNERURL_FLAG, data);
            data = value.depoProductsPageUrl;
            if (data == null) {
                data = "";
            }
            edit.putString(KEY_DEPO_PRODUCTSPAGEURL_FLAG, data);
            //--ADD NT-LWL 17/08/22 Depo TO -
            result = edit.commit();


        }
        if (!result) {
            throw new IOException("write error <config file>.");
        }
        return true;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public boolean setPaymentType(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_PAYMENT_TYPE, value);
            result = edit.commit();
        }
        paymentType = value;
        return result;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public boolean setSettlementType(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_SETTLEMENT_TYPE, value);
            result = edit.commit();
        }
        settlementType = value;
        return result;
    }

    public String getCategoryUpdateTime() {
        return categoryUpdateTime;
    }

    public boolean setCategoryUpdateTime(String value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putString(KEY_CATEGORY_UPDATE_TIME, value);
            result = edit.commit();
        }
        categoryUpdateTime = value;
        return result;
    }


    public Integer getCartCount() {
        return cartCount;
    }

    public boolean setCartCount(Integer value) {
        boolean result = true;
        if (value != null) {

            SharedPreferences.Editor edit = getSharedPreferencesEditor();
            edit.putInt(KEY_CART_COUNT, value);
            result = edit.commit();
        }
        cartCount = value;
        return result;
    }


    public boolean isIncludeTax() {

		/*
        決済形態
		settlementType
		  CRD: 売掛金
		  ADV: 前金
		  CCD: クレジットカード
		  COD: 代引き
		  ADI: 前金(インド用)
		  COI: 代引き(インド用)"
		*/

        boolean isIncludeTax = false;

        if (SubsidiaryCode.isJapan()) {

            String settlementType = getSettlementType();

            //日本は決済方法で変わる
            if ("CRD".equals(settlementType)) {
                //  CRD: 売掛金

            } else if ("CCD".equals(settlementType)) {
                //  CCD: クレジットカード
                isIncludeTax = true;

            } else if ("COD".equals(settlementType)) {
                //  COD: 代引き
                isIncludeTax = true;

            } else {

                //それ以外、規定無し
            }

        } else {

            //中国は税込み
            isIncludeTax = true;
        }

        return isIncludeTax;
    }


    //代引きユーザー
    public boolean isCodUser() {

		/*
		決済形態
		settlementType
		  CRD: 売掛金
		  ADV: 前金
		  CCD: クレジットカード
		  COD: 代引き
		  ADI: 前金(インド用)
		  COI: 代引き(インド用)"
		*/

        boolean isCodUser = false;

        String settlementType = getSettlementType();

        //日本は決済方法で変わる
        if ("COD".equals(settlementType)) {
            //  COD: 代引き
            isCodUser = true;
        }

        return isCodUser;
    }


    private SharedPreferences.Editor getSharedPreferencesEditor() {

        SharedPreferences pref = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);

        return pref.edit();
    }

}

