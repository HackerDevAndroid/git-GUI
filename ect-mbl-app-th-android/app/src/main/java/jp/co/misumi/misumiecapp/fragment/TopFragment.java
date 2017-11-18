package jp.co.misumi.misumiecapp.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.MisumiEcApp;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.SearchKeywordProcess;
import jp.co.misumi.misumiecapp.SessionRequiredDialog;
import jp.co.misumi.misumiecapp.activity.LoginActivity;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.autocomplete.AutoCompleteProcess;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.Information;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.RequestSearchOrder;
import jp.co.misumi.misumiecapp.data.RequestSearchQuotation;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.ResponseSearchOrder;
import jp.co.misumi.misumiecapp.data.ResponseSearchQuotation;
import jp.co.misumi.misumiecapp.data.SearchSuggest;
import jp.co.misumi.misumiecapp.data.StatusCheck;
import jp.co.misumi.misumiecapp.data.UrlList;
import jp.co.misumi.misumiecapp.data.WebViewData;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Browser;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.widget.TextClock;


/**
 * トップ画面
 */
public class TopFragment extends BaseGetSpProductApi {

    //	public final int STATUS_CHECK_DURATION = (60*1000);
    private OrderHistoryApi mOderHistoryApi;
    private CategorySearchApi mCategorySearchApi;
    private EstimateHistoryApi mEstimateHistoryApi;
    private LogoutApi mLogoutApi;
    private MyPartsApi mMyPartsApi;
    private StatusCheckApi mStatusCheckApi;
    private InformationApi mInformationApi;
    //	Handler mHandler;
    private AutoCompleteProcess autoCompleteProcess;
    private SearchKeywordProcess searchKeywordProcess;

//	private boolean isVersionChecking = false;
//	private boolean stopStatusCheck = false;

    private View mLayoutView;
//	private final int STATUS_CHECK = 1;

    private String mInformation = null;
    private String mCalendarUrl = null;
    private String mDateString = "";
    private String mTimeString = "";
//	private int mEstimateStatusImage = R.drawable.top_icon_status_ng;
//	private String mEstimateStatusResult = "";
//	private int mOrderStatusImage = R.drawable.top_icon_status_ng;
//	private String mOrderStatusResult = "";

    private TextView textViewDate;
    private TextView textViewTime;
    //	private AutoFitTextView textViewEstimateStatus;
//	private AutoFitTextView textViewOrderStatus;
//	private ImageView imageViewEstimateStatus;
//	private ImageView imageViewOrderStatus;
    private TextView textInfo;

    private GetSpProductApi mGetSpProductApi;


    private int[] CalendarImageIds = {
            R.string.top_label_calendar_01,
            R.string.top_label_calendar_02,
            R.string.top_label_calendar_03,
            R.string.top_label_calendar_04,
            R.string.top_label_calendar_05,
            R.string.top_label_calendar_06,
            R.string.top_label_calendar_07,
            R.string.top_label_calendar_08,
            R.string.top_label_calendar_09,
            R.string.top_label_calendar_10,
            R.string.top_label_calendar_11,
            R.string.top_label_calendar_12,
    };


    private String prevData = null;

    private AutoCompleteTextView mEditKeyword;
    private Button mKeywordClear;

