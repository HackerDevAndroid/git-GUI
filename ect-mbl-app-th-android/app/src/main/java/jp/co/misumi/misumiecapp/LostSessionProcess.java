package jp.co.misumi.misumiecapp;

import android.content.Context;

import jp.co.misumi.misumiecapp.activity.LoginActivity;

public class LostSessionProcess {
    public void run(Context context) {
        LoginActivity.launchActivity(context, LoginActivity.MODE_LOST_SESSION);
    }
}
