package jp.co.misumi.misumiecapp.api;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromCart;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromOrder;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromQuote;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromCart;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromQuote;
import jp.co.misumi.misumiecapp.data.RequestConfirmOrder;
import jp.co.misumi.misumiecapp.data.RequestConfirmQuotation;
import jp.co.misumi.misumiecapp.data.RequestGetOrder;
import jp.co.misumi.misumiecapp.data.RequestGetQuotation;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.RequestSearchOrder;
import jp.co.misumi.misumiecapp.data.RequestSearchQuotation;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseGetOrderDetail;
import jp.co.misumi.misumiecapp.data.ResponseGetQuotation;


/**
 * APIのパラメータ作成
 */
public class ApiBuilder {

    private static final String ENCODE_CHAR = "utf-8";

    public static final String BODY = "body";
    public static final String URL = "url";
    public static final String CONTENT_TYPE = "content-type";


    // NAS
    private static final String versionCheckApi = "versionInfo";
    private static final String getErrorMessageApi = "getError";
    private static final String getUrlListApi = "getUrlList";
    // 重要なお知らせ
    private static final String getInformationApi = "getInformation";

    // WEB
    private static final String loginApi = "sp/auth/login";
    private static final String logoutApi = "auth/logout";

    //API004 見積確認API
    //private static final String checkQuotation = "quotation/check";
    private static final String checkQuotationFromCart = "quotation/check/fromCart";
    private static final String checkQuotationFromQuotation = "quotation/check/fromQuotation";
    private static final String confirmQuotation = "quotation/confirm";

    //API005 注文確認API
    //private static final String checkOrder = "order/check2";
    private static final String checkOrderFromCart = "order/check/fromCart";
    private static final String checkOrderFromQuotation = "order/check/fromQuotation";
    private static final String checkOrderFromOrder = "order/check/fromOrder";
    private static final String confirmOrder = "order/confirm";

    //API018 見積履歴検索API
    private static final String searchQuotation = "quotation/search";
    private static final String getQuotation = "quotation"; // 0928再対応

    //API020 注文履歴検索API
    private static final String searchOrder = "order/search";
    private static final String getOrder = "order"; // 0928再対応

    //カートAPI
    private static final String getCartApi = "cart";
    private static final String getCartCountApi = "cart/count"; // 0928再対応
    private static final String updateCartApi = "cart/update"; // 0928再対応
    private static final String deleteCartApi = "cart/delete"; // 0928再対応
    private static final String addToCartApi = "cart/add";
    private static final String addToCartApiFromMyParts = "cart/add/fromMyComponents";
    private static final String addToCartApiFromOrder = "cart/add/fromOrder";
    private static final String addToCartApiFromQuotation = "cart/add/fromQuotation";

    private static final String searchCategory = "sp/category/search"; // 0928現在NG

    //得意先情報取得API
    //private static final String getUserInfo = "user"; // 0902版対応

    //My部品表取得
    private static final String getMyComponents = "myComponents"; // 0928再対応
    //private static final String updateMyComponents = "myComponents/update"; // 0928再対応
    //private static final String deleteMyComponents = "myComponents/delete"; // 0928再対応
    private static final String addToMyComponents = "myComponents/add"; // 0928再対応
    private static final String addToMyComponentsFromOrder = "myComponents/add/fromOrder";
    private static final String addToMyComponentsFromCart = "myComponents/add/fromCart"; // 12月再対応
    private static final String addToMyComponentsFromQuotation = "myComponents/add/fromQuotation";

    // ステータスチェック
    private static final String getAcceptStatus = "status/acceptStatus";

    //private static final String content_type_form = "application/x-www-form-encoded";
    private static final String content_type_json = "application/json";

    //シリーズ検索API
    private static final String getSeriesSearch = "series/search";
    private static final String getSpProduct = "sp/product";

    //キーワード検索API
    private static final String keywordSearchApi = "keyword/search";

    // サジェスト
    private static final String SearchSuggestApi = "keyword/suggest";

    //型番検索API
    //private static final String searchPartNumberApi = "partNumber/search";

    //価格チェックAPI
    //private static final String priceCheckApi = "price/check";

    //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
    private static final String getOnlinePayment = "charge/prepare";
    private static final String verifyApi = "charge/verify";
    //-- ADD NT-LWL 16/11/11 AliPay Payment TO -

    private ApiBuilder() {
        // スタティック関数として用意したのでインスタンス作成不要
    }

    private static String encode(String s) {
        try {

            return URLEncoder.encode(s, ENCODE_CHAR).replace("+", "%20").replace("*", "%2a").replace("-", "%2d");
        } catch (UnsupportedEncodingException e) {
            return s;
        } catch (NullPointerException e) {
            return s;
        }
    }

    //TODO:2015/10/21 sessionIdの扱いに付いてどうするか確認中
    private static String getUrlStringSub(String api, boolean checkLogin) {
        String url = AppConfig.getInstance().getApiBaseUrl() + api;
        url += "?subsidiaryCode=" + AppConst.subsidiaryCode;

        //セッションIDが無い場合はパラメータを付けない
        if (checkLogin) {
            if (AppConfig.getInstance() != null
                    && !isEmpty(AppConfig.getInstance().getSessionId())) {
                url += "&sessionId=" + AppConfig.getInstance().getSessionId();
            }
        } else {

            url += "&sessionId=" + AppConfig.getInstance().getSessionId();
        }

        url += "&applicationId=" + AppConst.AppID;
        return url;
    }

    private static String getUrlString(String api) {

        AppLog.d("api=" + api);
        return getUrlStringSub(api, false);
    }