    /**
     * TopFragment
     */
    public TopFragment() {
        mOderHistoryApi = new OrderHistoryApi();
        mCategorySearchApi = new CategorySearchApi();
        mEstimateHistoryApi = new EstimateHistoryApi();
        mLogoutApi = new LogoutApi();
        mMyPartsApi = new MyPartsApi();
        mStatusCheckApi = new StatusCheckApi();
        mInformationApi = new InformationApi();
        searchKeywordProcess = new SearchKeywordProcess();

        mGetSpProductApi = new GetSpProductApi() {

            @Override
            protected String getScreenId() {
                //画面IDを返す
                return TopFragment.this.getScreenId();
            }

            protected void onSuccess(ResponseGetSpProduct response) {

                //画面遷移する
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

            //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
            @Override
            public void onResult(int responseCode, String result) {
                super.onResult(responseCode, result);
                setSearchGA(responseCode);


            }

            @Override
            protected void onNetworkError(int responseCode) {
                super.onNetworkError(responseCode);
                setSearchGA(responseCode);
            }

            @Override
            protected void onLostSession(int responseCode, String result) {
                super.onLostSession(responseCode, result);
                setSearchGA(responseCode);
            }

            private void setSearchGA(int responseCode) {
                // 产品存在时 定义totalCount=1，反之NotFound
                String totalCount = responseCode == NetworkInterface.STATUS_OK ? "1" : "NotFound";
                String errorCode = responseCode == NetworkInterface.STATUS_OK ? "success" : responseCode + "";
                // 标签拼接规则 来源+型号+数量+responseCode
                JSONObject object = new JSONObject();
                try {
                    object.put("clickSource", GoogleAnalytics.suggest);
                    object.put("partNumber", mPartNumber);
                    object.put("totalCount", totalCount);
                    object.put("errorCode", errorCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//				String lable = object.toString();
                // 搜索 建议列表中 跟踪用户点击的产品
                GoogleAnalytics.sendProductTrack(mTracker, null, GoogleAnalytics.CATEGORY_SEARCH, object);
            }
            //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
        };

    }

    /**
     * onCreateView
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppLog.d("top onCreateView");


        View view = inflateLayout(inflater, R.layout.fragment_top, container, false);
        mLayoutView = view;

//		textViewDate = (TextView) mLayoutView.findViewById(R.id.textViewDate);
//		textViewTime = (TextView) mLayoutView.findViewById(R.id.textViewTime);

//		textViewEstimateStatus = (AutoFitTextView) mLayoutView.findViewById(R.id.autoTextViewEstimateStatus);
//		textViewOrderStatus = (AutoFitTextView) mLayoutView.findViewById(R.id.autoTextViewOrderStatus);
//
//		imageViewEstimateStatus = (ImageView) mLayoutView.findViewById(R.id.imageViewEstimateStatus);
//		imageViewOrderStatus = (ImageView) mLayoutView.findViewById(R.id.imageViewOrderStatus);


        //	China Standard Time, CTT GMT+8
        //	Japan JST GMT+9
        String timeZone = (SubsidiaryCode.isJapan()) ? "GMT+9" : "GMT+8";
        TextClock textClock = (TextClock) mLayoutView.findViewById(R.id.textClock);
        textClock.setTimeZone(timeZone);


        textInfo = (TextView) mLayoutView.findViewById(R.id.textViewInformation);


//		getHeader().showHeader();


        // 商品検索
        final View buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                //设置 点击来源为点击 搜索按钮
                searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                doSearchItem();
            }
        });

        mEditKeyword = (AutoCompleteTextView) view.findViewById(R.id.editKeyword);
        mKeywordClear = (Button) view.findViewById(R.id.buttonKeywordClear);
        mKeywordClear.setVisibility(View.INVISIBLE);
        mKeywordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditKeyword.setText("");
            }
        });
        //EditText監視
        mEditKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                AppLog.d("afterTextChanged");
                String str = s.toString();
                if (str.isEmpty()) {
                    buttonSearch.setEnabled(false);
                    mKeywordClear.setVisibility(View.INVISIBLE);
                } else {
                    buttonSearch.setEnabled(true);
                    mKeywordClear.setVisibility(View.VISIBLE);
                }
            }
        });
        //サジェストを選択した時のリスナー
        mEditKeyword.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AutoCompleteProcess.FilterObject filterObject = (AutoCompleteProcess.FilterObject) parent.getItemAtPosition(position);
//				showToast("CCC: "+ position);
//				showToast("CCC: "+ filterObject.suggestString);

                doSuggest(filterObject);
            }

        });


        String str = mEditKeyword.getText().toString();
        if (str.isEmpty()) {
            buttonSearch.setEnabled(false);
        } else {
            buttonSearch.setEnabled(true);
        }

        autoCompleteProcess = new AutoCompleteProcess(getContext(), mEditKeyword, new AutoCompleteProcess.KeyEnter() {
            @Override
            public void codeEnter() {
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                //设置 点击来源为点击 搜索按钮
                searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                doSearchItem();
            }
        });


        // お知らせ全文表示
        view.findViewById(R.id.textViewInformation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInformation != null) {
//					stopStatusCheck();
                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
//							nowStatusCheck();
                        }
                    }).show(mInformation, 0, R.string.dialog_button_close);
                }
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_notice);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });
        //-- ADD NT-LWL 17/07/14 3小时闪达 FR -
        // カレンダー
        view.findViewById(R.id.calendarTapArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (immediateDeliveryFlag().equals("1")) {
//					Toast.makeText(getContext(),"点击了3小时闪达",Toast.LENGTH_SHORT).show();
                    BaseFragment baseFragment = new WebViewFragment();
                    String url = AppConfig.getInstance().getUrlList().depoProductsPageUrl;
                    if (!TextUtils.isEmpty(url)) {
                        // 追加GA
                        String customercode = AppConfig.getInstance().getCustomerCode();
                        String userid = AppConfig.getInstance().getUserCode();
                        url = url + "?customercode=" + customercode + "&userid=" + userid;
                    }
                    getFragmentController().stackFragment(baseFragment, FragmentController.ANIMATION_SLIDE_IN, new WebViewData(url));
//					SubActivity.launchActivity(TopFragment.this,SubActivity.SUB_TYPE_WEB_VIEW,
//											   SubActivity.REQUEST_CODE_WEB_VIEW, new WebViewData("file:///android_asset/product_list.html"));
                } else {
                    if (AppConfig.getInstance().getUrlList().calendarFullUrl != null) {
                        Browser.run(getContext(), AppConfig.getInstance().getUrlList().calendarFullUrl);
                    }
                }
            }
        });
        if (immediateDeliveryFlag().equals("1")) {
            //Toast.makeText(getContext(),"点击了3小时闪达",Toast.LENGTH_SHORT).show();
        } else {
            if (AppConfig.getInstance().getUrlList().calendarFullUrl == null) {
                view.findViewById(R.id.calendarTapArea).setEnabled(false);
            }
        }

        // カレンダー
        /*view.findViewById(R.id.calendarTapArea).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					if (AppConfig.getInstance().getUrlList().calendarFullUrl != null) {
						Browser.run(getContext(), AppConfig.getInstance().getUrlList().calendarFullUrl);
					}
			}
		});
		if (AppConfig.getInstance().getUrlList().calendarFullUrl == null){
			view.findViewById(R.id.calendarTapArea).setEnabled(false);
		}*/

        //-- ADD NT-LWL 17/07/14 3小时闪达 TO -

        // カテゴリ検索
        view.findViewById(R.id.buttonCategorySearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCategorySearch();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_categorySearch);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

        // 見積履歴
        view.findViewById(R.id.buttonEstimateHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEstimateList();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_QThistory);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

        // 注文履歴
        view.findViewById(R.id.buttonOrderHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOrderList();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_SOhistory);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

        // My部品
        view.findViewById(R.id.buttonMyParts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMyParts();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_myParts);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

        // ガイド
        view.findViewById(R.id.buttonGuide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUsersGuide();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_guide);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

        // ログイン・ログアウト
        view.findViewById(R.id.groupLoginState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.getInstance().hasSessionId()) {
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_logout);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                mLogoutApi.connect(getContext());
                                //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
                                GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_LOGOUT, "-");
                                //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
                            }
                        }
                    }).show(R.string.message_logout, R.string.dialog_button_yes, R.string.dialog_button_no);
                } else {
                    //-- ADD NT-LWL 17/03/28 Live800 FR -
                    GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_login);
                    //-- ADD NT-LWL 17/03/28 Live800 TO -
                    LoginActivity.launchActivity(getActivity(), LoginActivity.MODE_NORMAL);
                }
            }
        });

        // ステータスチェック
        view.findViewById(R.id.statusTapArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStatusCheck();
                //-- ADD NT-LWL 17/03/28 Live800 FR -
                GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPPAGE, GoogleAnalytics.lable_statusConfirm);
                //-- ADD NT-LWL 17/03/28 Live800 TO -
            }
        });

