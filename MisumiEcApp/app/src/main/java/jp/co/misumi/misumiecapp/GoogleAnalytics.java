//-- ADD NT-LWL 16/12/14 AliPay Payment FR -
package jp.co.misumi.misumiecapp;

import android.text.TextUtils;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created date: 2016/12/14 16:26
 */
public class GoogleAnalytics {
    public static final String CATEGORY_LIVE800 = "Live800";
    public static final String CATEGORY_ALIPAY = "AliPay";

    public static final String MAINHEADER="MainHeaderView";

    public static final String ACTION_VIEW_CLICK = "Click";
    //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
    public static final String CATEGORY_LOGIN = "Login";
    public static final String CATEGORY_LOGOUT = "Logout";
    public static final String CATEGORY_SELECT_MODLE = "Codefix";
    public static final String CATEGORY_CONFIRM_PRICE = "CheckPrice";
    public static final String CATEGORY_ADD_CART= "AddCart";
    public static final String CATEGORY_SEARCH = "Search";
    // 分享GA 类别名称
    public static final String CATEGORY_SHARE = "Share";
    // 购物车
    public static final String CATEGORY_CART = "Cart";

    public static final String searchBtn="searchBtn";
    public static final String suggest="suggest";
    //-- ADD NT-LWL 17/03/22 AliPay Payment TO -

    //-- ADD NT-LWL 17/03/28 AliPay Payment FR -
    public static final String CATEGORY_TOPBAR = "Topbar";
    public static final String CATEGORY_MENU = "Menu";
    public static final String CATEGORY_TOPPAGE = "TopPage";
    // 扫码成功事件对象
    public static final String CATEGORY_QRSCAN_OK = "QRscanOK";
    //-- ADD NT-LWL 17/07/25 GA追加 FR -
    // top分类点击对象
    public static final String CATEGORY_TOPCATEGORY = "TopCategory";
    //-- ADD NT-LWL 17/07/25 GA追加 TO -

    public static final String lable_menu = "menu";
    public static final String lable_live800 = "live800";
    public static final String lable_myParts = "myParts";
    public static final String lable_cart = "cart";
    public static final String lable_search = "search";
    public static final String lable_top = "top";
    public static final String lable_logout = "logout";
    public static final String lable_categorySearch = "categorySearch";
    public static final String lable_QThistory = "QThistory";
    public static final String lable_SOhistory = "SOhistory";
    public static final String lable_guide = "guide";
    public static final String lable_terms = "terms";
    public static final String lable_privacy = "privacy";
    public static final String lable_contact = "contact";
    public static final String lable_phoneInfo = "phoneInfo";
    public static final String lable_register = "register";

    public static final String lable_login = "login";
    public static final String lable_statusConfirm = "statusConfirm";
    public static final String lable_notice = "notice";

    // QR扫码 分享事件标签
    public static final String lable_QRscan = "QRscan";
    public static final String lable_qq = "QQ";
    public static final String lable_wechat = "WeChat";
    public static final String lable_mail = "mail";

    // 购物车 报价标签
    public static final String lable_cart_qt = "QTbutton";
    // 购物车 订购标签
    public static final String lable_cart_so = "SObutton";

