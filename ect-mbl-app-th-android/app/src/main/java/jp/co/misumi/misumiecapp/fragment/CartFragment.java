package jp.co.misumi.misumiecapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.adapter.CartDetailAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.api.OrderConfirmOrderBaseApi;
import jp.co.misumi.misumiecapp.api.QuoteConfirmQuoteBaseApi;
import jp.co.misumi.misumiecapp.api.ShareSaveApi;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromCart;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromCart;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrderFromCart;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotation;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotationFromCart;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.ShareDetail;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.ShareUtil;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.widget.CheckBoxEx;


/**
 * カート画面
 */
public class CartFragment extends BaseToConfirmFragment {


    //画面表示用
    private GetCart mResponse;

    private CartDetailAdapter mListAdapter;
    private ListView mListView;
    private View mHeaderView;
    private View mFooterView;
    private TextView mSelectCount;
    private TextView mTotalPrice;
    private TextView mTotalPriceFooter;

    //API
    private RefreshApi mRefreshApi;
    private RefreshApi mRefresh2Api;

    private AddMyPartApi mAddMyPartApi;
    private UpdateApi mUpdateApi;
    private DeleteApi mDeleteApi;
    private EstimateApi mEstimateApi;
    private OrderApi mOrderApi;
    private GetSpProductApi mGetSpProductApi;

