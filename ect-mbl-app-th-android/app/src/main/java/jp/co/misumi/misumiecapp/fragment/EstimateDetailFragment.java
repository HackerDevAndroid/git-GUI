package jp.co.misumi.misumiecapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.adapter.QuotationDetailAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AddMyParts;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromQuote;
import jp.co.misumi.misumiecapp.data.RequestGetQuotation;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrderFromQuote;
import jp.co.misumi.misumiecapp.data.ResponseGetQuotation;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * 見積履歴詳細画面
 */
public class EstimateDetailFragment extends BaseToConfirmFragment {

    private ResponseGetQuotation mResponseGet;
    private QuotationDetailAdapter mListAdapter;
    private ListView mListView;
    private View mHeaderView;
    private View mFooterView;


    //API
    private RefreshApi mRefreshApi;
    private AddToCartApi mAddToCartApi;
    private AddMyPartApi mAddMyPartApi;
    private OrderApi mOrderApi;
    private GetSpProductApi mGetSpProductApi;

    private final boolean mIsIncludeTax;
    private boolean allStatusNg;

    //--ADD NT-LWL 17/06/19 Share FR -
    /*private View mShareTop;  //上部分享按钮
	private View mShareBottom;//下部分享按钮
	private View.OnClickListener mShareClickListener; //分享点击事件监听
	// 分享保存数据Api
	private ShareSaveApi mShareSaveApi = new ShareSaveApi() {
		@Override
		protected String getScreenId() {
			return getSaicataId();
		}
	};*/
    //--ADD NT-LWL 17/06/19 Share TO -


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("ResponseGet", mResponseGet);
    }


    public EstimateDetailFragment() {

        mRefreshApi = new RefreshApi() {
            @Override
            protected void onSuccess(ResponseGetQuotation response) {

                mResponseGet = response;
                makeDataView(getView());

                //スクロール位置を先頭に戻す
                mListView.setSelection(0);
            }
        };

        mAddToCartApi = new AddToCartApi();
        mAddMyPartApi = new AddMyPartApi();
        mOrderApi = new OrderApi();
        mGetSpProductApi = new GetSpProductApi() {
            @Override
            protected void onSuccess(ResponseGetSpProduct response) {
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

            @Override
            protected String getScreenId() {
                return EstimateDetailFragment.this.getScreenId();
            }
        };

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
    }


//	public class OrderConfirmOrderTypeApi extends OrderConfirmOrderBaseApi {
//
//		private int mOpenType;
//		private int mRequestCode;
//		private ResponseCheckOrder mResponse;
//
//		protected void setParameter2(final int openType, final int requestCode, final ResponseCheckOrder response) {
//			mOpenType = openType;
//			mRequestCode = requestCode;
//			mResponse = response;
//		}
//
//        @Override
//        protected void onSuccess(ResponseCheckOrder response){
//
//			doCheckStoke(mOpenType, mRequestCode, mResponse, response);
//        }
//
//	    @Override
//	    protected String getScreenId(){
//	        return EstimateDetailFragment.this.getScreenId();
//	    }
//
//	}


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

            mResponseGet = (ResponseGetQuotation) savedInstanceState.getSerializable("ResponseGet");

        } else {

            if (mResponseGet == null) {
                mResponseGet = (ResponseGetQuotation) getParameter();


                //画面遷移時の初期状態は全てにチェック
                ArrayList<ResponseGetQuotation.ItemInfo> itemList = mResponseGet.mItemList;
                for (ResponseGetQuotation.ItemInfo itemInfo : itemList) {
                    itemInfo.checked = true;
                }
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflateLayout(inflater, R.layout.fragment_estimate_detail, container, false);

        makeView(rootView);

        makeDataView(rootView);

        return rootView;
    }


    //
    protected void makeView(View rootView) {

        final LayoutInflater inflater = mParent.getLayoutInflater();

        //
        mListView = (ListView) rootView.findViewById(R.id.listView);

        //ヘッダー情報
        mHeaderView = inflateLayout(inflater, R.layout.list_item_estimate_detail_header, mListView, false);
        mFooterView = inflateLayout(inflater, R.layout.list_item_estimate_detail_footer, mListView, false);
        //--ADD NT-LWL 17/06/19 Share FR -
		/*if (SubsidiaryCode.isChinese()){
			// 分享按钮初始化
			mShareTop = mHeaderView.findViewById(R.id.share_estimate);
			mShareBottom = mFooterView.findViewById(R.id.share_estimate);
			mShareTop.setVisibility(View.VISIBLE);
			mShareBottom.setVisibility(View.VISIBLE);
			// 分享按点击监听
			mShareClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 报价分享数据
                    QTShareData qtShareData = new QTShareData();
                    // 报价单号
                    qtShareData.setQuotationSlipNo(mResponseGet.quotationSlipNo);
                    // 报价时间
                    qtShareData.setQuotationDateTime(mResponseGet.quotationDateTime);
                    // 报价有效期
                    qtShareData.setQuotationExpireDateTime(mResponseGet.quotationExpireDateTime);
                    // 订购人
                    qtShareData.setUserName(mResponseGet.userName);
					// 商品件数
                    qtShareData.setItemCount(mResponseGet.mItemList.size()+"");
                    // 总价
                    String totalPrice = mResponseGet.totalPrice == null ? getString(R.string.text_empty) : Format.formatAmount(mResponseGet.totalPrice);
                    qtShareData.setTotalPrice(totalPrice);
                    // 含税总价
                    String totalPriceIncludingTax = mResponseGet.totalPriceIncludingTax == null ? getString(R.string.text_empty) : Format.formatAmount(mResponseGet.totalPriceIncludingTax);
                    qtShareData.setTotalPriceIncludingTax(totalPriceIncludingTax);

					// 遍历 添加商品列表
                    for (ResponseGetQuotation.ItemInfo info : mResponseGet.mItemList){
                        ShareDetail shareDetail = new ShareDetail();
						// 商品图片
                        shareDetail.setImageUrl(info.productImageUrl);
						// 商品名称
                        shareDetail.setSeriesName(info.productName);
						// 品牌名称
                        shareDetail.setBrandName(info.brandName);
						// 型号
                        shareDetail.setPcode(info.partNumber);
						// 数量
                        shareDetail.setNumber(info.quantity+"");
                        // 发货日
                        shareDetail.setDaysToShip(MsmFormat.convertShip(mParent, info.daysToShip, info.shipType,true));
                        // 总价
                        String totalPrice1 = info.totalPrice == null ? getString(R.string.text_empty) : Format.formatAmount(info.totalPrice);
                        shareDetail.setTotalPrice(totalPrice1);
                        // 含税总价
                        String totalPriceIncludingTax1 = info.totalPriceIncludingTax == null ? getString(R.string.text_empty) : Format.formatAmount(info.totalPriceIncludingTax);
                        shareDetail.setTotalPriceIncludingTax(totalPriceIncludingTax1);
                        // 加入报价分享列表
                        qtShareData.getQtList().add(shareDetail);
                    }
                    // 设置数据
                    mShareSaveApi.setQtShareData(qtShareData);
					// 选择分享平台
					ShareUtil.show(mParent, qtShareData, getSaicataId(), mShareSaveApi);
					// 获取已选择的条目
//					List<ResponseGetQuotation.ItemInfo> checkedList = getCheckedItemList();
//						// 设置分享数据
//						ShareData shareData = new ShareData();
//						shareData.setTitle("报价详情");
//						shareData.setContent("报价详情测试数据");
//
//						// 呼出分享选中对话框
//						ShareUtil.show(mParent, shareData, getSaicataId());
				}
			};
			// 设置分享按钮 点击监听
			mShareBottom.setOnClickListener(mShareClickListener);
			mShareTop.setOnClickListener(mShareClickListener);
		}*/
        //--ADD NT-LWL 17/06/19 Share TO -

        mListView.addHeaderView(mHeaderView, mResponseGet, false);
        mListView.addFooterView(mFooterView, mResponseGet, false);

    }


    protected void makeDataView(View rootView) {

        ArrayList<ResponseGetQuotation.ItemInfo> itemList = mResponseGet.mItemList;
        mListAdapter = new QuotationDetailAdapter(getContext(), R.layout.list_item_estimate_detail_item, itemList, mOnItemClickListener, getScreenId());

        setHeaderViewData(mHeaderView, mResponseGet);
        setFooterViewData(mFooterView, mResponseGet);

        if (mResponseGet == null) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
        }

        //
        mListView.setAdapter(mListAdapter);

    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.EstimateDetail;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {

        mRefreshApi.close();
        mAddToCartApi.close();
        mAddMyPartApi.close();
        mOrderApi.close();
        mGetSpProductApi.close();
        //--ADD NT-LWL 17/06/19 Share FR -
//        mShareSaveApi.close();
        //--ADD NT-LWL 17/06/19 Share TO -

        super.onPause();
    }


    //
    private void setHeaderViewData(View baseView, final ResponseGetQuotation response) {

        int checkingCount = 0;

        ArrayList<ResponseGetQuotation.ItemInfo> itemList = response.mItemList;
        int itemCount = itemList.size();

        for (ResponseGetQuotation.ItemInfo info : itemList) {

            if ("z".equals(info.status)) {
                ++checkingCount;
            }
        }

        //--UDP NT-LWL 17/09/29 ExcludeFreight FR -
        //
//		if (checkingCount > 0) {
//			baseView.findViewById(R.id.textChecking).setVisibility(View.VISIBLE);
//		} else {
//			baseView.findViewById(R.id.textChecking).setVisibility(View.GONE);
//		}
        TextView textChecking = (TextView) baseView.findViewById(R.id.textChecking);
        textChecking.setVisibility(View.VISIBLE);
        if (checkingCount > 0) {
            textChecking.setText(R.string.quote_hist_detail_total_price_checking);
        } else {
            textChecking.setText(R.string.order_quote_hist_detail_tip);
        }
        //--UDP NT-LWL 17/09/29 ExcludeFreight TO -

        //
        if (itemCount > 0) {
            baseView.findViewById(R.id.layoutNotEmpty).setVisibility(View.VISIBLE);
        } else {
            baseView.findViewById(R.id.layoutNotEmpty).setVisibility(View.GONE);
        }


        setIncludeItemText(baseView.findViewById(R.id.infoSlipNo), getResourceString(R.string.quote_hist_detail_slip_no), response.quotationSlipNo, null);

        setIncludeItemText(baseView.findViewById(R.id.infoDateTime), getResourceString(R.string.quote_hist_detail_date), response.quotationDateTime, null);

        setIncludeItemText(baseView.findViewById(R.id.infoExpireDateTime), getResourceString(R.string.quote_hist_detail_expire), response.quotationExpireDateTime, null);

        {
            //2015/09/25 確認済み 担当者名だけで良い
            String str = "";
//			if (!android.text.TextUtils.isEmpty(response.userDeptName)) {
//				str += response.userDeptName;
//			}
            if (!android.text.TextUtils.isEmpty(response.userName)) {
                str += response.userName;
            }
            if (android.text.TextUtils.isEmpty(str)) {
                str = null;
            }
            setIncludeItemText(baseView.findViewById(R.id.userName), getResourceString(R.string.quote_hist_detail_user_name), str, null);
        }

        {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String str;

            if (itemCount == 0) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化
                ssb.append(str);

//				SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
//                ssb.append(ss);

//                ssb.append(getResourceString(R.string.quote_hist_detail_count_unit));
            } else {
                //赤色
                str = String.format("%1$,3d", itemCount);

                SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
                ssb.append(ss);

                ssb.append(getResourceString(R.string.quote_hist_detail_count_unit));

                if (checkingCount > 0) {

                    //(ミスミ確認中x件)
                    ssb.append("（");

                    str = String.format(getResourceString(R.string.quote_hist_detail_misumi_check_str), Format.formatCount(checkingCount));

                    ss = SpannableUtil.newSpannableString(str, 0, true, true);
                    ssb.append(ss);

                    ssb.append("）");
                }
            }

            setIncludeItemText(baseView.findViewById(R.id.infoCount), getResourceString(R.string.quote_hist_detail_item_count), ssb, null);
        }

        if (SubsidiaryCode.isJapan()) {
            if (!mIsIncludeTax) {
                //日本外税
                baseView.findViewById(R.id.totalPriceWithTax).setVisibility(View.GONE);

                SpannableStringBuilder ssb = new SpannableStringBuilder();

                if (response.totalPrice == null) {

                    ssb.append(getResourceString(R.string.label_hyphen));
                } else {

                    ssb.append(Format.formatAmountWithUnit(response.totalPrice, null));
                }

                setIncludeItemText(baseView.findViewById(R.id.totalPrice), getResourceString(R.string.quote_hist_detail_total_price), ssb, null);

//				//強制背景色設定
//				ViewBgUtil.requestLayout(baseView.findViewById(R.id.totalPrice), R.id.viewDiv, R.id.textView1, R.id.viewRight);

            } else {

                baseView.findViewById(R.id.totalPrice).setVisibility(View.GONE);

                SpannableStringBuilder ssb = new SpannableStringBuilder();

                if (response.totalPriceIncludingTax == null) {

                    ssb.append(getResourceString(R.string.label_hyphen));
                } else {

                    ssb.append(Format.formatAmountWithUnit(response.totalPriceIncludingTax, null));
                }

                setIncludeItemText(baseView.findViewById(R.id.totalPriceWithTax), getResourceString(R.string.quote_hist_detail_total_price_tax), ssb, null);

//				//強制背景色設定
//				ViewBgUtil.requestLayout(baseView.findViewById(R.id.totalPriceWithTax), R.id.viewDiv, R.id.textView1, R.id.viewRight);

            }

        } else {
            //中国外税
            SpannableStringBuilder ssb = new SpannableStringBuilder();

            if (response.totalPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));
            } else {

                ssb.append(Format.formatAmountWithUnit(response.totalPrice, null));
            }

            setIncludeItemText(baseView.findViewById(R.id.totalPrice), getResourceString(R.string.quote_hist_detail_total_price), ssb, null);

