package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.adapter.MyStockAdapter;
import jp.co.misumi.misumiecapp.adapter.OrderConfirmAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.api.OrderConfirmOrderBaseApi;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ErrorList;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromOrder;
import jp.co.misumi.misumiecapp.data.RequestConfirmOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheck;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.data.ResponseConfirmOrder;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewBgUtil;


/**
 * 注文確認画面
 */
public class OrderConfirmFragment extends CartConfirmFragment {

    //画面表示用
    private ResponseCheckOrder mResponse = null;
    private ResponseConfirmOrder mResponseConfirm = null;

    private final boolean mIsIncludeTax;
    private final boolean mIsCodUser;

    //必須項目
    private RadioGroup mRadioGroup1;
    private EditText mUserName;
    private EditText mDeliName;

    private View mButtonConfirm;

    private OrderConfirmAdapter mListAdapter;
    private ListView mListView;
    private View mHeaderView;
    private View mFooterView;
    private View mFooterView2;
    private View mFooterView3;

    //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
    private View mPaymentView;
    private RadioGroup mPaymentRadioGroup;

    //-- ADD NT-LWL 16/10/13 AliPay Payment TO -

    //在庫切れダイアログ用
//    private MessageDialog mMessageDialog;
    private boolean onPaused = false;

    private boolean mHasRadioArea = false;
    private String mRadioSelectValue = "";

