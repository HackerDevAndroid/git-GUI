package jp.co.misumi.misumiecapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager.LayoutParams;

import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.header.HeaderView;

//import com.adobe.mobile.Config;


public abstract class AppActivity extends AppCompatActivity {

    private FragmentController mFragmentController = null;
    private HeaderView mHeaderView = null;


    protected abstract int getContentViewId();

    protected abstract HeaderView createHeaderView();

    protected abstract void setFragment(Bundle savedInstanceState);

    public FragmentController getFragmentController() {
        return mFragmentController;
    }

    public HeaderView getHeaderView() {
        return mHeaderView;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeSetContentView();

        setContentView(getContentViewId());

        mFragmentController = new FragmentController(this);
        mHeaderView = createHeaderView();

        //2015/10/22 Appクラスに移動
//        NetworkInterface.createInstance(this);
//        ApiAccessObserver.createInstance();
//        ErrorMessageManager.createInstance(this);

        setFragment(savedInstanceState);
    }

    /**
     * beforeSetContentView
     */
    protected void beforeSetContentView() {
        //キーボードを非表示にする
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragmentController.close();
    }

    //サイカタ
    @Override
    protected void onResume() {
        super.onResume();
//       Config.collectLifecycleData();
    }

    @Override
    protected void onPause() {
        super.onPause();
//       Config.pauseCollectingLifecycleData();
    }

}
