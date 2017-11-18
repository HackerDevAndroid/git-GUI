package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.adapter.MyOrderableAdapterCom;
import jp.co.misumi.misumiecapp.api.OrderConfirmOrderBaseApi;
import jp.co.misumi.misumiecapp.api.QuoteConfirmQuoteBaseApi;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromOrder;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromQuote;
import jp.co.misumi.misumiecapp.data.ResponseCheck;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotation;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.SpannableUtil;


/**
 * 確認画面遷移共通クラス
 */
public abstract class BaseToConfirmFragment extends BaseGetSpProductApi {


    //API
    private QuoteConfirmQuoteTypeApi mCartConfirmQuoteTypeApi;
    private OrderConfirmOrderTypeApi mCartConfirmOrderTypeApi;

	//在庫切れダイアログ用
    private MessageDialog mMessageDialog;


    public BaseToConfirmFragment() {

		super();

		//
        mCartConfirmQuoteTypeApi = new QuoteConfirmQuoteTypeApi();
        mCartConfirmOrderTypeApi = new OrderConfirmOrderTypeApi();
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
        protected void onSuccess(ResponseCheckQuotation response){

			doCart(mOpenType, mRequestCode, response);
        }

        @Override
        protected String getScreenId(){
            return BaseToConfirmFragment.this.getScreenId();
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
        protected void onSuccess(ResponseCheckOrder response){

			doCheckStoke(mOpenType, mRequestCode, mResponse, response);
        }

	    @Override
	    protected String getScreenId(){
	        return BaseToConfirmFragment.this.getScreenId();
	    }

	}




    /**
     * getScreenId
     *
     * @return
     */
    public abstract String getScreenId();

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mCartConfirmOrderTypeApi.close();
        mCartConfirmQuoteTypeApi.close();

        super.onPause();
    }



	//========
	//========
	//========
	//注文
    protected void doCartOrderType(int openType, int requestCode, ResponseCheck dataContainer) {

		/*
		注文可能タイプ
		orderableType
		"注文可能タイプ
		　1: 全明細が注文可能
		　2: 一部明細が注文可能
		　3: 全明細が注文不可能"
		*/

		String orderableType = dataContainer.orderableType;

		if (dataContainer.isQuote()) {

			//見積{{{
			//全明細が見積可能（次に進む）
			if ("1".equals(orderableType)) {
				doCart(openType, requestCode, dataContainer);
				return;
			}
			//見積}}}

		} else {

			//注文{{{
			//全明細が注文可能（SOコンバートを確認する）
			if ("1".equals(orderableType)) {
				doCheckStoke(openType, requestCode, (ResponseCheckOrder)dataContainer, dataContainer);
				return;
			}
			//注文}}}

		}

		//一部（選択ダイアログを出す）
		if ("2".equals(orderableType)) {
			doCartOrderType2(openType, requestCode, dataContainer);
			return;
		}

		//全部（閉じるダイアログを出す）
		doCartOrderType3(openType, requestCode, dataContainer);
    }