    //API
    private ConfirmApi mConfirmApi;
    private OrderConfirmOrderBaseApi mOrderConfirmStockApi;


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("Response", mResponse);
        outState.putString("mRadioSelectValue", mRadioSelectValue);
    }


    public OrderConfirmFragment() {

        mConfirmApi = new ConfirmApi();

        //注文確認のAPI FROM 注文確認
        mOrderConfirmStockApi = new OrderConfirmOrderBaseApi() {

            @Override
            protected void onSuccess(ResponseCheckOrder response) {

                mResponse = response;
                makeDataView(getView());

                //スクロール位置を先頭に戻す
                mListView.setSelection(0);
            }

            @Override
            protected String getScreenId() {
                return OrderConfirmFragment.this.getScreenId();
            }

        };

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
        mIsCodUser = AppConfig.getInstance().isCodUser();
    }


    private ResponseCheckOrder getData() {

        return (ResponseCheckOrder) getDataContainer();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //リクエストデータを最初に作成して、このデータクラスを画面編集と同期させる
        if (savedInstanceState != null) {

            mResponse = (ResponseCheckOrder) savedInstanceState.getSerializable("Response");
            //デフォルト設定値 出荷オプション
            mRadioSelectValue = savedInstanceState.getString("mRadioSelectValue");

            mResponseConfirm = (ResponseConfirmOrder) savedInstanceState.getSerializable("ResponseConfirmOrder");

        } else {

            if (mResponse == null) {
                mResponse = getData();

                //デフォルト設定値 出荷オプション
                if (SubsidiaryCode.isJapan()) {

                    mRadioSelectValue = "C";
                } else {

                    mRadioSelectValue = "P";
                }

            }

            if (mResponseConfirm == null) {
                mResponseConfirm = new ResponseConfirmOrder();
            }

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onResume() {

        super.onResume();

        //在庫切れ２重表示防止
        if (!onPaused) {
            //在庫切れチェック
            checkOutOfStock();
        }

        onPaused = false;
    }


    @Override
    public void onPause() {

        mConfirmApi.close();
        mOrderConfirmStockApi.close();

        super.onPause();

        onPaused = true;
    }


    @Override
    protected int getLayoutId() {

        return R.layout.fragment_order_confirm;
    }


    //
    @Override
    protected void doSubmit() {

        doSubmitSub(false, false);

    }

    @Override
    protected void doSubmitQuote() {

        //見積変換
        //quotationConvertFlag
        doSubmitSub(true, false);
    }


    //アンフィット解決経由の時
    @Override
    protected void doSubmitUnfit() {

        //アンフィット解決経由の時
        doSubmitSub(false, true);
    }

    @Override
    protected void doSubmitUnfitQuote() {

        //見積変換
        //quotationConvertFlag
        //アンフィット解決経由の時
        doSubmitSub(true, true);
    }


    //API側の通信仕様に合わせ
    @Override
    protected void doSubmit430() {

        //注文確認のAPI FROM 注文確認
        boolean isFromCart = mResponse.isFromCart();

        RequestCheckOrderFromOrder requestData = new RequestCheckOrderFromOrder();
        requestData.receptionCode = mResponse.receptionCode;

        //-- ADD NT-LWL 16/12/05 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan() && selectPaymentGroup != null) {
            requestData.paymentGroup = selectPaymentGroup.paymentGroup;
        }
        // -- ADD NT-LWL 16/12/05 AliPay Payment TO -
        //いろいろなリクエストパラメータ
        mOrderConfirmStockApi.setParameter(isFromCart, requestData, mMessageDialog);
        mOrderConfirmStockApi.connect(getContext());
    }

    private void doSubmitSub(boolean quotationConvert, boolean unfitPass) {

        // ネットワーク通信で通信する
        hideKeyboard();

        //確定ボタンを押した時の送信情報の生成処理
        View rootView = getView();

        RequestConfirmOrder mRequestData = new RequestConfirmOrder();

        //発注者
        mRequestData.billingUserName = getIncludeItemEditString(rootView.findViewById(R.id.userName));
        mRequestData.billingDepartmentName = getIncludeItemEditString(rootView.findViewById(R.id.userDept));

        //納入先
        mRequestData.receiverUserName = getIncludeItemEditString(rootView.findViewById(R.id.deliName));
        mRequestData.receiverDepartmentName = getIncludeItemEditString(rootView.findViewById(R.id.deliDept));

        //出荷オプション
        int itemCount = mResponse.mItemList.size();
        if (itemCount > 1) {

            mRequestData.shipOption = mRadioSelectValue;
            if (mRadioGroup1.getCheckedRadioButtonId() == -1) {
//				return false;
            }
        } else {

            mRequestData.shipOption = null;
        }

        mRequestData.receptionCode = mResponse.receptionCode;
        mRequestData.receiverCode = mResponse.receiver.receiverCode;

        //見積変換？
        if (quotationConvert) {
            mRequestData.quotationConvertFlag = "1";
        }


        //追加のパラメータ（明示的に0を設定）
        //アンフィット素通しフラグ (unfitPassOnFlag)
        mRequestData.unfitPassOnFlag = (unfitPass) ? "1" : "0";

        //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan() && AppConfig.getInstance().getSettlementType().equals("ADV")) {
            if (selectPaymentGroup != null) {
                mRequestData.paymentGroup = selectPaymentGroup.paymentGroup;
            }
        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

        for (ResponseCheckOrder.ItemInfo itemInfo : mResponse.mItemList) {

/*
            TODO:仕様未確認なのでコメント
			if (!"0".equals(itemInfo.unfitFlag)) {
				//アンフィット素通し可以外
				continue;
			}
*/

            RequestConfirmOrder.ItemInfo reqItem;
            reqItem = new RequestConfirmOrder.ItemInfo();
            reqItem.orderItemNo = itemInfo.orderItemNo;
            reqItem.expressType = itemInfo.expressType;

//			mRequestData.mItemList.add(reqItem);
        }

        //いろいろなリクエストパラメータ
        mConfirmApi.setParameter(mRequestData, mMessageDialog);
        mConfirmApi.connect(getContext());

    }


    protected void doComplete(int openType, DataContainer dataContainer) {

        Bundle bundle = new Bundle();
        bundle.putInt("openType", openType);    //注文か見積もりか区別
        bundle.putSerializable("dataContainer", dataContainer);

        BaseFragment fragment = new OrderCompleteFragment();
        fragment.setBundleData(bundle);

        getFragmentController().replaceFragment(fragment, FragmentController.ANIMATION_FADE_IN);
    }


    protected void doCompleteQuote(int openType, DataContainer dataContainer) {

        Bundle bundle = new Bundle();
        bundle.putInt("openType", openType);    //注文か見積もりか区別
        bundle.putSerializable("dataContainer", dataContainer);

        BaseFragment fragment = new EstimateCompleteFragment();
        fragment.setBundleData(bundle);

        getFragmentController().replaceFragment(fragment, FragmentController.ANIMATION_FADE_IN);
    }


    protected void makeView(View rootView) {

        final LayoutInflater inflater = mParent.getLayoutInflater();

        //
        mListView = (ListView) rootView.findViewById(R.id.listView);

        //ヘッダー情報

        //通常共通ヘッダとフッタ
        mHeaderView = inflateLayout(inflater, R.layout.list_item_order_confirm_header_item, mListView, false);


        //進行ゲージ
        TextView textProgress1 = (TextView) mHeaderView.findViewById(R.id.textProgress1);
        TextView textProgress2 = (TextView) mHeaderView.findViewById(R.id.textProgress2);
        TextView textProgress3 = (TextView) mHeaderView.findViewById(R.id.textProgress3);
        if (mResponse.isFromCart()) {

            textProgress1.setText(getResourceString(R.string.progress_order_from_cart));
        } else {

            textProgress1.setText(getResourceString(R.string.progress_order_from_quote));
        }

        textProgress2.setSelected(true);
        textProgress2.setText(getResourceString(R.string.progress_order_2));
        textProgress3.setText(getResourceString(R.string.progress_order_3));

        //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan()) {
            selectPaymentGroup = null;
            mPaymentView = inflateLayout(inflater, R.layout.radio_group_pay_select, mListView, false);
            mPaymentRadioGroup = (RadioGroup) mPaymentView.findViewById(R.id.radioGroup_payment);
            mListView.addFooterView(mPaymentView);
            mPaymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    try {
                        RadioButton rb = (RadioButton) group.findViewById(checkedId);
                        selectPaymentGroup = (ResponseCheckOrder.PaymentGroup) rb.getTag();
                        AppLog.d(selectPaymentGroup.toString() + "checkedId=" + checkedId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

        //ListViewと EditTextに相性があるので複数分割している
        mFooterView = inflateLayout(inflater, R.layout.list_item_order_confirm_footer_item, mListView, false);
        mFooterView2 = inflateLayout(inflater, R.layout.list_item_order_confirm_footer2_item, mListView, false);
        mFooterView3 = inflateLayout(inflater, R.layout.list_item_order_confirm_footer3_item, mListView, false);


        TextView buttonBack = (TextView) mFooterView3.findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.finish();
            }
        });

        if (mResponse.isFromCart()) {

            buttonBack.setText(getResourceString(R.string.confirm_back_from_cart));
        } else {

            buttonBack.setText(getResourceString(R.string.confirm_back_from_quote));
        }


        // 確定ボタンの有効無効
        mButtonConfirm = mFooterView3.findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            private boolean isClicked;

            @Override
            public void onClick(View v) {

                // MISUMI_MOBILE_APP-567
                if (isClicked) return;
                isClicked = true;


//				//注文確定はいきなり確定APIにする
//				doDispComfirmDialog();
                doSubmitSub(false, false);


                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;
                    }
                }, 1000L);
            }
        });


        //消えないとバグ扱いなので消す