//		mHandler = new Handler(mHandlerCallback);
        return view;
    }

    /**
     * アプリの状態変化でコールバック
     */
    AppNotifier.AppNoticeListener mAppStateListener = new AppNotifier.AppNoticeListener() {
        @Override
        public void appNotice(AppNotifier.AppNotice notice) {
            AppLog.d("AppState Changed(TopFragment) = " + notice.event);
            if (notice.event == AppNotifier.USER_LOGIN) {
                updateLoginStatus();
            }
        }
    };

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.d("top onCreate");
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.Top;
    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();

//		stopStatusCheck = false;
        AppLog.d("Top Resume!");
        AppNotifier.getInstance().addListener(mAppStateListener, AppNotifier.USER_LOGIN);

        //ログイン状態を調べて表示とボタンを更新する
        updateLoginStatus();
        updateInformation();
        updateStatusResult();


        IntentFilter myFilter = new IntentFilter();
//		myFilter.addAction(Intent.ACTION_DATE_CHANGED);
//		myFilter.addAction(Intent.ACTION_TIME_CHANGED);
//		myFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        myFilter.addAction(getDateChangedAction(getContext()));
        getContext().registerReceiver(changeTime, myFilter);

        registerDateChangeReceiver(getContext());


//		if (isReturnedShow()){
//			return;
//		}
//		nowStatusCheck();
    }

