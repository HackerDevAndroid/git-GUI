package jp.co.misumi.misumiecapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class AppLifecycle implements Application.ActivityLifecycleCallbacks {

    private AppLifecycleCallback callback;

    public interface AppLifecycleCallback {
        void onForeground();

        void onBackground();
    }

    public AppLifecycle(AppLifecycleCallback lifecycleCallback) {
        callback = lifecycleCallback;
    }


    private int running = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        AppLog.d("Application onActivityStarted " + running);
        if (++running == 1) {
            callback.onForeground();
        } else if (running > 1) {
            callback.onForeground();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        AppLog.d("Application onActivityStopped " + running);
        if (--running == 0) {
            callback.onBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}