//			//強制背景色設定
//			ViewBgUtil.requestLayout(baseView.findViewById(R.id.totalPrice), R.id.viewDiv, R.id.textView1, R.id.viewRight);

            //中国税込み
            SpannableStringBuilder ssbTax = new SpannableStringBuilder();

            if (response.totalPriceIncludingTax == null) {

                ssbTax.append(getResourceString(R.string.label_hyphen));
            } else {

                ssbTax.append(Format.formatAmountWithUnit(response.totalPriceIncludingTax, ""));
            }

            setIncludeItemText(baseView.findViewById(R.id.totalPriceWithTax), getResourceString(R.string.quote_hist_detail_total_price_tax), ssbTax, null);

//			//強制背景色設定
//			ViewBgUtil.requestLayout(baseView.findViewById(R.id.totalPriceWithTax), R.id.viewDiv, R.id.textView1, R.id.viewRight);

        }


        //
        boolean oneChecked = false;    //チェックが有るか
        for (ResponseGetQuotation.ItemInfo itemInfo : itemList) {
            if (itemInfo.checked) {
                oneChecked = true;    //チェックが有るか
            }
        }
        allStatusNg = orderAllNgCheckState(itemList);
        if (allStatusNg) {
            oneChecked = false;
        }

        //注文へ進む
        baseView.findViewById(R.id.buttonOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //注文へ進む
                doOrderConfirm();
            }
        });


        updateHeaderState(baseView, oneChecked);
        updateHeaderState(mFooterView, oneChecked);

