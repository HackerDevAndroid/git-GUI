package jp.co.misumi.misumiecapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.VersionInfo;


/**
 *
 */
public class VersionCheckProc {

    private boolean mShowProgress;
    private Context mContext;
    private VersionCheckApi mApi;
    private VersionCheckCallback mCallback;

    public interface VersionCheckCallback {
        void success();
    }

    /**
     * runVersionCheck
     *
     * @param context
     * @param showprogress
     */
    public void run(Context context, boolean showprogress, VersionCheckCallback callback) {

        AppLog.d("Version check!!");

        mApi = new VersionCheckApi();
        mShowProgress = showprogress;
        mContext = context;
        mCallback = callback;
        versionCheck();
    }

    private void versionCheck() {
        mApi.connect(mContext);
    }

    /**
     * compareVersionInfo
     *
     * @param versionInfo
     */
    private void compareVersionInfo(final VersionInfo versionInfo) {

//        AppLog.d("versionCheck");

        int status;
        final String url;

        Integer now_version = AppConfig.getInstance().getVersionNumber();
        if (versionInfo.required_version_android != null && versionInfo.required_version_android > now_version) {
            status = 1; //必須
        } else {
            if (versionInfo.version_android != null && versionInfo.version_android > now_version) {
                status = 2; //任意
            } else {
                status = 0; //不要
            }
        }

        if (versionInfo.market_url_android != null) {
            url = versionInfo.market_url_android;
        } else {
            url = "market://details?id=";
        }


        switch (status) {
            case 0:
                mCallback.success();
                break;
            case 1:
//                AppLog.d("VERSION_CHECK_REQUIRE");
                new MessageDialog(mContext, new MessageDialog.MessageDialogListener() {
                    @Override
                    public void onDialogResult(Dialog dlg, View view, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        try {
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setAutoClose(false).show(R.string.message_update_require, R.string.dialog_button_yes);

                break;
            case 2:
//                AppLog.d("VERSION_CHECK_OPTION");
                new MessageDialog(mContext, new MessageDialog.MessageDialogListener() {
                    @Override
                    public void onDialogResult(Dialog dlg, View view, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dlg.dismiss();
                            mCallback.success();
                            return;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        try {
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setAutoClose(false).show(R.string.message_update_exist, R.string.dialog_button_yes, R.string.dialog_button_no);

                break;
        }
    }


    /**
     * VersionCheckApi
     */
    private class VersionCheckApi extends ApiAccessWrapper {
        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createVersionCheck();
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {

            if (responseCode == NetworkInterface.STATUS_OK) {
                VersionInfo versionInfo = new VersionInfo();
                if (versionInfo.setData(result)) {
                    compareVersionInfo(versionInfo);
                } else {
                    // パースエラー
                    showRetryMessage(R.string.message_unknown_error);
                }
            } else {
                showRetryMessage(R.string.message_unknown_error);
            }

        }

        @Override
        protected void onNetworkError(int responseCode) {
            showRetryMessage(R.string.message_network_error);
        }

        @Override
        protected void onTimeout() {
            showRetryMessage(R.string.message_network_error);
        }

        private void showRetryMessage(int messageid) {
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    versionCheck();
                }
            }).show(messageid, R.string.dialog_button_retry);
        }

        @Override
        protected String getScreenId() {
            return ScreenId.VersionCheck;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        protected void onStartConnect() {
            if (mShowProgress) {
                super.onStartConnect();
            }
        }

        @Override
        protected void onEndConnect() {
            if (mShowProgress) {
                super.onEndConnect();
            }
        }


    }
}

