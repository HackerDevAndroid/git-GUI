package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.VersionCheckProc;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.api.SchemeApi;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ErrorMessageManager;
import jp.co.misumi.misumiecapp.data.UrlList;
import jp.co.misumi.misumiecapp.util.BlackListUtils;
import jp.co.misumi.misumiecapp.util.CategoryQRUtils;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * スプラッシュ画面
 */
public class SplashFragment extends BaseFragment  {

    private static long start_time = 0;
    private boolean isRunning = false;

	private AppSchemeApi mAppSchemeApi;

    //--ADD NT-LWL 17/05/20 Share FR -
    public SchemeApi getSchemeApi(){
        return mAppSchemeApi;
    }
    //--ADD NT-LWL 17/05/20 Share TO -
//    private VersionCheckApi mVersionCheck;
    private ErrorMessageResourceApi mErrorMessageResource;
    private UrlListApi mUrlListApi;

    public SplashFragment() {
//        mVersionCheck = new VersionCheckApi();
        mErrorMessageResource = new ErrorMessageResourceApi();
        mUrlListApi = new UrlListApi();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

		mAppSchemeApi = new AppSchemeApi(getContext());

//        ((MainActivity)mParent).getSlideMenu().setEnable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sendScreenName("Start");

        ((MainActivity)mParent).getSlideMenu().setEnable(false);

        return inflateLayout(inflater, R.layout.fragment_splash, container, false);
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.Splash;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isRunning) {
            isRunning = true;
            start_time = System.currentTimeMillis();

            new VersionCheckProc().run(getContext(), false, new VersionCheckProc.VersionCheckCallback() {
                @Override
                public void success() {
                    getErrorMessageResource();
                }
            });
        }
    }



    public void onPause() {

        mAppSchemeApi.onPause();
        mErrorMessageResource.close();
        mUrlListApi.close();
        super.onPause();
    }


    /**
     * getErrorMessageResource
     */
    private void getErrorMessageResource(){
        AppLog.d("getErrorMessageResource start");
        mErrorMessageResource.connect(getContext());
    }

    /**
     * getUrlList
     */
    private void getUrlList(){
        AppLog.d("getUrlList start");

        mUrlListApi.connect(getContext());
    }

    private void doNextScreen(){
        AppLog.d("doNextScreen start");

        long slp = (start_time + 1000) - System.currentTimeMillis();
        AppLog.d("sleep time = " + slp + "(" + start_time + ")");
        if (slp > 0) {
            try {
                Thread.sleep(slp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AppLog.d("doNextScreen start");

        ((MainActivity)mParent).getSlideMenu().updateDisableItem();
        ((MainActivity)mParent).getSlideMenu().setEnable(true);

		//スキーマ起動か？
        Uri uri = getParameterUri();
		if (uri != null) {

			//MISUMI_MOBILE_APP-426
			if (uri.isHierarchical()) {
                //-- ADD NT-LWL 17/06/26 Share FR-
                // 分享点击统计 设置参数
                String uuid = uri.getQueryParameter("UUID");
                mAppSchemeApi.setOpenShareApiUuid(uuid);

                String scode = uri.getQueryParameter("scode");
                // 判断来自购物车直接调用
                if (TextUtils.isEmpty(scode)){
                    mAppSchemeApi.openShareApi();
                }
                //-- ADD NT-LWL 17/06/26 Share TO-
				//ここでスキーマ起動の通信処理
				mAppSchemeApi.onCreate(uri);
				return;
			}
		}
        //--UDP NT-LWL 17/08/30 Launch FR -
		//それ以外はトップ画面
        //getFragmentController().replaceFragment(new TopFragment(), FragmentController.ANIMATION_FADE_IN);

        if (SubsidiaryCode.isChinese()) {
            // 判断是否是首次进入
            if (LaunchFragment.isFirstEnter(mParent)) {
                LaunchFragment.saveFirstFlag(mParent);
                getFragmentController().replaceFragment(new LaunchFragment(), FragmentController.ANIMATION_FADE_IN);
            } else {
                getFragmentController().replaceFragment(new TopFragment(), FragmentController.ANIMATION_FADE_IN);
            }
        }else {
            getFragmentController().replaceFragment(new TopFragment(), FragmentController.ANIMATION_FADE_IN);
        }
        //--UDP NT-LWL 17/08/30 Launch TO -
    }

    @Override
    public void onApplicationForeground() {
        // 親クラスに任せるとバージョンチェックが行われるが、スプラッシュ画面は何もしない
    }

    /**
     * ErrorMessageResourceApi
     */
    private class ErrorMessageResourceApi extends ApiAccessWrapper {


        @Override
        protected String getScreenId() {
            return SplashFragment.this.getScreenId();
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.getErrorMessage();
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            if (responseCode == NetworkInterface.STATUS_OK){
                ErrorMessageManager errorMessageManager = new ErrorMessageManager(getContext());

                if (!errorMessageManager.setData(result)){
                    showRetryMessage(R.string.message_unknown_error);
                }else {
                    getUrlList();
                }
            }else{
                showRetryMessage(R.string.message_unknown_error);
            }
        }

        @Override
        protected void onNetworkError(int responseCode) {
            this.onTimeout();
        }

        @Override
        protected void onTimeout() {
            showRetryMessage(R.string.message_network_error);
        }

        private void showRetryMessage(int messageid){
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    getErrorMessageResource();
                }
            }).show(messageid, R.string.dialog_button_retry);
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        protected void onStartConnect() {}

        @Override
        protected void onEndConnect() {}
    }

    private class UrlListApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return SplashFragment.this.getScreenId();
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.getUrlList();
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            if (responseCode == NetworkInterface.STATUS_OK){
                UrlList urlList = new UrlList();
                if (urlList.setData(result)){

                    try {
                        AppConfig.getInstance().setUrlList(urlList);
                        //-- ADD NT-LWL 17/09/25 Category FR -
                        // 保存黑名单数据
                        BlackListUtils.getInstance().saveBlackList(urlList.excludeCategoryList);
                        // 保存新旧分类code 数据
                        CategoryQRUtils.saveCategoryQRData(mParent,urlList.categoryQRMap);
                        //-- ADD NT-LWL 17/09/25 Category TO -
                        doNextScreen();
                    } catch (Exception e) {
                        String message;
                        message = getString(R.string.message_write_file_error);
                        message += "(" + e.toString() + ")";
                        new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                            @Override
                            public void onDialogResult(Dialog dlg, View view, int which) {
                                getUrlList();
                            }
                        }).show(message);
                    }
                }else{
                    showRetryMessage(R.string.message_unknown_error);
                }
            }else{
                showRetryMessage(R.string.message_unknown_error);
            }
        }

        @Override
        protected void onNetworkError(int responseCode) {
            showRetryMessage(R.string.message_network_error);
        }

        @Override
        protected void onTimeout() {
            showRetryMessage(R.string.message_network_error);
        }

        private void showRetryMessage(int messageid){
            new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                @Override
                public void onDialogResult(Dialog dlg, View view, int which) {
                    getUrlList();
                }
            }).show(messageid, R.string.dialog_button_retry);
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        protected void onStartConnect() {}

        @Override
        protected void onEndConnect() {}
    }


    private class AppSchemeApi extends SchemeApi {

		public AppSchemeApi(Context context) {
			super(context);
		}

        @Override
	    protected void doNextScreen(DataContainer dataContainer) {

			BaseFragment nextFragment = getNextFragment(dataContainer);
			getFragmentController().replaceFragment(nextFragment, FragmentController.ANIMATION_FADE_IN, dataContainer);
		}

        @Override
		protected MessageDialog.MessageDialogListener getMessageDialogListener() {

			return new MessageDialog.MessageDialogListener() {
				public void onDialogResult(Dialog dlg, View view, int which) {

			        AppLog.e("SKXXX onDialogResult(): ");
					//通信失敗は一律トップ画面
			        getFragmentController().replaceFragment(new TopFragment(), FragmentController.ANIMATION_FADE_IN);
				}
			};
		}
	}


	//サイカタ
	protected String getSaicataId() {
		return jp.co.misumi.misumiecapp.SaicataId.Splash;
	}
}

