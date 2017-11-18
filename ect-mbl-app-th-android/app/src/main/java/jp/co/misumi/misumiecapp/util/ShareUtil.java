package jp.co.misumi.misumiecapp.util;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.api.ShareSaveApi;
import jp.co.misumi.misumiecapp.data.QTShareData;
import jp.co.misumi.misumiecapp.data.ShareData;
import jp.co.misumi.misumiecapp.data.ShareDetail;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;


/**
 * Created by sakamoto on 15/10/13.
 */
public class ShareUtil {
    //--ADD NT-LWL 17/05/19 Share FR -
    // 微信分享 图片压缩参数
    private static final int THUMB_SIZE = 150;
    // web商品详情地址
    public static final String PRODUCT_DETAIL = "http://cn.misumi-ec.com/vona2/detail/";
    // 分享H5画面 加载地址
    public static final String SHARE_URL = BuildConfig.ShareUrl;
    //--ADD NT-LWL 17/05/19 Share TO -

    private static final String ENCODE_CHAR = "utf-8";


    private final static String SHARE_BASE_URL = "http://stg-static-contents-mbl-cn-cnn1.s3.cn-north-1.amazonaws.com.cn/contents/redirect/redirect.html";


    public static String makeShareUrl(Context context, String screenName, String categoryCode, String seriesCode, String partNumber) {

        String urlStr = SHARE_BASE_URL;
        urlStr += "?sname=" + screenName;

        //カテゴリ
        if (!android.text.TextUtils.isEmpty(categoryCode)) {
            urlStr += "&ccode=" + categoryCode;
        }

        //シリーズ
        if (!android.text.TextUtils.isEmpty(seriesCode)) {
            urlStr += "&scode=" + seriesCode;
        }

        //型番有り
        if (!android.text.TextUtils.isEmpty(partNumber)) {
            urlStr += "&pcode=" + encode(partNumber);
        }

        return urlStr;
    }

    private static String encode(String s) {
        try {
            //*-文字はそのままで良い
            return URLEncoder.encode(s, ENCODE_CHAR);
        } catch (UnsupportedEncodingException e) {

            return s;
        }
    }


    public static boolean doShareUrl(Context context, String url) {

        //TODO:WeChat呼び出し
        return doShareWeChatUrl(context, url);
    }


