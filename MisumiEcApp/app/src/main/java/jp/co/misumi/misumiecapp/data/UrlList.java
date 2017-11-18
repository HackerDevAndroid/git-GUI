package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;


/**
 * UrlList
 */
public class UrlList extends DataContainer {

    // ログインID、パスワードを忘れた方はこちら
    public String forgetPasswordUrl;
//    // 会員登録後にご利用いただけるサービス一覧
//    public String memberGuideUrl;
    // 新規会員登録(無料)
    public String newRegistUrl;
    // 単月カレンダー画像URL
    public String calendarTopUrl;
    // ミスミカレンダー
    public String calendarFullUrl;
    // ご利用ガイド
    public String userGuidUrl;
    // 規約一覧
    public String userPolicyUrl;
    // その他
    public String othersUrl;
    //個人情報
    public String personalInformationUrl;
    //お問い合わせ先
    public String contactUrl;
    // 営業時間
    public String workingHourUrl;
    // 注文キャンセル
    public String cancelOrderUrl;
    //--ADD NT-LWL 17/07/06 Category FR -
    public String categoryImgURLReplaceList;
    //--ADD NT-LWL 17/07/06 Category To -
    //--ADD NT-LWL 17/08/22 Depo FR -
    // 闪达banner图片
    public String depoBannerUrl;
    // 闪达H5地址
    public String depoProductsPageUrl;
    //--ADD NT-LWL 17/08/22 Depo TO -

    //-- ADD NT-LWL 17/09/25 Category FR -
    // 黑名单json
    public String excludeCategoryList;
    // 新旧分类codeMap
    public String categoryQRMap;
    //-- ADD NT-LWL 17/09/25 Category TO -
    /**
     * setData
     * @param src
     * @return
     */
    public boolean setData(String src){

        if (src == null || src.length() == 0){
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);

            forgetPasswordUrl = getJsonStringWithThrows(json,"forgetPasswordUrl");
//            memberGuideUrl = getJsonStringWithThrows(json,"memberGuideUrl");
            newRegistUrl = getJsonStringWithThrows(json, "newRegistUrl");
            calendarTopUrl = getJsonStringWithThrows(json,"calendarTopUrl");
            calendarFullUrl = getJsonStringWithThrows(json,"calendarFullUrl");
            userGuidUrl = getJsonStringForWebView(json, "userGuidUrl");
            userPolicyUrl = getJsonStringWithThrows(json, "userPolicyUrl");
            othersUrl = getJsonStringWithThrows(json, "othersUrl");
            personalInformationUrl = getJsonStringWithThrows(json, "personalInformationUrl");
            contactUrl = getJsonStringForWebView(json, "contactUrl");
            workingHourUrl = getJsonStringWithThrows(json, "workingHourUrl");
            cancelOrderUrl = getJsonStringWithThrows(json, "cancelOrderUrl");

            //--ADD NT-LWL 17/07/06 Category FR -
            categoryImgURLReplaceList = getJsonStringWithThrows(json,"CategoryImgURLReplaceList");
            //--ADD NT-LWL 17/07/06 Category TO -
            //--ADD NT-LWL 17/08/22 Depo FR -
            depoBannerUrl = getJsonStringWithThrows(json,"depo_BannerUrl");
            depoProductsPageUrl = getJsonStringWithThrows(json,"depo_productsPageUrl");
            //--ADD NT-LWL 17/08/22 Depo TO -

            //-- ADD NT-LWL 17/09/25 Category FR -
            excludeCategoryList = getJsonStringWithThrows(json,"excludeCategoryList");
            categoryQRMap = getJsonStringWithThrows(json,"CategoryQRMap");
            //-- ADD NT-LWL 17/09/25 Category TO -
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }


    protected String getJsonStringForWebView(JSONObject jsonObj, String tag) throws JSONException {
        String result = getJsonStringWithThrows(jsonObj, tag);
        if (result == null){
            result = AppConst.UnsetUrl;
        }
        return result;
    }

    protected String getJsonStringWithThrows(JSONObject jsonObj, String tag) throws JSONException {
        String result = getJsonString(jsonObj,tag);
        if (result != null && result.isEmpty()){
            return null;
        }
        return result;
    }

}
