package jp.co.misumi.misumiecapp.observer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * AppNotifier
 */
public class AppNotifier {

    // ログイン
    public static final int USER_LOGIN = 0x0001;
    public static final int USER_LOGOUT = 0x0002;
    // カート数変更
    public static final int CART_CHANGED = 0x0004;
    public static final int CART_ADD_REQ = 0x0008;
    // 現地コード変更
    public static final int LOCAL_SUBSIDIARY_REQ = 0x0010;

    // ストーク情報変更
    public static final int STOKE_CHANGED = 0x0020;
    // 直送先変更
    public static final int ORDER_RECEIVER_CHANGED = 0x0040;
    // フラグメントリフレッシュ依頼
    public static final int UPDATE_FRAGMENT = 0x0080;
    // 別ユーザーログイン
    public static final int USER_NEW_LOGIN = 0x0100;

    private static AppNotifier mInstance = null;

    private static List<AppNoticeTarget> observers = new ArrayList();
    private Handler mHandler;

    public class AppNotice{
        public int event;
        public Object option;
    }

    public interface AppNoticeListener{
        void appNotice(AppNotice notice);
    }

    private class AppNoticeTarget{
        int filter;
        AppNoticeListener listener;
        AppNoticeTarget(AppNoticeListener appNoticeListener, int f){
            listener = appNoticeListener;
            filter = f;
        }
        boolean isTarget(int f){

            return (filter & f) != 0;

        }
    }

    /**
     * AppStateObserver
     * @param context
     */
    private AppNotifier(Context context){
        mHandler = new Handler(mCallback);
    }

    /**
     * AppStateObserver
     * @param context
     * @return
     */
    public static AppNotifier createInstance(Context context){
        if (mInstance == null){
            mInstance = new AppNotifier(context);
        }
        return mInstance;
    }

    /**
     * getInstance
     * @return
     */
    public static AppNotifier getInstance(){
        return mInstance;
    }

    /**
     * addListener
     * @param listener
     */
    public void addListener(AppNoticeListener listener, int filter){
        synchronized (mInstance) {
            AppLog.d("Add AppStateListener");
            remove(listener);
            observers.add(new AppNoticeTarget(listener,filter));
        }
    }

    /**
     * remove
     * @param listener
     */
    private void remove(AppNoticeListener listener){
        int pos = -1;
        for (int ii = 0 ; ii < observers.size() ; ii++){
            if (observers.get(ii).listener.equals(listener)){
                pos = ii;
                break;
            }
        }
        if (pos >= 0){
            observers.remove(pos);
        }
    }

    /**
     * removeListener
     * @param listener
     */
    public void removeListener(AppNoticeListener listener){
        synchronized (mInstance) {
            AppLog.d("Remove AppStateListener");
            remove(listener);
        }
    }


    /**
     * setLogin
     * @param login
     */
    public void setLogin(boolean login){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            if (login) {
                msg.what = USER_LOGIN;
            } else {
                msg.what = USER_LOGOUT;
            }
            msg.sendToTarget();
        }
    }

    /**
     * setNewLogin
     */
    public void setNewLogin(){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = USER_NEW_LOGIN;msg.sendToTarget();
        }
    }

    /**
     * setCartCount
     * @param count
     */
    public void setCartCount(Integer count){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = CART_CHANGED;
            msg.obj = count;
            msg.sendToTarget();
        }
    }

    /**
     * updateCurrentFragment
     */
    public void updateCurrentFragment(){
        Message msg = getInstance().mHandler.obtainMessage();
        msg.what = UPDATE_FRAGMENT;
        msg.obj = null;
        msg.sendToTarget();
    }

    /**
     * add_count
     * @param add_count
     */
    public void addCartCount(Integer add_count){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = CART_ADD_REQ;
            msg.obj = add_count;
            msg.sendToTarget();
        }
    }

    public void updateCartCount(){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = CART_ADD_REQ;
            msg.obj = 0;
            msg.sendToTarget();
        }
    }

    /**
     * changeLocalSubsidiary
     */
    public void changeLocalSubsidiary(){

        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = LOCAL_SUBSIDIARY_REQ;
            msg.obj = null;
            msg.sendToTarget();
        }
    }

    /**
     * changeOrderReceiver
     * @param object
     */
    private void changeOrderReceiver(Object object){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = ORDER_RECEIVER_CHANGED;
            msg.obj = object;
            msg.sendToTarget();
        }
    }

    /**
     * changeStoke
     * @param object
     */
    private void changeStoke(Object object){
        synchronized (mInstance) {
            Message msg = getInstance().mHandler.obtainMessage();
            msg.what = STOKE_CHANGED;
            msg.obj = object;
            msg.sendToTarget();
        }
    }


    /**
     * notice
     * @param notice
     */
    void notice(AppNotice notice){
        synchronized (mInstance) {
//            AppLog.d("AppNotifier notice event=" + notice.event);
            for (int ii = 0; ii < observers.size(); ii++) {
                AppNoticeTarget o = observers.get(ii);
                if( o.isTarget(notice.event) ) {
                    AppLog.d("AppNotifier listener=" + o.toString());
                    o.listener.appNotice(notice);
                }
            }
        }
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            AppNotice notice = new AppNotice();
            notice.event = msg.what;
            notice.option = msg.obj;
            notice(notice);
            return false;
        }
    };

}

