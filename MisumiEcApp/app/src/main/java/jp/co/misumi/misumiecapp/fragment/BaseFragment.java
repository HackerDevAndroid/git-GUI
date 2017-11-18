package jp.co.misumi.misumiecapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adobe.mobile.Analytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Objects;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.LoginState;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.activity.AppActivity;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.header.HeaderView;
import jp.co.misumi.misumiecapp.header.MainHeader;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * フラグメント基底クラス
 */
public abstract class BaseFragment extends Fragment {

    protected AppActivity mParent;
    private ProgressDialog mProgressDialog = null;
    private Dialog dialog;

    private final static String DATA_TAG = "DataContainer";

    //-- ADD NT-LWL 16/12/14 AliPay Payment FR -
    protected Tracker mTracker;
    private DataContainer dataContainer;
    //-- ADD NT-LWL 16/12/14 AliPay Payment TO -

//    private boolean isReturnedShow = false;
    //static int mCounter = 0;
//    private long index;

    //FragmentDestroyListener mDestroyListener;

//    /**
//     * FragmentDestroyListener
//     */
//    public interface FragmentDestroyListener{
//        void onDestroyFragment(BaseFragment baseFragment);
//    }

    /**
     * getScreenId
     * @return
     */
    public abstract String getScreenId();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mDestroyListener = null;
        mParent = (AppActivity) activity;
        //mCounter++;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mDestroyListener = null;
        mParent = (AppActivity) context;

        //-- ADD NT-LWL 16/12/14 AliPay Payment FR -
        if (BuildConfig.subsidiaryCode.equals("CHN")) {
            MisumiEcApp application = (MisumiEcApp) mParent.getApplication();
            mTracker = application.getDefaultTracker();
        }
        //-- ADD NT-LWL 16/12/14 AliPay Payment TO -
    }

    /**
     * getContext
     * @return
     */
    public Context getContext(){
        return mParent;
    }


    /**
     * sendScreenName
     * @param name
     */
    protected void sendScreenName(String name){
    }

    /**
     * setParameter
     * @param parameter
     */
    public void setParameter(DataContainer parameter){

		Bundle bundle = new Bundle();
		bundle.putSerializable(DATA_TAG, parameter);

		//TODO:圧縮
//		byte[] bytes = DataContainerUtil.convDataContainer(parameter);
//		bundle.putSerializable(DATA_TAG, bytes);

		// フラグメントに渡す値をセット
		setArguments(bundle);
    }

    /**
     * getParameter
     * @return
     */
    public DataContainer getParameter(){

		return (DataContainer)getArguments().getSerializable(DATA_TAG);

		//TODO:圧縮
//		byte[] bytes = (byte[])getArguments().getSerializable(DATA_TAG);
//		return DataContainerUtil.reconvDataContainer(bytes);
    }


    /**
     * getParameter
     * @return
     */
    public DataContainer getParameterFromActivity(){

		return (DataContainer) getBundleData().getSerializable("dataContainer");

		//TODO:圧縮
//		byte[] bytes = (byte[])getBundleData().getSerializable("dataContainer");
//		return DataContainerUtil.reconvDataContainer(bytes);
    }


    /**
     * setParameter
     * @param
     */
    public void setParameterUri(Uri uri){

		Bundle bundle = new Bundle();
		bundle.putParcelable("Uri", uri);

		// フラグメントに渡す値をセット
		setArguments(bundle);
    }

    /**
     * getParameter
     * @return
     */
    public Uri getParameterUri(){
		return (Uri)getArguments().getParcelable("Uri");

    }

    /**
     * setBundleData
     * @param bundle
     */
    public void setBundleData(Bundle bundle){

		setArguments(bundle);
    }

    /**
     * getBundleData
     * @return
     */
    public Bundle getBundleData(){
		Bundle	bundle = getArguments();
		if (bundle == null) {
			//null避け
			bundle = new Bundle();
		}
		return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (BuildConfig.subsidiaryCode.equals("MJP")) {
            //サイカタ
            sendTrackInfo();
        }else {
            //-- UDP NT-LWL 17/03/22 AliPay Payment FR -
//            if (BuildConfig.subsidiaryCode.equals("CHN")&&mTracker!=null){
//                AppConfig config = AppConfig.getInstance();
//                String userCode = "";
//
//                if (config.hasSessionId()) {
//                    userCode = config.getUserCode();
//                    if (android.text.TextUtils.isEmpty(userCode)) {
//                        userCode = "";
//                    }
//                }
//                mTracker.set("&uid", userCode);
////                mTracker.setScreenName(getClass().getSimpleName());
//                mTracker.setScreenName(getSaicataId());
//                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//            }
            Bundle bundle=getArguments();
            if (bundle!=null){
                dataContainer= (DataContainer) bundle.getSerializable(DATA_TAG);
            }
            // 子分类画面 跟踪用户选择的categoryCode
            if (this instanceof CategorySearchListFragment){
                if (dataContainer!=null) {
                    CategoryList.Category category = (CategoryList.Category) dataContainer;
                    //添加屏幕跟踪
                    GoogleAnalytics.sendScreenTrack(mTracker, getSaicataId(), category.categoryCode, null);
                }
               // 系列一览画面 跟踪用户选择的categoryCode
            }else if (this instanceof SearchResultCategoryFragment){
                if (dataContainer!=null) {
                    SearchSeriesList seriesList = (SearchSeriesList) dataContainer;
                    if (seriesList.mSeriesList.size() > 0) {
                        String categoryCode = seriesList.mSeriesList.get(0).categoryCode;
                        //添加屏幕跟踪
                        GoogleAnalytics.sendScreenTrack(mTracker, getSaicataId(), categoryCode, null);
                    }
                }
                // 商品详情画面 跟踪用户选择的seriesCode
            }else if (this instanceof ItemDetailFragment){
                if (dataContainer!=null){
                    ResponseGetSpProduct mResponse= (ResponseGetSpProduct) dataContainer;
                    //添加屏幕跟踪
                    GoogleAnalytics.sendScreenTrack(mTracker, getSaicataId(), null, mResponse.seriesCode);
                }
            }else {
                //添加屏幕跟踪
                GoogleAnalytics.sendScreenTrack(mTracker,getSaicataId());
            }
            //-- UDP NT-LWL 17/03/22 AliPay Payment TO -
        }
	}


    @Override
    public void onResume() {
        super.onResume();
        //--UDP NT-LWL 17/08/30 Launch FR -
//        if (!(this instanceof SplashFragment)){
//            getHeader().showHeader();
//        }
        if (!(this instanceof SplashFragment || this instanceof LaunchFragment)){
            getHeader().showHeader();
        }
        //--UDP NT-LWL 17/08/30 Launch TO -
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mDestroyListener != null){
//            AppLog.d("onDestroy(Fragment) = " + this.getClass());
//            mDestroyListener.onDestroyFragment(this);
//        }else{
//            AppLog.d("onDestroy = " + this.getClass());
//        }
    }