    //--ADD NT-LWL 17/05/19 Share FR -
    private View mShareTop;  //上部分享按钮
    private View mShareBottom;//下部分享按钮
    private View.OnClickListener mShareClickListener; //分享点击事件监听
    // 分享保存数据API
    private ShareSaveApi mShareSaveApi = new ShareSaveApi() {
        @Override
        protected String getScreenId() {
            return getSaicataId();
        }
    };
    //--ADD NT-LWL 17/05/19 Share TO -


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("GetCart", mResponse);
    }


    public CartFragment() {

        super();

        //MISUMI_MOBILE_APP-640
        //価格チェック時、カート削除後は前回の状態を維持
        //MISUMI_MOBILE_APP-607
        //My部品表追加後にカートリフレッシュを行う
        mRefreshApi = new RefreshApi() {
            @Override
            protected void onSuccess(GetCart response) {

                //チェック状態を引き継ぐ
                List<GetCart.Product> itemList = mResponse.mProductList;
                List<GetCart.Product> newItemList = response.mProductList;

                for (GetCart.Product itemInfo : itemList) {

                    if (itemInfo.cartId == null) {
                        continue;
                    }

                    if (itemInfo.checked) {

                        for (GetCart.Product newItemInfo : newItemList) {

                            if (itemInfo.cartId.equals(newItemInfo.cartId)) {
                                newItemInfo.checked = true;
                                break;
                            }
                        }
                    }
                }

                //スクロール位置を覚えておく
                int position;
                int yOffset = 0;
                int childCount = newItemList.size()
                        + mListView.getFooterViewsCount()
                        + mListView.getHeaderViewsCount();

                position = mListView.getFirstVisiblePosition();
                if (position != AdapterView.INVALID_POSITION) {

                    if (position < childCount) {

                        yOffset = mListView.getChildAt(0).getTop();
                    } else {

                        position = 0;
                        yOffset = 0;
                    }
                }

                mResponse = response;
                makeDataView(getView());

                if (position != AdapterView.INVALID_POSITION) {
                    mListView.setSelectionFromTop(position, yOffset);
                }

//				//スクロール位置を先頭に戻す
//				mListView.setSelection(0);
            }
        };

        //リフレッシュ後に全チェックの動作仕様
        mRefresh2Api = new RefreshApi() {
            @Override
            protected void onSuccess(GetCart response) {

                mResponse = response;

                //MISUMI_MOBILE_APP-555
                //全チェックの動作仕様
                for (GetCart.Product itemInfo : mResponse.mProductList) {
                    itemInfo.checked = true;
                }

                makeDataView(getView());

                //スクロール位置を先頭に戻す
                mListView.setSelection(0);
            }
        };

//		mAddToCartApi = new AddToCartApi();
        mAddMyPartApi = new AddMyPartApi();
        mUpdateApi = new UpdateApi();
        mDeleteApi = new DeleteApi();

        mEstimateApi = new EstimateApi();
        mOrderApi = new OrderApi();
        mGetSpProductApi = new GetSpProductApi() {

            @Override
            protected String getScreenId() {
                //画面IDを返す
                return CartFragment.this.getScreenId();
            }

            protected void onSuccess(ResponseGetSpProduct response) {

                //画面遷移する
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

        };
    }


    //見積
    public class QuoteConfirmQuoteTypeApi extends QuoteConfirmQuoteBaseApi {

        private int mOpenType;
        private int mRequestCode;
        private ResponseCheckQuotation mResponse;

        protected void setParameter2(final int openType, final int requestCode, final ResponseCheckQuotation response) {
            mOpenType = openType;
            mRequestCode = requestCode;
            mResponse = response;
        }

        @Override
        protected void onSuccess(ResponseCheckQuotation response) {

            doCart(mOpenType, mRequestCode, response);
        }

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

    }


    //注文
    public class OrderConfirmOrderTypeApi extends OrderConfirmOrderBaseApi {

        private int mOpenType;
        private int mRequestCode;
        private ResponseCheckOrder mResponse;

        protected void setParameter2(final int openType, final int requestCode, final ResponseCheckOrder response) {
            mOpenType = openType;
            mRequestCode = requestCode;
            mResponse = response;
        }

        @Override
        protected void onSuccess(ResponseCheckOrder response) {

            doCheckStoke(mOpenType, mRequestCode, mResponse, response);
        }

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

            mResponse = (GetCart) savedInstanceState.getSerializable("GetCart");

        } else {

            if (mResponse == null) {
                mResponse = (GetCart) getParameter();

                //画面遷移時の初期状態は全てにチェック
                List<GetCart.Product> itemList = mResponse.mProductList;
                for (GetCart.Product itemInfo : itemList) {
                    itemInfo.checked = true;
                }
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateLayout(inflater, R.layout.fragment_cart, container, false);

        makeView(rootView);
        //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
        if (AppConfig.getInstance().hasSessionId()) {
            AppNotifier.getInstance().setCartCount(AppConfig.getInstance().getCartCount());
        }
        //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
        makeDataView(rootView);
        //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
        refreshCart();
        //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
        return rootView;
    }

    //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
    private void refreshCart() {
        MainActivity mainActivity = (MainActivity) mParent;
        if (mainActivity.isRefreshCart) {
            mRefresh2Api.connect(getContext());
//            mainActivity.isRefreshCart=false;
        }
    }
    //-- ADD NT-LWL 16/11/17 AliPay Payment TO -

    //
    protected void makeView(View rootView) {

        final LayoutInflater inflater = mParent.getLayoutInflater();

        mSelectCount = (TextView) rootView.findViewById(R.id.textSelectCount);
//        ((jp.co.misumi.misumiecapp.widget.AutoFitTextView)mSelectCount).setSpannableFlag();
        mTotalPrice = (TextView) rootView.findViewById(R.id.textTotalPrice);

        //
        mListView = (ListView) rootView.findViewById(R.id.listView);

        //ヘッダー情報

        //通常共通ヘッダとフッタ
        mHeaderView = inflateLayout(inflater, R.layout.list_item_cart_detail_header, mListView, false);

        mFooterView = inflateLayout(inflater, R.layout.list_item_cart_detail_footer, mListView, false);

        //--ADD NT-LWL 17/05/19 Share FR -
        if (!SubsidiaryCode.isJapan()) {
            // 分享按钮初始化
            mShareTop = mHeaderView.findViewById(R.id.share_cart);
            mShareBottom = mFooterView.findViewById(R.id.share_cart);
            // 设置按钮可见
            mShareTop.setVisibility(View.VISIBLE);
            mShareBottom.setVisibility(View.VISIBLE);
            // 分享按点击监听
            mShareClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 获取已选中的商品列表
                    List<GetCart.Product> checkedList = getCheckedItemList();
                    // 是否数量有变化
                    boolean quantityIsChange = false;
                    // 分享商品列表
                    final List<ShareDetail> cartList = new ArrayList<>();
                    if (!checkedList.isEmpty()) {
                        for (GetCart.Product product : checkedList) {
                            // 数量有变化 但未询价
                            if (!product.editedQuantity.equals(product.quantity.toString())) {
                                quantityIsChange = true;
                            }

                            ShareDetail shareDetail = new ShareDetail();
                            // 商品图片
                            shareDetail.setImageUrl(product.productImageUrl);
                            // 商品名称
                            shareDetail.setSeriesName(product.productName);
                            // 品牌名称
                            shareDetail.setBrandName(product.brandName);
                            // 品牌code
                            shareDetail.setBrandCode(product.brandCode);
                            // 型番
                            shareDetail.setPcode(product.partNumber);
                            // 商品code
                            shareDetail.setScode(product.seriesCode);
                            // 数量
                            shareDetail.setNumber(product.quantity + "");
                            // 发货日
                            shareDetail.setDaysToShip(MsmFormat.convertShip(mParent, product.daysToShip, product.shipType));
                            // 总价
                            String totalPrice = product.totalPrice == null ? getString(R.string.text_empty) : Format.formatAmount(product.totalPrice);
                            shareDetail.setTotalPrice(totalPrice);
                            // 含税总价
                            String totalPriceIncludingTax = product.totalPriceWithTax == null ? getString(R.string.text_empty) : Format.formatAmount(product.totalPriceWithTax);
                            shareDetail.setTotalPriceIncludingTax(totalPriceIncludingTax);
                            // 加入列表
                            cartList.add(shareDetail);
                        }
                        // 设置参数
                        mShareSaveApi.setCartList(cartList);

                        if (quantityIsChange) {
                            // 弹出数量不一致提示
                            new MessageDialog(mParent, new MessageDialog.MessageDialogListener() {
                                @Override
                                public void onDialogResult(Dialog dlg, View view, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        // 选择分享平台
                                        ShareUtil.show(mParent, cartList, getSaicataId(), mShareSaveApi);
                                    }
                                }
                            }).show(R.string.share_number_tip, R.string.share_continue, R.string.cancel);
                        } else {
                            // 选择分享平台
                            ShareUtil.show(mParent, cartList, getSaicataId(), mShareSaveApi);
                        }

                    }
                }
            };
            // 设置分享按钮 点击事件
            mShareBottom.setOnClickListener(mShareClickListener);
            mShareTop.setOnClickListener(mShareClickListener);
        }
        //--ADD NT-LWL 17/05/19 Share TO -

        mTotalPriceFooter = (TextView) mFooterView.findViewById(R.id.textTotalPrice);

        mListView.addHeaderView(mHeaderView, mResponse, false);
        mListView.addFooterView(mFooterView, mResponse, false);

        //スクロール
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


    protected void makeDataView(View rootView) {

        List<GetCart.Product> itemList = mResponse.mProductList;
        int itemCount = itemList.size();

        //カートの数量バッチを上書き
        AppNotifier.getInstance().setCartCount(itemCount);

        //通常共通ヘッダとフッタ
        View emptyList = rootView.findViewById(R.id.emptyList);
        if (itemCount == 0) {
            int dp24 = 24;
            int dp6 = 6;
            int px24 = dp24 * AppConfig.getInstance().dp;
            int px6 = dp6 * AppConfig.getInstance().dp;

            //空の時
            rootView.findViewById(R.id.layoutHeader).setVisibility(View.GONE);
            rootView.findViewById(R.id.listView).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.viewVisible).setVisibility(View.GONE);
            mFooterView.findViewById(R.id.viewVisible).setVisibility(View.GONE);

            emptyList.setPadding(0, px6, 0, px6);

            View line = emptyList.findViewById(R.id.divider);
            line.setVisibility(View.GONE);


            ImageView imageView = (ImageView) emptyList.findViewById(R.id.iconNotFound);
            TextView textView = (TextView) emptyList.findViewById(R.id.textMessage);
            imageView.setBackgroundResource(R.drawable.icon_cart2);
            //imageView.setBackground(getResources().getDrawable(R.drawable.icon_cart2));
            imageView.getLayoutParams().height = px24;
            textView.setText(getResourceString(R.string.cart_empty_str));
            emptyList.setVisibility(View.VISIBLE);

        } else {

            //中身が有る時
//			rootView.findViewById(R.id.layoutHeader).setVisibility(View.VISIBLE);

            mHeaderView.findViewById(R.id.viewVisible).setVisibility(View.VISIBLE);
            mFooterView.findViewById(R.id.viewVisible).setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);

            setHeaderViewData(mHeaderView, mResponse);
            setHeaderViewData(mFooterView, mResponse);
            setViewClickListener(mHeaderView, mResponse);
            setViewClickListener(mFooterView, mResponse);
        }


        //
        mListAdapter = new CartDetailAdapter(getContext(), R.layout.list_item_cart_detail_item, itemList, mOnItemClickListener);
        mListView.setAdapter(mListAdapter);

    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.Cart;
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mRefreshApi.close();
        mRefresh2Api.close();

        mAddMyPartApi.close();
        mUpdateApi.close();
        mDeleteApi.close();
        mEstimateApi.close();
        mOrderApi.close();
        mGetSpProductApi.close();
        //--ADD NT-LWL 17/05/19 Share FR -
        mShareSaveApi.close();
        //--ADD NT-LWL 17/05/19 Share TO -

        super.onPause();
    }


    //
    private void setHeaderViewData(View baseView, final GetCart response) {

        List<GetCart.Product> itemList = response.mProductList;

        TextView tv;

        double totalPriceWithTax = 0.0;
        int campaignEndCount = 0;
        for (GetCart.Product info : itemList) {

            if (info.totalPriceWithTax != null) {
                totalPriceWithTax += info.totalPriceWithTax;
            }

            if (info.campaignEndFlag != null && info.campaignEndFlag.equals("1")) {
                ++campaignEndCount;
            }
        }

        if (campaignEndCount == 0) {

            baseView.findViewById(R.id.viewCampaign).setVisibility(View.GONE);
        } else {

            baseView.findViewById(R.id.viewCampaign).setVisibility(View.VISIBLE);
            View viewCampaign = baseView.findViewById(R.id.viewCampaign1);
            tv = (TextView) viewCampaign.findViewById(R.id.textMessage);
            String add = Format.formatCount(campaignEndCount);
            String str = String.format(getResourceString(R.string.cart_app_campaign_1), add);
            tv.setText(str);

            viewCampaign = baseView.findViewById(R.id.viewCampaign2);
            tv = (TextView) viewCampaign.findViewById(R.id.textMessage);
            str = getResourceString(R.string.cart_app_campaign_2);
            tv.setText(str);
        }


        //選択チェックボックス
        boolean allChecked = false;    //全部チェックされているか
        boolean oneChecked = false;    //チェックが有るか

        //項目が無い場合
        if (itemList.size() == 0) {

        } else {

            allChecked = true;    //全部チェックされているか

            for (GetCart.Product itemInfo : itemList) {

                if (itemInfo.checked) {
                    oneChecked = true;    //チェックが有るか
                } else {
                    allChecked = false;    //全部チェックされているか
                }
            }
        }

        //
        updateHeaderState(baseView, allChecked, oneChecked);
        updateHeaderState(mFooterView, allChecked, oneChecked);

        //
        updateSelectedInfo();
    }


    private void updateHeaderState(final View baseView, boolean allChecked, boolean oneChecked) {

        CheckBoxEx cb = (CheckBoxEx) baseView.findViewById(R.id.checkAll);
        cb.setOnCheckedChangeListener(null);
        cb.setCheckedEx(allChecked);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//				showToast("onCheckedChanged: "+ isChecked);

                List<GetCart.Product> itemList = mResponse.mProductList;

                //項目が無い場合
                if (itemList.size() == 0) {

                    isChecked = false;
                } else {

                    for (GetCart.Product itemInfo : itemList) {

                        itemInfo.checked = isChecked;
                    }
                }

                updateHeaderState(mHeaderView, isChecked, isChecked);
                updateHeaderState(mFooterView, isChecked, isChecked);

                //
                updateSelectedInfo();

                mListAdapter.notifyDataSetChanged();
            }
        });


        //ボタンの有効無効を更新する
        //カートへ追加

        //My部品表へ追加
        baseView.findViewById(R.id.buttonQuote).setEnabled(oneChecked);

        //注文へ進む
        baseView.findViewById(R.id.buttonOrder).setEnabled(oneChecked);

        //--ADD NT-LWL 17/05/19 Share FR -
        // 分享按钮 中国环境
        if (!SubsidiaryCode.isJapan()) {
            ViewGroup view = (ViewGroup) baseView.findViewById(R.id.share_cart);
            view.setEnabled(oneChecked);
            int n = view.getChildCount();
            // 设置子view的点击状态
            for (int i = 0; i < n; i++) {
                view.getChildAt(i).setEnabled(oneChecked);
            }
        }
        //--ADD NT-LWL 17/05/19 Share TO -
    }


    private void updateSelectedInfo() {

        List<GetCart.Product> checkedList = getCheckedItemList();

        int itemCount = checkedList.size();

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String str = getResourceString(R.string.cart_selected_item);
        SpannableString ss = new SpannableString(str);
        ssb.append(ss);

        str = Format.formatCount(itemCount);
        ss = SpannableUtil.newSpannableString(str, 14, true, true);
        ssb.append(ss);

        str = getResourceString(R.string.cart_selected_item_unit);
        ss = new SpannableString(str);
        ssb.append(ss);

        //全角空白
        ssb.append("　");

//		mSelectCount.setText(ssb);


        //日中
        //
        double totalPrice = 0.0;
        int cart_total_price = R.string.cart_total_price;
        int cart_total_price_unit = R.string.cart_total_price_unit;

        if (SubsidiaryCode.isJapan()) {

            //日本外税
            for (GetCart.Product info : checkedList) {

                if (info.totalPrice != null) {
                    totalPrice += info.totalPrice;
                }
            }

        } else {

            cart_total_price = R.string.cart_total_price_wtax;
            if (AppConfig.getInstance().isDollar()) {
                cart_total_price_unit = R.string.cart_total_price_unit_wtax_dollar;
            } else {
                cart_total_price_unit = R.string.cart_total_price_unit_wtax;
            }

            //中国税込み
            for (GetCart.Product info : checkedList) {

                if (info.totalPriceWithTax != null) {
                    totalPrice += info.totalPriceWithTax;
                }
            }

        }


//		ssb = new SpannableStringBuilder();
        str = getResourceString(cart_total_price);
        ss = new SpannableString(str);
        ssb.append(ss);

        if (AppConfig.getInstance().isDollar()) {
            // dollar
            str = getResourceString(R.string.cart_total_price_unit_dollar);
            ss = new SpannableString(str);
            ssb.append(ss);
        }

        str = Format.formatAmount(totalPrice);
        ss = SpannableUtil.newSpannableString(str, 14, true, true);
        ssb.append(ss);

        str = getResourceString(cart_total_price_unit);
        ss = new SpannableString(str);
        ssb.append(ss);

        mSelectCount.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 12);
        mSelectCount.setText(ssb);


        //
        ssb = new SpannableStringBuilder();
        if (AppConfig.getInstance().isDollar()) {
            str = getResourceString(cart_total_price) + getResourceString(R.string.cart_total_price_unit_dollar);
        } else {
            str = getResourceString(cart_total_price);
        }
        ss = new SpannableString(str);
        ssb.append(ss);


        str = Format.formatAmount(totalPrice);
        ss = SpannableUtil.newSpannableString(str, 15, true, true);
        ssb.append(ss);

        str = getResourceString(cart_total_price_unit);
        ss = new SpannableString(str);
        ssb.append(ss);

