//-- ADD NT-SLJ 16/11/17 AliPay Payment FR -
package jp.co.misumi.misumiecapp.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.LostSessionProcess;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.LoadingActivity;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AliPaymentInfo;
import jp.co.misumi.misumiecapp.data.GetCartCount;
import jp.co.misumi.misumiecapp.data.PayResult;
import jp.co.misumi.misumiecapp.data.RequestGetOrder;
import jp.co.misumi.misumiecapp.data.ResponseGetOrderDetail;
import jp.co.misumi.misumiecapp.observer.AppNotifier;

/**
 * Created by Administrator on 2016/11/1.
 */
public class OrderPayFragment extends BaseFragment {

    private MyDetailApi mMyDetailApi;

    private CartCountApi mCartCountApi;

    private AliPaymentInfo mRequestOnlinePayment;

    private static final int SDK_PAY_FLAG = 1;

    private View rootView;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    AppLog.d("resultInfo=" + resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoadingActivity.class);
                        intent.putExtra("ALIPAYRESULT", resultInfo);
//                        Bundle bundle = new Bundle();
//                        Alipay_trade_app_pay_result alipayResponse = new Alipay_trade_app_pay_result();
//                        alipayResponse.setData(resultInfo);
//                        bundle.putSerializable("ALIPAYRESULT",alipayResponse);
//                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_ALIPAY, getResources().getString(R.string.pay_dialog_6001));
//                        showDialog(getResourceString(R.string.pay_dialog_6001));
                    } else if (TextUtils.equals(resultStatus, "8000") || TextUtils.equals(resultStatus, "6004")) {
                        //错误码8000||6004 填出错误框并将支付按钮改为不可点
                        showDialog(getResourceString(R.string.pay_dialog_8000));
                        rootView.findViewById(R.id.buttonPay).setEnabled(false);
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        //错误码5000 填出错误框并将支付按钮改为不可点
                        showDialog(getResourceString(R.string.pay_dialog_5000));
                        rootView.findViewById(R.id.buttonPay).setEnabled(false);
                    } else {
                        showDialog(getResourceString(R.string.pay_dialog_error));
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    //弹出message
    private void showDialog(String message) {
        new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
            }
        }).show(message, R.string.dialog_button_kettei);
    }

    @Override
    public String getScreenId() {
        return ScreenId.PayMentOnline;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.PayMentOnline;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflateLayout(inflater, R.layout.fragment_order_pay, container, false);

        makeDataView(rootView);

        return rootView;
    }

    public OrderPayFragment() {
        mMyDetailApi = new MyDetailApi();
        mCartCountApi = new CartCountApi();
    }

    protected void makeDataView(final View rootView) {
        mRequestOnlinePayment = (AliPaymentInfo) getBundleData().getSerializable("DataContainer");

        MainActivity activity = (MainActivity) mParent;
        //从订单完了
        if (mRequestOnlinePayment.isFromComplete) {
            activity.paymentfromWhere = "1";
            mCartCountApi.connect(mParent, true);
        } else {//从订单列表和订单详情
            activity.paymentfromWhere = "2";
        }
        if (mRequestOnlinePayment.userName != null && !mRequestOnlinePayment.userName.isEmpty()) {
            setIncludeItemText(rootView.findViewById(R.id.receiverName), getResourceString(R.string.order_payment_username), mRequestOnlinePayment.userName, null);
        } else {
            setIncludeItemText(rootView.findViewById(R.id.receiverName), getResourceString(R.string.order_payment_username), getResourceString(R.string.common_status_unknown), null);
        }
        //用于显示用户地址
        String showCustomerAddress = "";
        AppConfig config = AppConfig.getInstance();
        if (config.getCustomerName() != null && !config.getCustomerName().isEmpty()) {
            showCustomerAddress = config.getCustomerName();
        }
        //显示address1 + address2组成的地址
        String showAddress1 = "";
        if (mRequestOnlinePayment.address1 != null && !mRequestOnlinePayment.address1.isEmpty()) {
            showAddress1 += mRequestOnlinePayment.address1;
        }
        if (mRequestOnlinePayment.address2 != null && !mRequestOnlinePayment.address2.isEmpty()) {
            if (showAddress1.isEmpty()) {
                showAddress1 += mRequestOnlinePayment.address2;
            } else {
                showAddress1 += " " + mRequestOnlinePayment.address2;
            }
        }
        //显示address3 + address4组成的地址
        String showAddress2 = "";
        if (mRequestOnlinePayment.address3 != null && !mRequestOnlinePayment.address3.isEmpty()) {
            showAddress2 += mRequestOnlinePayment.address3;
        }
        if (mRequestOnlinePayment.address4 != null && !mRequestOnlinePayment.address4.isEmpty()) {
            if (showAddress2.isEmpty()) {
                showAddress2 += mRequestOnlinePayment.address4;
            } else {
                showAddress2 += " " + mRequestOnlinePayment.address4;
            }
        }
        //用于显示完整地址 showCustomerAddress + showAddress1 + showAddress2
        String showAddress = "";
        if (!showCustomerAddress.isEmpty()) {
            showAddress += showCustomerAddress;
        }
        if (!showAddress1.isEmpty()) {
            if (showAddress.isEmpty()) {
                showAddress += showAddress1;
            } else {
                showAddress += "\n" + showAddress1;
            }
        }
        if (!showAddress2.isEmpty()) {
            if (showAddress.isEmpty()) {
                showAddress += showAddress2;
            } else {
                showAddress += "\n" + showAddress2;
            }
        }
        if (showAddress.isEmpty()) {
            showAddress = getResourceString(R.string.common_status_unknown);
        }

        setIncludeItemText(rootView.findViewById(R.id.address), getResourceString(R.string.order_payment_address), showAddress, null);
        if (mRequestOnlinePayment.tel != null && !mRequestOnlinePayment.tel.isEmpty()) {
            setIncludeItemText(rootView.findViewById(R.id.phone), getResourceString(R.string.order_payment_tel), mRequestOnlinePayment.tel, null);
        } else {
            setIncludeItemText(rootView.findViewById(R.id.phone), getResourceString(R.string.order_payment_tel), getResourceString(R.string.common_status_unknown), null);
        }
        if (mRequestOnlinePayment.totalPriceIncludingTax != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.order_payment_money), moneyFormat(df.format(mRequestOnlinePayment.totalPriceIncludingTax)) + "(" + getResourceString(R.string.order_list_online_pay) + ")", null);
        } else {
            setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.order_payment_money), getResourceString(R.string.common_status_unknown), null);
        }
        String deadlineInfo = "";
        if (mRequestOnlinePayment.paymentDeadlineDateTime != null && !mRequestOnlinePayment.paymentDeadlineDateTime.isEmpty()) {
            deadlineInfo = String.format(getResourceString(R.string.order_detail_deadline_info4), mRequestOnlinePayment.paymentDeadlineDateTime);
            Date nowdate = new Date();
            Date d;
            boolean compareFlag = true;//时间比较标示符 用于比较当前时间和订单最后支付时间
            try {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                d = sDateFormat.parse(mRequestOnlinePayment.paymentDeadlineDateTime);
                compareFlag = d.before(nowdate);
                AppLog.d("systemTime:" + sDateFormat.format(nowdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!compareFlag) { // 支付时间大于当前时间
                rootView.findViewById(R.id.buttonPay).setEnabled(true);
            } else {
                rootView.findViewById(R.id.buttonPay).setEnabled(false);
            }
        } else {
            deadlineInfo = String.format(getResourceString(R.string.order_detail_deadline_info4), getResourceString(R.string.common_status_unknown));
            rootView.findViewById(R.id.buttonPay).setEnabled(false);
        }
        if (mRequestOnlinePayment.totalPriceIncludingTax < 0.01) {
            rootView.findViewById(R.id.buttonPay).setEnabled(false);
        } else if (mRequestOnlinePayment.totalPriceIncludingTax > 100000000) {
            rootView.findViewById(R.id.buttonPay).setEnabled(false);
        }
        TextView deadLine = (TextView) rootView.findViewById(R.id.textDeadlineInfo);
        deadLine.setText(deadlineInfo);
        rootView.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.buttonPay).setEnabled(false);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                Date nowdate = new Date();
                Date d;
                boolean compareFlag = true;//时间比较标示符 用于比较当前时间和订单最后支付时间
                try {
                    d = sDateFormat.parse(mRequestOnlinePayment.paymentDeadlineDateTime);
                    compareFlag = d.before(nowdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //时间比较
                if (!compareFlag) { // 支付时间大于当前时间
                    RequestGetOrder req = new RequestGetOrder();
                    req.orderSlipNo = mRequestOnlinePayment.orderSlipNo;
                    mMyDetailApi.setParameter(req);
                    mMyDetailApi.connect(getActivity());
                } else {
                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                        }
                    }).show(getResourceString(R.string.order_out_time), R.string.dialog_button_close);
                }
            }
        });
    }


    protected void setIncludeItemText(View subView, String str1, String str2, String str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);

        if (TextUtils.isEmpty(str2)) {
            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);

        if (str3 == null) {
            subView.findViewById(R.id.textView3).setVisibility(View.GONE);
        } else {
            ((TextView) subView.findViewById(R.id.textView3)).setText(str3);
            subView.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
        }

    }

    private class MyDetailApi extends ApiAccessWrapper {

        RequestGetOrder mRequest;

        @Override
        protected String getScreenId() {
            return OrderPayFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        public void setParameter(RequestGetOrder request) {

            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createGetOrder(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseGetOrderDetail response = new ResponseGetOrderDetail();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    boolean isAllW = true; //订单下的item状态是否全为w
                    for (int i = 0; i < response.mItemList.size(); i++) {
                        if (!"w".equals(response.mItemList.get(i).status)) {
                            isAllW = false;
                            break;
                        }
                    }
                    if (isAllW) {//全为w可以支付
//                        Toast.makeText(getActivity(),"去支付",Toast.LENGTH_SHORT).show();
                        rootView.findViewById(R.id.buttonPay).setEnabled(true);
                        gotoPay();
                    } else {
                        String statue = getResourceString(getStatue(response));
                        String showStatue = String.format(getResourceString(R.string.show_order_status), statue);
                        new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                            @Override
                            public void onDialogResult(Dialog dlg, View view, int which) {
                            }
                        }).show(showStatue, R.string.dialog_button_close);
                    }
                    break;

                default:
                    showErrorMessage(response.errorList);
                    rootView.findViewById(R.id.buttonPay).setEnabled(true);
                    break;
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            new LostSessionProcess().run(getContext());
            rootView.findViewById(R.id.buttonPay).setEnabled(true);
        }

        @Override
        protected void onTimeout() {
            showMessageDialog(R.string.message_timeout_error);
            rootView.findViewById(R.id.buttonPay).setEnabled(true);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            showMessageDialog(R.string.message_timeout_error);
            rootView.findViewById(R.id.buttonPay).setEnabled(true);
        }

    }

    //获取订单当前显示状态
    public int getStatue(ResponseGetOrderDetail response) {
        String[] mPriorityStatus = new String[]{"a1", "a2", "a3", "z", "1", "3", "4", "x", "f"};
        int status_res = R.string.common_status_unknown;
        status_check:
        {
            for (String pri : mPriorityStatus) {
                for (ResponseGetOrderDetail.ItemInfo item : response.mItemList) {
                    if (item.status.equals(pri)) {
                        switch (pri) {
                            case "1":
                                status_res = R.string.common_status_1;
                                break;
                            case "3":
                                status_res = R.string.common_status_3;
                                break;
                            case "4":
                                status_res = R.string.common_status_4;
                                break;
                            case "f":
                                status_res = R.string.common_status_f;
                                break;
                            case "z":
                                status_res = R.string.common_status_z;
                                break;
                            case "x":
                                status_res = R.string.common_status_x;
                                break;
                            case "w":
                                status_res = R.string.common_status_w;
                                break;
                            case "a1":
                                status_res = R.string.common_status_a1;
                                break;
                            case "a2":
                                status_res = R.string.common_status_a2;
                                break;
                            case "a3":
                                status_res = R.string.common_status_a3;
                                break;
                        }
                        break status_check;
                    }
                }
            }
        }
        return status_res;
    }


    //支付 构建支付参数并调用接口
    private void gotoPay() {
//        Map<String, String> params =buildInfoMap();
//        String orderParam = buildOrderParam(params);
//        String sign = mRequestOnlinePayment.mAlipayInfo.sign;
//        final String orderInfo = orderParam + "&sign=" + sign;
        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_ALIPAY, getResources().getString(R.string.order_pay_go_to_pay));
        final String orderInfo = mRequestOnlinePayment.mAlipayInfo.query;
        AppLog.d("info=" + orderInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                Map<String, String> result = alipay.payV2(orderInfo, true);
                AppLog.d("msp:" + result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 构造订单参数列表
     *
     * @return
     */
//    public Map<String, String> buildInfoMap() {
//        Map<String, String> keyValues = new HashMap<String, String>();
//
//        keyValues.put("app_id", mRequestOnlinePayment.mAlipayInfo.app_id);
//
//        keyValues.put("biz_content", "{\"timeout_express\":\""+mRequestOnlinePayment.mAlipayInfo.mbizContent.timeout_express+"\",\"product_code\":\""+mRequestOnlinePayment.mAlipayInfo.mbizContent.product_code+"\",\"total_amount\":\""+mRequestOnlinePayment.mAlipayInfo.mbizContent.total_amount+"\",\"subject\":\""+mRequestOnlinePayment.mAlipayInfo.mbizContent.subject+"\",\"body\":\""  + mRequestOnlinePayment.mAlipayInfo.mbizContent.body + "\",\"out_trade_no\":\"" + mRequestOnlinePayment.mAlipayInfo.mbizContent.out_trade_no +  "\"}");
//
//        keyValues.put("charset", mRequestOnlinePayment.mAlipayInfo.charset);
//
//        keyValues.put("method", mRequestOnlinePayment.mAlipayInfo.method);
//
//        keyValues.put("sign_type", mRequestOnlinePayment.mAlipayInfo.sign_type);
//
//        keyValues.put("timestamp", mRequestOnlinePayment.mAlipayInfo.timestamp);
//
//        keyValues.put("version", mRequestOnlinePayment.mAlipayInfo.version);
//
//        keyValues.put("notify_url", mRequestOnlinePayment.mAlipayInfo.notify_url);
//
//        keyValues.put("format", mRequestOnlinePayment.mAlipayInfo.format);
//
//        return keyValues;
//    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     * @return
     */
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    @Override
    public void onPause() {
        mMyDetailApi.close();
        mCartCountApi.close();
        super.onPause();
    }


    private String moneyFormat(String strMoney) {
        if (strMoney != null && !strMoney.isEmpty()) {
            String[] moneys = strMoney.split("\\.");
            String lastMoney = "00";
            if (moneys.length > 1) {
                lastMoney = moneys[1];
                if (lastMoney.length() < 2) {
                    lastMoney = lastMoney + "0";
                }
            }
            if (moneys[0].length() <= 3) {
                return moneys[0] + "." + lastMoney;
            } else {
                String firstMoney = moneys[0];
                StringBuilder accum = new StringBuilder();
                int len = accum.append(moneys[0]).length();
                if (len <= 3) return strMoney;
                while ((len -= 3) > 0) { //从个位开始倒序插入
                    accum.insert(len, ",");
                }
                return accum.toString() + "." + lastMoney;
            }
        } else {
            return "0.00";
        }
    }

    /**
     * CartCountApi
     */
    private class CartCountApi extends ApiAccessWrapper {

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
            if (!cartCount.setData(result)) {
                return;
            }

            if (responseCode == NetworkInterface.STATUS_OK) {
                AppNotifier.getInstance().setCartCount(cartCount.count);
                AppConfig.getInstance().setCartCount(cartCount.count);
            }
        }

        @Override
        protected void onTimeout() {
        }

        @Override
        protected void onNetworkError(int responseCode) {
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
        }
    }


}
//-- ADD NT-LWL 16/11/17 AliPay Payment TO -