//		setFooterError(1, mFooterView, mResponseConfirm);
//		setFooterError(2, mFooterView2, mResponseConfirm);


        //
        mListView.addHeaderView(mHeaderView, mResponse, false);
        mListView.addFooterView(mFooterView, mResponse, false);
        mListView.addFooterView(mFooterView2, mResponse, false);
        mListView.addFooterView(mFooterView3, mResponse, false);


        //スクロールでキーボードを閉じる
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });


    }

    //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
    private void setPaymentViewData() {
        String settlementType = AppConfig.getInstance().getSettlementType();
        //只有login中settlementType 为ADV时 才显示选择支付方式
        if (settlementType != null && settlementType.equals("ADV")) {
            mPaymentView.setVisibility(View.VISIBLE);

            final LayoutInflater inflater = mParent.getLayoutInflater();
            int id = 100;
            mPaymentRadioGroup.clearCheck();
            mPaymentRadioGroup.removeAllViews();

            for (ResponseCheckOrder.PaymentGroup paymentGroup : mResponse.paymentGroupList) {
                View view = inflater.inflate(R.layout.item_radio_payment, mPaymentRadioGroup, true);
                RadioButton rb = (RadioButton) view.findViewById(R.id.radio_payment);
                rb.setText(paymentGroup.paymentGroupName);
                rb.setTag(paymentGroup);
                rb.setId(++id);
            }

            //最后一个支付方式 不显示线条
            int conut = mPaymentRadioGroup.getChildCount();
            int size = mResponse.paymentGroupList.size();
            if (conut > 0) {
                View v = mPaymentRadioGroup.getChildAt(conut - 1);
                v.findViewById(R.id.line).setVisibility(View.GONE);
            }

            boolean isSelect = false;//是否选中
            //刷新之后 选中之前选中的
            if (selectPaymentGroup != null) {
                for (int i = 0; i < size; i++) {
                    if (mResponse.paymentGroupList.get(i).paymentGroupName.equals(selectPaymentGroup.paymentGroupName)) {
                        try {
                            RadioButton radioButton = (RadioButton) mPaymentRadioGroup.getChildAt((i * 2));
                            radioButton.setChecked(true);
                            isSelect = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }

            //默认选择 在线支付
            if (!isSelect) {
                for (int i = 0; i < size; i++) {
                    RadioButton radioButton = (RadioButton) mPaymentRadioGroup.getChildAt((i * 2));
                    ResponseCheckOrder.PaymentGroup paymentGroup = (ResponseCheckOrder.PaymentGroup) radioButton.getTag();
                    if (paymentGroup.paymentGroupName != null && paymentGroup.paymentGroupName.equals(getResourceString(R.string.order_list_online_pay))) {
                        radioButton.setChecked(true);
                        isSelect = true;
                        break;
                    }
                }
            }
            //如果没有在线支付 默认选择第一项
            if (!isSelect && conut > 0) {
                RadioButton radioButton = (RadioButton) mPaymentRadioGroup.getChildAt(0);
                radioButton.setChecked(true);
            }

        } else {
            mPaymentView.setVisibility(View.GONE);
        }
    }
    //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

    private void setFooterError(int type, View footerView, ResponseConfirmOrder responseConfirm) {

        final LayoutInflater inflater = mParent.getLayoutInflater();

        View layoutError = footerView.findViewById(R.id.layoutError);
        final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
        layoutErrorItemList.removeAllViews();

        boolean errorMessageFlag = false;

        if (responseConfirm.errorList != null && !responseConfirm.errorList.isEmpty()) {

            for (ErrorList.ErrorInfo errorInfo : responseConfirm.errorList.ErrorInfoList) {

                if (errorInfo.fieldList == null)
                    continue;

                if (errorInfo.fieldList.isEmpty())
                    continue;

                //判別
                boolean addFlag = false;

                for (Object field : errorInfo.fieldList) {

                    if (!(field instanceof String))
                        continue;

                    String fieldStr = (String) field;

                    if (type == 1) {
                        if (fieldStr.equals("billingUserName")) {
                            addFlag = true;
                            break;
                        } else if (fieldStr.equals("billingDepartmentName")) {
                            addFlag = true;
                            break;
                        }
                    }

                    if (type == 2) {
                        if (fieldStr.equals("receiverUserName")) {
                            addFlag = true;
                            break;
                        } else if (fieldStr.equals("receiverDepartmentName")) {
                            addFlag = true;
                            break;
                        }
                    }
                }

                if (!addFlag)
                    continue;

                String errorStr = errorInfo.getErrorMessage(getScreenId());
                if (android.text.TextUtils.isEmpty(errorStr)) {
                    continue;
                }

                //追加
                errorMessageFlag = true;

                View subView = inflateLayout(inflater, R.layout.include_confirm_edit_error, layoutErrorItemList, false);

                TextView tv = (TextView) subView.findViewById(R.id.textMessage);
                tv.setText(errorStr);

                layoutErrorItemList.addView(subView);
            }

        }

        //エラー
        if (!errorMessageFlag) {

            layoutError.setVisibility(View.GONE);
        } else {

            layoutError.setVisibility(View.VISIBLE);
        }

    }


    protected void setSubmitEnabled(boolean enabled) {

        // 確定ボタンの有効無効
        mButtonConfirm.setEnabled(enabled);
    }


    @Override
    protected void makeDataView(View rootView) {

        int itemCount = mResponse.mItemList.size();

        //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan()) {
            setPaymentViewData();
        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

        //出荷オプション
        View layoutShipping = mFooterView.findViewById(R.id.layoutShipping);

//        12/10 MISUMI_MOBILE_APP-572 代引きユーザでは出荷オプションをトルツメ
        if (mIsCodUser) {
            layoutShipping.setVisibility(View.GONE);
        } else {

            mHasRadioArea = (itemCount > 1);

            //ラジオエリア表示
            if (mHasRadioArea) {

                layoutShipping.setVisibility(View.VISIBLE);
            } else {

                layoutShipping.setVisibility(View.GONE);
            }
        }

        View layoutEmpty = mFooterView.findViewById(R.id.layoutEmpty);
        if (itemCount > 0) {

            layoutEmpty.setVisibility(View.GONE);
        } else {

            layoutEmpty.setVisibility(View.VISIBLE);
        }

        View changeDirectShip = mFooterView2.findViewById(R.id.textChangeDirectShip);
        if (mResponse.mReceiverList.size() > 0) {

            changeDirectShip.setVisibility(View.VISIBLE);
        } else {

            changeDirectShip.setVisibility(View.GONE);
        }


        //2が仮受付中
        View textOrderStatus = mFooterView3.findViewById(R.id.textOrderStatus);

        if ("2".equals(mResponse.orderStatus)) {

            textOrderStatus.setVisibility(View.VISIBLE);
        } else {

            textOrderStatus.setVisibility(View.GONE);
        }


        //必須項目
        mRadioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);

        //入力文字数制限
        mUserName = getIncludeItemEdit(rootView.findViewById(R.id.userName));
        mDeliName = getIncludeItemEdit(rootView.findViewById(R.id.deliName));
/*
        InputFilter inputFilter = new InputFilter() {

			//全角のみ受付
			private boolean isZenkaku(String s) {

		        for( int i=0; i<s.length(); i++ ) {

		            char c = s.charAt( i );
		            if( ( c<='\u007e' )|| // 英数字
		                ( c=='\u00a5' )|| // \記号
		                ( c=='\u203e' )|| // ~記号
		                ( c>='\uff61' && c<='\uff9f' ) // 半角カナ
		            )
		                return false;
		        }

                return true;
			}

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if (isZenkaku(source.toString())) {
                    return source;
                } else {
                    return "";
                }
            }
        };
*/

/*
"10 (日本) 20 (中国)"
"15 (日本) 30 (中国)"
*/
        InputFilter inputFilterLength;
        if (SubsidiaryCode.isJapan()) {
            inputFilterLength = new InputFilter.LengthFilter(10);
        } else {
            inputFilterLength = new InputFilter.LengthFilter(20);
        }
        mUserName.setFilters(new InputFilter[]{inputFilterLength});
        mDeliName.setFilters(new InputFilter[]{inputFilterLength});

        View userDept = getIncludeItemEdit(rootView.findViewById(R.id.userDept));
        View deliDept = getIncludeItemEdit(rootView.findViewById(R.id.deliDept));

        if (SubsidiaryCode.isJapan()) {
            inputFilterLength = new InputFilter.LengthFilter(15);
        } else {
            inputFilterLength = new InputFilter.LengthFilter(30);
        }
        ((EditText) userDept).setFilters(new InputFilter[]{inputFilterLength});
        ((EditText) deliDept).setFilters(new InputFilter[]{inputFilterLength});

        ((EditText) userDept).setHint(getResourceString(R.string.edit_hint_zenkaku_1));
        mUserName.setHint(getResourceString(R.string.edit_hint_zenkaku_2));
        ((EditText) deliDept).setHint(getResourceString(R.string.edit_hint_zenkaku_3));
        mDeliName.setHint(getResourceString(R.string.edit_hint_zenkaku_4));

        //必須項目の監視
        mRadioGroup1.setOnCheckedChangeListener(null);


        //デフォルト設定値 出荷オプション
        if ("C".equals(mRadioSelectValue)) {
            mRadioGroup1.check(R.id.radio_ship_c);
        }
        if ("P".equals(mRadioSelectValue)) {
            mRadioGroup1.check(R.id.radio_ship_p);
        }

        mRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radio_ship_c)
                    mRadioSelectValue = "C";

                if (checkedId == R.id.radio_ship_p)
                    mRadioSelectValue = "P";

                setSubmitEnabled(judgeEnableButton());

                //選択されたら通信する
                storeEditInfo();
                doSelectedShipOption(mResponse, ("C".equals(mRadioSelectValue)));
            }
        });


        //
        mUserName.addTextChangedListener(mTextWatcher);
        mDeliName.addTextChangedListener(mTextWatcher);


        //
        mListAdapter = new OrderConfirmAdapter(getContext(), R.layout.list_item_order_confirm_detail_item, mResponse.mItemList, mOnItemClickListener, getScreenId());
        mListView.setAdapter(mListAdapter);


        // 直送先変更
        setDirectShipInfo(rootView, mResponse.receiver);

        //-- ADD NT-LWL 17/08/07 Depo FR -
        // 判断是否显示非闪达地址提示文言
        if (!SubsidiaryCode.isJapan()) {
            // 地址是否支持闪达
            boolean isDepoAddress = "1".equals(mResponse.receiver.immediateDeliveryFlag);
            // 列表是否选择闪达
            boolean listHasDepo = false;
            for (ResponseCheck.ItemInfo itemInfo : mResponse.mItemList) {
                if ("V0".equals(itemInfo.expressType)) {
                    listHasDepo = true;
                    break;
                }
            }

            // 显示/隐藏 警告信息
            if (listHasDepo) {
                if (isDepoAddress) {
                    //隐藏 警告信息
                    rootView.findViewById(R.id.address_attention).setVisibility(View.GONE);
                    // 按钮恢复可点
                    mButtonConfirm.setEnabled(true);
                    mButtonConfirm.setClickable(true);
                } else {
                    // 显示 警告信息
                    rootView.findViewById(R.id.address_attention).setVisibility(View.VISIBLE);
                    // 按钮不可点击
                    mButtonConfirm.setEnabled(false);
                    mButtonConfirm.setClickable(false);
                }

            } else {

                //隐藏 警告信息
                rootView.findViewById(R.id.address_attention).setVisibility(View.GONE);
                // 按钮恢复可点
                mButtonConfirm.setEnabled(true);
                mButtonConfirm.setClickable(true);
            }
        }
        //-- ADD NT-LWL 17/08/07 Depo TO -


        // 直送先変更
        rootView.findViewById(R.id.textChangeDirectShip).setOnClickListener(new View.OnClickListener() {
            private boolean isClicked;

            @Override
            public void onClick(View v) {

                // MISUMI_MOBILE_APP-567
                if (isClicked) return;
                isClicked = true;

                doChangeDirectShip();

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;
                    }
                }, 1000L);
            }
        });


        //
        SpannableStringBuilder ssb;
        SpannableString ss;
        String str;

        //
        ssb = new SpannableStringBuilder();
        if (itemCount == 0) {

            str = getResourceString(R.string.label_hyphen);    //ハイフン化
        } else {

            str = Format.formatCount(itemCount);
        }

        ss = SpannableUtil.newSpannableString(str, 15, true, true);
        ssb.append(ss);
        ss = new SpannableString(getResourceString(R.string.confirm_item_count_unit));
        ssb.append(ss);

        setIncludeItemText(rootView.findViewById(R.id.totalCount), getResourceString(R.string.confirm_item_count), ssb);

        //
        ssb = new SpannableStringBuilder();

        if (mResponse.totalPrice == null) {

            ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化

        } else {

            ssb.append(Format.formatAmountWithUnit(mResponse.totalPrice, null));

        }

        if (SubsidiaryCode.isJapan() && mIsIncludeTax) {

            //日本非表示
            rootView.findViewById(R.id.totalPrice).setVisibility(View.GONE);
        } else {

            setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.confirm_total_price), ssb);
        }

        //
        if (SubsidiaryCode.isJapan()) {

            rootView.findViewById(R.id.deliveryCharge).setVisibility(View.GONE);
            rootView.findViewById(R.id.deliveryChargeDiscount).setVisibility(View.GONE);

            if (!mIsIncludeTax) {
                //日本非表示
                rootView.findViewById(R.id.totalPriceWithTax).setVisibility(View.GONE);
            } else {

                //合計金額(税込)
                ssb = new SpannableStringBuilder();

                if (mResponse.totalPriceIncludingTax == null) {

                    ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化

                } else {

                    ssb.append(Format.formatAmountWithUnit(mResponse.totalPriceIncludingTax, null));

                }

                setIncludeItemText(rootView.findViewById(R.id.totalPriceWithTax), getResourceString(R.string.confirm_total_price_wtax), ssb);

            }

        } else {

            //中国版は送料値引き
            //送料
            ssb = new SpannableStringBuilder();

            if (mResponse.standardDeliveryCharge == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(mResponse.standardDeliveryCharge, (15.0f / 12.0f), true, true, null));
            }


            setIncludeItemText(rootView.findViewById(R.id.deliveryCharge), getResourceString(R.string.confirm_delivery), ssb);

            //送料値引き
            ssb = new SpannableStringBuilder();

            if (mResponse.deliveryChargeDiscount == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(mResponse.deliveryChargeDiscount, (15.0f / 12.0f), true, true, null));
            }


            setIncludeItemText(rootView.findViewById(R.id.deliveryChargeDiscount), getResourceString(R.string.confirm_delivery_discount), ssb);

            //合計金額(税込)
            ssb = new SpannableStringBuilder();

            if (mResponse.totalPriceIncludingTax == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化

            } else {

                ssb.append(Format.formatAmountWithUnit(mResponse.totalPriceIncludingTax, (15.0f / 12.0f), true, true, null));

            }

            setIncludeItemText(rootView.findViewById(R.id.totalPriceWithTax), getResourceString(R.string.confirm_total_price_wtax), ssb);
        }

        //代引き手数料
        //cashOnDeliveryChargeIncludingTax
        if (mIsCodUser) {

            CharSequence cstr;
            if (mResponse.cashOnDeliveryChargeIncludingTax == null) {
//				cstr = Format.getHyphenWithUnit();
                cstr = getResourceString(R.string.label_hyphen);    //ハイフン化
            } else {
                cstr = Format.formatAmountWithUnit(mResponse.cashOnDeliveryChargeIncludingTax, false, false, false, null);
            }

            setIncludeItemText(rootView.findViewById(R.id.codCharge), getResourceString(R.string.confirm_cod_charge), cstr);

            //強制背景色設定
            ViewBgUtil.requestLayout(rootView.findViewById(R.id.codCharge), R.id.viewDiv, R.id.textView1, R.id.textView2);

        } else {

            rootView.findViewById(R.id.codCharge).setVisibility(View.GONE);
        }


        // Map purchaser	//○発注者
        if (mResponse.hasPurchaser) {

//			rootView.findViewById(R.id.userComp).setVisibility(View.VISIBLE);
//			rootView.findViewById(R.id.userCode).setVisibility(View.VISIBLE);

        } else {

//			rootView.findViewById(R.id.userComp).setVisibility(View.GONE);
//			rootView.findViewById(R.id.userCode).setVisibility(View.GONE);
        }

        setIncludeItemText(rootView.findViewById(R.id.userComp), getResourceString(R.string.confirm_user_company_label), "", mResponse.purchaser.customerName, false);
        setIncludeItemText(rootView.findViewById(R.id.userCode), getResourceString(R.string.confirm_user_code_label), "", mResponse.purchaser.customerCode, false);


        setIncludeItemEdit(rootView.findViewById(R.id.userDept), getResourceString(R.string.confirm_user_dept_label), "", mResponse.purchaser.userDepartmentName);
        setIncludeItemEdit(rootView.findViewById(R.id.userName), getResourceString(R.string.confirm_user_name_label), getResourceString(R.string.require_mark), mResponse.purchaser.userName);

        //消えないとバグ扱いなので消す
        mFooterView.findViewById(R.id.layoutError).setVisibility(View.GONE);
        mFooterView2.findViewById(R.id.layoutError).setVisibility(View.GONE);
    }


    protected void setIncludeItemText(View subView, String str1, String str2, String str3, boolean doHidden) {

        if (android.text.TextUtils.isEmpty(str3)) {

            if (doHidden) {
                subView.setVisibility(View.GONE);
                return;
            }

            str3 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView3)).setText(str3);
    }


    //EditText監視
    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            setSubmitEnabled(judgeEnableButton());
        }
    };


    //直送先変更
    protected void doChangeDirectShip() {

        hideKeyboard();

        storeEditInfo();

        dispChangeDirectShip(mResponse);
    }


    //確定ボタン判別
    private boolean judgeEnableButton() {

        //ラジオエリア表示
        if (mHasRadioArea) {
            if (mRadioGroup1.getCheckedRadioButtonId() == -1) {
                return false;
            }
        }

        String str;
        str = mUserName.getText().toString();
        if (str.isEmpty())
            return false;

        str = mDeliName.getText().toString();
        return !str.isEmpty();

    }


    private void storeEditInfo() {

        //発注者
        mResponse.purchaser.userName = getIncludeItemEditString(mFooterView.findViewById(R.id.userName));
        mResponse.purchaser.userDepartmentName = getIncludeItemEditString(mFooterView.findViewById(R.id.userDept));

        //納入先
        mResponse.receiver.receiverUserName = getIncludeItemEditString(mFooterView2.findViewById(R.id.deliName));
        mResponse.receiver.receiverDepartmentName = getIncludeItemEditString(mFooterView2.findViewById(R.id.deliDept));

    }