    private void doCartOrderType2(final int openType, final int requestCode, final ResponseCheck mResponse) {

		//		　2: 一部明細が注文可能

		//素通し確認ダイアログの表示処理
        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){

					doSubmitOrderType2Common(openType, requestCode, mResponse);
                } else {

					//閉じるだけ
					mMessageDialog.hide();
                }
            }
        }).setAutoClose(false);


		final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_orderable_layout, null, false);


		ListView listView = (ListView)view.findViewById(R.id.listView);

        View viewH = inflateLayout(inflater, R.layout.dialog_orderable_layout_h, listView, false);

		listView.addHeaderView(viewH, null, false);

		MyOrderableAdapterCom listAdapter = new MyOrderableAdapterCom(getContext(), R.layout.list_item_orderable_item, new ArrayList<ResponseCheck.ItemInfo>(), getScreenId());
		listView.setAdapter(listAdapter);

		if (mResponse.isQuote()) {

			//見積{{{
			{
				TextView tv;
				tv = (TextView) viewH.findViewById(R.id.textTitle);
				tv.setText(R.string.orderable_type_dialog_titlle_quotation);
			}
			//見積}}}

		}

		String orderableType = mResponse.orderableType;
		if ("3".equals(orderableType)) {
//			LinearLayout listLayout = (LinearLayout) view.findViewById(R.id.listLayout);
//			listLayout.setVisibility(View.GONE);

			if (mResponse.isQuote()) {

				TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
				tv.setText(R.string.orderable_type_dialog_info_quotation_3);

			} else {
				//注文{{{
				TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
				tv.setText(R.string.orderable_type_dialog_info_3);
				//注文}}}
			}

		}


		if (mResponse.isQuote()) {

			TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
			tv.setText(R.string.orderable_type_dialog_info_quotation_type2);

		}

		//ダイアログに表示する中身を作成
		int orderableCount = 0;

		if (mResponse.isQuote()) {

			for (ResponseCheck.ItemInfo itemInfo: ((ResponseCheckQuotation)mResponse).mItemList) {

				if ("1".equals(itemInfo.orderableFlag)) {
					//注文可能フラグ
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

		} else {

			for (ResponseCheck.ItemInfo itemInfo: ((ResponseCheckOrder)mResponse).mItemList) {

				if ("1".equals(itemInfo.orderableFlag)) {
					//注文可能フラグ
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

		}

		listAdapter.notifyDataSetChanged();

		//注文不可の商品
		{
			String str = ""+ orderableCount;
			SpannableStringBuilder ssb = new SpannableStringBuilder();

			ssb.append(getResourceString(R.string.orderable_type_dialog_list_title));
			SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
			ssb.append(ss);

			ssb.append(getResourceString(R.string.orderable_type_dialog_list_title_unit));

			TextView tv = (TextView) viewH.findViewById(R.id.textCount);
			tv.setText(ssb);
		}

		//
		if (mResponse.isQuote()) {

			//見積{{{
			//一部（選択ダイアログを出す）
			if ("2".equals(orderableType)) {
				mMessageDialog.showCart(view, R.string.orderable_type_dialog_yes_quotation, R.string.orderable_type_dialog_no);
				return;
			}
			//見積}}}

		} else {

			//注文{{{
			//一部（選択ダイアログを出す）
			if ("2".equals(orderableType)) {
				mMessageDialog.showCart(view, R.string.orderable_type_dialog_yes, R.string.orderable_type_dialog_no);
				return;
			}
			//注文}}}

		}

		//全部（閉じるダイアログを出す）
		mMessageDialog.showCart(view, 0, R.string.orderable_type_dialog_no);
	}


    private void doCartOrderType3(final int openType, final int requestCode, final ResponseCheck mResponse) {
//		　3: 全明細が不可能"

		//doCartOrderType2(openType, requestCode, dataContainer);

		//素通し確認ダイアログの表示処理
		mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
			@Override
			public void onDialogResult(Dialog dlg, View view, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE){

					doSubmitOrderType2Common(openType, requestCode, mResponse);
				} else {

					//閉じるだけ
					mMessageDialog.hide();
				}
			}
		}).setAutoClose(false);


		final LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflateLayout(inflater, R.layout.dialog_orderable_layout, null, false);


		ListView listView = (ListView)view.findViewById(R.id.listView);

		View viewH = inflateLayout(inflater, R.layout.dialog_orderable_layout_h, listView, false);

		listView.addHeaderView(viewH, null, false);

		MyOrderableAdapterCom listAdapter = new MyOrderableAdapterCom(getContext(), R.layout.list_item_orderable_item, new ArrayList<ResponseCheck.ItemInfo>(), getScreenId());
		listView.setAdapter(listAdapter);

		if (mResponse.isQuote()) {

			//見積{{{
			{
				TextView tv;
				tv = (TextView) viewH.findViewById(R.id.textTitle);
				tv.setText(R.string.orderable_type_dialog_titlle_quotation);
			}
			//見積}}}

		}

		String orderableType = mResponse.orderableType;
		if ("3".equals(orderableType)) {
//			LinearLayout listLayout = (LinearLayout) view.findViewById(R.id.listLayout);
//			listLayout.setVisibility(View.GONE);

			if (mResponse.isQuote()) {

				TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
				tv.setText(R.string.orderable_type_dialog_info_quotation_3);

			} else {
				//注文{{{
				TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
				tv.setText(R.string.orderable_type_dialog_info_3);
				//注文}}}
			}

		}


		if (mResponse.isQuote()) {

			TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
			tv.setText(R.string.orderable_type_dialog_info_quotation_type3);

		}

		//ダイアログに表示する中身を作成
		int orderableCount = 0;

		if (mResponse.isQuote()) {

			for (ResponseCheck.ItemInfo itemInfo: ((ResponseCheckQuotation)mResponse).mItemList) {

				if ("1".equals(itemInfo.orderableFlag)) {
					//注文可能フラグ
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

		} else {

			for (ResponseCheck.ItemInfo itemInfo: ((ResponseCheckOrder)mResponse).mItemList) {

				if ("1".equals(itemInfo.orderableFlag)) {
					//注文可能フラグ
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

		}

		if (orderableCount == 0){
			TextView tv = (TextView) viewH.findViewById(R.id.textMessage);
			tv.setText(R.string.orderable_type_dialog_info_quotation_4);

			viewH.findViewById(R.id.listLayout).setVisibility(View.GONE);
		}else{
			viewH.findViewById(R.id.listLayout).setVisibility(View.VISIBLE);
		}

		listAdapter.notifyDataSetChanged();

		//注文不可の商品
		{
			String str = ""+ orderableCount;
			SpannableStringBuilder ssb = new SpannableStringBuilder();

			ssb.append(getResourceString(R.string.orderable_type_dialog_list_title));
			SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
			ssb.append(ss);

			ssb.append(getResourceString(R.string.orderable_type_dialog_list_title_unit));

			TextView tv = (TextView) viewH.findViewById(R.id.textCount);
			tv.setText(ssb);
		}

		//
		if (mResponse.isQuote()) {

			//見積{{{
			//一部（選択ダイアログを出す）
			if ("2".equals(orderableType)) {
				mMessageDialog.showCart(view, R.string.orderable_type_dialog_yes_quotation, R.string.orderable_type_dialog_no);
				return;
			}
			//見積}}}

		} else {

			//注文{{{
			//一部（選択ダイアログを出す）
			if ("2".equals(orderableType)) {
				mMessageDialog.showCart(view, R.string.orderable_type_dialog_yes, R.string.orderable_type_dialog_no);
				return;
			}
			//注文}}}

		}

		//全部（閉じるダイアログを出す）
		mMessageDialog.showCart(view, 0, R.string.orderable_type_dialog_no);

	}


	//
	private void doSubmitOrderType2Common(final int openType, final int requestCode, final ResponseCheck mResponse) {

		if (mResponse.isQuote()) {
			doSubmitOrderType2(openType, requestCode, (ResponseCheckQuotation)mResponse);
		} else {
			doSubmitOrderType2(openType, requestCode, (ResponseCheckOrder)mResponse);		}
	}


	//見積確認のAPI FROM 見積確認
	private void doSubmitOrderType2(final int openType, final int requestCode, final ResponseCheckQuotation mResponse) {

		//見積確認のAPI FROM 見積確認
		boolean isFromCart = mResponse.isFromCart();

		RequestCheckQuotationFromQuote requestData = new RequestCheckQuotationFromQuote();
		requestData.receptionCode = mResponse.receptionCode;

		//1: 素通しエラーを解消する
		requestData.resolveErrorPassOnFlag = "1";

		//いろいろなリクエストパラメータ
		mCartConfirmQuoteTypeApi.setParameter(isFromCart, requestData, mMessageDialog);
		mCartConfirmQuoteTypeApi.setParameter2(openType, requestCode, mResponse);
		mCartConfirmQuoteTypeApi.connect(getContext());
    }



	//注文確認のAPI FROM 注文確認
	private void doSubmitOrderType2(final int openType, final int requestCode, final ResponseCheckOrder mResponse) {

		//注文確認のAPI FROM 注文確認
		boolean isFromCart = mResponse.isFromCart();

		RequestCheckOrderFromOrder requestData = new RequestCheckOrderFromOrder();
		requestData.receptionCode = mResponse.receptionCode;

		//1: 素通しエラーを解消する
		requestData.resolveErrorPassOnFlag = "1";

		//いろいろなリクエストパラメータ
		mCartConfirmOrderTypeApi.setParameter(isFromCart, requestData, mMessageDialog);
		mCartConfirmOrderTypeApi.setParameter2(openType, requestCode, mResponse);
		mCartConfirmOrderTypeApi.connect(getContext());
    }



	/*
	expressConfirmTypeList
	"ストーク確認タイプ
	　""1"": ストークTで、注文時刻が12:00を超過
	　""2"": ストークTで、注文時刻が18:00を超過
	　""3"": ストークAで、注文時刻が15:00以前
	　""4"": ストークA早割で、注文時刻が15:00を超過
	　""5"": ストークAまたはA早割で、注文時刻が18:00を超過"
	*/
    protected void doCheckStoke(final int openType, final int requestCode, final ResponseCheckOrder mResponse, final ResponseCheck nextResponse) {

		if (mResponse.expressConfirmTypeList.size() == 0) {
			doCart(openType, requestCode, nextResponse);
			return;
		}


		//ストーク確認ダイアログの表示処理
        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){

					doCart(openType, requestCode, nextResponse);
                }
            }
        });


		final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_so_layout, null, false);

		//項目を動的に追加する
		LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);
		itemListLayout.removeAllViews();

		View childView;

		//ダイアログに表示する中身を作成
		for (String itemInfo: mResponse.expressConfirmTypeList) {

			//ストーク確認タイプを動的に追加する
			childView = inflateLayout(inflater, R.layout.include_item_text_so, itemListLayout, false);

			String str = MsmFormat.getExpressConfirmTypeString(getContext(), itemInfo);

			//
			TextView tv;
			tv = (TextView) childView.findViewById(R.id.textMessage);
			setTextEmptyGone(tv, str);

			//
			itemListLayout.addView(childView);
		}

		//
		mMessageDialog.showCart(view, R.string.so_dialog_yes, R.string.so_dialog_no);

	}


	protected abstract void doCart(int openType, int requestCode, DataContainer dataContainer);



	protected void setTextEmptyGone(TextView tv, String str) {
		if (str == null || str.isEmpty()) {
			tv.setText("");
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}


}
