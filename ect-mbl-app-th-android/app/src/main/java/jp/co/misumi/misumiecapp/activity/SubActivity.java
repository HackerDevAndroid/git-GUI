package jp.co.misumi.misumiecapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.Objects;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.fragment.BaseFragment;
import jp.co.misumi.misumiecapp.fragment.ConsultingProblemsFragment;
import jp.co.misumi.misumiecapp.fragment.EstimateConfirmFragment;
import jp.co.misumi.misumiecapp.fragment.OrderConfirmFragment;
import jp.co.misumi.misumiecapp.fragment.WebViewFragment;
import jp.co.misumi.misumiecapp.header.HeaderView;
import jp.co.misumi.misumiecapp.header.SubHeader;


/**
 * サブ（カート見積注文画面、別タスク）
 */
public class SubActivity extends AppActivity {

    //CartFragmentから遷移の SubActivityの画面種類
    public static final int SUB_TYPE_ORDER = 1;
    public static final int SUB_TYPE_ESTIMATE = 2;
    public static final int SUB_TYPE_WEB_VIEW = 3;
    //-- ADD NT-SLJ 16/11/11 Live800 FR -
    public static final int SUB_TYPE_CONSULTATION = 4;
    public static final int REQUEST_CODE_CONSULTION_VIEW = 1238;
    //-- ADD NT-SLJ 16/11/11 Live800 TO -
    //startActivityForResultの識別定数
    public static final int REQUEST_CODE_CART = 1234;
    //	public static final int	REQUEST_CODE_LOGIN	= 1235;
    public static final int REQUEST_CODE_WEB_VIEW = 1236;


    //BroadcastReceiverで別タスクを閉じる
    MyBroadcastReceiver myReceiver;
    IntentFilter intentFilter;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent i) {

//			Toast.makeText(context, "message: "+ isFinishing(), Toast.LENGTH_LONG).show();

            if (!isFinishing()) {
                finish();
            }
        }
    }


    @Override
    protected int getContentViewId() {

        return R.layout.activity_sub;
    }

    @Override
    protected HeaderView createHeaderView() {
        SubHeader view = new SubHeader(this);
        view.addHeaderEventListener(mHeaderEvent);
        return view;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //BroadcastReceiverでスキマー起動時のダイアログを閉じる
        myReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter(SchemeActivity.BROADCAST_MESSAGE_DIALOG_DISMISS);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);

        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        AppLog.d("onBackPressed");
        if (!getFragmentController().onBackKey()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void setFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {

            //フラグメントにそのまま Bundleデータを受け渡す
            Bundle bundle = getIntent().getExtras();
            int openType = bundle.getInt("openType");

            //AppConst.SUB_TYPE_ORDER		注文確認
            //AppConst.SUB_TYPE_ESTIMATE	見積確認
            BaseFragment fragment = null;

            if (openType == SUB_TYPE_ORDER) {
                fragment = new OrderConfirmFragment();
            } else if (openType == SUB_TYPE_ESTIMATE) {
                fragment = new EstimateConfirmFragment();
            } else if (openType == SUB_TYPE_WEB_VIEW) {
                fragment = new WebViewFragment();
            }
            //-- ADD NT-SLJ 16/11/11 Live800 FR -
            else if (openType == SUB_TYPE_CONSULTATION) {
                fragment = new ConsultingProblemsFragment();
            }
            //-- ADD NT-SLJ 16/11/11 Live800 TO -
            fragment.setBundleData(bundle);
            getFragmentController().replaceFragment(fragment, FragmentController.ANIMATION_NON);
        }
    }


    /**
     *
     */
    HeaderView.HeaderEventListener mHeaderEvent = new HeaderView.HeaderEventListener() {

        @Override
        public void onHeaderEvent(int event, Objects objects) {
            getFragmentController().onHeaderEvent(event, objects);
        }
    };


    /**
     * launchActivity
     *
     * @param fragment
     * @param openType
     * @param dataContainer
     */
    public static void launchActivity(BaseFragment fragment, int openType, int requestCode, DataContainer dataContainer) {
        Intent intent = new Intent(fragment.getContext(), SubActivity.class);
        intent.putExtra("openType", openType);    //注文か見積もりか区別
        intent.putExtra("dataContainer", dataContainer);

        //TODO:圧縮
//        intent.putExtra("dataContainer", DataContainerUtil.convDataContainer(dataContainer));
        fragment.startActivityForResult(intent, requestCode);
    }


}