//    public BaseFragment setDestroyListener(FragmentDestroyListener listener){
//        mDestroyListener = listener;
//        return this;
//    }

    protected FragmentController getFragmentController(){
        return mParent.getFragmentController();
    }

    protected HeaderView getHeader(){
        return mParent.getHeaderView();
    }

    /**
     * プログレスバー表示
     * @param message
     */
    protected void showProgress(int message){

        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
       //--UDP NT-LWL 17/09/05 Loading FR -
//        String str = getContext().getResources().getString(message);
//        if (str == null){
//            str = "";
//        }
//
//        mProgressDialog = new ProgressDialog(mParent);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setMessage(str);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        dialog = new Dialog(mParent,R.style.progressDialog);
        LayoutInflater inflater = LayoutInflater.from(mParent);
        View view = inflater.inflate(R.layout.progress_dialog,null);
        final ImageView v1 = (ImageView) view.findViewById(R.id.loading_progress);
        final AnimationDrawable mAnimationDrawable = (AnimationDrawable) v1.getBackground();
        mAnimationDrawable.start();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
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
        //--UDP NT-LWL 17/09/05 Loading TO -
    }

    /**
     * プログレスバー非表示
     */
    protected void hideProgress(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        //--UDP NT-LWL 17/09/05 Loading FR -
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
        //--UDP NT-LWL 17/09/05 Loading TO -
    }

//    /**
//     *
//     */
//    public void onRemoveStack(){
//        AppLog.d("fragment onRemoveStack " + getScreenId());
//    }
//
//    public void onAddStack(){
//        AppLog.d("fragment onAddStack " + getScreenId());
//    }
//
//    public void onReturnStack(){
//        AppLog.d("fragment onReturnStack " + getScreenId());
//        isReturnedShow = true;
//    }

