//-- ADD NT-LWL 16/11/17 AliPay Payment FR -
package jp.co.misumi.misumiecapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.RequestSearchOrder;
import jp.co.misumi.misumiecapp.data.ResponseSearchOrder;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

/**
 * Created by Administrator on 2016/11/17.
 */
public class LoadingActivity extends Activity {
    private TimeCount timeCount;
//    private Alipay_trade_app_pay_result mRequest;

    private OrderHistoryApi mOrderHistoryApi;
    private VerifyAlipayApi mVerifyAlipayApi;
    private ImageView mImageView;
    private AnimationDrawable mAnimationDrawable;
    private String mAlipayResult;
    //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
    protected Tracker mTracker;
    //-- ADD NT-LWL 17/03/22 AliPay Payment TO -

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan()) {
            MisumiEcApp application = (MisumiEcApp) getApplication();
            mTracker = application.getDefaultTracker();
        }
        GoogleAnalytics.sendScreenTrack(mTracker, SaicataId.Loading);
        //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
        mImageView = (ImageView) findViewById(R.id.iv_bgloading);
//        mRequest = new Alipay_trade_app_pay_result();
//        mRequest = (Alipay_trade_app_pay_result) getIntent().getExtras().get("ALIPAYRESULT");
        mAlipayResult = getIntent().getStringExtra("ALIPAYRESULT");
        mOrderHistoryApi = new OrderHistoryApi();
        mVerifyAlipayApi = new VerifyAlipayApi();
        mAnimationDrawable = (AnimationDrawable) mImageView.getBackground();
        mAnimationDrawable.start();
        //设置等待时间 5秒
        timeCount = new TimeCount(BuildConfig.VerificationCountdown, 1000);
        timeCount.start();
        mVerifyAlipayApi.connect(LoadingActivity.this, false);
    }


    private void gotoOrderList() {
        cancelTime();
        mOrderHistoryApi.connect(LoadingActivity.this, false);
    }


    /**
     * 倒计时
     */
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mVerifyAlipayApi.close();
            gotoOrderList();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }
        cancelTime();
        if (mOrderHistoryApi != null) {
            mOrderHistoryApi.close();
        }
        if (mVerifyAlipayApi != null) {
            mVerifyAlipayApi.close();
        }
        super.onDestroy();
    }

    private void gotoTop() {
        Intent intent = new Intent();
        intent.putExtra("flag", "top");
        intent.setClass(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 注文履歴
     */
    private class OrderHistoryApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return ScreenId.Loading;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchOrder request = new RequestSearchOrder();
            return ApiBuilder.createSearchOrder(request);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseSearchOrder response = new ResponseSearchOrder();
            boolean pars = response.setData(result);
            if (!pars) {
                new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                    @Override
                    public void onDialogResult(Dialog dlg, View view, int which) {
                        gotoTop();
                    }
                }).show(R.string.login_info_fail, R.string.dialog_button_retry);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    Intent intent = new Intent();
                    intent.putExtra("flag", "loading");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order_list", response);
                    intent.putExtras(bundle);
                    intent.setClass(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            gotoTop();
                        }
                    }).show(R.string.login_info_unknown_fail, R.string.dialog_button_retry);
                    break;
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.login_info_fail, R.string.dialog_button_retry);
        }

        @Override
        protected void onTimeout() {
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.login_info_time_out_fail, R.string.dialog_button_retry);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.message_network_error, R.string.dialog_button_retry);
        }


    }


    /**
     * 注文履歴
     */
    private class VerifyAlipayApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return ScreenId.Loading;
        }

        @Override
        protected boolean getMethod() {
            return API_POST;
        }


        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createVerifyApi("60", mAlipayResult);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseSearchOrder response = new ResponseSearchOrder();
            boolean pars = response.setData(result);
            if (!pars) {
                cancelTime();
                new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                    @Override
                    public void onDialogResult(Dialog dlg, View view, int which) {
                        gotoTop();
                    }
                }).show(R.string.login_info_statue_error, R.string.dialog_button_retry);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
//                    gotoOrderList();
                    break;
                default:
                    cancelTime();
                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            gotoOrderList();
                        }
                    }).show(R.string.login_info_statue_error, R.string.dialog_button_retry);
                    break;
            }
        }


        @Override
        protected void onLostSession(int responseCode, String result) {
            cancelTime();
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.login_info_fail, R.string.dialog_button_retry);
        }

        @Override
        protected void onTimeout() {
            cancelTime();
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.login_info_time_out_fail, R.string.dialog_button_retry);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            cancelTime();
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    gotoTop();
                }
            }).show(R.string.message_network_error, R.string.dialog_button_retry);
        }


    }

    private void cancelTime() {
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

}
//-- ADD NT-LWL 16/11/17 AliPay Payment TO -