    /**
     * createLogin
     *
     * @param loginid
     * @param loginPass
     * @return
     */
    public static HashMap<String, String> createLogin(String loginid, String loginPass) {

        AppConfig config = AppConfig.getInstance();

        StringBuilder body = new StringBuilder();
        String url = config.getApiBaseUrl() + loginApi;

        body.append("subsidiaryCode=");
        body.append(AppConst.subsidiaryCode);
        body.append("&loginId=");
        body.append(loginid);
        body.append("&password=");
        body.append(loginPass);
        body.append("&applicationId=");
        body.append(AppConst.AppID);

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createLogout
     *
     * @return
     */
    public static HashMap<String, String> createLogout() {

        AppConfig config = AppConfig.getInstance();

        StringBuilder body = new StringBuilder();
        String url = config.getApiBaseUrl() + logoutApi;

        body.append("subsidiaryCode=");
        body.append(AppConst.subsidiaryCode);
        body.append("&sessionId=");
        body.append(config.getSessionId());
        body.append("&applicationId=");
        body.append(AppConst.AppID);

        HashMap<String, String> result = new HashMap<>();

        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;

    }

//    /**
//     * createCheckPrice
//     * @param partNumber
//     * @param quantity
//     * @return
//     */
//    public static HashMap<String, String> createCheckPrice(String partNumber, int quantity){
//
//
//        String url = getUrlString(searchPartNumberApi);
//
//        JSONObject body = new JSONObject();
//        JSONArray product_list = new JSONArray();
//        JSONObject product = new JSONObject();
//
//        try {
//
//            product.put("partNumber", partNumber);
//            product.put("quantity", quantity);
//            product_list.put(product);
//
//            body.put("productList", product_list);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        HashMap<String, String> result = new HashMap<>();
//
//        result.put(CONTENT_TYPE, content_type_json);
//        result.put(URL, url);
//        result.put(BODY, body.toString());
//
//        return result;
//    }


    /**
     * createVersionCheck
     *
     * @return
     */
    public static HashMap<String, String> createVersionCheck() {


        String url = AppConfig.getInstance().getNasBaseUrl() + versionCheckApi;

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, url);

        return result;
    }

    /**
     * getErrorMessage
     *
     * @return
     */
    public static HashMap<String, String> getErrorMessage() {

        String url = AppConfig.getInstance().getNasBaseUrl() + getErrorMessageApi;

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, url);

