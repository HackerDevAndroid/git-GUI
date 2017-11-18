package jp.co.misumi.misumiecapp.api;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.ApiResponseDispatcher;
import jp.co.misumi.misumiecapp.LostSessionProcess;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ErrorList;
import jp.co.misumi.misumiecapp.observer.ApiAccessObserver;
import jp.co.misumi.misumiecapp.observer.NetworkObserver;

/**
 * ApiAccessWrapper
 */
public abstract class ApiAccessWrapper {

    private boolean isEnabled = false;
    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    //--ADD NT-LWL 17/09/04 Loading FR -
    private Dialog dialog;
    //--ADD NT-LWL 17/09/04 Loading TO -

    public static final boolean	API_POST	= ApiAccessObserver.API_POST;
    public static final boolean	API_GET		= ApiAccessObserver.API_GET;

    protected abstract String getScreenId();

    /**
     * connect
     * @param context
     */
    public void connect(Context context){
        mContext = context;
        onStartConnect();
        ApiAccessObserver.getInstance().requestApi(getMethod(), getParameter(), mObserver);
        isEnabled = true;
    }

    public void connect(Context context,boolean needProgress){
        mContext = context;
        if(needProgress) {
            onStartConnect();
        }else{}
        ApiAccessObserver.getInstance().requestApi(getMethod(), getParameter(), mObserver);
        isEnabled = true;
    }

    /**
     * close
     */
    public void close(){
        hideProgress();
        if (isEnabled) {
            ApiAccessObserver.getInstance().removeObserver(mObserver);
            isEnabled = false;
        }
    }

    protected Context getContext(){
        return mContext;
    }

    /**
     * getMethod
     * @return
     */
    protected boolean getMethod(){
        return ApiAccessObserver.API_POST;
    }

    /**
     * onStartConnect
     */
    protected void onStartConnect(){
        showProgress();
    }

    /**
     * onEndConnect
     */
    protected void onEndConnect(){
        hideProgress();
    }


    /**
     * onLostSession
     * @param responseCode
     * @param result
     */
    protected void onLostSession(int responseCode, String result) {
        new LostSessionProcess().run(getContext());
    }

    /**
     * onNetworkError
     * @param responseCode
     */
    protected void onNetworkError(int responseCode){
//        new MessageDialog(mContext, null).show(R.string.message_network_error, R.string.dialog_button_ok);
		showMessageDialog(R.string.message_network_error);
    }

    protected void onTimeout(){
//        new MessageDialog(mContext, null).show(R.string.message_timeout_error, R.string.dialog_button_ok);
		showMessageDialog(R.string.message_timeout_error);
    }

    protected void showErrorMessage(ErrorList errorList){
        if (errorList == null){
            showUnknownError();
        }else {
            String message = errorList.getErrorMessage(getScreenId());
//            new MessageDialog(mContext, null).show(message, R.string.dialog_button_ok);
			showMessageDialog(message);
        }
    }

    NetworkObserver mObserver = new NetworkObserver() {
        @Override
        public void notice(int responseCode, String result) {
            isEnabled = false;
            ApiAccessObserver.getInstance().removeObserver(mObserver);
            onEndConnect();
            new ApiResponseDispatcher().dispatch(responseCode, result, mCallback);
        }
    };

    ApiResponseDispatcher.CallBackDispatcher mCallback = new ApiResponseDispatcher.CallBackDispatcher() {

        /**
         * onResult
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            ApiAccessWrapper.this.onResult(responseCode, result);
        }

        /**
         * onNetworkError
         * @param responseCode
         */
        @Override
        public void onNetworkError(int responseCode) {
            ApiAccessWrapper.this.onNetworkError(responseCode);
        }

        /**
         * onLostSession
         * @param responseCode
         * @param result
         */
        @Override
        public void onLostSession(int responseCode, String result) {
            ApiAccessWrapper.this.onLostSession(responseCode, result);
        }

        @Override
        public void onTimeOut() {
            ApiAccessWrapper.this.onTimeout();
        }
    };


    /**
     * プログレスバー表示
     */
    private void showProgress(){
        //--UDP NT-LWL 17/09/04 Loading FR -
//        if (mProgressDialog != null){
//            mProgressDialog.dismiss();
//        }
//
//        String str = mContext.getResources().getString(R.string.message_progress_transmitting);
//
//        mProgressDialog = new ProgressDialog(mContext);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setMessage(str);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        if (dialog !=null){
            dialog.dismiss();
        }
        dialog = new Dialog(mContext,R.style.progressDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.progress_dialog,null);
        final ImageView v1 = (ImageView) view.findViewById(R.id.loading_progress);
        final AnimationDrawable mAnimationDrawable = (AnimationDrawable) v1.getBackground();
        mAnimationDrawable.start();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,200);
        dialog.setContentView(view,params);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mAnimationDrawable!=null) {
                    mAnimationDrawable.stop();
                }
            }
        });

        dialog.show();
        //--UDP NT-LWL 17/09/04 Loading TO -
    }

    /**
     * プログレスバー非表示
     */
    private void hideProgress(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        //--ADD NT-LWL 17/09/04 Loading FR -
        if (dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        //--ADD NT-LWL 17/09/04 Loading TO -
    }

    /**
     * showUnknownError
     */
    private void showUnknownError(){

//        new MessageDialog(mContext, null).show(R.string.message_unknown_error, R.string.dialog_button_ok);
		showMessageDialog(R.string.message_unknown_error);
    }

    /**
     * getParameter
     * @return
     */
    public abstract HashMap<String, String> getParameter();


    /**
     * onResult
     * @param responseCode
     * @param result
     */
    public abstract void onResult(int responseCode, String result);


	//
	protected void showMessageDialog(String message) {

        new MessageDialog(mContext, getMessageDialogListener()).show(message, R.string.dialog_button_yes);
	}

	protected void showMessageDialog(int mesResId) {

        new MessageDialog(mContext, getMessageDialogListener()).show(mesResId, R.string.dialog_button_yes);
	}

	//ダイアログボタン押下リスナー
	protected MessageDialog.MessageDialogListener getMessageDialogListener() {

		return null;
	}

}

