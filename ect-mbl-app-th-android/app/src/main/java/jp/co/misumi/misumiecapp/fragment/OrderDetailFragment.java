package jp.co.misumi.misumiecapp.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.OrderDetailAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AddMyParts;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.AliPaymentInfo;
import jp.co.misumi.misumiecapp.data.ResponseGetOrderDetail;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * 注文履歴詳細画面
 */
//TODO:表示項目の内容が暫定
//TODO:各ボタンを押した時の動作が暫定
//TODO:破棄された時の変数状態の保持 savedInstanceState
public class OrderDetailFragment extends BaseGetSpProductApi {

    //画面表示用
    private ResponseGetOrderDetail mResponseGet;

    private OrderDetailAdapter mListAdapter;
    private ListView mListView;
    private View mHeaderView;
    private View mFooterView;

//	private String mCurrencyFormat;

    private AddToCartApi mAddToCartApi;
    private AddMyPartApi mAddMyPartApi;
    private GetSpProductApi mGetSpProductApi;

    private final boolean mIsIncludeTax;
    private final boolean mIsCodUser;

    //-- ADD NT-SLJ 16/10/25 AliPay Payment FR -
    private MyOnlinePaymentApi mMyOnlinePaymentApi;
    private boolean mNeedShowPayButton = false;
    //-- ADD NT-SLJ 16/10/25 AliPay Payment TO -

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("ResponseGet", mResponseGet);
    }


    public OrderDetailFragment() {
        //-- ADD NT-SLJ 16/10/25 AliPay Payment FR -
        mMyOnlinePaymentApi = new MyOnlinePaymentApi();
        //-- ADD NT-SLJ 16/10/25 AliPay Payment TO -
        mAddToCartApi = new AddToCartApi();
        mAddMyPartApi = new AddMyPartApi();
        mGetSpProductApi = new GetSpProductApi() {
            @Override
            protected void onSuccess(ResponseGetSpProduct response) {
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

            @Override
            protected String getScreenId() {
                return OrderDetailFragment.this.getScreenId();
            }
        };

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
        mIsCodUser = AppConfig.getInstance().isCodUser();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

            mResponseGet = (ResponseGetOrderDetail) savedInstanceState.getSerializable("ResponseGet");


        } else {

            if (mResponseGet == null) {
                mResponseGet = (ResponseGetOrderDetail) getParameter();
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateLayout(inflater, R.layout.fragment_order_detail, container, false);

        if (savedInstanceState == null) {
            if (mResponseGet != null) {
//				return rootView;
            }
        }

        ArrayList<ResponseGetOrderDetail.ItemInfo> itemList = mResponseGet.mItemList;

        //
        mListAdapter = new OrderDetailAdapter(getContext(), R.layout.list_item_order_detail_item, itemList, mOnItemClickListener, getScreenId());

        //
        mListView = (ListView) rootView.findViewById(R.id.listView);

        //ヘッダー情報
        mHeaderView = inflateLayout(inflater, R.layout.list_item_order_detail_header, mListView, false);
        mFooterView = inflateLayout(inflater, R.layout.list_item_order_detail_footer, mListView, false);

        setHeaderViewData(mHeaderView, mResponseGet);
        mListView.addHeaderView(mHeaderView, mResponseGet, false);

        setFooterViewData(mFooterView, mResponseGet);
        mListView.addFooterView(mFooterView, mResponseGet, false);

        View inquiryView = inflateLayout(inflater, R.layout.list_item_order_detail_footer_inquiry, mListView, false);
        mListView.addFooterView(inquiryView, null, false);

        if (mResponseGet == null || mResponseGet.mItemList == null) {
            mListView.setVisibility(View.GONE);
        }


        mListView.setAdapter(mListAdapter);

        return rootView;
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.OrderDetail;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        mAddToCartApi.close();
        mAddMyPartApi.close();
        mGetSpProductApi.close();
        mMyOnlinePaymentApi.close();
        super.onPause();
    }


    private void setHeaderViewData(View headerView, final ResponseGetOrderDetail response) {
        double totalPrice = 0.0;

        // 振込み期限の文言
        TextView deadLine = (TextView) headerView.findViewById(R.id.textDeadlineInfo);
        deadLine.setVisibility(View.GONE);
        //-- DEL NT-LWL 16/11/13 AliPay Payment FR -
        /*if (SubsidiaryCode.isJapan()){
        }else{
            boolean isW = false;
            for (ResponseGetOrderDetail.ItemInfo item : response.mItemList){
                if (item.status.equals("w")) {
                    isW = true;
                    break;
                }
            }

            if (isW && "ADV".equals(AppConfig.getInstance().getSettlementType())){

                String paymentType = AppConfig.getInstance().getPaymentType();

                //前金 + 振込 + ステータス＝入金待ち
                if ( "10".equals(paymentType) ){
                    String deadlineInfo = getResourceString(R.string.order_detail_deadline_info1) + "\n"
                            + getResourceString(R.string.order_detail_deadline_info2) + "\n"
                            + getResourceString(R.string.order_detail_deadline_info3);
                    deadLine.setVisibility(View.VISIBLE);
                    deadLine.setText(deadlineInfo);
                }else if ( "60".equals(paymentType) ||
                        "61".equals(paymentType) ||
                        "62".equals(paymentType) ||
                        "63".equals(paymentType)) {
                    // 前金 + オンライン支払い + ステータス＝入金待ち
                    String paymentDeadlineDateTime = response.paymentDeadlineDateTime;
                    if (paymentDeadlineDateTime != null && !paymentDeadlineDateTime.isEmpty()){
                        String deadlineInfo = String.format(getResourceString(R.string.order_detail_deadline_info4),paymentDeadlineDateTime);
                        deadLine.setVisibility(View.VISIBLE);
                        deadLine.setText(deadlineInfo);
                    }
                }
            }
        }*/
        //-- DEL NT-LWL 16/11/13 AliPay Payment TO -
        //-- ADD NT-SLJ 16/11/13 AliPay Payment FR -
        headerView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
        if (SubsidiaryCode.isJapan()) {
        } else {
            boolean isAllW = true;
            for (ResponseGetOrderDetail.ItemInfo item : response.mItemList) {
                if (!item.status.equals("w")) {
                    isAllW = false;
                    break;
                }
            }
            //有等待支付商品，settlementType等于ADV
            if (isAllW && response.settlementType != null && "ADV".equals(response.settlementType)) {
                //支付类型为线上支付 paymentGroup等于1  支付期限显示
                if (response.paymentGroup != null && "1".equals(response.paymentGroup)) {
                    String paymentDeadlineDateTime = response.paymentDeadlineDateTime;
                    if (paymentDeadlineDateTime != null) {
                        if (paymentDeadlineDateTime.isEmpty()) {
                            paymentDeadlineDateTime = getResourceString(R.string.common_status_unknown);
                        }
                        String deadlineInfo = String.format(getResourceString(R.string.order_detail_deadline_info4), paymentDeadlineDateTime);
                        deadLine.setVisibility(View.VISIBLE);
                        deadLine.setText(deadlineInfo);
                        //支付按钮显示
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                        Date nowdate = new Date();
                        Date d;
                        boolean compareFlag = true;
                        try {
                            d = sDateFormat.parse(response.paymentDeadlineDateTime);
                            compareFlag = d.before(nowdate);
                            AppLog.d("now time :" + sDateFormat.format(nowdate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (!compareFlag) {
                            mNeedShowPayButton = true;
                            headerView.findViewById(R.id.buttonPay).setVisibility(View.VISIBLE);
                            headerView.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gotoPay(response.orderSlipNo, "60");
                                }
                            });
                        }
                    }
                }
                //支付类型为银行转账 paymentGroup等于2  银行转账说明
                else if (response.paymentGroup != null && "2".equals(response.paymentGroup)) {
                    String deadlineInfo = getResourceString(R.string.order_detail_deadline_info1) + "\n"
                            + getResourceString(R.string.order_detail_deadline_info2) + "\n"
                            + getResourceString(R.string.order_detail_deadline_info3);
                    deadLine.setVisibility(View.VISIBLE);
                    deadLine.setText(deadlineInfo);
                } else {
                }
                //支付方式显示
                if (response.paymentGroupName != null && !response.paymentGroupName.isEmpty()) {
                    setIncludeItemText(headerView.findViewById(R.id.paymentType), getResourceString(R.string.order_list_payment_type),
                            response.paymentGroupName, null);
                } else {
                    setIncludeItemText(headerView.findViewById(R.id.paymentType), getResourceString(R.string.order_list_payment_type),
                            getResourceString(R.string.label_hyphen), null);
                }


            } else {
                headerView.findViewById(R.id.paymentType).setVisibility(View.GONE);
            }

        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment TO -
        ArrayList<ResponseGetOrderDetail.ItemInfo> itemList = response.mItemList;
        for (ResponseGetOrderDetail.ItemInfo info : itemList) {

            if (info.totalPrice != null) {
                totalPrice += info.totalPrice;
            }
        }

        //ステータスがミスミ確認中のものを数える
        int z = mListAdapter.counterZ(response.mItemList);

        setIncludeItemText(headerView.findViewById(R.id.infoSlipNo), getResourceString(R.string.order_detail_slip_no),
                response.orderSlipNo, null);

        setIncludeItemText(headerView.findViewById(R.id.infoDateTime), getResourceString(R.string.order_detail_date),
                response.orderDateTime, null);

        {
            setIncludeItemText(headerView.findViewById(R.id.textNameMethod), getResourceString(R.string.order_detail_user_name) + "\n" + getResourceString(R.string.order_detail_user_method),
                    convertOrderType(response.userName, response.orderType), null);
        }

        if (response.mItemList == null || response.mItemList.isEmpty()) {
            setIncludeItemText(headerView.findViewById(R.id.textDeliveryCompanyName), getResourceString(R.string.order_detail_deliverycompanyname),
                    getText(R.string.label_hyphen), null);
        } else {
            setIncludeItemText(headerView.findViewById(R.id.textDeliveryCompanyName), getResourceString(R.string.order_detail_deliverycompanyname),
                    response.mItemList.get(0).deliveryCompanyName, null);
        }


        int itemCount = response.mItemList.size();

        {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String str;

            if (itemCount == 0) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化

                ssb.append(str);
            } else {
                //赤色
                str = String.format("%1$,3d", itemCount);

                SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
                ssb.append(ss);

                ssb.append(getResourceString(R.string.quote_hist_detail_count_unit));

                if (z > 0) {

                    //(ミスミ確認中x件)
                    ssb.append("（");

                    str = String.format(getResourceString(R.string.order_detail_misumi_check_str), Format.formatCount(z));

                    ss = SpannableUtil.newSpannableString(str, 0, true, true);
                    ssb.append(ss);

                    ssb.append("）");
                }
            }

            setIncludeItemText(headerView.findViewById(R.id.infoCount), getResourceString(R.string.order_detail_item_count), ssb, null);
        }


        //中国は税込み税抜きの２行
        {
            if (SubsidiaryCode.isJapan()) {
                if (!mIsIncludeTax) {
                    //日本外税
                    headerView.findViewById(R.id.totalPriceWithTax).setVisibility(View.GONE);

                    SpannableStringBuilder ssb = new SpannableStringBuilder();

                    if (response.totalPrice == null) {

                        ssb.append(getResourceString(R.string.label_hyphen));
                    } else {


                        ssb.append(Format.formatAmountWithUnit(response.totalPrice, null));
                    }
                    setIncludeItemText(headerView.findViewById(R.id.totalPrice),
                            getResourceString(R.string.quote_hist_detail_total_price), ssb, null);

//					//強制背景色設定
//					ViewBgUtil.requestLayout(headerView.findViewById(R.id.totalPrice), R.id.viewDiv, R.id.textView1, R.id.viewRight);

                } else {
                    //日本内税
                    headerView.findViewById(R.id.totalPrice).setVisibility(View.GONE);

                    SpannableStringBuilder ssb = new SpannableStringBuilder();

                    if (response.totalPriceIncludingTax == null) {

                        ssb.append(getResourceString(R.string.label_hyphen));
                    } else {


                        ssb.append(Format.formatAmountWithUnit(response.totalPriceIncludingTax, null));
                    }
                    setIncludeItemText(headerView.findViewById(R.id.totalPriceWithTax),
                            getResourceString(R.string.quote_hist_detail_total_price_tax), ssb, null);

//					//強制背景色設定
//					ViewBgUtil.requestLayout(headerView.findViewById(R.id.totalPriceWithTax), R.id.viewDiv, R.id.textView1, R.id.viewRight);

                }
            } else {
                //中国外税
                SpannableStringBuilder ssb = new SpannableStringBuilder();

                if (response.totalPrice == null) {

                    ssb.append(getResourceString(R.string.label_hyphen));

                } else {

                    ssb.append(Format.formatAmountWithUnit(response.totalPrice, null));
                }

                setIncludeItemText(headerView.findViewById(R.id.totalPrice),
                        getResourceString(R.string.quote_hist_detail_total_price), ssb, null);

//				//強制背景色設定
//				ViewBgUtil.requestLayout(headerView.findViewById(R.id.totalPrice), R.id.viewDiv, R.id.textView1, R.id.viewRight);

                //中国税込み
                SpannableStringBuilder ssbTax = new SpannableStringBuilder();

                if (response.totalPriceIncludingTax == null) {

                    ssbTax.append(getResourceString(R.string.label_hyphen));
                } else {

                    ssbTax.append(Format.formatAmountWithUnit(response.totalPriceIncludingTax, null));
                }

                setIncludeItemText(headerView.findViewById(R.id.totalPriceWithTax),
                        getResourceString(R.string.quote_hist_detail_total_price_tax), ssbTax, null);

//				//強制背景色設定
//				ViewBgUtil.requestLayout(headerView.findViewById(R.id.totalPriceWithTax), R.id.viewDiv, R.id.textView1, R.id.viewRight);
            }
        }

        //--UDP NT-LWL 17/09/29 ExcludeFreight FR -
//        if (z > 0) {
//            headerView.findViewById(R.id.textChecking).setVisibility(View.VISIBLE);
//        } else {
//            headerView.findViewById(R.id.textChecking).setVisibility(View.GONE);
//        }
        TextView textChecking = (TextView) headerView.findViewById(R.id.textChecking);
        textChecking.setVisibility(View.VISIBLE);
        if (z > 0) {
            textChecking.setText(R.string.quote_hist_detail_total_price_checking);
        } else {
            textChecking.setText(R.string.order_quote_hist_detail_tip);
        }
        //--UDP NT-LWL 17/09/29 ExcludeFreight TO -

        //代引き手数料
        //cashOnDeliveryChargeIncludingTax
        if (mIsCodUser) {

            CharSequence cstr;
            if (response.cashOnDeliveryChargeIncludingTax == null) {
                cstr = getResourceString(R.string.label_hyphen);
            } else {
                cstr = Format.formatAmountWithUnit(response.cashOnDeliveryChargeIncludingTax, false, false, false, null);
            }

            setIncludeItemText(headerView.findViewById(R.id.codCharge), getResourceString(R.string.order_detail_cod_charge),
                    cstr, null);

//			//強制背景色設定
//			ViewBgUtil.requestLayout(headerView.findViewById(R.id.codCharge), R.id.viewDiv, R.id.textView1, R.id.viewRight);

        } else {

            headerView.findViewById(R.id.codCharge).setVisibility(View.GONE);
        }
    }


    private void setFooterViewData(View baseView, final ResponseGetOrderDetail response) {

        //TODO:キャンセルの詳細
        //商品変更または、キャンセル、返品の条件ご確認の上、ご希望の場合はミスミQCTセンターまでご連絡ください。
        TextView textView;
        textView = (TextView) baseView.findViewById(R.id.cancelInfo);
        textView.setText(getText(R.string.order_detail_cancel_info));
        if (AppConfig.getInstance().getUrlList() != null && AppConfig.getInstance().getUrlList().cancelOrderUrl != null) {
            final String strUrl = AppConfig.getInstance().getUrlList().cancelOrderUrl;
            if (android.text.TextUtils.isEmpty(strUrl)) {
                textView.setVisibility(View.GONE);
            } else {

                Pattern pattern = Pattern.compile((String) getText(R.string.order_detail_cancel_info_link));

                Linkify.TransformFilter filter = new Linkify.TransformFilter() {
                    @Override
                    public String transformUrl(Matcher match, String url) {
                        return strUrl;
                    }
                };

                Linkify.addLinks(textView, pattern, strUrl, null, filter);
            }
        } else {
            textView.setVisibility(View.GONE);
        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
        if (mNeedShowPayButton) {//显示底部付款按钮
            baseView.findViewById(R.id.buttonPay).setVisibility(View.VISIBLE);
            baseView.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoPay(response.orderSlipNo, "60");
                }
            });
        } else {
            baseView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
        }
        //-- ADD NT-LWL 16/11/13 AliPay Payment TO -
    }


    // リストビューのアイテムがクリックされた時
    OrderDetailAdapter.OnItemClickListener mOnItemClickListener = new OrderDetailAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view, int position, long id) {

//			showToast("通信してアイテム詳細画面に遷移："+ position);
//            doSpProduct(mResponseGet.mItemList.get(position));

            ResponseGetOrderDetail.ItemInfo itemInfo = (ResponseGetOrderDetail.ItemInfo) parent.getItem(position);

            doSpProduct(itemInfo);
        }

        public void onItemAddMy(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseGetOrderDetail.ItemInfo itemInfo = (ResponseGetOrderDetail.ItemInfo) parent.getItem(position);

            doAddMyPart(itemInfo);
        }

        public void onItemAddCart(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseGetOrderDetail.ItemInfo itemInfo = (ResponseGetOrderDetail.ItemInfo) parent.getItem(position);

            doAddToCart(itemInfo);
        }
    };

    private void doSpProduct(ResponseGetOrderDetail.ItemInfo itemInfo) {

        hideKeyboard();

        String completeType = "4";    //API仕様書より "4"固定
        String seriesCode = itemInfo.seriesCode;
        String innerCode = itemInfo.innerCode;
        String partNumber = itemInfo.partNumber;
        Integer quantity = itemInfo.quantity;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, CharSequence str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);

        if (android.text.TextUtils.isEmpty(str2)) {

            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
//		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
    }


    //
    private void doAddToCart(ResponseGetOrderDetail.ItemInfo itemInfo) {

        mAddToCartApi.setParameter(mResponseGet.orderSlipNo, itemInfo);
        mAddToCartApi.connect(getContext());
    }

    private class AddToCartApi extends ApiAccessWrapper {

        private String mOrderSlipNo;
        private ResponseGetOrderDetail.ItemInfo mInfo;

        @Override
        protected String getScreenId() {
            return OrderDetailFragment.this.getScreenId();
        }

        public void setParameter(String orderSlipNo, ResponseGetOrderDetail.ItemInfo info) {
            mOrderSlipNo = orderSlipNo;
            mInfo = info;
        }

        public HashMap<String, String> getParameter() {
            return ApiBuilder.createAddToCartFromOrder(mOrderSlipNo, mInfo);
        }

        public void onResult(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    AppNotifier.getInstance().addCartCount(1);
                    showSimpleMessageDialog(null, getResourceString(R.string.quote_hist_detail_dialog_added_cart), R.string.dialog_button_close);
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    //
    private void doAddMyPart(ResponseGetOrderDetail.ItemInfo itemInfo) {

        mAddMyPartApi.setParameter(mResponseGet.orderSlipNo, itemInfo);
        mAddMyPartApi.connect(getContext());
    }

    private class AddMyPartApi extends ApiAccessWrapper {

        private String mOrderSlipNo;
        private ResponseGetOrderDetail.ItemInfo mInfo;

        @Override
        protected String getScreenId() {
            return OrderDetailFragment.this.getScreenId();
        }

        public void setParameter(String orderSlipNo, ResponseGetOrderDetail.ItemInfo info) {
            mOrderSlipNo = orderSlipNo;
            mInfo = info;
        }


        public HashMap<String, String> getParameter() {

            return ApiBuilder.createAddToMyComponentsFromOrder(mOrderSlipNo, mInfo);
        }

        public void onResult(int responseCode, String result) {

            AddMyParts response = new AddMyParts();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }
            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    showSimpleMessageDialog(null, getResourceString(R.string.quote_hist_detail_dialog_added_my_parts), R.string.dialog_button_close);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    public String convertOrderType(String userName, String orderType) {
        String result;
        String name;
        String type;

        if (orderType == null) {
            if (userName == null) {
                result = getString(R.string.order_detail_type_default);
            } else {
                result = userName;
            }
        } else {
            name = userName;
            switch (orderType) {
                case "M1":
                case "P1":
                    type = "MAIL";
                    break;
                case "F1":
                case "F2":
                case "F3":
                case "F4":
                    type = "FAX";
                    break;
                case "T1":
                    type = "TEL";
                    break;
                case "D1":
                    type = "EDI";
                    break;
                case "V1":
                case "K1":
                    type = getString(R.string.order_detail_type_other);
                    break;
                case "E1":
                case "D3":
                    type = "WEB";
                    break;
                default:
                    type = "-";
                    break;
            }
            result = name + getText(R.string.order_detail_type_srash) + type;
        }
        return result;
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.OrderDetail;
    }


    //-- ADD NT-LWL 16/11/13 AliPay Payment FR -
    private void gotoPay(String orderSlipNo, String paymentType) {
        GoogleAnalytics.sendAction(mTracker, getSaicataId(), GoogleAnalytics.CATEGORY_ALIPAY, getResources().getString(R.string.order_list_go_to_pay));
        mMyOnlinePaymentApi.setParameter("2", orderSlipNo, paymentType);
        mMyOnlinePaymentApi.connect(getActivity());
    }


    private class MyOnlinePaymentApi extends ApiAccessWrapper {

        String mcallerPage;
        String morderSlipNo;
        String mpaymentType;

        @Override
        protected String getScreenId() {
            return OrderDetailFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        public void setParameter(String callerPage, String orderSlipNo, String paymentType) {

            mcallerPage = callerPage;
            morderSlipNo = orderSlipNo;
            mpaymentType = paymentType;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createGetOnlinePayment(mcallerPage, morderSlipNo, mpaymentType);
        }

        @Override
        public void onResult(int responseCode, String result) {
            AliPaymentInfo response = new AliPaymentInfo();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }
            //传入订单号
            response.orderSlipNo = morderSlipNo;
            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    getFragmentController().stackFragment(new OrderPayFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }
    //-- ADD NT-LWL 16/11/13 AliPay Payment TO -

}