//	@Override
//	public void onRemoveStack() {
//		super.onRemoveStack();
//
//	}
//
//
//	@Override
//	public void onAddStack() {
//		super.onAddStack();
//
//	}


    public static void registerDateChangeReceiver(Context context) {
        unregisterDateChangeReceiver(context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);    //1日単位で動く

//	    calendar.add(Calendar.SECOND, 20);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createDateChangePendingIntent(context);
//	    if(AndroidUtils.isLessThanBuildVersion(Build.VERSION_CODES.KITKAT)){
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
//	    }else{
//	        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//	    }

/*
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// AlarmManager.RTC_WAKEUPで端末スリープ時に起動させるようにする
		// 1回だけ通知の場合はalarmManager.set()を使う
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
			// 一日毎にアラームを呼び出す
			AlarmManager.INTERVAL_DAY, pendingIntent);
*/
    }

    /**
     * すでにセットされた Alarm を解除する
     */
    public static void unregisterDateChangeReceiver(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createDateChangePendingIntent(context));
    }

    /**
     * 日付変更の PendingIntent を生成する
     */
    private static PendingIntent createDateChangePendingIntent(Context context) {
        Intent intent = new Intent(getDateChangedAction(context));
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static String getDateChangedAction(Context context) {
        return context.getPackageName() + ".action.DATE_CHANGED";
    }


    private BroadcastReceiver changeTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //カレンダー更新
            AppLog.e("status updateStatusResult !");
            updateStatusResult();

            registerDateChangeReceiver(context);
        }
    };


    /**
     * updateLoginStatus
     */
    private void updateLoginStatus() {

        ViewGroup customerInfo = (ViewGroup) mLayoutView.findViewById(R.id.customerInfo);
        TextView textCustomerName = (TextView) mLayoutView.findViewById(R.id.textViewCustomerName);
        TextView textCustomerCode = (TextView) mLayoutView.findViewById(R.id.textViewCustomerCode);
        TextView textUserName = (TextView) mLayoutView.findViewById(R.id.textViewUserName);
        TextView textViewLoginState = (TextView) mLayoutView.findViewById(R.id.textViewLoginSate);

        //ログイン状態を調べて表示とボタンを更新する
        AppConfig config = AppConfig.getInstance();
        if (config.hasSessionId()) {
            customerInfo.setVisibility(View.VISIBLE);
            String name = config.getUserName();

            // 受け取った文字列が20文字以上なら20文字に切り出し
            // Todo: 様をリソース定義
            if (name != null && name.length() > 0) {
                if (SubsidiaryCode.isJapan()) {
                    if (name.length() <= 20) {
                        textUserName.setText(name + "様");
                    } else {
                        String cutOutName = name.substring(0, 20);
                        textUserName.setText(cutOutName + "様");
                    }
                } else {
                    if (name.length() <= 20) {
                        textUserName.setText(name);
                    } else {
                        String cutOutName = name.substring(0, 20);
                        textUserName.setText(cutOutName);
                    }
                }

            }

            textCustomerName.setText(config.getCustomerName());

            String customerCode = config.getCustomerCode();
            if (android.text.TextUtils.isEmpty(customerCode)) {
                customerCode = getResourceString(R.string.label_hyphen);
            }
            textCustomerCode.setText(String.format(getResourceString(R.string.top_label_customer_code), customerCode));
            textViewLoginState.setText(R.string.top_label_logout);
            //--ADD NT-SLJ 17/07/14 3小时必达 FR -
            View pv = mLayoutView.findViewById(R.id.progressView);
            ImageView imageView = (ImageView) mLayoutView.findViewById(R.id.ImageViewCalendarTop);
            if (immediateDeliveryFlag().equals("1")) {
//				imageView.setImageResource(R.drawable.depo);
                String url = AppConfig.getInstance().getUrlList().depoBannerUrl;
                PicassoUtil.PicassoLoadForCalendar(imageView, pv, url, new PicassoUtil().new RoundedTransformation(10, 0));
            } else {
                PicassoUtil.PicassoLoadForCalendar(imageView, pv, mCalendarUrl, new PicassoUtil().new RoundedTransformation(10, 0));
            }
            //--ADD NT-SLJ 17/07/14 3小时必达 TO -

        } else {
            customerInfo.setVisibility(View.GONE);
            textCustomerCode.setText(R.string.top_label_welcome);
            textViewLoginState.setText(R.string.top_label_login);
            //--ADD NT-SLJ 17/07/14 3小时必达 FR -
            View pv = mLayoutView.findViewById(R.id.progressView);
            ImageView imageView = (ImageView) mLayoutView.findViewById(R.id.ImageViewCalendarTop);
            PicassoUtil.PicassoLoadForCalendar(imageView, pv, mCalendarUrl, new PicassoUtil().new RoundedTransformation(10, 0));
            //--ADD NT-SLJ 17/07/14 3小时必达 TO -
        }
    }

    private void updateInformation() {
        if (mInformation != null) {
            textInfo.setText(mInformation);
        } else {
            mInformationApi.connect(getContext());
        }
    }

    @Override
    public void onPause() {
        AppLog.d("top onPause");

        unregisterDateChangeReceiver(getContext());
        getContext().unregisterReceiver(changeTime);
        mOderHistoryApi.close();
        mCategorySearchApi.close();
        mEstimateHistoryApi.close();
        mLogoutApi.close();
        mMyPartsApi.close();
        mStatusCheckApi.close();
        searchKeywordProcess.close();
//		stopStatusCheck();
//		stopStatusCheck = true;
        mGetSpProductApi.close();
        AppNotifier.getInstance().removeListener(mAppStateListener);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.d("top onDestroy");
    }

    private void updateStatusResult() {


//		textViewDate.setText(mDateString);
//		textViewTime.setText(mTimeString);

        //端末時刻からカレンダー画像
// -- UDP NT-LWL 17/01/22 AliPay Payment FR –
//    	Calendar mTime;
//        mTime = Calendar.getInstance();
//        mTime.setTimeInMillis(System.currentTimeMillis());
//
//		int month;
//		month = mTime.get(Calendar.MONTH);
//
//		UrlList urllist = AppConfig.getInstance().getUrlList();
//		String imageUrl;
//		if (urllist != null) {
//			imageUrl = urllist.calendarTopUrl;
//		}else{
//			imageUrl = null;
//		}
//
//		if (imageUrl != null){
//			imageUrl += getResourceString(CalendarImageIds[month]);
//		}else{
//			imageUrl = "";
//		}
//		mCalendarUrl = imageUrl;

        UrlList urllist = AppConfig.getInstance().getUrlList();
        String imageUrl;
        if (urllist != null) {
            imageUrl = urllist.calendarTopUrl;
        } else {
            imageUrl = null;
        }
        //中文环境不作月份判断
        if (!SubsidiaryCode.isJapan()) {
            mCalendarUrl = imageUrl;
        } else {
            Calendar mTime;
            mTime = Calendar.getInstance();
            mTime.setTimeInMillis(System.currentTimeMillis());

            int month;
            month = mTime.get(Calendar.MONTH);

            if (imageUrl != null) {
                imageUrl += getResourceString(CalendarImageIds[month]);
            } else {
                imageUrl = "";
            }
            mCalendarUrl = imageUrl;
        }
// -- UDP NT-LWL 17/01/22 AliPay Payment TO –

        // カレンダー
        AppLog.d("CalendarUrl=" + mCalendarUrl);
        // --EDIT NT-SLJ 17/07/14 3小时闪达 FR -


//		View pv = mLayoutView.findViewById(R.id.progressView);
//		ImageView imageView = (ImageView) mLayoutView.findViewById(R.id.ImageViewCalendarTop);
//
//
//		// ステータス
////		imageViewEstimateStatus.setImageResource(mEstimateStatusImage);
////		textViewEstimateStatus.setText(mEstimateStatusResult);
////
////		imageViewOrderStatus.setImageResource(mOrderStatusImage);
////		textViewOrderStatus.setText(mOrderStatusResult);
//
//		PicassoUtil.PicassoLoadForCalendar(imageView, pv, mCalendarUrl, new PicassoUtil().new RoundedTransformation(10, 0));
        AppConfig config = AppConfig.getInstance();
        if (immediateDeliveryFlag().equals("1")) {
            View pv = mLayoutView.findViewById(R.id.progressView);
            ImageView imageView = (ImageView) mLayoutView.findViewById(R.id.ImageViewCalendarTop);
//			imageView.setImageResource(R.drawable.depo);
            PicassoUtil.PicassoLoadForCalendar(imageView, pv, config.getUrlList().depoBannerUrl, new PicassoUtil().new RoundedTransformation(10, 0));
        } else {
            View pv = mLayoutView.findViewById(R.id.progressView);
            ImageView imageView = (ImageView) mLayoutView.findViewById(R.id.ImageViewCalendarTop);
            PicassoUtil.PicassoLoadForCalendar(imageView, pv, mCalendarUrl, new PicassoUtil().new RoundedTransformation(10, 0));
        }

        // --EDIT NT-SLJ 17/07/14 3小时闪达 TO -
    }