/*
    //アンフィット確認ダイアログの表示処理
	protected void doDispComfirmDialog() {

		doDispComfirmDialog(mResponse);
	}
*/


    // リストビューのアイテムがクリックされた時
    OrderConfirmAdapter.OnItemClickListener mOnItemClickListener = new OrderConfirmAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view, int position, long id) {
        }


        public void onItemChange(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseCheckOrder.ItemInfo itemInfo = (ResponseCheckOrder.ItemInfo) parent.getItem(position);

            storeEditInfo();

            doChangeExpressType(mResponse, itemInfo);
        }

        //当日出荷
        public void onItemToday(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseCheckOrder.ItemInfo itemInfo = (ResponseCheckOrder.ItemInfo) parent.getItem(position);

            storeEditInfo();

            doChangeTodayType(mResponse, itemInfo);
        }
    };


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.OrderConfirm;
    }


    /**
     * 注文振り分け
     */
    private class ConfirmApi extends ApiAccessWrapper {

        private RequestConfirmOrder mRequest;
        private MessageDialog mMessageDialog;

        @Override
        protected String getScreenId() {
            return OrderConfirmFragment.this.getScreenId();
        }

        public void setParameter(RequestConfirmOrder request, MessageDialog messageDialog) {
            mRequest = request;
            mMessageDialog = messageDialog;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createConfirmOrder(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseConfirmOrder response = new ResponseConfirmOrder();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }

                    //保持の情報をクリアする
                    mResponseConfirm = new ResponseConfirmOrder();

                    //カートから、見積履歴からの区別をレスポンスに設定
                    if (mResponse.isFromCart()) {

                        response.setFromCart();
                    } else {

                        response.setFromQuote();
                    }

                    //見積変換？
                    if ("1".equals(mRequest.quotationConvertFlag)) {
                        doCompleteQuote(SubActivity.SUB_TYPE_ORDER, response);
                        return;
                    }

                    doComplete(SubActivity.SUB_TYPE_ORDER, response);

                    break;

                //確定時刻バリデーションエラー
                case NetworkInterface.VALIDATION_ERROR:

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }

//					showToast("430 確定時刻バリデーションエラー");

                    doDisp430Dialog(response.errorList);

                    break;

                //アンフィットエラー
                case NetworkInterface.UNFIT_ERROR:

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }

//					showToast("431 アンフィットエラー");

                    String unfitConfirmType = getUnfitConfirmType(response);
                    doDispComfirmDialog(unfitConfirmType, mResponse);

                    break;

                default:

                    showErrorMessage(response.errorList);

                    //エラーの時に保存する
                    mResponseConfirm = response;

                    //入力エラーを画面に反映する
                    setFooterError(1, mFooterView, mResponseConfirm);
                    setFooterError(2, mFooterView2, mResponseConfirm);
                    break;
            }
        }
    }


    @Override
    protected void onSuccessDirectShipping(DataContainer response) {

        ResponseCheckOrder responseCheckOrder = (ResponseCheckOrder) response;

        mResponse = responseCheckOrder;
        makeDataView(getView());

        //スクロール位置を先頭に戻す
        mListView.setSelection(0);
    }

    @Override
    protected void onSuccessExpressType(DataContainer response) {

        mResponse = (ResponseCheckOrder) response;
        makeDataView(getView());

        //スクロール位置を先頭に戻す
        mListView.setSelection(0);
    }


    private void checkOutOfStock() {

        //在庫切れフラグを判定
        if (!"1".equals(mResponse.stockoutConfirmFlag)) {
            return;
        }


        //ここから在庫切れの処理
        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {

                    doSubmitOutOfStock(true);
                } else {

                    doSubmitOutOfStock(false);
                }
            }
        }).setAutoClose(false);


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_out_of_stock_layout, null, false);

        ListView listView = (ListView) view.findViewById(R.id.listView);

        View viewH = inflateLayout(inflater, R.layout.dialog_out_of_stock_layout_h, listView, false);

        listView.addHeaderView(viewH, null, false);

        MyStockAdapter listAdapter = new MyStockAdapter(getContext(), R.layout.list_item_stock_item, new ArrayList<ResponseCheckOrder.ItemInfo>(), getScreenId());
        listView.setAdapter(listAdapter);

        //在庫切れの商品
        //ダイアログに表示する中身を作成
        int orderableCount = 0;
        for (ResponseCheckOrder.ItemInfo itemInfo : mResponse.mItemList) {

            //次回入荷が空は表示しない
            if (android.text.TextUtils.isEmpty(itemInfo.nextArrivalDate)) {
                continue;
            }

            ++orderableCount;

            listAdapter.add(itemInfo);
        }

        listAdapter.notifyDataSetChanged();

        {
            String str = "" + orderableCount;
            SpannableStringBuilder ssb = new SpannableStringBuilder();

            ssb.append(getResourceString(R.string.out_of_stock_dialog_list_title));
            SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
            ssb.append(ss);

            ssb.append(getResourceString(R.string.out_of_stock_dialog_list_title_unit));

            TextView tv = (TextView) viewH.findViewById(R.id.textCount);
            tv.setText(ssb);
        }

        mMessageDialog.show(view, R.string.out_of_stock_dialog_yes, R.string.out_of_stock_dialog_no);

    }


    //注文確認のAPI FROM 注文確認
    private void doSubmitOutOfStock(boolean flag) {

        //注文確認のAPI FROM 注文確認
        boolean isFromCart = mResponse.isFromCart();

        RequestCheckOrderFromOrder requestData = new RequestCheckOrderFromOrder();
        requestData.receptionCode = mResponse.receptionCode;
        requestData.resolveOutstockFlag = flag ? "1" : "0";

        //-- ADD NT-LWL 16/12/05 AliPay Payment FR -
        if (!SubsidiaryCode.isJapan() && selectPaymentGroup != null) {
            requestData.paymentGroup = selectPaymentGroup.paymentGroup;
        }
        // -- ADD NT-LWL 16/12/05 AliPay Payment TO -
        //いろいろなリクエストパラメータ
        mOrderConfirmStockApi.setParameter(isFromCart, requestData, mMessageDialog);
        mOrderConfirmStockApi.connect(getContext());
    }


    private String getUnfitConfirmType(ResponseConfirmOrder response) {

/*
https://misumi-imj.backlog.jp/view/NPF_SP-708
1は欠番(返却しない)
アンフィットエラーの場合
※ parameterListでアンフィット確認タイプを返します
※ アンフィット確認タイプ
　"2": 見積確定容認
　"3": ミスミ確認中としての注文確定容認
　"4": 見積 or 注文確定選択
*/
        String unfitConfirmType = "";

        if (response.errorList == null) {
            return unfitConfirmType;
        }

        if (response.errorList.ErrorInfoList != null) {
            if (!response.errorList.ErrorInfoList.isEmpty()) {
                ErrorList.ErrorInfo errorInfo = response.errorList.ErrorInfoList.get(0);

                if (errorInfo.errorParameterList != null) {
                    if (!errorInfo.errorParameterList.isEmpty()) {

                        try {
                            Object object = errorInfo.errorParameterList.get(0);
                            unfitConfirmType = (String) object;
                        } catch (Exception e) {
                            unfitConfirmType = "";
                        }

                    }
                }
            }
        }

        //サーババグの異常値避け
        if ("2".equals(unfitConfirmType)) {
        } else if ("3".equals(unfitConfirmType)) {
        } else if ("4".equals(unfitConfirmType)) {
        } else {
            unfitConfirmType = "";
        }

        return unfitConfirmType;
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.OrderConfirm;
    }
}


