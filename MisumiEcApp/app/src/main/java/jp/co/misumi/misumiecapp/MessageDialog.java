package jp.co.misumi.misumiecapp;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.activity.SchemeActivity;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * 汎用メッセージダイアログ
 */
public class MessageDialog {

//    AlertDialog mAlertDialog = null;
    Dialog mAlertDialog = null;
    Button mButtonPositive;
    Button mButtonNegative;
    Button mButtonMedium;
    boolean mAutoClose = true;

    public interface MessageDialogListener{
        void onDialogResult(Dialog dlg, View view, int which);
    }

    Context mContext;
    MessageDialogListener mInterface;

	//BroadcastReceiverでスキマー起動時のダイアログを閉じる
	MyBroadcastReceiver myReceiver;
	IntentFilter intentFilter;

	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent i){

            mAlertDialog.dismiss();

//			android.widget.Toast.makeText(mContext, "TODO:デバグメッセージ、スキーマ起動", android.widget.Toast.LENGTH_LONG).show();
		}
	}


    public MessageDialog(Context context, MessageDialogListener dialogListener){
        mContext = context;
        mInterface = dialogListener;

		//BroadcastReceiverでスキマー起動時のダイアログを閉じる
		myReceiver = new MyBroadcastReceiver();
		intentFilter = new IntentFilter(SchemeActivity.BROADCAST_MESSAGE_DIALOG_DISMISS);
		mContext.registerReceiver(myReceiver, intentFilter);
    }

    public void show(int message){
        show(message, 0, R.string.dialog_button_yes);
    }
    public void show(String message){
        show(message, 0, R.string.dialog_button_yes);
    }

    public void show(int message, int positive){
        show(message, 0, positive);
    }
    public void show(String message, int positive){
        show(message, 0, positive);
    }

    public void show(int message, int positive, int negative){
        show(mContext.getResources().getString(message), positive, negative);
    }


    public void show(String message, int positive, int negative){
    	show(null, message, null, positive, negative, false);
    }

    public void show(String title, String message, int positive, int negative){
    	show(title, message, null, positive, negative, false);
    }

    public void showT(String title, View view, int positive, int negative){
    	show(title, null, view, positive, negative, false);
    }

    public void show(View view, int positive, int negative){
    	show(null, view, positive, negative);
    }

    public void showCart(View view, int positive, int negative){

    	show(null, null, view, positive, negative, true);
    }

    public void showCart(View view, int positive, int negative, int medium){

    	show(null, null, view, positive, negative, medium, true);
    }

    private void show(String message, View view, int positive, int negative){
    	show(null, message, view, positive, negative, false);
	}

    public void show(CharSequence message,int positive, int negative){
        show(null, message, null, positive, negative, false);
    }

    public void showCart(String title, String message, int positive, int negative) {
        show(title, message, null, positive, negative, true);
    }

    private void show(String title, CharSequence message, View view, int positive, int negative, boolean cartFlag){

		show(title, message, view, positive, negative, 0, cartFlag);
	}

    private void show(String title, CharSequence message, View view, int positive, int negative, int medium, boolean cartFlag){

        if (mAlertDialog != null){
            mAlertDialog.dismiss();
        }

/*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		if (title != null) {
	        alertDialogBuilder.setTitle(title);
		}
		if (message != null) {
	        alertDialogBuilder.setMessage(message);
		}

        if (positive != 0){
            alertDialogBuilder.setPositiveButton(positive, null);
        }

        if (negative != 0) {
            alertDialogBuilder.setNegativeButton(negative, null);
        }

        alertDialogBuilder.setCancelable(false);
        mAlertDialog = alertDialogBuilder.create();

		if (view != null) {
			mAlertDialog.setView(view);
		}
*/


		mAlertDialog = new Dialog(mContext, R.style.MessageDialog);
        mAlertDialog.setOnDismissListener(dismissListener);
        mAlertDialog.setCancelable(false);

		LayoutInflater inflater = mAlertDialog.getLayoutInflater();
		final View childView = inflater.inflate(R.layout.dialog_layout, null, false);
		ViewUtil.setSplitMotionEventsToAll(childView);

		View vw;
		TextView tv;

        mButtonPositive = (Button)childView.findViewById(R.id.button1);
        mButtonNegative = (Button)childView.findViewById(R.id.button2);
        mButtonMedium = (Button)childView.findViewById(R.id.button3);
		if (medium == 0) {
	        mButtonMedium.setVisibility(View.GONE);
		} else {
            mButtonMedium.setText(medium);
		}

		if ( (positive != 0) || (negative != 0) ) {

			if (positive != 0) {
	            mButtonPositive.setText(positive);
                if (cartFlag){
					mButtonPositive.setBackgroundDrawable(null);
                    mButtonPositive.setBackgroundResource(R.drawable.btn_bg_orange_selector);
                }
			} else {
	            mButtonPositive.setVisibility(View.GONE);
			}

			if (negative != 0) {
	            mButtonNegative.setText(negative);
			} else {
	            mButtonNegative.setVisibility(View.GONE);
			}

		} else {

	        vw = childView.findViewById(R.id.buttonPanel);
	        vw.setVisibility(View.GONE);
		}


		if (title != null) {

	        tv = (TextView)childView.findViewById(R.id.alertTitle);
			tv.setText(title);
		} else {

	        vw = childView.findViewById(R.id.topPanel);
	        vw.setVisibility(View.GONE);
		}


		if (message != null) {

	        tv = (TextView)childView.findViewById(R.id.message);
			tv.setText(message);
		} else {

	        vw = childView.findViewById(R.id.contentPanel);
	        vw.setVisibility(View.GONE);
		}


		if (view != null) {

			ViewUtil.setSplitMotionEventsToAll(view);

            FrameLayout custom = (FrameLayout) childView.findViewById(R.id.custom);
            custom.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		} else {

	        vw = childView.findViewById(R.id.customPanel);
	        vw.setVisibility(View.GONE);
		}


		// Dialog のレイアウトを指定
		mAlertDialog.setContentView(childView);

		LayoutParams lp = mAlertDialog.getWindow().getAttributes();
		lp.width  = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;


        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

//                mButtonPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                mButtonNegative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                mButtonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAutoClose) {
                            mAlertDialog.dismiss();
                        }
                        if (mInterface == null) {
                            return;
                        }
                        mInterface.onDialogResult(mAlertDialog, mButtonPositive, DialogInterface.BUTTON_POSITIVE);
                    }
                });

                mButtonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAutoClose) {
                            mAlertDialog.dismiss();
                        }
                        if (mInterface == null) {
                            return;
                        }
                        mInterface.onDialogResult(mAlertDialog, mButtonNegative, DialogInterface.BUTTON_NEGATIVE);
                    }
                });

                mButtonMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAutoClose) {
                            mAlertDialog.dismiss();
                        }
                        if (mInterface == null) {
                            return;
                        }
                        mInterface.onDialogResult(mAlertDialog, mButtonMedium, DialogInterface.BUTTON_NEUTRAL);
                    }
                });

            }
        });

        mAlertDialog.show();
    }


    public void hide(){
        if (mAlertDialog != null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    public MessageDialog setAutoClose(boolean autoclose){
        mAutoClose = autoclose;
        return this;
    }


/*
    DialogInterface.OnClickListener mPositive = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mAutoClose){
                mAlertDialog.dismiss();
            }
            if (mInterface == null){
                return;
            }
            mInterface.onDialogResult(mAlertDialog, mButtonPositive, which);
        }
    };
    DialogInterface.OnClickListener mNegative = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mAutoClose){
                mAlertDialog.dismiss();
            }
            if (mInterface == null){
                return;
            }
            mInterface.onDialogResult(mAlertDialog, mButtonNegative, which);
        }
    };
*/

//	private void AlertDialogDismiss() {
//
//		//TODO:BroadcastReceiverでスキマー起動時のダイアログを閉じる
//		mContext.unregisterReceiver(myReceiver);
//
//		mAlertDialog.dismiss();
//	}

    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mContext.unregisterReceiver(myReceiver);
        }
    };


}
