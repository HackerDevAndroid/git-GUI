//-- ADD NT-SLJ 16/11/12 Live800 FR -
package jp.co.misumi.misumiecapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.adapter.QuestionAdapter;
import jp.co.misumi.misumiecapp.data.Live800Data;
import jp.co.misumi.misumiecapp.data.WebViewData;
import jp.co.misumi.misumiecapp.util.BtnClickUtils;
import jp.co.misumi.misumiecapp.widget.CallPhoneDialog;

/**
 * Created date: 2016/11/12 13:06
 * Description: Consulting problems
 */
public class ConsultingProblemsFragment extends BaseFragment {
    //显示问题的
    private ListView lvQuestions;
    //配置文件解析的的数据
    private Live800Data live800Data;
    private AssetManager assetManager;
    //电话号码
    private TextView tel;
    private static final int REQUEST_CALL_PHONE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_consulting_problems, container, false);
        lvQuestions = (ListView) v.findViewById(R.id.listView_questtions);
        tel = (TextView) v.findViewById(R.id.tell);
        String json = getFromAssets("live800_config.json");
        live800Data = new Live800Data();
        if (live800Data.setData(json)) {
            QuestionAdapter adapter = new QuestionAdapter(mParent, R.layout.item_question_lay, R.id.question_name, live800Data.questionList);
            lvQuestions.setAdapter(adapter);
            //点击咨询问题
            lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //防止双击
                    if (!BtnClickUtils.isFastDoubleClick()) {
                        WebViewData webViewData = new WebViewData(live800Data.prefixURL);
                        Live800Data.Question question = (Live800Data.Question) parent.getAdapter().getItem(position);
                        webViewData.question = question;
                        SubActivity.launchActivity(ConsultingProblemsFragment.this, SubActivity.SUB_TYPE_WEB_VIEW,
                                                   SubActivity.SUB_TYPE_WEB_VIEW, webViewData);
                    }
                }
            });
            if (!TextUtils.isEmpty(live800Data.tel)) {
                tel.setText(live800Data.tel);
                tel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickTell();
                        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800,live800Data.tel);
                    }
                });
            }
        }
        return v;
    }

    /**
     * 点击电话号码
     */
    private void clickTell() {
        new CallPhoneDialog(mParent, live800Data.tel, new CallPhoneDialog.OnCallPhoneListen() {
            @Override
            public void onCall() {
                checkPermission();
                GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800,ConsultingProblemsFragment.this.getString(R.string.call));
            }

            @Override
            public void onCacel() {
                GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800,ConsultingProblemsFragment.this.getString(R.string.cancel_call));
            }
        }).show();
    }

    /**
     * 检查权限
     */
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(mParent, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
            if (Build.VERSION.SDK_INT < 23) {

                showMessage();

            }
        } else {
            callPhone();
        }
    }

    /**
     * 显示设置权限提示框
     */
    private void showMessage() {
        new MessageDialog(mParent, new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                switch (which){
                    //立即设置
                    case DialogInterface.BUTTON_POSITIVE:
                        openSetting();
                        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800,ConsultingProblemsFragment.this.getString(R.string.set));
                        break;
                    //取消
                    case DialogInterface.BUTTON_NEGATIVE:
                        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_LIVE800,ConsultingProblemsFragment.this.getString(R.string.cancel_set));
                        break;
                    default:
                }
            }
        }).show(R.string.open_permission, R.string.set, R.string.cancel);
    }

    /**
     * 打开设置界面
     */
    private void openSetting() {
        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            showToast(getResourceString(R.string.open_error_tip));
        }
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + tel.getText().toString().trim());
            intent.setData(data);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户点击允许
                callPhone();
            } else {
                //用户点击拒绝
            }
        }

    }

    /**
     * Get content in assest
     *
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName) {
        BufferedReader bufReader = null;
        try {
            assetManager = mParent.getAssets();
            bufReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                Result += line;
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getScreenId() {
        return ScreenId.ConsultingProblems;
    }

    @Override
    protected String getSaicataId() {
        return SaicataId.ConsultingProblems;
    }

    @Override
    public void onHeaderEvent(int event, Objects objects) {
        super.onHeaderEvent(event, objects);
        mParent.finish();
    }
}
//-- ADD NT-SLJ 16/11/12 Live800 TO -