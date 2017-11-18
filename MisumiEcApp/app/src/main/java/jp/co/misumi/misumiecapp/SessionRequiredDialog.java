package jp.co.misumi.misumiecapp;

import android.content.Context;

import jp.co.misumi.misumiecapp.activity.LoginActivity;

public class SessionRequiredDialog {

    public boolean judgeLaunchRestriction(final Context context){
        if (!AppConfig.getInstance().hasSessionId()){

            LoginActivity.launchActivity(context,LoginActivity.MODE_REQUIRED);
//            context.startActivity(new Intent(context, LoginActivity.class));
//            new MessageDialog(context, new MessageDialog.MessageDialogListener() {
//                @Override
//                public void onDialogResult(Dialog dlg, View view, int which) {
//                    if (which == DialogInterface.BUTTON_POSITIVE){
//                        context.startActivity(new Intent(context, LoginActivity.class));
//                    }
//                }
//            }).show(R.string.message_limited_session, R.string.dialog_button_yes, R.string.dialog_button_no);
            return false;
        }
        return true;
    }

}
