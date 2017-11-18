//--ADD NT-LWL 17/05/19 Share FR -
package jp.co.misumi.misumiecapp.api;


import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.data.ShareData;
import jp.co.misumi.misumiecapp.data.ShareDetail;
import jp.co.misumi.misumiecapp.util.ShareUtil;

/**
 * 分享内容保存 api
 */

public abstract class ShareSaveApi extends ApiAccessWrapper {
    private String json;
    // 分享平台 1:QQ 2：微信 3:邮件
    private int platform;
    // 商品详情使用
    private ShareDetail shareDetail;
    // 购物车使用
    private List<ShareDetail> cartList;
    // 报价详情使用
//    private QTShareData qtShareData;

    public void setShareDetail(ShareDetail shareDetail) {
        this.shareDetail = shareDetail;
    }

    public void setCartList(List<ShareDetail> cartList) {
        this.cartList = cartList;
    }

//    public void setQtShareData(QTShareData qtShareData) {
//        this.qtShareData = qtShareData;
//    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    @Override
    public HashMap<String, String> getParameter() {
        HashMap<String, String> result = new HashMap<>();
//        result.put(ApiBuilder.CONTENT_TYPE, "application/json");
        // 获取用户code
        String userCode = AppConfig.getInstance().getUserCode();
        if (TextUtils.isEmpty(userCode)) {
            userCode = "";
        }
        // 获取客户code
        String customerCode = AppConfig.getInstance().getCustomerCode();
        if (TextUtils.isEmpty(customerCode)) {
            customerCode = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ShareUtil.SHARE_URL);
        sb.append("ShareServlet?userCode=");
        sb.append(userCode);
        sb.append("&customerCode=");
        sb.append(customerCode);
        sb.append("&os=Android");
        if (platform == 1) {
            sb.append("&media=QQ");
        } else if (platform == 2) {
            sb.append("&media=WeChat");
        } else if (platform == 3) {
            sb.append("&media=mail");
        }

        if (getScreenId().equals(SaicataId.ItemDetail)) {
            // 详情分享

            sb.append("&shareType=detail");

            if (platform == 3) {
                // 邮件分享 content直接传模板
                String uName = AppConfig.getInstance().getUserName();
                // 用户未登录
                if (TextUtils.isEmpty(uName)) {
                    uName = "";
                }
                // 组装模板
                json = Uri.encode(ShareUtil.getItemDetailContent(getContext(), shareDetail, uName, null));
            } else {
                json = getDetailJson(shareDetail).toString();
            }

        } else if (getScreenId().equals(SaicataId.Cart)) {
            // 购物车分享

            sb.append("&shareType=cart");

            if (platform == 3) {
                // 邮件分享 content直接传模板
                String uName = AppConfig.getInstance().getUserName();
                // 用户未登录
                if (TextUtils.isEmpty(uName)) {
                    uName = "";
                }
                // 组装模板
                json = Uri.encode(ShareUtil.getCartContent(getContext(), cartList, uName, null));
            } else {
                json = getArrayJson(cartList).toString();
            }

        } else if (getScreenId().equals(SaicataId.EstimateDetail)) {
            // 报价分享 已舍弃
            json = getEstimateJson();
        }
        JSONObject body = new JSONObject();
        try {
            body.put("content", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.put(ApiBuilder.URL, sb.toString());
        result.put(ApiBuilder.BODY, body.toString());
        return result;
    }

    /**
     * 组合报价分享参数
     *
     * @return
     */
    private String getEstimateJson() {
        JSONObject object = new JSONObject();

        /*try {
            if (qtShareData != null) {
                // 商品件数
                if (!TextUtils.isEmpty(qtShareData.getItemCount())) {
                    object.put("itemCount", Uri.encode(qtShareData.getItemCount()));
                }
                // 总价
                if (!TextUtils.isEmpty(qtShareData.getTotalPrice())) {
                    object.put("totalPrice", qtShareData.getTotalPrice());
                }
                // 含税总价
                if (!TextUtils.isEmpty(qtShareData.getTotalPriceIncludingTax())) {
                    object.put("totalPriceIncludingTax", qtShareData.getTotalPriceIncludingTax());
                }
                // 运费
                if (!TextUtils.isEmpty(qtShareData.getFreight())) {
                    object.put("freight", Uri.encode(qtShareData.getFreight()));
                }
                // 运费折扣
                if (!TextUtils.isEmpty(qtShareData.getFreightDiscount())) {
                    object.put("freightDiscount", Uri.encode(qtShareData.getFreightDiscount()));
                }
                // 报价商品列表
                if (!qtShareData.getQtList().isEmpty()) {
                    object.put("list", getArrayJson(qtShareData.getQtList()));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return object.toString();
    }

    /**
     * 组合json列表
     *
     * @param cartList
     * @return
     */
    private JSONArray getArrayJson(List<ShareDetail> cartList) {
        JSONArray jsonArray = new JSONArray();
        if (cartList != null) {
            for (ShareDetail shareDetail : cartList) {
                jsonArray.put(getDetailJson(shareDetail));
            }
        }
        return jsonArray;
    }

    /**
     * 组合商品json对象
     *
     * @param shareDetail
     * @return
     */
    private JSONObject getDetailJson(ShareDetail shareDetail) {
        JSONObject object = new JSONObject();

        try {
            // 品牌名称
            if (!TextUtils.isEmpty(shareDetail.getBrandName())) {
                object.put("brandName", Uri.encode(shareDetail.getBrandName()));
            }
            // 品牌code 目前就购物车存DB
            if (!TextUtils.isEmpty(shareDetail.getBrandCode())) {
                object.put("brandCode", Uri.encode(shareDetail.getBrandCode()));
            }
            // 发货日
            if (!TextUtils.isEmpty(shareDetail.getDaysToShip())) {
                object.put("daysToShip", Uri.encode(shareDetail.getDaysToShip()));
            }
            // 型番
            if (!TextUtils.isEmpty(shareDetail.getPcode())) {
                object.put("pcode", Uri.encode(shareDetail.getPcode()));
            }
            // 商品code
            if (!TextUtils.isEmpty(shareDetail.getScode())) {
                object.put("scode", Uri.encode(shareDetail.getScode()));
            }
            // 商品名称
            if (!TextUtils.isEmpty(shareDetail.getSeriesName())) {
                object.put("seriesName", Uri.encode(shareDetail.getSeriesName()));
            }
            // 合计价格（不含税）
            if (!TextUtils.isEmpty(shareDetail.getTotalPrice())) {
                object.put("totalPrice", shareDetail.getTotalPrice());
            }
            // 合计价格（含税）
            if (!TextUtils.isEmpty(shareDetail.getTotalPriceIncludingTax())) {
                object.put("totalPriceIncludingTax", shareDetail.getTotalPriceIncludingTax());
            }
            // 单价
            if (!TextUtils.isEmpty(shareDetail.getUnitPrice())) {
                object.put("unitPrice", shareDetail.getUnitPrice());
            }
            // 商品图片
            if (!TextUtils.isEmpty(shareDetail.getImageUrl())) {
                object.put("imageUrl", shareDetail.getImageUrl());
            }
            // 询价参数
            if (!TextUtils.isEmpty(shareDetail.getCtype())) {
                object.put("ctype", shareDetail.getCtype());
            }
            // 数量
            if (!TextUtils.isEmpty(shareDetail.getNumber())) {
                object.put("number", shareDetail.getNumber());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public void onResult(int responseCode, String result) {
        try {
            JSONObject object = new JSONObject(result);

            if (responseCode == NetworkInterface.STATUS_OK) {
                // 服务端保存分享数据成功

                if (object.has("UUID")) {
                    // 获取分享标识 分享详情H5需要使用
                    String uuid = object.getString("UUID");
                    if (!TextUtils.isEmpty(uuid)) {
                        // 开始分享
                        startShare(uuid);
                        return;
                    } else {
                        saveDateFail();
                    }
                } else {
                    saveDateFail();
                }


            } else if (responseCode == NetworkInterface.SERVER_ERROR) {
                // 服务器端错误

                saveDateFail();

            } else {
                saveDateFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            saveDateFail();
        }
    }

    // 服务器保存数据失败
    private void saveDateFail() {
        if (platform == 3) {
            String uName = AppConfig.getInstance().getUserName();
            // 用户未登录
            if (TextUtils.isEmpty(uName)) {
                uName = getContext().getString(R.string.misumi_name);
            }
            // 邮件分享
            String title = "";
            Object data = null;
            String content = "";
            if (getScreenId().equals(SaicataId.Cart)) {
                data = cartList;
                title = String.format(getContext().getString(R.string.email_cart_title_text_format), uName);
                content = ShareUtil.getCartContent(getContext(), data, uName, null);
            } else {
                data = shareDetail;
                title = String.format(getContext().getString(R.string.email_detail_title), uName);
                content = ShareUtil.getItemDetailContent(getContext(), data, uName, null);
            }

            ShareUtil.mailShare(getContext(), title, content);
        } else {
            showMessageDialog(getContext().getString(R.string.share_fail));
        }
    }

    /**
     * 开始分享
     *
     * @param uuid
     */
    private void startShare(String uuid) {
        ShareData shareData = new ShareData();


        if (getScreenId().equals(SaicataId.ItemDetail)) {
            // 商品详情分享

            String uName = AppConfig.getInstance().getUserName();
            // 用户未登录
            if (TextUtils.isEmpty(uName)) {
                uName = getContext().getString(R.string.misumi_name);
            }

            // 设置分享内容点击时 跳转链接
            shareData.setTargetUrl(ShareUtil.SHARE_URL + "item_detail.html?UUID=" + uuid);
            // 设置分享图片
            shareData.setImageUrl(shareDetail.getImageUrl());
            // 开始分享
            if (platform == 1) {
                // QQ分享

                String title = String.format(getContext().getString(R.string.share_detail_title), uName);
                // 设置分享标题
                // QQ SDK 内部已做长度处理，超过之后会截取
//                if (title.length()>30){
//                    shareData.setTitle(title.substring(0,29));
//                }else {
                shareData.setTitle(title);
//                }
                String content = shareDetail.getSeriesName() + "　" + shareDetail.getBrandName() + "　" + shareDetail.getPcode();
                // 设置分享内容
                // QQ SDK 内部已做长度处理，超过之后会截取
//                if (content.length()>40){
//                    shareData.setContent(content.substring(0,39));
//                }else {
                shareData.setContent(content);
//                }
                ShareUtil.shareToQQ(getContext(), shareData, getScreenId());
            } else if (platform == 2) {
                // 微信分享

                String title = String.format(getContext().getString(R.string.share_detail_title), uName);
                // 设置分享标题
                if (title.length() > 512) {
                    shareData.setTitle(title.substring(0, 511));
                } else {
                    shareData.setTitle(title);
                }
                String content = shareDetail.getSeriesName() + "　" + shareDetail.getBrandName() + "　" + shareDetail.getPcode();
                // 设置分享内容
                if (content.length() > 1024) {
                    shareData.setContent(content.substring(0, 1023));
                } else {
                    shareData.setContent(content);
                }
                ShareUtil.shareToWeXin(getContext(), shareData, getScreenId());
            } else if (platform == 3) {
                // 邮件分享
                String title = String.format(getContext().getString(R.string.email_detail_title), uName);
                String content = ShareUtil.getItemDetailContent(getContext(), shareDetail, uName, uuid);
                ShareUtil.mailShare(getContext(), title, content);
            }
        } else if (getScreenId().equals(SaicataId.Cart)) {
            // 购物车分享

            // 设置分享标题
            String uName = AppConfig.getInstance().getUserName();
            // 用户未登录
            if (TextUtils.isEmpty(uName)) {
                uName = getContext().getString(R.string.misumi_name);
            }
            String title = String.format(getContext().getString(R.string.share_cart_title_text_format), uName);

            // 设置分享内容点击时 跳转链接
            shareData.setTargetUrl(ShareUtil.SHARE_URL + "cart_share.html?UUID=" + uuid);
            // 设置分享图片
            shareData.setImageUrl(cartList.get(0).getImageUrl());
            // 设置分享内容摘要
            shareData.setContent(getContext().getString(R.string.share_cart_content));
            // 开始分享
            if (platform == 1) {
                // QQ分享
                // QQ SDK 内部已做长度处理，超过之后会截取
//                if (title.length()>30){
//                    // 防止长度超过30
//                    shareData.setTitle(title.substring(0,29));
//                }else {
                shareData.setTitle(title);
//                }
                ShareUtil.shareToQQ(getContext(), shareData, getScreenId());
            } else if (platform == 2) {
                // 微信分享

                if (title.length() > 512) {
                    // 防止长度超过512
                    shareData.setTitle(title.substring(0, 511));
                } else {
                    shareData.setTitle(title);
                }
                ShareUtil.shareToWeXin(getContext(), shareData, getScreenId());
            } else if (platform == 3) {
                // 邮件分享
                title = String.format(getContext().getString(R.string.email_cart_title_text_format), uName);
                String content = ShareUtil.getCartContent(getContext(), cartList, uName, uuid);
                ShareUtil.mailShare(getContext(), title, content);
            }
        } else if (getScreenId().equals(SaicataId.EstimateDetail)) {
            // 报价详情分享  不需要了

            // 设置分享标题
            /*shareData.setTitle("报价详情");
            // 设置分享内容点击时 跳转链接
            shareData.setTargetUrl(ShareUtil.SHARE_URL + "qt_share.html?UUID=" + uuid);
            // 设置分享图片
//            shareData.setImageUrl(shareDetail.getImageUrl());
            // 设置分享内容摘要
            shareData.setContent("报价内容.....");
            // 开始分享
            if (platform == 1){
                // QQ分享
                ShareUtil.shareToQQ(getContext(),shareData,getScreenId());
            }else if (platform == 2){
                // 微信分享
                ShareUtil.shareToWeXin(getContext(),shareData,getScreenId());
            }*/
        }


    }
}
//--ADD NT-LWL 17/05/19 Share TO -