//		mTotalPrice.setText(ssb);
        mTotalPriceFooter.setText(ssb);

    }


    private void setViewClickListener(View baseView, final GetCart response) {

        View vw;

        //カートへ追加

        //見積りへ進む
        vw = baseView.findViewById(R.id.buttonQuote);
        if (vw != null) {
            vw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doEstimateConfirm();
                    //-- ADD NT-LWL 17/08/04 GA追加 FR -
                    GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_CART, GoogleAnalytics.lable_cart_qt);
                    //-- ADD NT-LWL 17/08/04 GA追加 TO -
                }
            });
        }

        //注文へ進む
        vw = baseView.findViewById(R.id.buttonOrder);
        if (vw != null) {
            vw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doOrderConfirm();
                    //-- ADD NT-LWL 17/08/04 GA追加 FR -
                    GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_CART, GoogleAnalytics.lable_cart_so);
                    //-- ADD NT-LWL 17/08/04 GA追加 TO -
                }
            });
        }

    }


    // リストビューのアイテムがクリックされた時
    CartDetailAdapter.OnItemClickListener mOnItemClickListener = new CartDetailAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            //通信してアイテム詳細画面に遷移
            doSpProduct(itemInfo);
        }

        public void onItemCheck(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            //選択チェックボックス
            boolean allChecked = false;    //全部チェックされているか
            boolean oneChecked = false;    //チェックが有るか

            //項目が無い場合
            int itemCount = parent.getCount();

            if (itemCount == 0) {

            } else {

                allChecked = true;    //全部チェックされているか

                for (int idx = 0; idx < itemCount; ++idx) {

                    itemInfo = (GetCart.Product) parent.getItem(idx);

                    if (itemInfo.checked) {
                        oneChecked = true;    //チェックが有るか
                    } else {
                        allChecked = false;    //全部チェックされているか
                    }
                }
            }

            updateHeaderState(mHeaderView, allChecked, oneChecked);
            updateHeaderState(mFooterView, allChecked, oneChecked);

            //
            updateSelectedInfo();

        }

        public void onItemUpdate(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            doUpdateMyComponents(itemInfo);
        }

        public void onItemDelete(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            doDelete(itemInfo);
        }

        public void onItemAddMy(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            //My部品追加
            doAddMyPart(itemInfo);
        }

        //数量入力
        public void onItemEdit(ArrayAdapter<?> parent, View view, int position, long id) {

            GetCart.Product itemInfo = (GetCart.Product) parent.getItem(position);

            doInputQty(itemInfo);
        }
    };


    //
    private void doInputQty(final GetCart.Product itemInfo) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_edit_layout, null, false);
        final EditText editQuantity = (EditText) view.findViewById(R.id.editQuantity);
        editQuantity.setText(itemInfo.editedQuantity);

        //数量入力ダイアログの表示処理
        MessageDialog messageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String qty = editQuantity.getText().toString();
                    if (android.text.TextUtils.isEmpty(qty)) {
                        qty = "1";    //入力無しは数量１
                    }
                    itemInfo.editedQuantity = qty;
                    mListAdapter.notifyDataSetChanged();
                }
            }
        });


        //全部（閉じるダイアログを出す）
        messageDialog.showT(getResourceString(R.string.message_input_qty), view, R.string.dialog_button_kettei, R.string.dialog_button_cancel);


        editQuantity.post(new Runnable() {

            @Override
            public void run() {

                editQuantity.setSelection(editQuantity.getText().toString().length());

                // ソフトキーボードを表示する
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                //			imm.showSoftInput(mEditQuantity, InputMethodManager.SHOW_FORCED);
                //			imm.showSoftInput(mEditQuantity, InputMethodManager.RESULT_SHOWN);
                imm.showSoftInput(editQuantity, InputMethodManager.SHOW_IMPLICIT);
            }
        });

    }


    /**
     * 自分画面更新用
     */
    private class RefreshApi extends ApiAccessWrapper {

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCart();
        }

        @Override
        public void onResult(int responseCode, String result) {

            GetCart response = new GetCart();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    onSuccess(response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        protected void onSuccess(GetCart response) {
        }

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }


    /**
     * 数量更新
     */
    private void doUpdateMyComponents(GetCart.Product itemInfo) {

        hideKeyboard();

        //数量の入力値は保障されている
        mUpdateApi.setParameter(itemInfo);
        mUpdateApi.connect(getContext());
    }


    private class UpdateApi extends ApiAccessWrapper {

        GetCart.Product mItemInfo;

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        public void setParameter(GetCart.Product itemInfo) {

            mItemInfo = itemInfo;
        }

        @Override
        public HashMap<String, String> getParameter() {

            int qty = getQuantity(mItemInfo);

            Map<String, Integer> mapData = new HashMap<>();
            mapData.put(mItemInfo.cartId, qty);

            return ApiBuilder.createUpdateCart(mapData);
        }

        @Override
        public void onResult(int responseCode, String result) {

            AddToCart cart = new AddToCart();
            if (!cart.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //一覧を更新する
                    mRefreshApi.connect(getContext());
                    break;

                default:

                    showErrorMessage(cart.errorList);
                    break;
            }
        }
    }


    private List<GetCart.Product> getCheckedItemList() {

        List<GetCart.Product> checkedList = new ArrayList<>();

        List<GetCart.Product> itemList = mResponse.mProductList;
        for (GetCart.Product itemInfo : itemList) {

            if (itemInfo.checked) {
                checkedList.add(itemInfo);
            }
        }

        return checkedList;
    }


    /**
     * カートから削除
     */
    private void doDelete(final GetCart.Product itemInfo) {

        hideKeyboard();

        final MessageDialog messageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {

                    doDeleteMyComponents(itemInfo);
                }
            }
        });

        messageDialog.show(getResourceString(R.string.cart_dialog_delete_cart), getResourceString(R.string.cart_dialog_delete_cart_msg), R.string.dialog_button_delete, R.string.dialog_button_cancel);
    }


    private void doDeleteMyComponents(GetCart.Product itemInfo) {

        hideKeyboard();

        mDeleteApi.setParameter(itemInfo);
        mDeleteApi.connect(getContext());
    }


    private class DeleteApi extends ApiAccessWrapper {

        GetCart.Product mItemInfo;

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        public void setParameter(GetCart.Product itemInfo) {

            mItemInfo = itemInfo;
        }

        @Override
        public HashMap<String, String> getParameter() {

            List<String> list = new ArrayList<>();
            list.add(mItemInfo.cartId);

            return ApiBuilder.createDeleteCart(list);
        }

        @Override
        public void onResult(int responseCode, String result) {

            AddToCart cart = new AddToCart();
            if (!cart.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //一覧を更新する
                    mRefreshApi.connect(getContext());
                    break;

                default:

                    showErrorMessage(cart.errorList);
                    break;
            }
        }
    }


    //注文確認
    private void doOrderConfirm() {

        hideKeyboard();


        List<GetCart.Product> checkedList = getCheckedItemList();

        //
        RequestCheckOrderFromCart request = new RequestCheckOrderFromCart();

        for (GetCart.Product checkedItem : checkedList) {
            RequestCheckOrderFromCart.ItemInfo item = new RequestCheckOrderFromCart.ItemInfo();

            item.cartId = checkedItem.cartId;
            item.quantity = getQuantity(checkedItem);

            request.mItemList.add(item);
        }

        mOrderApi.setParameter(request);
        mOrderApi.connect(getContext());
    }


    //見積確認
    private void doEstimateConfirm() {

        hideKeyboard();


        List<GetCart.Product> checkedList = getCheckedItemList();

        //
        RequestCheckQuotationFromCart request = new RequestCheckQuotationFromCart();

        for (GetCart.Product checkedItem : checkedList) {
            RequestCheckQuotationFromCart.ItemInfo item = new RequestCheckQuotationFromCart.ItemInfo();

            item.cartId = checkedItem.cartId;
            item.quantity = getQuantity(checkedItem);

            request.mItemList.add(item);
        }

        mEstimateApi.setParameter(request);
        mEstimateApi.connect(getContext());
    }


    private int getQuantity(GetCart.Product item) {

        int qty = 1;
        String str = item.editedQuantity;
        if (str != null && !str.isEmpty() && !(str.equals("0"))) {
            try {
                qty = Integer.parseInt(str);
            } catch (Exception ignored) {
            }
        }

        return qty;
    }


    //
    @Override
    protected void doCart(int openType, int requestCode, DataContainer dataContainer) {
        SubActivity.launchActivity(this, openType, requestCode, dataContainer);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //カート画面から戻って来た時の処理
        if (requestCode == SubActivity.REQUEST_CODE_CART) {

            if (resultCode == Activity.RESULT_OK) {

                //カート画面処理が完了した時の処理
                //一覧を更新する
                mRefresh2Api.connect(getContext());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //見積確認
    private class EstimateApi extends ApiAccessWrapper {

        RequestCheckQuotationFromCart mRequest;

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        public void setParameter(RequestCheckQuotationFromCart request) {
            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createCheckQuotationFromCart(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseCheckQuotationFromCart response = new ResponseCheckQuotationFromCart();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    doCartOrderType(SubActivity.SUB_TYPE_ESTIMATE, SubActivity.REQUEST_CODE_CART, response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }

    //注文確認
    private class OrderApi extends ApiAccessWrapper {

        RequestCheckOrderFromCart mRequest;

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        public void setParameter(RequestCheckOrderFromCart request) {
            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createCheckOrderFromCart(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseCheckOrderFromCart response = new ResponseCheckOrderFromCart();
            boolean pars = response.setData(result);

            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    doCartOrderType(SubActivity.SUB_TYPE_ORDER, SubActivity.REQUEST_CODE_CART, response);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    //
    private void doAddMyPart(GetCart.Product itemInfo) {

        hideKeyboard();

        mAddMyPartApi.connect(getContext(), itemInfo);
    }

    private class AddMyPartApi extends ApiAccessWrapper {

        private GetCart.Product mItem;

        @Override
        protected String getScreenId() {
            return CartFragment.this.getScreenId();
        }

        public HashMap<String, String> getParameter() {

            //その時の入力状態の数値を使う様に仕様変更
            //※空欄や０の時は数量１とみなす
            int qty = getQuantity(mItem);

            return ApiBuilder.createAddToMyComponentsFromCart(mItem.cartId, qty);
        }

        public void connect(Context context, GetCart.Product itemInfo) {
            mItem = itemInfo;
            super.connect(context);
        }

        public void onResult(int responseCode, String result) {

            AddToCart cart = new AddToCart();
            if (!cart.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            //MISUMI_MOBILE_APP-607
                            //My部品表追加後にカートリフレッシュを行う
                            mRefreshApi.connect(CartFragment.this.getContext());
                        }
                    }).show(getResourceString(R.string.cart_dialog_add_my),
                            getResourceString(R.string.my_parts_dialog_moved_my_parts), 0, R.string.dialog_button_close);
                    break;

                default:

                    showErrorMessage(cart.errorList);
                    break;
            }
        }
    }


    private void doSpProduct(GetCart.Product itemInfo) {

        hideKeyboard();

        String completeType = "4";    //API仕様書より "4"固定
        String seriesCode = itemInfo.seriesCode;
        String innerCode = itemInfo.innerCode;
        String partNumber = itemInfo.partNumber;
        Integer quantity = itemInfo.quantity;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


/*
    protected void setTextEmptyGone(TextView tv, String str) {
		if (str == null || str.isEmpty()) {
			tv.setText("");
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}
*/


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
//		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
    }


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, boolean doHidden) {


        if (android.text.TextUtils.isEmpty(str2)) {

            if (doHidden) {
                subView.setVisibility(View.GONE);
                return;
            }

            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        subView.setVisibility(View.VISIBLE);

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.Cart;
    }
}
