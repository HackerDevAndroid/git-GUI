package jp.co.misumi.misumiecapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager.LayoutParams;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.fragment.LaunchFragment;


public class SchemeActivity extends AppCompatActivity {

    public final static String BROADCAST_MESSAGE_DIALOG_DISMISS = "BROADCAST_MESSAGE_DIALOG_DISMISS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //--ADD NT-LWL 17/08/30 Launch FR -
        // 判断如果是协议启动 第一次保存标识
        if (LaunchFragment.isFirstEnter(this)) {
            LaunchFragment.saveFirstFlag(this);
        }
        //--ADD NT-LWL 17/08/30 Launch TO -
        beforeSetContentView();

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scheme);


        //BroadcastReceiverでスキマー起動時のダイアログを閉じる
        {
            Intent i = new Intent(BROADCAST_MESSAGE_DIALOG_DISMISS);
//			i.putExtra("key", "SchemeActivity");
            sendBroadcast(i);
        }


        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri;

        if (Intent.ACTION_VIEW.equals(action)) {

            uri = intent.getData();

            //スキーマ起動
            if (uri != null) {
                launchActivity(uri);
            }
        }

        //それ以外は何もしない
        finish();

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
    }


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        //スキーム起動中にスキームで二重起動された場合対応
        //前回のインスタンスを破棄
        finish();

        //再度アプリを起動する
        startActivity(intent);
    }


    /**
     * launchActivity
     *
     * @param uri
     */
    public void launchActivity(Uri uri) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Uri", uri);

        startActivity(intent);
    }

}