        return result;
    }

    /**
     * getUrlList
     *
     * @return
     */
    public static HashMap<String, String> getUrlList() {

        String url = AppConfig.getInstance().getNasBaseUrl() + getUrlListApi;

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, url);

        return result;
    }

    /**
     * createGetCart
     *
     * @return
     */
    public static HashMap<String, String> createGetCart() {

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(getCartApi));

        return result;
    }

    /**
     * createGetCartCount
     *
     * @return
     */
    public static HashMap<String, String> createGetCartCount() {

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(getCartCountApi));

        return result;
    }


    /**
     * createUpdateCart
     *
     * @param cartid_quantity
     * @return
     */
    public static HashMap<String, String> createUpdateCart(Map<String, Integer> cartid_quantity) {

        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(updateCartApi));

        JSONObject body = new JSONObject();

        try {
            JSONArray product_list = new JSONArray();
            for (Map.Entry<String, Integer> entry : cartid_quantity.entrySet()) {
                JSONObject product = new JSONObject();
                product.put("cartId", entry.getKey());
                product.put("quantity", entry.getValue());
                product_list.put(product);
            }
            body.put("cartItemList", product_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createDeleteCart
     *
     * @param cardids
     * @return
     */
    public static HashMap<String, String> createDeleteCart(List<String> cardids) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(deleteCartApi));


        JSONObject body = new JSONObject();


        try {
            JSONArray product_list = new JSONArray();
            for (String cardid : cardids) {
                JSONObject product = new JSONObject();
                product.put("cartId", cardid);
                product_list.put(product);
            }
            body.put("cartItemList", product_list);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToCart
     *
     * @param itemInfo
     * @return
     */
    public static HashMap<String, String> createAddToCartFromMyParts(ResponseGetMyComponents.Product itemInfo) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApiFromMyParts));


        JSONObject body = new JSONObject();
        try {
            JSONArray product_list = new JSONArray();
//            for(Map.Entry<String, Integer> entry : cartid_quantity.entrySet()) {
            JSONObject product = new JSONObject();
            product.put("componentId", itemInfo.componentId);
            product.put("quantity", itemInfo.quantity);

            product_list.put(product);
//            }

            body.put("cartItemList", product_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToCartFromOrder 12月版
     *
     * @param itemInfo
     * @return
     */
    public static HashMap<String, String> createAddToCartFromOrder(String orderSlipNo, ResponseGetOrderDetail.ItemInfo itemInfo) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApiFromOrder));

        JSONObject body = new JSONObject();
        try {
            body.put("orderSlipNo", orderSlipNo);

            JSONArray product_list = new JSONArray();
            JSONObject product = new JSONObject();
            product.put("orderItemNo", itemInfo.orderItemNo);
            product_list.put(product);

            body.put("orderItemList", product_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    public static HashMap<String, String> createAddToCartFromQuotation(String quotationSlipNo, ResponseGetQuotation.ItemInfo itemInfo) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApiFromQuotation));


        JSONObject body = new JSONObject();
        try {
            JSONArray item_list = new JSONArray();
            JSONObject itemNo = new JSONObject();

            body.put("quotationSlipNo", quotationSlipNo);
            itemNo.put("quotationItemNo", itemInfo.quotationItemNo);
            item_list.put(itemNo);

            body.put("quotationItemList", item_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }


    public static HashMap<String, String> createAddToCart(String brandCode, String partNumber, int quantity) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApi));
        JSONObject body = new JSONObject();

        try {
            JSONArray list = new JSONArray();
            JSONObject object = new JSONObject();

            object.put("brandCode", brandCode);
            object.put("partNumber", partNumber);
            object.put("quantity", quantity);
            list.put(object);

            body.put("cartItemList", list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToCart 12/10版 商品詳細から
     *
     * @return
     */
    public static HashMap<String, String> createAddToCart(String brandCode, String seriesCode, String partNumber, int quantity) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApi));
        JSONObject body = new JSONObject();

        try {
            JSONArray list = new JSONArray();
            JSONObject object = new JSONObject();

            object.put("brandCode", brandCode);
            //seriesCodeはオプションなので空チェックする
            if (!isEmpty(seriesCode)) {
                object.put("seriesCode", seriesCode);
            }
            object.put("partNumber", partNumber);
            object.put("quantity", quantity);
            list.put(object);

            body.put("cartItemList", list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }
    //-- ADD NT-LWL 17/08/14 Depo FR -

    /**
     * 添加多个商品进购物车
     *
     * @param cartList
     * @return
     */
    public static HashMap<String, String> createAddToCart(String cartList) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToCartApi));
        JSONObject body = new JSONObject();

        try {
            JSONArray list = new JSONArray(cartList);

            body.put("cartItemList", list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }
    //-- ADD NT-LWL 17/08/14 Depo TO -

//    /**
//     * 見積確認API 2015/08/28版
//     * @param request
//     * @return
//     */
//    public static HashMap<String, String> createCheckQuotation(RequestCheckQuotation request){
//
//
//        String url = getUrlString(checkQuotation);
//        url += "&field=@all";
//
//        JSONObject body = new JSONObject();
//        JSONArray json_list = new JSONArray();
//
//        try {
//            body.put("customerOrderNo", request.customerOrderNo);
//            body.put("userName", request.userName);
//            body.put("userDepartmentName", request.userDepartmentName);
//            body.put("receiverCode", request.receiverCode);
//            body.put("receiverUserName", request.receiverUserName);
//            body.put("receiverDepartmentName", request.receiverDepartmentName);
//            body.put("deliveryType", request.deliveryType);
//            body.put("quotationDate", request.quotationDate);
//            body.put("campaignBaseDate", request.campaignBaseDate);
//
//            for (int idx=0; idx<request.mItemList.size(); ++idx) {
//
//                RequestCheckQuotation.ItemInfo item = request.mItemList.get(idx);
//                JSONObject json = new JSONObject();
//
//                json.put("customerOrderItemNo", item.customerOrderItemNo);
//                json.put("customerOrderItemSubNo", item.customerOrderItemSubNo);
//                json.put("brandCode", item.brandCode);
//                json.put("partNumber", item.partNumber);
//                json.put("productName", item.productName);
//                json.put("quantity", item.quantity);
//                //ストーク未設定の初回呼び出し時は JSONキーを付与しない
//                if (!isEmpty(item.expressType)) {
//                    json.put("expressType", item.expressType);	//　ストーク
//                }
//                json.put("requestDeliveryDate", item.requestDeliveryDate);
//                json_list.put(json);
//            }
//
//            body.put("quotationItemList", json_list);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        HashMap<String, String> result = new HashMap<>();
//
//        result.put(CONTENT_TYPE, content_type_json);
//        result.put(URL, url);
//        result.put(BODY, body.toString());
//
//        return result;
//    }


    //2015/10/27 実装
    public static HashMap<String, String> createCheckQuotationFromQuote(RequestCheckQuotationFromQuote request) {

        String url = getUrlString(checkQuotationFromQuotation);
        url += "&field=@all";

        JSONObject body = new JSONObject();
        JSONArray json_list = new JSONArray();

        try {
            body.put("receptionCode", request.receptionCode);
//            body.put("billingUserName", request.billingUserName);
//            body.put("billingDepartmentName", request.billingDepartmentName);
//            body.put("receiverCode", request.receiverCode);
//            body.put("receiverUserName", request.receiverUserName);
//            body.put("receiverDepartmentName", request.receiverDepartmentName);
            //
            // 請求先担当名
            if (null != request.billingUserName) {
                body.put("billingUserName", request.billingUserName);
            }
            // 請求先部課名
            if (null != request.billingDepartmentName) {
                body.put("billingDepartmentName", request.billingDepartmentName);
            }
            //　直送先コード
            if (null != request.receiverCode) {
                body.put("receiverCode", request.receiverCode);
            }
            // 直送先担当名
            if (null != request.receiverUserName) {
                body.put("receiverUserName", request.receiverUserName);
            }
            // 直送先部課名
            if (null != request.receiverDepartmentName) {
                body.put("receiverDepartmentName", request.receiverDepartmentName);
            }
            //

            body.put("shipOption", request.shipOption);
            body.put("resolveOutstockFlag", request.resolveOutstockFlag);
            body.put("resolveErrorPassOnFlag", request.resolveErrorPassOnFlag);

            //expressADiscountFlag (早割Aフラグ)
            if (!isEmpty(request.expressADiscountFlag)) {
                body.put("expressADiscountFlag", request.expressADiscountFlag);
            }

            if (request.mItemList != null) {

                for (int idx = 0; idx < request.mItemList.size(); ++idx) {

                    RequestCheckQuotationFromQuote.ItemInfo item = request.mItemList.get(idx);
                    JSONObject json = new JSONObject();

                    json.put("quotationItemNo", item.quotationItemNo);
                    json.put("todayShipFlag", item.todayShipFlag);
                    //ストーク未設定の初回呼び出し時は JSONキーを付与しない
                    if (!isEmpty(item.expressType)) {
                        json.put("expressType", item.expressType);    //　ストーク
                    }

                    json_list.put(json);
                }

                if (json_list.length() > 0) {
                    body.put("quotationItemList", json_list);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * 見積確認API 2015/08/28版
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createCheckQuotationFromCart(RequestCheckQuotationFromCart request) {


        String url = getUrlString(checkQuotationFromCart);
        url += "&field=@all";

        JSONObject body = new JSONObject();
        JSONArray json_list = new JSONArray();

        try {

            for (int idx = 0; idx < request.mItemList.size(); ++idx) {

                RequestCheckQuotationFromCart.ItemInfo item = request.mItemList.get(idx);
                JSONObject json = new JSONObject();

                json.put("cartId", item.cartId);
                json.put("quantity", item.quantity);
                //ストーク未設定の初回呼び出し時は JSONキーを付与しない
//                if (!isEmpty(item.expressType)) {
//                    json.put("expressType", item.expressType);	//　ストーク
//                }
                json_list.put(json);
            }

            body.put("quotationItemList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * 見積確定API 2015/10/23版
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createConfirmQuotation(RequestConfirmQuotation request) {


        String url = getUrlString(confirmQuotation);
        url += "&field=@all";

        JSONObject body = new JSONObject();
//        JSONArray json_list = new JSONArray();

        try {
            body.put("receptionCode", request.receptionCode);
//            body.put("billingUserName", request.billingUserName);
//            body.put("billingDepartmentName", request.billingDepartmentName);
//            body.put("receiverCode", request.receiverCode);
//            body.put("receiverUserName", request.receiverUserName);
//            body.put("receiverDepartmentName", request.receiverDepartmentName);
            //
            // 請求先担当名
            if (null != request.billingUserName) {
                body.put("billingUserName", request.billingUserName);
            }
            // 請求先部課名
            if (null != request.billingDepartmentName) {
                body.put("billingDepartmentName", request.billingDepartmentName);
            }
            //　直送先コード
            if (null != request.receiverCode) {
                body.put("receiverCode", request.receiverCode);
            }
            // 直送先担当名
            if (null != request.receiverUserName) {
                body.put("receiverUserName", request.receiverUserName);
            }
            // 直送先部課名
            if (null != request.receiverDepartmentName) {
                body.put("receiverDepartmentName", request.receiverDepartmentName);
            }
            //

//            for (int idx=0; idx<request.mItemList.size(); ++idx) {
//
//                RequestConfirmQuotation.ItemInfo item = request.mItemList.get(idx);
//                JSONObject json = new JSONObject();
//
//                json.put("quotationItemNo", item.quotationItemNo);
//                json.put("expressType", item.expressType);
//
//                json_list.put(json);
//            }
//
//            body.put("quotationItemList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * 注文確認API 2015/08/28版
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createCheckOrderFromCart(RequestCheckOrderFromCart request) {


        String url = getUrlString(checkOrderFromCart);
        url += "&field=@all";

        JSONObject body = new JSONObject();
        JSONArray json_list = new JSONArray();

        try {

            for (int idx = 0; idx < request.mItemList.size(); ++idx) {

                RequestCheckOrderFromCart.ItemInfo item = request.mItemList.get(idx);
                JSONObject json = new JSONObject();

                json.put("cartId", item.cartId);
                json.put("quantity", item.quantity);

                json_list.put(json);
            }

            body.put("orderItemList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * 注文確認API 2015/08/28版
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createCheckOrderFromQuote(RequestCheckOrderFromQuote request) {


        String url = getUrlString(checkOrderFromQuotation);
        url += "&field=@all";

        JSONObject body = new JSONObject();
        JSONArray json_list = new JSONArray();

        try {

            body.put("quotationSlipNo", request.quotationSlipNo);

            if (request.mItemList != null) {

                for (int idx = 0; idx < request.mItemList.size(); ++idx) {

                    RequestCheckOrderFromQuote.ItemInfo item = request.mItemList.get(idx);
                    JSONObject json = new JSONObject();

                    json.put("quotationItemNo", item.quotationItemNo);

                    json_list.put(json);
                }

                if (json_list.length() > 0) {
                    body.put("quotationItemList", json_list);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * 注文確認API 2015/08/28版
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createCheckOrderFromOrder(RequestCheckOrderFromOrder request) {

        String url = getUrlString(checkOrderFromOrder);
        url += "&field=@all";

        JSONObject body = new JSONObject();
        JSONArray json_list = new JSONArray();

        try {
            // 受付番号
            if (!isEmpty(request.receptionCode)) {
                body.put("receptionCode", request.receptionCode);
            }

            //
            // 請求先担当名
            if (null != request.billingUserName) {
                body.put("billingUserName", request.billingUserName);
            }
            // 請求先部課名
            if (null != request.billingDepartmentName) {
                body.put("billingDepartmentName", request.billingDepartmentName);
            }
            //　直送先コード
            if (null != request.receiverCode) {
                body.put("receiverCode", request.receiverCode);
            }
            // 直送先担当名
            if (null != request.receiverUserName) {
                body.put("receiverUserName", request.receiverUserName);
            }
            // 直送先部課名
            if (null != request.receiverDepartmentName) {
                body.put("receiverDepartmentName", request.receiverDepartmentName);
            }
            //

            // 出荷オプション
            if (!isEmpty(request.shipOption)) {
                body.put("shipOption", request.shipOption);
            }
            // 在庫切れ解消フラグ
            if (!isEmpty(request.resolveOutstockFlag)) {
                body.put("resolveOutstockFlag", request.resolveOutstockFlag);
            }

            // 素通しエラー解消フラグ
            if (!isEmpty(request.resolveErrorPassOnFlag)) {
                body.put("resolveErrorPassOnFlag", request.resolveErrorPassOnFlag);
            }
            //-- ADD NT-LWL 16/12/05 AliPay Payment FR -
            //決済グループ
            if (!isEmpty(request.paymentGroup)) {
                body.put("paymentGroup", request.paymentGroup);
            }
            // -- ADD NT-LWL 16/12/05 AliPay Payment TO -
            if (request.mItemList != null) {
                for (int idx = 0; idx < request.mItemList.size(); ++idx) {
                    RequestCheckOrderFromOrder.ItemInfo item = request.mItemList.get(idx);
                    JSONObject json = new JSONObject();
                    if (item.orderItemNo != null) {
                        json.put("orderItemNo", item.orderItemNo);
                    }
                    if (!isEmpty(item.todayShipFlag)) {
                        json.put("todayShipFlag", item.todayShipFlag);
                    }
                    if (!isEmpty(item.expressType)) {
                        json.put("expressType", item.expressType);
                    }
                    json_list.put(json);
                }

                if (json_list.length() > 0) {
                    body.put("orderItemList", json_list);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * createConfirmOrder
     *
     * @param request
     * @return
     */
    public static HashMap<String, String> createConfirmOrder(RequestConfirmOrder request) {


        String url = getUrlString(confirmOrder);
        url += "&field=@all";

        JSONObject body = new JSONObject();
//        JSONArray json_list = new JSONArray();

        // TODO:送信データの生成が暫定
        try {

            body.put("receptionCode", request.receptionCode);
//            body.put("billingUserName", request.billingUserName);
//            body.put("billingDepartmentName", request.billingDepartmentName);
//            body.put("receiverCode", request.receiverCode);
//            body.put("receiverUserName", request.receiverUserName);
//            body.put("receiverDepartmentName", request.receiverDepartmentName);
            //
            // 請求先担当名
            if (null != request.billingUserName) {
                body.put("billingUserName", request.billingUserName);
            }
            // 請求先部課名
            if (null != request.billingDepartmentName) {
                body.put("billingDepartmentName", request.billingDepartmentName);
            }
            //　直送先コード
            if (null != request.receiverCode) {
                body.put("receiverCode", request.receiverCode);
            }
            // 直送先担当名
            if (null != request.receiverUserName) {
                body.put("receiverUserName", request.receiverUserName);
            }
            // 直送先部課名
            if (null != request.receiverDepartmentName) {
                body.put("receiverDepartmentName", request.receiverDepartmentName);
            }
            //

            body.put("shipOption", request.shipOption);
            body.put("quotationConvertFlag", request.quotationConvertFlag);

            //アンフィット素通しフラグ
            body.put("unfitPassOnFlag", request.unfitPassOnFlag);

            //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
            // 支付方式
            if (null != request.paymentGroup) {
                body.put("paymentGroup", request.paymentGroup);
            }
            //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

//            for (int idx=0; idx<request.mItemList.size(); ++idx) {
//
//                RequestConfirmOrder.ItemInfo item = request.mItemList.get(idx);
//                JSONObject json = new JSONObject();
//
//                json.put("orderItemNo", item.orderItemNo);
//                json.put("expressType", item.expressType);
//                json.put("requestShipDate", item.requestShipDate);
//
//                json_list.put(json);
//            }
//
//            body.put("orderItemList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> result = new HashMap<>();

        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    /**
     * createSearchCategory
     *
     * @param categoryCode
     * @param categoryLevel
     * @return
     */
    public static HashMap<String, String> createSearchCategory(String categoryCode, Integer categoryLevel) {

        return createSearchCategory(categoryCode, categoryLevel, true);
    }


    public static HashMap<String, String> createSearchCategory(String categoryCode, Integer categoryLevel, boolean checkDate) {
        HashMap<String, String> result = new HashMap<>();

        String url = getUrlString(searchCategory);

        // updateDateTime
        if (checkDate) {
            String updateDateTime = AppConfig.getInstance().getCategoryUpdateTime();
            if (updateDateTime != null && updateDateTime.length() != 0) {
                url += "&updateDateTime=" + encode(updateDateTime);

            }
        }

        // categoryCode
        if (categoryCode != null) {
            url += "&categoryCode=" + encode(categoryCode);
        }

        // categoryLevel
        if (categoryLevel != null) {
            url += "&categoryLevel=" + categoryLevel;
        } else {
            url += "&categoryLevel=0";
        }

        result.put(URL, url);
//        result.put(CONTENT_TYPE, content_type_form);

        return result;
    }


    //見積履歴検索
    public static HashMap<String, String> createSearchQuotation(RequestSearchQuotation request) {

        String url = getUrlString(searchQuotation);
        url += "&field=@all";

        //URL引数
        if (!isEmpty(request.userCode)) {
            url += "&userCode=" + encode(request.userCode);
        }
        if (!isEmpty(request.quotationSlipNo)) {
            url += "&quotationSlipNo=" + encode(request.quotationSlipNo);
        }
        if (!isEmpty(request.dateFrom)) {
            url += "&dateFrom=" + encode(request.dateFrom);
        }
        if (!isEmpty(request.dateTo)) {
            url += "&dateTo=" + encode(request.dateTo);
        }
        if (!isEmpty(request.page)) {
            url += "&page=" + request.page;
        }
        if (!isEmpty(request.pageSize)) {
            url += "&pageSize=" + request.pageSize;
        }
        if (!isEmpty(request.sort)) {
            url += "&sort=" + request.sort;
        }

        JSONObject body = new JSONObject();
        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    //見積履歴詳細
    public static HashMap<String, String> createGetQuotation(RequestGetQuotation request) {

        String url = getUrlString(getQuotation);
        url += "&field=@all";

        //URL引数
        url += "&quotationSlipNo=" + encode(request.quotationSlipNo);

        JSONObject body = new JSONObject();
        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    //注文履歴検索
    public static HashMap<String, String> createSearchOrder(RequestSearchOrder request) {


        String url = getUrlString(searchOrder);
        url += "&field=@all";

        //URL引数
        if (!isEmpty(request.userCode)) {
            url += "&userCode=" + encode(request.userCode);
        }
        if (!isEmpty(request.orderSlipNo)) {
            url += "&orderSlipNo=" + encode(request.orderSlipNo);
        }
        if (!isEmpty(request.dateFrom)) {
            url += "&dateFrom=" + encode(request.dateFrom);
        }
        if (!isEmpty(request.dateTo)) {
            url += "&dateTo=" + encode(request.dateTo);
        }
        if (!isEmpty(request.page)) {
            url += "&page=" + request.page;
        }
        if (!isEmpty(request.pageSize)) {
            url += "&pageSize=" + request.pageSize;
        }
        if (!isEmpty(request.sort)) {
            url += "&sort=" + request.sort;
        }

        JSONObject body = new JSONObject();
        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


    //注文履歴詳細
    public static HashMap<String, String> createGetOrder(RequestGetOrder request) {

        String url = getUrlString(getOrder);
        url += "&field=@all";

        //URL引数
        url += "&orderSlipNo=" + encode(request.orderSlipNo);

        JSONObject body = new JSONObject();
        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);
        result.put(BODY, body.toString());

        return result;
    }


//    //得意先情報取得
//    public static HashMap<String, String> createGetUserInfo(){
//
//
//        String url = getUrlString(getUserInfo);
//        url += "&field=@all";
//
//        //URL引数
//        JSONObject body = new JSONObject();
//        HashMap<String, String> result = new HashMap<>();
//
//        result.put(CONTENT_TYPE, content_type_json);
//        result.put(URL, url);
//        result.put(BODY, body.toString());
//
//        return result;
//    }


    //My部品表取得
    public static HashMap<String, String> createGetMyComponents(String folderId, String sort) {

        HashMap<String, String> result = new HashMap<>();

        //2015/08/28版
        JSONObject body = new JSONObject();

        String url = getUrlString(getMyComponents);
        url += "&field=@all";

        //URL引数
        url += "&folderId=" + encode(folderId);
        url += "&sort=" + sort;
        //	My部品表ID componentId String 特定のIDの明細のみを取得する場合に指定
        result.put(URL, url);

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }


//    /**
//     * createUpdateMyComponents
//     * @param updateList
//     * @return
//     */
//    public static HashMap<String, String> createUpdateMyComponents(Map<String, Integer> updateList){
//        HashMap<String, String> result = new HashMap<>();
//        result.put(URL, getUrlString(updateMyComponents));
//
//        //2015/08/28版
//        JSONObject body = new JSONObject();
//
//        try {
//            JSONArray jsonList = new JSONArray();
//            for(Map.Entry<String, Integer> entry : updateList.entrySet()) {
//                JSONObject item = new JSONObject();
//                item.put("componentId", entry.getKey());
//                item.put("quantity", entry.getValue());
//                jsonList.put(item);
//            }
//            body.put("componentList", jsonList);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        result.put(CONTENT_TYPE, content_type_json);
//        result.put(BODY, body.toString());
//
//        return result;
//    }


    //シリーズ検索
    public static HashMap<String, String> createGetSeries(String categoryCode, Integer page, Integer pageSize) {

        return createGetSeries(categoryCode, null, null, false, page, pageSize);
    }


    //シリーズ検索
    public static HashMap<String, String> createGetSeries(String categoryCode, String seriesCode, String innerCode, boolean requestPrice, Integer page, Integer pageSize) {

        String url = getUrlString(getSeriesSearch);
        if (!isEmpty(categoryCode)) {
            url += "&categoryCode=" + encode(categoryCode);
        }
        if (!isEmpty(seriesCode)) {
            url += "&seriesCode=" + encode(seriesCode);
        }
        if (!isEmpty(innerCode)) {
            url += "&innerCode=" + encode(innerCode);
        }
//        url += "&requestPrice=" + (requestPrice? "1": "0");	//価格情報要求
        url += "&page=" + page;
        url += "&pageSize=" + pageSize;

        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);

        return result;
    }


    //SP10_API外部設計書(スマホ用商品詳細API)_20150918
    public static HashMap<String, String> createGetSpProduct(String completeType, String seriesCode, String innerCode, String partNumber, Integer quantity, Integer page, Integer pageSize) {

        //セッションIDが無い場合にサーバの動作がエラーになるのでパラメータを付けない
        String url = getUrlStringSub(getSpProduct, true);
        url += "&field=@all";

        if (!isEmpty(completeType)) {
            url += "&completeType=" + encode(completeType);
        }

        if (!isEmpty(seriesCode)) {
            url += "&seriesCode=" + encode(seriesCode);
        }

        if (!isEmpty(innerCode)) {
            url += "&innerCode=" + encode(innerCode);
        }

        boolean requestPrice;
        if (!isEmpty(partNumber)) {

            url += "&partNumber=" + encode(partNumber);
            url += "&getPartNumberFlag=" + "1";
            requestPrice = true;    //フラグ
        } else {

            url += "&getPartNumberFlag=" + "1";
            requestPrice = false;    //フラグ
        }

        url += "&checkPriceFlag=" + (requestPrice ? "1" : "0");    //価格情報要求

        if (quantity == null) {
            quantity = 1;
        }
        url += "&quantity=" + quantity;

        //
        url += "&page=" + page;
        url += "&pageSize=" + pageSize;

        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);

        return result;
    }

    //--ADD NT-LWL 17/06/26 Share FR -
    // sessionId动态传入
    public static HashMap<String, String> createGetSpProduct(String completeType, String seriesCode, String innerCode, String partNumber, Integer quantity, Integer page, Integer pageSize, String sessionId) {

        //セッションIDが無い場合にサーバの動作がエラーになるのでパラメータを付けない
        String url = getUrlStringSub(getSpProduct, sessionId);
        url += "&field=@all";

        if (!isEmpty(completeType)) {
            url += "&completeType=" + encode(completeType);
        }

        if (!isEmpty(seriesCode)) {
            url += "&seriesCode=" + encode(seriesCode);
        }

        if (!isEmpty(innerCode)) {
            url += "&innerCode=" + encode(innerCode);
        }

        boolean requestPrice;
        if (!isEmpty(partNumber)) {

            url += "&partNumber=" + encode(partNumber);
            url += "&getPartNumberFlag=" + "1";
            requestPrice = true;    //フラグ
        } else {

            url += "&getPartNumberFlag=" + "1";
            requestPrice = false;    //フラグ
        }

        url += "&checkPriceFlag=" + (requestPrice ? "1" : "0");    //価格情報要求

        if (quantity == null) {
            quantity = 1;
        }
        url += "&quantity=" + quantity;

        //
        url += "&page=" + page;
        url += "&pageSize=" + pageSize;

        HashMap<String, String> result = new HashMap<>();

//        result.put(CONTENT_TYPE, content_type_json);
        result.put(URL, url);

        return result;
    }

    // sessionId动态传入
    private static String getUrlStringSub(String api, String sessionId) {
        String url = AppConfig.getInstance().getApiBaseUrl() + api;
        url += "?subsidiaryCode=" + AppConst.subsidiaryCode;

        //セッションIDが無い場合はパラメータを付けない
        if (!isEmpty(sessionId)) {
            url += "&sessionId=" + sessionId;
        }
        url += "&applicationId=" + AppConst.AppID;
        return url;
    }
    //--ADD NT-LWL 17/06/26 Share TO -

//    /**
//     * createDeleteMyComponents
//     * @param deleteList
//     * @return
//     */
//    public static HashMap<String, String> createDeleteMyComponents(List<String> deleteList){
//        HashMap<String, String> result = new HashMap<>();
//        result.put(URL, getUrlString(deleteMyComponents));
//
//        //2015/08/28版
//        JSONObject body = new JSONObject();
//
//        try {
//            JSONArray jsonList = new JSONArray();
//            for(String itemId : deleteList) {
//                JSONObject item = new JSONObject();
//                item.put("componentId", itemId);
//                jsonList.put(item);
//            }
//            body.put("componentList", jsonList);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        result.put(CONTENT_TYPE, content_type_json);
//        result.put(BODY, body.toString());
//
//        return result;
//    }

    /**
     * createAddToMyComponents
     *
     * @return
     */
    public static HashMap<String, String> createAddToMyComponents(String brandCode, String partNumber, int quantity) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToMyComponents));
        JSONObject body = new JSONObject();

        try {
            JSONArray json_list = new JSONArray();
            JSONObject object = new JSONObject();

            object.put("brandCode", brandCode);
            object.put("partNumber", partNumber);
            object.put("quantity", quantity);
            json_list.put(object);

            body.put("componentList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToMyComponentsFromOrder 12月版
     *
     * @return
     */
    public static HashMap<String, String> createAddToMyComponentsFromOrder(String orderSlipNo, ResponseGetOrderDetail.ItemInfo itemInfo) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToMyComponentsFromOrder));
        JSONObject body = new JSONObject();

        try {
            body.put("orderSlipNo", orderSlipNo);

            JSONArray product_list = new JSONArray();
            JSONObject product = new JSONObject();
            product.put("orderItemNo", itemInfo.orderItemNo);
            product_list.put(product);

            body.put("orderItemList", product_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToMyComponentsFromQuotation 12月版
     *
     * @return
     */
    public static HashMap<String, String> createAddToMyComponentsFromQuotation(String quotationSlipNo, ResponseGetQuotation.ItemInfo itemInfo) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToMyComponentsFromQuotation));
        JSONObject body = new JSONObject();

        try {
            body.put("quotationSlipNo", quotationSlipNo);

            JSONArray product_list = new JSONArray();
            JSONObject product = new JSONObject();
            product.put("quotationItemNo", itemInfo.quotationItemNo);
            product_list.put(product);

            body.put("quotationItemList", product_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToMyComponents 12/10版 商品詳細から
     *
     * @return
     */
    public static HashMap<String, String> createAddToMyComponents(String brandCode, String seriesCode, String partNumber, int quantity) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToMyComponents));
        JSONObject body = new JSONObject();

        try {
            JSONArray json_list = new JSONArray();
            JSONObject object = new JSONObject();

            object.put("brandCode", brandCode);
            object.put("partNumber", partNumber);
            //seriesCodeはオプションなので空チェックする
            if (!isEmpty(seriesCode)) {
                object.put("seriesCode", seriesCode);
            }
            object.put("quantity", quantity);
            json_list.put(object);

            body.put("componentList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    /**
     * createAddToMyComponentsFromCart 12月版
     *
     * @param cartId
     * @param quantity
     * @return
     */
    public static HashMap<String, String> createAddToMyComponentsFromCart(String cartId, int quantity) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(addToMyComponentsFromCart));
        JSONObject body = new JSONObject();

        try {
            JSONArray json_list = new JSONArray();
            JSONObject object = new JSONObject();

            object.put("cartId", cartId);
            object.put("quantity", quantity);
            json_list.put(object);

            body.put("componentList", json_list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }

    //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
    public static HashMap<String, String> createGetOnlinePayment(String callerPage, String orderSlipNo, String paymentType) {
        HashMap<String, String> result = new HashMap<>();
        String url = getUrlString(getOnlinePayment);
        url += "&field=@default";
        //URL引数
        if (!isEmpty(callerPage)) {
            url += "&callerType=" + callerPage;
        }
        if (!isEmpty(orderSlipNo)) {
            url += "&orderSlipNo=" + orderSlipNo;
        }
        if (!isEmpty(paymentType)) {
            url += "&paymentType=" + paymentType;
        }
        result.put(URL, url);

        return result;
    }


    public static HashMap<String, String> createVerifyApi(String paymentType, String mAlipay_trade_app_pay_result) {
        HashMap<String, String> result = new HashMap<>();
        result.put(URL, getUrlString(verifyApi) + "&field=@default");
        JSONObject body = new JSONObject();

        try {
            body.put("paymentType", paymentType);
//            if(mAlipay_trade_app_pay_result!=null) {
//                JSONObject object = new JSONObject();
//                if(mAlipay_trade_app_pay_result.sign!=null) {
//                    object.put("sign", mAlipay_trade_app_pay_result.sign);
//                }
//                if(mAlipay_trade_app_pay_result.response!=null) {
//                    object.put("response",mAlipay_trade_app_pay_result.response);
//                }
//                body.put("alipay", object);
//            }
            if (!mAlipay_trade_app_pay_result.isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("response", mAlipay_trade_app_pay_result);
                body.put("alipay", object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put(CONTENT_TYPE, content_type_json);
        result.put(BODY, body.toString());

        return result;
    }
    //-- ADD NT-LWL 16/11/11 AliPay Payment TO -


    /**
     * createStatusCheck
     *
     * @return
     */
    public static HashMap<String, String> createStatusCheck() {
        HashMap<String, String> result = new HashMap<>();

        String url = getUrlString(getAcceptStatus);
        result.put(URL, url);
        return result;
    }

    /**
     * createInformation
     *
     * @return
     */
    public static HashMap<String, String> createInformation() {
        HashMap<String, String> result = new HashMap<>();

        String url = AppConfig.getInstance().getNasBaseUrl() + getInformationApi;
        result.put(URL, url);
        return result;
    }

    //キーワード検索
    public static HashMap<String, String> createKeywordSearch(RequestKeywordSearch keywordSearch) {

        return createKeywordSearchSub(keywordSearch, false);
    }

    public static HashMap<String, String> createKeywordSearchDefaut(RequestKeywordSearch keywordSearch) {

        return createKeywordSearchSub(keywordSearch, true);
    }

    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    // 关键字 品牌 检索
    public static HashMap<String, String> createKeywordBrandSearch(RequestKeywordSearch keywordSearch, boolean isDefault) {
        HashMap<String, String> result = new HashMap<>();
        String url = getUrlString(keywordSearchApi);
        if (isDefault) {
            url += "&field=@default";
        } else {
            url += "&field=@all";
        }

        //URL引数
        url += "&keyword=" + encode(keywordSearch.keyword);
        url += "&brandCode=" + encode(keywordSearch.brandCode);
        if (!TextUtils.isEmpty(keywordSearch.categoryCode)) {
            url += "&categoryCode=" + keywordSearch.categoryCode;
        }
        url += "&page=" + keywordSearch.page;
        url += "&pageSize=" + keywordSearch.pageSize;

        result.put(URL, url);

        return result;
    }

    // 品牌 系列 检索
    public static HashMap<String, String> createGetSeriesBrand(String categoryCode, String seriesCode, String innerCode, String brandCode, Integer page, Integer pageSize) {

        String url = getUrlString(getSeriesSearch);
        if (!isEmpty(categoryCode)) {
            url += "&categoryCode=" + encode(categoryCode);
        }
        if (!isEmpty(seriesCode)) {
            url += "&seriesCode=" + encode(seriesCode);
        }
        if (!isEmpty(innerCode)) {
            url += "&innerCode=" + encode(innerCode);
        }
        if (!isEmpty(brandCode)) {
            url += "&brandCode=" + encode(brandCode);
        }
        url += "&page=" + page;
        url += "&pageSize=" + pageSize;

        HashMap<String, String> result = new HashMap<>();

        result.put(URL, url);

        return result;
    }
    //--ADD NT-LWL 17/09/08 BrandSearch TO -

    private static HashMap<String, String> createKeywordSearchSub(RequestKeywordSearch keywordSearch, boolean isDefault) {

        HashMap<String, String> result = new HashMap<>();
        String url = getUrlString(keywordSearchApi);
        if (isDefault) {
            url += "&field=@default";
        } else {
            url += "&field=@all";
        }

        //URL引数
        url += "&keyword=" + encode(keywordSearch.keyword);
        url += "&page=" + keywordSearch.page;
        url += "&pageSize=" + keywordSearch.pageSize;

        result.put(URL, url);

        return result;
    }

    /**
     * createSearchSuggest
     *
     * @param keyword
     * @param size
     * @return
     */
    public static String createSearchSuggest(String keyword, Integer size) {

        String url = getUrlString(SearchSuggestApi);
        url += "&keyword=" + encode(keyword);
        if (size != null) {
            url += "&size=" + size;
        }
        return url;
    }


//    //型番検索
//    public static HashMap<String, String> createSearchPartNumber(String seriesCode, String innerCode, Integer page, Integer pageSize){
//
//        String url = getUrlString(searchPartNumberApi);
//        url += "&field=@all";
//
//        if (!isEmpty(seriesCode)) {
//            url += "&seriesCode=" + encode(seriesCode);
//        }
//        if (!isEmpty(innerCode)) {
//            url += "&innerCode=" + encode(innerCode);
//        }
//        url += "&page=" + page;
//        url += "&pageSize="+ pageSize;
//
//        HashMap<String, String> result = new HashMap<>();
//
////        result.put(CONTENT_TYPE, content_type_json);
//        result.put(URL, url);
//
//        return result;
//    }


    private static boolean isEmpty(String str) {

        return android.text.TextUtils.isEmpty(str);
    }

    private static boolean isEmpty(Integer integer) {

        return (integer == null);
    }


}