    //-- ADD NT-LWL 17/03/28 AliPay Payment TO -
    /**
     * 发送点击事件
     * @param mTracker  跟踪器
     * @param screenId 画面
     * @param category  分类名称
     * @param label  标签
     */
    public static void sendAction(Tracker mTracker,String screenId , String category, String label){
        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null){
            AppConfig config = AppConfig.getInstance();
            //未登录时的默认值
            String userID = "None";
            // 客户code
            String customerCode = "None";
            String loginStatus ="N";
            if (config.hasSessionId()) {
                userID = config.getUserCode();
                customerCode = config.getCustomerCode();
                loginStatus = "Y";
            } else {
                loginStatus = "N";
            }
            mTracker.set("&uid", userID);
            mTracker.setScreenName(screenId);
            HitBuilders.EventBuilder build = new HitBuilders.EventBuilder()
                    .setCustomDimension(1,userID) // 定义纬度1 记录用户登录ID
                    .setCustomDimension(4,loginStatus) //定义纬度4 记录登录状态
                    .setCustomDimension(5,customerCode)// 定义纬度5 记录客户Code
                    .setCategory(category)
                    .setAction(ACTION_VIEW_CLICK)
                    .setLabel(label);
            mTracker.send(build.build());
            mTracker.setScreenName(null);
        }
    }

    /**
     * 产品检索事件跟踪
     * 方便后续找到检索相关画面 处理不同逻辑
     * @param mTracker 跟踪器
     * @param screenId 屏幕id
     * @param category 事件对象
     * @param label 事件标签
     */
    public static void sendProductTrack(Tracker mTracker,String screenId , String category, JSONObject label){
        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null) {
            AppConfig config = AppConfig.getInstance();
            //未登录时的默认值
            String userID = "None";
            // 客户code
            String customerCode = "None";
            String loginStatus = "N";
            if (config.hasSessionId()) {
                userID = config.getUserCode();
                customerCode = config.getCustomerCode();
                loginStatus = "Y";
            } else {
                loginStatus = "N";
            }
            mTracker.set("&uid", userID);
            mTracker.setScreenName(screenId);
            StringBuilder sb=new StringBuilder();
            try {
                sb.append("{\"clickSource\":");
                sb.append("\"");
                sb.append(label.get("clickSource"));
                sb.append("\",");

                if (label.has("partNumber")) {
                    sb.append("\"partNumber\":");
                    sb.append("\"");
                    sb.append(label.get("partNumber"));
                    sb.append("\",");
                }else if (label.has("keyword")){
                    sb.append("\"keyword\":");
                    sb.append("\"");
                    sb.append(label.get("keyword"));
                    sb.append("\",");
                }

                sb.append("\"totalCount\":");
                sb.append("\"");
                sb.append(label.get("totalCount"));
                sb.append("\",");

                sb.append("\"errorCode\":");
                sb.append("\"");
                sb.append(label.get("errorCode"));
                sb.append("\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HitBuilders.EventBuilder build = new HitBuilders.EventBuilder()
                    .setCustomDimension(1, userID) // 定义纬度1 记录用户登录ID
                    .setCustomDimension(4, loginStatus) //定义纬度4 记录登录状态
                    .setCustomDimension(5,customerCode)// 定义纬度5 记录客户Code
                    .setCategory(category)
                    .setAction(ACTION_VIEW_CLICK)
                    .setLabel(sb.toString());
            mTracker.send(build.build());
            mTracker.setScreenName(null);
        }
    }

    /**
     * 屏幕跟踪 需要纬度2、3的画面
     * @param mTracker 跟踪器
     * @param screenId 屏幕id
     * @param categoryCode 分类id
     * @param seriesCode 系列id
     */
    public static void sendScreenTrack(Tracker mTracker,String screenId,String categoryCode,String seriesCode){
        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null) {
            AppConfig config = AppConfig.getInstance();
            //未登录时的默认值
            String userID = "None";
            // 客户code
            String customerCode = "None";
            String loginStatus = "N";
            if (config.hasSessionId()) {
                userID = config.getUserCode();
                customerCode = config.getCustomerCode();
                loginStatus = "Y";
            } else {
                loginStatus = "N";
            }
            mTracker.set("&uid", userID);
            mTracker.setScreenName(screenId);
            HitBuilders.ScreenViewBuilder build = new HitBuilders.ScreenViewBuilder()
                    .setCustomDimension(1, userID) // 定义纬度1 记录用户登录ID
                    .setCustomDimension(4, loginStatus) //定义纬度4 记录登录状态
                    .setCustomDimension(5,customerCode);// 定义纬度5 记录客户Code
            if (!TextUtils.isEmpty(categoryCode)){
                //定义纬度2 记录分类Code
                build.setCustomDimension(2,categoryCode);
            }
            if (!TextUtils.isEmpty(seriesCode)){
                //定义纬度3 记录系列Code
                build.setCustomDimension(3,seriesCode);
            }
            mTracker.send(build.build());
            mTracker.setScreenName(null);
        }
    }

    /**
     * 屏幕跟踪 不需要纬度2、3的 画面
     * @param mTracker 跟踪器
     * @param screenId 屏幕id
     */
    public static void sendScreenTrack(Tracker mTracker,String screenId){
        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null) {
            AppConfig config = AppConfig.getInstance();
            //未登录时的默认值
            String userID = "None";
            // 客户code
            String customerCode = "None";
            String loginStatus = "N";
            if (config.hasSessionId()) {
                userID = config.getUserCode();
                customerCode = config.getCustomerCode();
                loginStatus = "Y";
            } else {
                loginStatus = "N";
            }
            mTracker.set("&uid", userID);
            mTracker.setScreenName(screenId);
            HitBuilders.ScreenViewBuilder build = new HitBuilders.ScreenViewBuilder()
                    .setCustomDimension(1, userID) // 定义纬度1 记录用户登录ID
                    .setCustomDimension(4, loginStatus) //定义纬度4 记录登录状态
                    .setCustomDimension(5,customerCode);// 定义纬度5 记录客户Code
            mTracker.send(build.build());
            mTracker.setScreenName(null);
        }
    }
    /**
     * MainHeader、Menu、toppage 等发送点击事件
     * @param mTracker  事件跟踪器
     * @param category  分类名称
     * @param label  标签
     */
    public static void sendAction(Tracker mTracker,String category, String label){
        if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null){
            AppConfig config = AppConfig.getInstance();
            //未登录时的默认值
            String userID = "None";
            // 客户code
            String customerCode = "None";
            String loginStatus ="N";
            if (config.hasSessionId()) {
                userID = config.getUserCode();
                customerCode = config.getCustomerCode();
                loginStatus = "Y";
            } else {
                loginStatus = "N";
            }
            mTracker.set("&uid", userID);
            mTracker.setScreenName(null);
            Map<String, String> build = new HitBuilders.EventBuilder()
                    .setCustomDimension(1,userID) // 定义纬度1 记录用户登录ID
                    .setCustomDimension(4,loginStatus) //定义纬度4 记录登录状态
                    .setCustomDimension(5,customerCode)// 定义纬度5 记录客户Code
                    .setCategory(category)
                    .setAction(ACTION_VIEW_CLICK)
                    .setLabel(label)
                    .build();
            mTracker.send(build);
            mTracker.setScreenName(null);
        }
    }
}
//-- ADD NT-LWL 16/12/14 AliPay Payment TO -