//	/**
//	 * onApplicationForeground
//	 */
//	@Override
//	public void onApplicationForeground() {
//		// ここではsuperクラスに処理を任せずに直接バージョンチェックを行う
//		isVersionChecking = true;
//		new VersionCheckProc().run(getContext(), true, new VersionCheckProc.VersionCheckCallback() {
//			@Override
//			public void success() {
//				isVersionChecking = false;
//				// ステータスチェックを行う
//				nowStatusCheck();
//			}
//		});
//	}

    /**
     * doCategorySearch
     */
    private void doCategorySearch() {
        hideKeyboard();
        mCategorySearchApi.connect(getContext());
    }

    /**
     * doEstimateList
     */
    private void doEstimateList() {
        hideKeyboard();
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {
            mEstimateHistoryApi.connect(getContext());
        }
    }

    /**
     * doOrderList
     */
    private void doOrderList() {
        hideKeyboard();
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {
            mOderHistoryApi.connect(getContext());
        }
    }

    /**
     * doMyParts
     */
    private void doMyParts() {
        hideKeyboard();
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {
            mMyPartsApi.connect(getContext());
        }
    }

    /**
     * doUsersGuide
     */
    private void doUsersGuide() {
        hideKeyboard();
        SubActivity.launchActivity(this, SubActivity.SUB_TYPE_WEB_VIEW,
                SubActivity.REQUEST_CODE_WEB_VIEW, new WebViewData(AppConfig.getInstance().getUrlList().userGuidUrl));
    }


    /**
     * doSearchItem
     */
    private void doSearchItem() {
        hideKeyboard();

        String str = mEditKeyword.getText().toString();
        if (str.isEmpty()) {
            return;
        }

        RequestKeywordSearch request = new RequestKeywordSearch();
        request.keyword = str;
//		searchKeywordProcess.runDefault(getContext(), getFragmentController(), request, getScreenId());
// 2015/09/28 field=@defaultだと検索結果が 0件になるから下記に変更
        searchKeywordProcess.run(getContext(), getFragmentController(), request, getScreenId());
    }


    /**
     * doStatusCheck
     */
    private void doStatusCheck() {
        mStatusCheckApi.connect(getContext());
    }

    private void onStatusChecked(StatusCheck statusCheck) {

        //TODO:デザイン
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_status_layout, null, false);

        TextView textViewEstimateStatus;
        TextView textViewOrderStatus;
        ImageView imageViewEstimateStatus;
        ImageView imageViewOrderStatus;

        textViewEstimateStatus = (TextView) view.findViewById(R.id.autoTextViewEstimateStatus);
        textViewOrderStatus = (TextView) view.findViewById(R.id.autoTextViewOrderStatus);

        imageViewEstimateStatus = (ImageView) view.findViewById(R.id.imageViewEstimateStatus);
        imageViewOrderStatus = (ImageView) view.findViewById(R.id.imageViewOrderStatus);

        //
        int mEstimateStatusImage;
        String mEstimateStatusResult;
        int mOrderStatusImage;
        String mOrderStatusResult;

        mEstimateStatusResult = "";
        mOrderStatusResult = "";
        // 受付ステータス
        switch (statusCheck.getQuotationStatus()) {
            case StatusCheck.ESTIMATE_ACCEPT:
                mEstimateStatusImage = R.drawable.top_icon_status_ok;
                mEstimateStatusResult += getResourceString(R.string.top_label_status_accept);
                break;
            case StatusCheck.ESTIMATE_MAINTENANCE:
                mEstimateStatusImage = R.drawable.top_icon_status_mente;
                mEstimateStatusResult += getResourceString(R.string.top_label_status_maintenance);
                break;
            case StatusCheck.UNSET:
            default:
                mEstimateStatusImage = R.drawable.top_icon_status_ng;
                mEstimateStatusResult = getResourceString(R.string.top_label_status_unset);
                break;
        }


        // 注文ステータス
        switch (statusCheck.getOrderStatus()) {
            case StatusCheck.ORDER_ACCEPT:
                mOrderStatusImage = R.drawable.top_icon_status_ok;
                mOrderStatusResult += getResourceString(R.string.top_label_status_accept);
                break;
            case StatusCheck.ORDER_PROVISIONAL:
                mOrderStatusImage = R.drawable.top_icon_status_ng;
                mOrderStatusResult += getResourceString(R.string.top_label_status_accept_next_day);
                break;
            case StatusCheck.ORDER_MAINTENANCE:
                mOrderStatusImage = R.drawable.top_icon_status_mente;
                mOrderStatusResult += getResourceString(R.string.top_label_status_maintenance);
                break;
            case StatusCheck.UNSET:
            default:
                mOrderStatusImage = R.drawable.top_icon_status_ng;
                mOrderStatusResult = getResourceString(R.string.top_label_status_unset);
                break;
        }

        // ステータス
        imageViewEstimateStatus.setImageResource(mEstimateStatusImage);
        textViewEstimateStatus.setText(mEstimateStatusResult);

        imageViewOrderStatus.setImageResource(mOrderStatusImage);
        textViewOrderStatus.setText(mOrderStatusResult);


        new MessageDialog(getContext(), null)
                .show(view, 0, R.string.dialog_button_close);
    }

