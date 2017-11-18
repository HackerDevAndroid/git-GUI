package jp.co.misumi.misumiecapp.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.R;

/**
 * Author: liuwanlin
 * E-mail: wanlin.liu@newtouch.cn
 * Created date: 2016/11/25 14:38
 * Description: 拨号确认界面
 */
public class CallPhoneDialog implements View.OnClickListener {
    View call;
    View cacel;
    TextView tell;
    Dialog mAlertDialog;
    String number;
    Context context;
    OnCallPhoneListen onCallPhoneListen;

    public interface OnCallPhoneListen {
        void onCall();

        void onCacel();
    }

    public CallPhoneDialog(Context context, String number, OnCallPhoneListen onCallPhoneListen) {
        this.context = context;
        this.number = number;
        this.onCallPhoneListen = onCallPhoneListen;
    }

    public void show() {
        mAlertDialog = new Dialog(context, R.style.MessageDialog);
        LayoutInflater inflater = mAlertDialog.getLayoutInflater();
        View view = inflater.inflate(R.layout.callphone_dialog, null);
        cacel = view.findViewById(R.id.cacel);
        call = view.findViewById(R.id.call);
        tell = (TextView) view.findViewById(R.id.tell);
        tell.setText(number);

        call.setOnClickListener(this);
        cacel.setOnClickListener(this);

        mAlertDialog.setContentView(view);
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cacel:
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                break;
            case R.id.call:
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                if (onCallPhoneListen != null) {
                    onCallPhoneListen.onCall();
                }
                break;
        }
    }
}
