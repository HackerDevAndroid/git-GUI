package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.util.ClipBoard;
import jp.co.misumi.misumiecapp.util.ViewBgUtil;


/**
 * デバイスインフォ画面
 */
public class DeviceInfoFragment extends BaseFragment {

    String osVersion;
    String deviceName;
    String appVersion;

    String info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflateLayout(inflater, R.layout.fragment_device_info, container, false);


        //端末情報の取得
        osVersion = Build.VERSION.RELEASE;
        deviceName = Build.BRAND + " " + Build.DEVICE;
        appVersion = BuildConfig.VERSION_NAME;

//        TextView mOSversion = (TextView) view.findViewById(R.id._OSversion);
//        mOSversion.setText(osVersion);
//
//        TextView mDeviceName = (TextView) view.findViewById(R.id._devicename);
//        mDeviceName.setText(deviceName);
//
//        TextView mAppVersion = (TextView) view.findViewById(R.id._appversion);
//        mAppVersion.setText(appVersion);

        //それぞれの項目のinclude
        View osVersionInclude = view.findViewById(R.id.OSversion);
        View deviceNameInclude = view.findViewById(R.id.devicename);
        View appVersionInclude = view.findViewById(R.id.appversion);

        //項目setText
        TextView osVersionLabel = (TextView) osVersionInclude.findViewById(R.id.textView1);
        osVersionLabel.setText(R.string.deviceinfo_label_os_ver);
        TextView deviceNameLabel = (TextView) deviceNameInclude.findViewById(R.id.textView1);
        deviceNameLabel.setText(R.string.deviceinfo_label_device_name);
        TextView appVersionLabel = (TextView) appVersionInclude.findViewById(R.id.textView1);
        appVersionLabel.setText(R.string.deviceinfo_label_app_ver);

        //情報setText
        TextView osVersionInfo = (TextView) osVersionInclude.findViewById(R.id.textView3);
        osVersionInfo.setText(osVersion);
        TextView deviceNameInfo = (TextView) deviceNameInclude.findViewById(R.id.textView3);
        deviceNameInfo.setText(deviceName);
        TextView appVersionInfo = (TextView) appVersionInclude.findViewById(R.id.textView3);
        appVersionInfo.setText(appVersion);

        ViewBgUtil.requestLayout(osVersionInclude, R.id.viewDiv, R.id.viewLeft, R.id.textView3);
        ViewBgUtil.requestLayout(deviceNameInclude, R.id.viewDiv, R.id.viewLeft, R.id.textView3);
        ViewBgUtil.requestLayout(appVersionInclude, R.id.viewDiv, R.id.viewLeft, R.id.textView3);

        info = String.format(getString(R.string.deviceinfo_format_copy), osVersion, deviceName, appVersion);

        //クリップボード
        Button btn = (Button) view.findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipBoard.setData(getContext(), info);
                new MessageDialog(getContext(), null).show(R.string.message_request_copy, R.string.dialog_button_close);
            }
        });

        WebView mWebView = (WebView) view.findViewById(R.id.webLicense);
        mWebView.clearHistory();
        mWebView.loadUrl(AppConst.LicenseFile);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        return view;
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.DeviceInfo;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


	//サイカタ
	protected String getSaicataId() {
		return jp.co.misumi.misumiecapp.SaicataId.DeviceInfo;
	}
}