//	/**
//	 * nowStatusCheck
//	 */
//	private void nowStatusCheck(){
//		if (stopStatusCheck){
//			AppLog.d("status check cancel(stopStatusCheck).");
//			return;
//		}
//		if (!isVersionChecking) {
//			Message msg = mHandler.obtainMessage();
//			msg.what = 1;
//			msg.sendToTarget();
//		} else {
//			AppLog.d("status check cancel(version checking).");
//		}
//	}

//	/**
//	 * nextStatusCheck
//	 */
//	private void nextStatusCheck(){
//		stopStatusCheck();
//		if (stopStatusCheck){
//			AppLog.d("status next check cancel(stopStatusCheck).");
//			return;
//		}
//		Message msg = mHandler.obtainMessage();
//		msg.what = STATUS_CHECK;
//		mHandler.sendMessageDelayed(msg, STATUS_CHECK_DURATION);
//	}

//	private void stopStatusCheck(){
//		mHandler.removeMessages(STATUS_CHECK);
//	}


//	android.os.Handler.Callback mHandlerCallback = new android.os.Handler.Callback() {
//		@Override
//		public boolean handleMessage(Message msg) {
//			if (STATUS_CHECK == msg.what) {
//				AppLog.d("status check");
//				mStatusCheckApi.connect(getContext());
//			}
//			return false;
//		}
//	};

    /**
     * カテゴリ検索
     */
    private class CategorySearchApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createSearchCategory(null, null);
        }

        @Override
        public void onResult(int responseCode, String result) {
            CategoryList categorylist = new CategoryList();
            if (!categorylist.setData(result)) {
                showErrorMessage(null);
                return;
            }
            if (responseCode == NetworkInterface.STATUS_OK) {
                if (categorylist.isEmpty()) {
                    //ネットワーク読み込みして空の場合はアプリキャッシュ
                    categorylist = ((MisumiEcApp) getActivity().getApplication()).getTopCategoryList();
                } else {
                    // カテゴリデータを読み込んだときは外部ファイルへ書き出しを行う
                    ((MisumiEcApp) getActivity().getApplication()).setTopCategoryList(categorylist);
                }
                getFragmentController().stackFragment(new CategorySearchFragment(), FragmentController.ANIMATION_SLIDE_IN, categorylist);
            } else {
                showErrorMessage(categorylist.errorList);
            }
        }
    }

    /**
     * 見積履歴
     */
    private class EstimateHistoryApi extends ApiAccessWrapper {
        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }


        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchQuotation request = new RequestSearchQuotation();
            return ApiBuilder.createSearchQuotation(request);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseSearchQuotation response = new ResponseSearchQuotation();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    getFragmentController().stackFragment(new EstimateListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;

            }
        }
    }

    /**
     * 注文履歴
     */
    private class OrderHistoryApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            RequestSearchOrder request = new RequestSearchOrder();
            return ApiBuilder.createSearchOrder(request);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseSearchOrder response = new ResponseSearchOrder();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    getFragmentController().stackFragment(new OrderListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }

    /**
     * ログアウト
     */
    private class LogoutApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createLogout();
        }

        @Override
        public void onResult(int responseCode, String result) {
            onLogout();
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            onLogout();
        }
    }

    /**
     * My部品表
     */
    private class MyPartsApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            String folderId = "0";
            String sort = "0";
            return ApiBuilder.createGetMyComponents(folderId, sort);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseGetMyComponents response = new ResponseGetMyComponents();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    getFragmentController().stackFragment(new MyPartsListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }

    /**
     * ステータスチェック
     */
    private class StatusCheckApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createStatusCheck();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            StatusCheck statusCheck = new StatusCheck();
            if (!statusCheck.setData(result)) {
                showErrorMessage(statusCheck.errorList);
            } else {
                onStatusChecked(statusCheck);
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
        }

        //@Override
        //protected void onStartConnect() {}
        //@Override
        //protected void onEndConnect() {}
    }

    /**
     * 重要なお知らせ
     */
    private class InformationApi extends ApiAccessWrapper {

        @Override
        protected String getScreenId() {
            return TopFragment.this.getScreenId();
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createInformation();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {
            Information info = new Information();
            if (!info.setData(result)) {
                info = new Information();
            }

            if (textInfo == null) {
                textInfo = (TextView) mLayoutView.findViewById(R.id.textViewInformation);
            }
            mInformation = info.serviceInformation;
            if (mInformation == null || mInformation.isEmpty()) {
                setDefaultMessage();
            }
        }

        private void setDefaultMessage() {
            mInformation = getResourceString(R.string.top_label_information);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            setDefaultMessage();
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            setDefaultMessage();
        }

        @Override
        protected void onTimeout() {
            setDefaultMessage();
        }

        @Override
        protected void onEndConnect() {
            super.onEndConnect();
            updateInformation();
        }
    }


    private void doSuggest(AutoCompleteProcess.FilterObject filterObject) {

//		showToast("CCC: "+ position);
//		showToast("CCC: "+ filterObject.suggestString);
//		doSuggest(filterObject);
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        //设置 点击来源为点击 推荐列表
        searchKeywordProcess.setClickSource(GoogleAnalytics.suggest);
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        SearchSuggest.PartNumber partNumber = filterObject.partNumber;

        //キーワード検索？
        if (partNumber == null) {
            //キーワード検索
            doSearchItem();
            return;
        }

        //型番検索
        //BaseGetSpProductApi
        hideKeyboard();

        String completeType = partNumber.completeType;
        String seriesCode = partNumber.seriesCode;
        String innerCode = partNumber.innerCode;
        String partNumberStr = partNumber.partNumber;
        Integer quantity = 1;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumberStr, quantity);
        mGetSpProductApi.connect(getContext());

    }

    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.Top;
    }

    //-- ADD NT-LWL 17/07/14 3小时闪达 FR -
    //判断是否是3小时闪达对象中 是返回1  不是返回0
    private String immediateDeliveryFlag() {
        AppConfig config = AppConfig.getInstance();
        String returnFlag = "0";
        if (!android.text.TextUtils.isEmpty(config.getImmediateDeliveryFlag()) && config.getImmediateDeliveryFlag().equals("1")) {
            returnFlag = "1";
        }
        return returnFlag;
    }
    //-- ADD NT-LWL 17/07/14 3小时闪达 TO -
}