    private static boolean doShareWeChatUrl(Context context, String url) {

        IWXAPI mWeixinApi;
        mWeixinApi = WXAPIFactory.createWXAPI(context, AppConst.WECHAT_APP_ID, false);

/*
        if (!mWeixinApi.isWXAppInstalled()) {
            //アプリをインストールしていない
            return false;
        }
*/

        mWeixinApi.registerApp(AppConst.WECHAT_APP_ID);

        WXWebpageObject webpage = new WXWebpageObject();
//		webpage.webpageUrl = "http://www.baidu.com";
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
//		msg.title = "WebPage Title";
//		msg.description = "WebPage Description";
        msg.title = "";
        msg.description = url;
//		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
//		msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;

        return mWeixinApi.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 显示分享选择 对话框
     *
     * @param context    上下文
     * @param screenName 来自画面
     */
    //--ADD NT-LWL 17/05/19 Share FR -
    public static void show(final Context context, final Object data, final String screenName, final ShareSaveApi shareSaveApi) {
        final Dialog mAlertDialog = new Dialog(context, R.style.MessageDialog);
        // 设置点击外部关闭
        mAlertDialog.setCancelable(true);


        LayoutInflater inflater = mAlertDialog.getLayoutInflater();
        final View childView = inflater.inflate(R.layout.dialog_share, null, false);
        ViewUtil.setSplitMotionEventsToAll(childView);
        mAlertDialog.setContentView(childView);

        WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
        // 设置宽高
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window dialogWindow = mAlertDialog.getWindow();
        // 设置宽度占满
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        // 设置底部显示
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 设置进出动画
        dialogWindow.setWindowAnimations(R.style.share_view_anim);
        mAlertDialog.show();

        // 获取GA 跟踪器
        MisumiEcApp app = (MisumiEcApp) context.getApplicationContext();
        final Tracker tracker = app.getDefaultTracker();

        // 点击QQ好友分享
        childView.findViewById(R.id.qq_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareSaveApi != null) {
                    shareSaveApi.setPlatform(1);
                    shareSaveApi.connect(context);
                    // GA追加 点击QQ分享
                    GoogleAnalytics.sendAction(tracker, GoogleAnalytics.CATEGORY_SHARE, GoogleAnalytics.lable_qq);
                }
                mAlertDialog.dismiss();
            }
        });
        // 点击微信好友分享
        childView.findViewById(R.id.wechat_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareSaveApi != null) {
                    shareSaveApi.setPlatform(2);
                    shareSaveApi.connect(context);
                    // GA追加 点击微信分享
                    GoogleAnalytics.sendAction(tracker, GoogleAnalytics.CATEGORY_SHARE, GoogleAnalytics.lable_wechat);
                }
                mAlertDialog.dismiss();
            }
        });
        // 点击邮件分享
        childView.findViewById(R.id.email_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareSaveApi != null) {
                    shareSaveApi.setPlatform(3);
                    shareSaveApi.connect(context);
                    // GA追加 点击邮件分享
                    GoogleAnalytics.sendAction(tracker, GoogleAnalytics.CATEGORY_SHARE, GoogleAnalytics.lable_mail);
                }
                // GA追加 点击微信分享
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    /**
     * 分享到邮件
     *
     * @param context    上下文
     * @param object     分享原数据
     * @param screenName 画面
     */
    private static void shareToEmail(Context context, Object object, String screenName) {
        String uName = AppConfig.getInstance().getUserName();

        // 邮件标题
        String title = "";
        // 邮件内容
        String content = "";

        // 用户未登录
        if (TextUtils.isEmpty(uName)) {
            uName = "";
        }

        if (screenName.equals(SaicataId.ItemDetail)) {
            // 商品详情分享

            title = "";
            content = getItemDetailContent(context, object, uName, null);

        } else if (screenName.equals(SaicataId.Cart)) {
            // 购物车分享

            title = "";
            content = getCartContent(context, object, uName, null);

        } else if (screenName.equals(SaicataId.EstimateDetail)) {
            // 报价详情分享  不需要了  不会进入此处

            title = "";
            content = getQTContent(context, object, uName);

        }

        mailShare(context, title, content);
    }

    /**
     * 邮件分享
     *
     * @param context
     * @param title
     * @param content
     */
    public static void mailShare(Context context, String title, String content) {
        try {
            // 无附件分享
            Intent data = new Intent(Intent.ACTION_SENDTO);
            // 邮箱地址 mailto:way.ping.li@gmail.com
            data.setData(Uri.parse("mailto:"));

            // 标题
            data.putExtra(Intent.EXTRA_SUBJECT, title);
            // 内容 可以用 Html.fromHtml(body)网页格式
            data.putExtra(Intent.EXTRA_TEXT, content);

            context.startActivity(data);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "未找到邮件客户端哦！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 商品详情文本组合
     *
     * @param context
     * @param object
     * @param uName
     * @return
     */
    public static String getItemDetailContent(Context context, Object object, String uName, String uuid) {
        ShareDetail detail = (ShareDetail) object;
        StringBuilder sburl = new StringBuilder();
        String content = "";

        sburl.append(PRODUCT_DETAIL);
        // 添加商品code
        sburl.append(detail.getScode());

        String userCode = AppConfig.getInstance().getUserCode();
        String customerCode = AppConfig.getInstance().getCustomerCode();
        // 追加参数 uuid 和 ProductCode
        sburl.append("/?ProductCode=");
        sburl.append(detail.getPcode());
        sburl.append("&uuid=");
        if (!TextUtils.isEmpty(uuid)) {
            sburl.append(uuid);
        } else {
            sburl.append(customerCode);
            sburl.append("_");
            sburl.append(userCode);
        }

        String pcode = TextUtils.isEmpty(detail.getPcode()) ? context.getString(R.string.text_empty) : detail.getPcode();
        // 商品名称
        String seriesName = TextUtils.isEmpty(detail.getSeriesName()) ? context.getString(R.string.text_empty) : detail.getSeriesName();
        // 品牌名称
        String brandName = TextUtils.isEmpty(detail.getBrandName()) ? context.getString(R.string.text_empty) : detail.getBrandName();
        // 商品数量
        String number = detail.getNumber().equals("unconfirmed") ? context.getString(R.string.unconfirmed) : detail.getNumber();
        // 发货日
        String daysToShip = detail.getDaysToShip().equals("unconfirmed") ? context.getString(R.string.unconfirmed) : detail.getDaysToShip();
        // 总价
        String totalPrice = detail.getTotalPrice();
        // 含税总价
        String totalPriceIncludingTax = detail.getTotalPriceIncludingTax();
        // 单价
        String unitPrice = detail.getUnitPrice().equals("unconfirmed") ? context.getString(R.string.unconfirmed) : detail.getUnitPrice() + context.getString(R.string.label_currency_rmb);
        // 合计
        String total = "";
        if (detail.getTotalPrice().equals("unconfirmed")) {
            total = context.getString(R.string.unconfirmed);
        } else {
            total = String.format(context.getString(R.string.total_price_format), totalPrice, totalPriceIncludingTax);
        }
        content = String.format(context.getString(R.string.email_itemdetail_text_format),
                pcode, seriesName, brandName,
                number, total,
                daysToShip, unitPrice, sburl.toString());
        return content;
    }

    /**
     * 报价文本组合
     *
     * @param context
     * @param object
     * @param uName
     * @return
     */
    private static String getQTContent(Context context, Object object, String uName) {
        // 类型转换
        QTShareData qtShareData = (QTShareData) object;
        StringBuilder sb = new StringBuilder();
        // 添加标题
        sb.append(String.format(context.getString(R.string.email_qt_title_text_format), uName));
        // 报价单号
        String quotationSlipNo = TextUtils.isEmpty(qtShareData.getQuotationSlipNo()) ? context.getString(R.string.text_empty) : qtShareData.getQuotationSlipNo();
        // 报价时间
        String quotationDateTime = TextUtils.isEmpty(qtShareData.getQuotationDateTime()) ? context.getString(R.string.text_empty) : qtShareData.getQuotationDateTime();
        // 报价有效期
        String quotationExpireDateTime = TextUtils.isEmpty(qtShareData.getQuotationExpireDateTime()) ? context.getString(R.string.text_empty) : qtShareData.getQuotationExpireDateTime();
        // 订购人
        String userName = TextUtils.isEmpty(qtShareData.getUserName()) ? context.getString(R.string.text_empty) : qtShareData.getUserName();
        // 商品件数
        String itemCount = TextUtils.isEmpty(qtShareData.getItemCount()) ? context.getString(R.string.text_empty) : qtShareData.getItemCount();
        // 总价
        String totalPrice = TextUtils.isEmpty(qtShareData.getTotalPrice()) ? context.getString(R.string.text_empty) : qtShareData.getTotalPrice();
        // 含税总价
        String totalPriceIncludingTax = TextUtils.isEmpty(qtShareData.getTotalPriceIncludingTax()) ? context.getString(R.string.text_empty) : qtShareData.getTotalPriceIncludingTax();

        // 添加头部信息
        sb.append(String.format(context.getString(R.string.email_qt_header), quotationSlipNo,
                quotationDateTime, quotationExpireDateTime,
                userName, itemCount, totalPrice,
                totalPriceIncludingTax));

        int n = qtShareData.getQtList().size();
        // 循环添加商品列表
        for (int i = 0; i < n; i++) {
            // 获取型番
            String pcode = TextUtils.isEmpty(qtShareData.getQtList().get(i).getPcode()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getPcode();
            // 获取商品名称
            String seriesName = TextUtils.isEmpty(qtShareData.getQtList().get(i).getSeriesName()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getSeriesName();
            // 获取品牌名称
            String brandName = TextUtils.isEmpty(qtShareData.getQtList().get(i).getBrandName()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getBrandName();
            // 获取商品数量
            String number = TextUtils.isEmpty(qtShareData.getQtList().get(i).getNumber()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getNumber();
            // 获取发货日
            String daysToShip = TextUtils.isEmpty(qtShareData.getQtList().get(i).getDaysToShip()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getDaysToShip();
            // 获取总价
            String totalPrice1 = TextUtils.isEmpty(qtShareData.getQtList().get(i).getTotalPrice()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getTotalPrice();
            // 获取含税总价
            String totalPriceIncludingTax1 = TextUtils.isEmpty(qtShareData.getQtList().get(i).getTotalPriceIncludingTax()) ? context.getString(R.string.text_empty) : qtShareData.getQtList().get(i).getTotalPriceIncludingTax();

            sb.append(String.format(context.getString(R.string.email_qt_item_text_format),
                    pcode, seriesName,
                    brandName, number,
                    daysToShip, totalPrice1,
                    totalPriceIncludingTax1));
            if (i != n - 1) {
                // 添加商品之间单行分割线
                sb.append(context.getString(R.string.email_qt_line1));
            }
        }

        // 添加商品结束双分割线
        sb.append(context.getString(R.string.email_qt_line2));
        // 添加注意事项
        sb.append(context.getString(R.string.email_qt_end));
        return sb.toString();
    }

    /**
     * 购物车文本组合
     *
     * @param context 上下文
     * @param object  分享对象
     * @param uName   用户名
     * @return
     */
    public static String getCartContent(Context context, Object object, String uName, String uuid) {
        // 类型转换
        List<ShareDetail> cartList = (List<ShareDetail>) object;
        StringBuilder sb = new StringBuilder();
        // 添加标题 String.format(context.getString(R.string.email_cart_title_text_format), uName)
        sb.append(context.getString(R.string.email_cart_header));
        // 添加双分割线
        sb.append(context.getString(R.string.email_cart_line2));

        int n = cartList.size();
        // 循环添加商品列表
        for (int i = 0; i < n; i++) {
            // 型番
            String pcode = TextUtils.isEmpty(cartList.get(i).getPcode()) ? context.getString(R.string.text_empty) : cartList.get(i).getPcode();
            // 商品名称
            String seriesName = TextUtils.isEmpty(cartList.get(i).getSeriesName()) ? context.getString(R.string.text_empty) : cartList.get(i).getSeriesName();
            // 品牌名称
            String brandName = TextUtils.isEmpty(cartList.get(i).getBrandName()) ? context.getString(R.string.text_empty) : cartList.get(i).getBrandName();
            // 商品数量
            String number = TextUtils.isEmpty(cartList.get(i).getNumber()) ? context.getString(R.string.text_empty) : cartList.get(i).getNumber();
            // 发货日
            String daysToShip = TextUtils.isEmpty(cartList.get(i).getDaysToShip()) ? context.getString(R.string.text_empty) : cartList.get(i).getDaysToShip();
            // 总价
            String totalPrice = TextUtils.isEmpty(cartList.get(i).getTotalPrice()) ? context.getString(R.string.text_empty) : cartList.get(i).getTotalPrice();
            // 含税总价
            String totalPriceIncludingTax = TextUtils.isEmpty(cartList.get(i).getTotalPriceIncludingTax()) ? context.getString(R.string.text_empty) : cartList.get(i).getTotalPriceIncludingTax();
            // 商品地址
            StringBuilder sburl = new StringBuilder();
            sburl.append(PRODUCT_DETAIL);
            // 添加商品code
            sburl.append(cartList.get(i).getScode());
            String userCode = AppConfig.getInstance().getUserCode();
            String customerCode = AppConfig.getInstance().getCustomerCode();
            // 追加参数 uuid 和 ProductCode
            sburl.append("/?ProductCode=");
            sburl.append(cartList.get(i).getPcode());
            sburl.append("&uuid=");
            if (!TextUtils.isEmpty(uuid)) {
                sburl.append(uuid);
            } else {
                sburl.append(customerCode);
                sburl.append("_");
                sburl.append(userCode);
            }

            sb.append(String.format(context.getString(R.string.email_cart_item_text_format),
                    pcode, seriesName,
                    brandName, number,
                    daysToShip, totalPrice,
                    totalPriceIncludingTax, sburl.toString()));
            if (i != n - 1) {
                // 添加商品之间单行分割线
                sb.append(context.getString(R.string.email_cart_line1));
            }
        }

        // 添加商品结束双分割线
        sb.append(context.getString(R.string.email_cart_line3));
        // 添加注意事项
        sb.append(context.getString(R.string.email_cart_end));
        return sb.toString();
    }

    /**
     * 分享到微信好友
     *
     * @param context
     * @param shareData  分享数据
     * @param screenName 画面
     */
    public static void shareToWeXin(Context context, final ShareData shareData, String screenName) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, AppConst.WEIXIN_APP_ID);
        if (!api.isWXAppInstalled()) {
            // 如果没有安装微信，提示不能分享
            Toast.makeText(context, "您还未安装微信客户端哦！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!api.registerApp(AppConst.WEIXIN_APP_ID)) {
            return;
        }

        // 商品详情分享
        new Thread() {
            @Override
            public void run() {
                Bitmap bmp = null;
                try {
                    if (!TextUtils.isEmpty(shareData.getImageUrl())) {
                        // 网络获取图片
                        bmp = BitmapFactory.decodeStream(new URL(shareData.getImageUrl()).openStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    AppLog.e(e.getMessage());
                }
                WXWebpageObject wxPage = new WXWebpageObject();
                // 消息点击后跳转地址
                wxPage.webpageUrl = shareData.getTargetUrl();

                WXMediaMessage msg = new WXMediaMessage();

                msg.mediaObject = wxPage;
                if (null != bmp) {
                    // 图片压缩
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//                    bmp.recycle();
                    msg.thumbData = WeiXinUtil.bmpToByteArray(thumbBmp, false);  //设置缩略图
                    AppLog.e("thumbDataSize = " + msg.thumbData.length);
                }

                // 分享标题
                msg.title = shareData.getTitle();
                // 描述信息
                msg.description = shareData.getContent();
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                // transaction用于标示一个唯一请求
                req.transaction = buildTransaction("share");
                req.message = msg;
                req.scene = WXSceneSession; // 分享到聊天界面
                api.sendReq(req);
            }
        }.start();
    }

    /**
     * 分享到QQ好友
     *
     * @param context
     * @param shareData  分享数据
     * @param screenName 画面
     */
    public static void shareToQQ(Context context, ShareData shareData, String screenName) {
        if (!isQQClientAvailable(context)) {
            // 如果没有安装QQ，提示不能分享
            Toast.makeText(context, "您还未安装QQ客户端哦！", Toast.LENGTH_SHORT).show();
            return;
        }
        Tencent mTencent = Tencent.createInstance(AppConst.QQ_APP_ID, context.getApplicationContext());
        final Bundle params = new Bundle();
        // 设置分享类型为图文分享
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        // 分享标题
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
        // 分享摘要
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getContent());
        // 点击分享消息跳转链接
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTargetUrl());//"http://cn.misumi-ec.com"
        // 分享图片url
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareData.getImageUrl());
        // 开始分享
        MainActivity mainActivity = (MainActivity) context;
        mTencent.shareToQQ(mainActivity, params, mainActivity.getShareListener());
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
    //--ADD NT-LWL 17/05/19 Share TO -
}

