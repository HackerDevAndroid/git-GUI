//--ADD NT-LWL 17/05/18 QR scan FR -
package jp.co.misumi.misumiecapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.Tracker;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.fragment.BaseFragment;
import jp.co.misumi.misumiecapp.header.HeaderView;

/**
 * QR 识别画面
 */
public class ScanActivity extends AppActivity {
    //进入扫码请求code
    public static final int REQUEST_CODE = 101;
    //header高度
    private int headerHeight = 0;
    //跟踪器
    protected Tracker mTracker;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MisumiEcApp application = (MisumiEcApp) getApplication();
        mTracker = application.getDefaultTracker();
        Intent intent = getIntent();
        headerHeight = intent.getIntExtra("height", 0);
        if (headerHeight != 0) {
            View header = findViewById(R.id.header);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) header.getLayoutParams();
            params.height = headerHeight;
        }

        //添加屏幕跟踪
        GoogleAnalytics.sendScreenTrack(mTracker, getSaicataId());
    }

    // 点击返回按钮
    public void clickBack(View view) {
        finish();
    }

    @Override
    protected HeaderView createHeaderView() {
//        SubHeader view = new SubHeader(this);
//        view.addHeaderEventListener(mHeaderEvent);
        return null;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void setFragment(Bundle savedInstanceState) {
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.custom_scan_layout);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.body, captureFragment).commit();
    }

    /**
     * 进入扫码画面
     *
     * @param fragment
     */
    public static void launchActivity(BaseFragment fragment, int headerHeight) {
        Intent intent = new Intent(fragment.getContext(), ScanActivity.class);
        intent.putExtra("height", headerHeight);
        MainActivity mainActivity = (MainActivity) fragment.getContext();
        mainActivity.startActivityForResult(intent, REQUEST_CODE);
    }
//    HeaderView.HeaderEventListener mHeaderEvent = new HeaderView.HeaderEventListener() {
//
//        @Override
//        public void onHeaderEvent(int event, Objects objects) {
//            getFragmentController().onHeaderEvent(event, objects);
//        }
//    };
    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            // 成功回调
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            // 失败回调
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };

    @Override
    protected void onDestroy() {
        analyzeCallback = null;
        super.onDestroy();
    }


    public String getSaicataId() {
        return SaicataId.QRScanId;
    }
}
//--ADD NT-LWL 17/05/18 QR scan TO -