//		mListAdapter.notifyDataSetChanged();
    }


    private boolean orderAllNgCheckState(ArrayList<ResponseGetQuotation.ItemInfo> itemList) {
        boolean oneChecked = true;

        for (ResponseGetQuotation.ItemInfo itemInfo : itemList) {
            if (itemInfo.status == null) {
                continue;
            }
            if (!itemInfo.status.equals("3") && !itemInfo.status.equals("4")) {
                oneChecked = false;    //チェックが有るか
            }
        }

        return oneChecked;
    }

    private void updateHeaderState(final View baseView, boolean oneChecked) {

        //注文へ進む
        baseView.findViewById(R.id.buttonOrder).setEnabled(oneChecked);
    }


    private void setFooterViewData(View baseView, final ResponseGetQuotation response) {

        ArrayList<ResponseGetQuotation.ItemInfo> itemList = response.mItemList;
        int itemCount = itemList.size();

        //
        if (itemCount > 0) {

            baseView.findViewById(R.id.layoutNotEmpty).setVisibility(View.VISIBLE);
//			baseView.findViewById(R.id.layoutEmpty).setVisibility(View.GONE);
        } else {

            baseView.findViewById(R.id.layoutNotEmpty).setVisibility(View.GONE);
//			baseView.findViewById(R.id.layoutEmpty).setVisibility(View.VISIBLE);
        }

//		//合計金額
//		int checkingCount = 0;

//        for (ResponseGetQuotation.ItemInfo info: itemList) {
//
//			if ("z".equals(info.status)) {
//				++checkingCount;
//			}
//		}

        //注文へ進む
        baseView.findViewById(R.id.buttonOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //注文へ進む
                doOrderConfirm();
            }
        });
    }


    // リストビューのアイテムがクリックされた時
    QuotationDetailAdapter.OnItemClickListener mOnItemClickListener = new QuotationDetailAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view,
                                int position, long id) {

            ResponseGetQuotation.ItemInfo itemInfo = (ResponseGetQuotation.ItemInfo) parent.getItem(position);

            doSpProduct(itemInfo);

        }

        public void onItemCheck(ArrayAdapter<?> parent, View view,
                                int position, long id) {

            boolean oneChecked = false;    //チェックが有るか

            ArrayList<ResponseGetQuotation.ItemInfo> itemList = mResponseGet.mItemList;
            for (ResponseGetQuotation.ItemInfo itemInfo : itemList) {

                if (itemInfo.checked) {
                    oneChecked = true;    //チェックが有るか
                }
            }
            //--ADD NT-LWL 17/06/19 Share FR -
            // 未选中商品时分享按钮不可点击
//			if (SubsidiaryCode.isChinese()) {
//				mShareTop.setEnabled(oneChecked);
//				mShareBottom.setEnabled(oneChecked);
//				ViewGroup view1 = (ViewGroup) mShareTop;
//				ViewGroup view2 = (ViewGroup) mShareBottom;
//				int n = view1.getChildCount();
//				// 此处两个按钮层级完全一样 一次遍历即可
//				for (int i=0;i<n;i++){
//					view1.getChildAt(i).setEnabled(oneChecked);
//					view2.getChildAt(i).setEnabled(oneChecked);
//				}
//			}
            //--ADD NT-LWL 17/06/19 Share TO -
            if (allStatusNg) {
                oneChecked = false;
            }

            updateHeaderState(mHeaderView, oneChecked);
            updateHeaderState(mFooterView, oneChecked);
        }


        public void onItemAddCart(ArrayAdapter<?> parent, View view,
                                  int position, long id) {

            ResponseGetQuotation.ItemInfo itemInfo = (ResponseGetQuotation.ItemInfo) parent.getItem(position);

            doAddToCart(itemInfo);
        }


        public void onItemAddMy(ArrayAdapter<?> parent, View view,
                                int position, long id) {

            ResponseGetQuotation.ItemInfo itemInfo = (ResponseGetQuotation.ItemInfo) parent.getItem(position);

            doAddMyPart(itemInfo);
        }

    };


    private List<ResponseGetQuotation.ItemInfo> getCheckedItemList() {

        List<ResponseGetQuotation.ItemInfo> checkedList = new ArrayList<>();

        List<ResponseGetQuotation.ItemInfo> itemList = mResponseGet.mItemList;
        for (ResponseGetQuotation.ItemInfo itemInfo : itemList) {

            if (itemInfo.checked) {
                checkedList.add(itemInfo);
            }
        }

        return checkedList;
    }


    /**
     * カートに追加
     */
    private void doAddToCart(ResponseGetQuotation.ItemInfo item) {

        hideKeyboard();

        mAddToCartApi.setParameter(item);
        mAddToCartApi.connect(getContext());
    }


    private class AddToCartApi extends ApiAccessWrapper {

        ResponseGetQuotation.ItemInfo mItem;

        @Override
        protected String getScreenId() {
            return EstimateDetailFragment.this.getScreenId();
        }

        public void setParameter(ResponseGetQuotation.ItemInfo item) {
            mItem = item;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createAddToCartFromQuotation(mResponseGet.quotationSlipNo, mItem);
        }

        @Override
        public void onResult(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //ヘッダのカート件数を更新する
                    //無条件に足し込む
//					int addCount = mCheckedList.size();
                    int addCount = 1;

                    AppNotifier.getInstance().addCartCount(addCount);

                    showSimpleMessageDialog(null, getResourceString(R.string.my_parts_dialog_added_cart), R.string.dialog_button_close);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }

    //商品詳細
    private void doSpProduct(ResponseGetQuotation.ItemInfo itemInfo) {
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
	private void setTextEmptyGone(TextView tv, String str) {

		if (android.text.TextUtils.isEmpty(str)) {
			tv.setText("");
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}
*/


    //
    private void doAddMyPart(ResponseGetQuotation.ItemInfo itemInfo) {

        hideKeyboard();

        mAddMyPartApi.connect(getContext(), itemInfo);
    }

    private class AddMyPartApi extends ApiAccessWrapper {

        private ResponseGetQuotation.ItemInfo mItem;

        @Override
        protected String getScreenId() {
            return EstimateDetailFragment.this.getScreenId();
        }

        public HashMap<String, String> getParameter() {

            return ApiBuilder.createAddToMyComponentsFromQuotation(mResponseGet.quotationSlipNo, mItem);
        }

        public void connect(Context context, ResponseGetQuotation.ItemInfo itemInfo) {
            mItem = itemInfo;
            super.connect(context);
        }

        public void onResult(int responseCode, String result) {

            AddMyParts response = new AddMyParts();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }
            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    showSimpleMessageDialog(null, getResourceString(R.string.my_parts_dialog_added_my_parts), R.string.dialog_button_close);
                    break;

                default:

                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    //注文確認
    private void doOrderConfirm() {

        hideKeyboard();

        List<ResponseGetQuotation.ItemInfo> checkedList = getCheckedItemList();

/*

		boolean validQty = checkValidQuantity(checkedList);

		if (!validQty) {
			showSimpleMessageDialog(null, getResourceString(R.string.cart_error_invalid_quantity), R.string.dialog_button_close);
			return;
		}
*/

        //
        RequestCheckOrderFromQuote request = new RequestCheckOrderFromQuote();
        request.quotationSlipNo = mResponseGet.quotationSlipNo;    //○見積伝票番号

        for (ResponseGetQuotation.ItemInfo checkedItem : checkedList) {
            RequestCheckOrderFromQuote.ItemInfo item = new RequestCheckOrderFromQuote.ItemInfo();

            item.quotationItemNo = checkedItem.quotationItemNo;    //　見積明細番号

            request.mItemList.add(item);
        }

        mOrderApi.setParameter(request);
        mOrderApi.connect(getContext());
    }

    private class OrderApi extends ApiAccessWrapper {

        RequestCheckOrderFromQuote mRequest;

        @Override
        protected String getScreenId() {
            return EstimateDetailFragment.this.getScreenId();
        }

        public void setParameter(RequestCheckOrderFromQuote request) {
            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createCheckOrderFromQuote(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseCheckOrderFromQuote response = new ResponseCheckOrderFromQuote();
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
                RequestGetQuotation request = new RequestGetQuotation();
                request.quotationSlipNo = mResponseGet.quotationSlipNo;
                mRefreshApi.setParameter(request);
                mRefreshApi.connect(getContext());
            } else {

                //カート画面処理をキャンセルした
                //何もしない
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //======
    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, CharSequence str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);

        if (android.text.TextUtils.isEmpty(str2)) {

            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
//		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
    }


    /**
     * 自分画面更新用
     */
    private class RefreshApi extends ApiAccessWrapper {

        RequestGetQuotation mRequest;

        public void setParameter(RequestGetQuotation request) {

            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createGetQuotation(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseGetQuotation response = new ResponseGetQuotation();
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

        protected void onSuccess(ResponseGetQuotation response) {
        }

        @Override
        protected String getScreenId() {
            return EstimateDetailFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.EstimateDetail;
    }
}