//    /**
//     * isReturnedShow
//     * @return
//     */
//    public boolean isReturnedShow(){
//        return isReturnedShow;
//    }

    /**
     * onLogout
     */
    protected void onLogout(){
        new LoginState().logout();

        new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                getFragmentController().clearStack(new TopFragment());
            }
        }).show(R.string.message_logout_success);

        //-- ADD NT-SLJ 16/11/11 Live800 FR -
        try {
            if (AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_CHN)) {
                MainHeader mainHeader = (MainHeader) mParent.getHeaderView();
                mainHeader.showConsultButton(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-- ADD NT-SLJ 16/11/11 Live800 TO -
    }

    protected void onMaintenance(){
//        new MessageDialog(getContext(),null).show(R.string.message_maintenance_error);
    }

//    public boolean onBackKey(){return  false;}

    public void onHeaderEvent(int event, Objects objects){
    }

    /**
     * リソース文字列取得
     * @param id
     * @return
     */
    protected String getResourceString(int id){
        return getContext().getString(id);
    }


    protected void hideKeyboard(){
        View v = getView();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * トーストの表示
     * @param message
     */
    protected void showToast(String message) {
		showToast(message, false);
	}

    /**
     * トーストの表示
     * @param message
     * @param isLong
     */
    protected void showToast(String message, boolean isLong) {

		int	length	= isLong? Toast.LENGTH_LONG: Toast.LENGTH_SHORT;
		Toast.makeText(getContext(), message, length).show();
	}

    /**
     * Activityの終了
     * @param activityResult
     */
    protected void doFinish(int activityResult) {
		Intent intent = new Intent();
    	doFinish(activityResult, intent);
	}


    protected void doFinish(int activityResult, Intent intent) {
		mParent.setResult(activityResult, intent);
		mParent.finish();
	}


//    @Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
////		if (requestCode == AppConst.REQUEST_CODE_LOGIN) {
////	        if (resultCode == Activity.RESULT_OK) {
////				showToast("ログインが完了した");
////		        // TODO:ログインが完了した時
////	        } else {
////				showToast("ログインをキャンセルした");
////			}
////		}
//
//		super.onActivityResult(requestCode, resultCode, data);
//	}


    /**
     * onApplicationForeground
     */
    public void onApplicationForeground(){
//        new VersionCheckProc().run(getContext(), true, new VersionCheckProc.VersionCheckCallback() {
//            @Override
//            public void success() {
//            }
//        });
    }

    /**
     * onApplicationBackground
     */
    public void onApplicationBackground(){
    }

    /**
     * dispMessageDialog
     */
    protected void showSimpleMessageDialog(String title, String message, int positive){

        new MessageDialog(getContext(), null)
			.show(title, message, 0, positive);
    }

//    public void setIndex(long index){
//        this.index = index;
//    }
//
//    public long getIndex(){
//        return index;
//    }



	protected View inflateLayout(LayoutInflater inflater, int resource, ViewGroup root, boolean attachToRoot) {

		View view = inflater.inflate(resource, root, attachToRoot);
		ViewUtil.setSplitMotionEventsToAll(view);
		return view;
	}



	//サイカタ
	protected void sendTrackInfo() {

		String saicataId = getSaicataId();
		if (saicataId == null) return;

		HashMap scSndData = new HashMap<String,Object>();
		scSndData.put("scAppFlg", "android");

		//アプリ計測_計測指示書(Android)_20151217.xlsx
		AppConfig config = AppConfig.getInstance();
		String userCode = "";
		String customerCode = "";

		//ログイン状態なら得意先コードとログインID
		if (config.hasSessionId()) {
			userCode = config.getUserCode();
			if (android.text.TextUtils.isEmpty(userCode)) {
				userCode = "";
			}

			customerCode = config.getCustomerCode();
			if (android.text.TextUtils.isEmpty(customerCode)) {
				customerCode = "";
			}
		}

		scSndData.put("scUserCD", userCode);		//userCode
		scSndData.put("scTokuCD", customerCode);	//customerCode

		Analytics.trackState(saicataId, scSndData);
	}

	//サイカタ
	protected abstract String getSaicataId();

}
