package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.adapter.MyUnfitAdapterCom;
import jp.co.misumi.misumiecapp.api.OrderConfirmOrderBaseApi;
import jp.co.misumi.misumiecapp.api.QuoteConfirmQuoteBaseApi;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ErrorList;
import jp.co.misumi.misumiecapp.data.ExpressInfo;
import jp.co.misumi.misumiecapp.data.ReceiverInfo;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromOrder;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromQuote;
import jp.co.misumi.misumiecapp.data.ResponseCheck;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotation;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * 見積、注文確認画面
 */
public abstract class CartConfirmFragment extends BaseFragment {


    private int mOpenType;
    private DataContainer mDataContainer;

	//ストークを利用しない時のリクエストパラメータ
	private static final String STOKE_NO_SELECT = "00";
	//当日出荷
	private static final String TODAY_NO_SELECT = "0";
	private static final String TODAY_SELECT = "1";

    protected static final String STOKE_SPEED_A = "0A";	//早割りA
    protected static final String STOKE_NORMAL_A = "A0";	//通常A

    //API
    private OrderConfirmOrderBaseApi mCartConfirmOrderApi;
    private OrderConfirmOrderBaseApi mCartConfirmOrderExpressApi;
    private QuoteConfirmQuoteBaseApi mCartConfirmQuoteApi;
    private QuoteConfirmQuoteBaseApi mCartConfirmQuoteExpressApi;

	//-- ADD NT-LWL 16/11/13 AliPay Payment FR -
	//ADV时 选中的支付方式
	public ResponseCheckOrder.PaymentGroup selectPaymentGroup;
	//-- ADD NT-LWL 16/11/13 AliPay Payment TO -
    protected MessageDialog mMessageDialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

    }


    public CartConfirmFragment() {

		//注文系
        mCartConfirmOrderApi = new OrderConfirmOrderBaseApi() {

		        @Override
		        protected void onSuccess(ResponseCheckOrder response){

					onSuccessDirectShipping(response);
		        }

			    @Override
			    protected String getScreenId(){
			        return CartConfirmFragment.this.getScreenId();
			    }

			};

		mCartConfirmOrderExpressApi = new OrderConfirmOrderBaseApi() {

		        @Override
		        protected void onSuccess(ResponseCheckOrder response){

					onSuccessExpressType(response);
		        }

			    @Override
			    protected String getScreenId(){
			        return CartConfirmFragment.this.getScreenId();
			    }

			};


		//見積系
        mCartConfirmQuoteApi = new QuoteConfirmQuoteBaseApi() {

		        @Override
		        protected void onSuccess(ResponseCheckQuotation response){

					onSuccessDirectShipping(response);
		        }

		        @Override
		        protected String getScreenId(){
		            return CartConfirmFragment.this.getScreenId();
		        }

		    };

		mCartConfirmQuoteExpressApi = new QuoteConfirmQuoteBaseApi() {

		        @Override
		        protected void onSuccess(ResponseCheckQuotation response){

					onSuccessExpressType(response);
		        }

		        @Override
		        protected String getScreenId(){
		            return CartConfirmFragment.this.getScreenId();
		        }

		    };
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //アプリ内で変更しないので保存不要
        mOpenType = getBundleData().getInt("openType");
        mDataContainer = getParameterFromActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        getHeader().showHeader();


        View rootView = inflateLayout(inflater, getLayoutId(), container, false);

		makeView(rootView);

        makeDataView(rootView);

        return rootView;
    }


    @Override
    public void onResume() {

        super.onResume();

    }


    @Override
    public void onPause() {

        mCartConfirmOrderApi.close();
        mCartConfirmOrderExpressApi.close();
        mCartConfirmQuoteApi.close();
        mCartConfirmQuoteExpressApi.close();

        super.onPause();
    }

//    @Override
//    public boolean onBackKey() {
//        //バックボタンは何もしない
//        return true;
//    }

    @Override
    public void onHeaderEvent(int event, Objects objects) {
        //この画面のヘッダは閉じるだけなので eventを判別しなくても良い
        mParent.finish();
    }


    protected DataContainer getDataContainer() {

        return mDataContainer;
    }


    protected abstract int getLayoutId();

    protected void makeView(View rootView) {
	}
    protected abstract void makeDataView(View rootView);


    // 確定ボタンの有効無効
    protected abstract void setSubmitEnabled(boolean enabled);


    protected void setIncludeItemText(View subView, String str1, String str2, String str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView3)).setText(str3);
    }


    protected void setIncludeItemText(View subView, String str1, CharSequence str2) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
//		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
    }


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
//		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
    }


	protected void setIncludeItemText(View subView, String str1, String str2, String str3, boolean doHidden) {

		if (android.text.TextUtils.isEmpty(str3)) {

			if (doHidden) {
				subView.setVisibility(View.GONE);
				return;
			}

			str3 = getResourceString(R.string.label_hyphen);	//ハイフン化
		}

		((TextView)subView.findViewById(R.id.textView1)).setText(str1);
		((TextView)subView.findViewById(R.id.textView2)).setText(str2);
		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
	}


    protected void setIncludeItemEdit(View subView, String str1, String str2, String str3) {
        if (subView instanceof EditText) {
            ((EditText) subView).setText(str3);
            return;
        }

        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((EditText) subView.findViewById(R.id.editText1)).setText(str3);

        //includeした EditTextが復帰時に全部同じになるバグの回避
        //	http://stackoverflow.com/questions/26150877/all-edittexts-are-populated-with-data-from-one-edittext-after-rotation-and-backp
        // includeレイアウトの IDを上書き設定する
        int id = subView.getId();
        subView.setId(View.NO_ID);
        ((EditText) subView.findViewById(R.id.editText1)).setId(id);
    }


    protected EditText getIncludeItemEdit(View subView) {

        if (subView instanceof EditText) {

            return (EditText) subView;
        }

        return (EditText) subView.findViewById(R.id.editText1);
    }

    protected String getIncludeItemEditString(View subView) {
        return getIncludeItemEdit(subView).getText().toString();
    }


    //直送先変更
    protected abstract void doChangeDirectShip();


	//================
	//================
	//注文確認の直送先
	//見積確認の直送先
    //直送先ダイアログの表示
    protected void dispChangeDirectShip(final ResponseCheck responseCheck) {

		List<ReceiverInfo> mReceiverList;
		String receiverCode;
		if (responseCheck.isQuote()) {
			mReceiverList = ((ResponseCheckQuotation)responseCheck).mReceiverList;
			receiverCode = ((ResponseCheckQuotation)responseCheck).receiver.receiverCode;
		} else {
			mReceiverList = ((ResponseCheckOrder)responseCheck).mReceiverList;
			receiverCode = ((ResponseCheckOrder)responseCheck).receiver.receiverCode;
		}

		//
        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
            }
        });


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_direct_ship_layout, null, false);

        //項目を動的に追加する
        LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);
        for (final ReceiverInfo itemInfo : mReceiverList) {

            final ReceiverInfo itemInfoF = itemInfo;
            View childView = inflateLayout(inflater, R.layout.list_item_direct_ship_item, itemListLayout, false);

            TextView tv;
			//-- ADD NT-SLJ 17/07/13 3小时闪达 FR -
			//判断是否在三小闪达对象内，显示不同图片
			ImageView iv;
			iv = (ImageView) childView.findViewById(R.id.iv_flash_status);
			//3小时闪达对象内
			if(immediateDeliveryFlag(itemInfo.immediateDeliveryFlag).equals("1")){
				iv.setVisibility(View.VISIBLE);
			}else{
				iv.setVisibility(View.GONE);
			}

			//-- ADD NT-SLJ 17/07/13 3小时闪达 TO -
            tv = (TextView) childView.findViewById(R.id.textView2);

			String str = getShipInfoString(itemInfo, false);
	        tv.setText(str);


            //選択ボタン
            childView.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //選択ボタンを押した時の処理
                    doSelectedDirectShipCommon(responseCheck, itemInfoF);
                }
            });

            tv = (TextView) childView.findViewById(R.id.textView3);

            //同じ物を選択中にする
            if (itemInfo.receiverCode.equals(receiverCode)) {
                tv.setEnabled(false);
                tv.setText(R.string.confirm_directship_selected);
                childView.setSelected(true);
            }

            itemListLayout.addView(childView);
        }

        mMessageDialog.show(view, 0, R.string.dialog_button_close);
    }


    private String getShipInfoString(ReceiverInfo itemInfo, boolean isMain) {

		String str = "";

		//直送先名(現地語)
		if (!android.text.TextUtils.isEmpty(itemInfo.receiverName)) {
			str += itemInfo.receiverName +"\n";
		}

		//納入者氏名(現地語)
		if (!isMain && !android.text.TextUtils.isEmpty(itemInfo.receiverUserName)) {
			str += itemInfo.receiverUserName +"\n";
		}

		//郵便番号
		boolean hasBefore = false;
		if (isMain && !android.text.TextUtils.isEmpty(itemInfo.postalCode)) {
			str += itemInfo.postalCode;
			hasBefore = true;
		}

		//住所1-4(現地語)
		if (!android.text.TextUtils.isEmpty(itemInfo.address1)) {
			if (hasBefore) {
				str += "　";
			}
			str += itemInfo.address1;
			hasBefore = true;
		}

		if (!android.text.TextUtils.isEmpty(itemInfo.address2)) {
			if (hasBefore) {
				str += "　";
			}
			str += itemInfo.address2;
			hasBefore = true;
		}

		if (!android.text.TextUtils.isEmpty(itemInfo.address3)) {
			if (hasBefore) {
				str += "　";
			}
			str += itemInfo.address3;
			hasBefore = true;
		}

		if (!android.text.TextUtils.isEmpty(itemInfo.address4)) {
			if (hasBefore) {
				str += "　";
			}
			str += itemInfo.address4;
        }

		return str;
	}


    //直送先表示
    protected void setDirectShipInfo(View rootView, ReceiverInfo itemInfo) {

        if (rootView == null) return;
		//-- ADD NT-LWL 17/08/04 Depo FR -
		if (SubsidiaryCode.isChinese()) {
			TextView addressAttention = (TextView) rootView.findViewById(R.id.address_attention);
			if (addressAttention != null){
				Drawable drawable = addressAttention.getResources().getDrawable(R.drawable.icon_attention);
				drawable.setBounds(0,0,38,30);
				addressAttention.setCompoundDrawables(drawable,null,null,null);
			}
		}
		//-- ADD NT-LWL 17/08/04 Depo TO -
        TextView tv;

        tv = (TextView) rootView.findViewById(R.id.receiver2);
		String str = getShipInfoString(itemInfo, true);
        tv.setText(str);


		//
        tv = (TextView) rootView.findViewById(R.id.receiver3);
		if (!android.text.TextUtils.isEmpty(itemInfo.tel)) {

			str = "TEL:" + itemInfo.tel;
	        tv.setText(str);
			tv.setVisibility(View.VISIBLE);
		} else {

			tv.setVisibility(View.GONE);
		}

		//
        tv = (TextView) rootView.findViewById(R.id.receiver4);
		if (!android.text.TextUtils.isEmpty(itemInfo.fax)) {

			str = "FAX:" + itemInfo.fax;
	        tv.setText(str);
			tv.setVisibility(View.VISIBLE);
		} else {

			tv.setVisibility(View.GONE);
		}

		//
        setIncludeItemEdit(rootView.findViewById(R.id.deliDept), getResourceString(R.string.confirm_destination_dept_label), "", itemInfo.receiverDepartmentName);

        setIncludeItemEdit(rootView.findViewById(R.id.deliName), getResourceString(R.string.confirm_destination_name_label), getResourceString(R.string.require_mark), itemInfo.receiverUserName);
    }


	//================
	//直送先選択後の処理
    protected void doSelectedDirectShipCommon(ResponseCheck responseCheck, ReceiverInfo receiverInfo) {

		if (responseCheck.isQuote()) {
			doSelectedDirectShip((ResponseCheckQuotation)responseCheck, receiverInfo);
		} else {
			doSelectedDirectShip((ResponseCheckOrder)responseCheck, receiverInfo);
		}
	}

	//注文確認の直送先
    protected void doSelectedDirectShip(ResponseCheckOrder response, ReceiverInfo receiverInfo) {

        //直送先が選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

        RequestCheckOrderFromOrder request = new RequestCheckOrderFromOrder();

		//○受付番号
        request.receptionCode = response.receptionCode;

		//直送先情報を更新設定
        request.receiverCode = receiverInfo.receiverCode;
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -

		mCartConfirmOrderApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmOrderApi.connect(getContext());
    }


	//見積確認の直送先
    protected void doSelectedDirectShip(ResponseCheckQuotation response, ReceiverInfo receiverInfo) {

        //直送先が選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

        RequestCheckQuotationFromQuote request = new RequestCheckQuotationFromQuote();

		//○受付番号
        request.receptionCode = response.receptionCode;

		//直送先情報を更新設定
        request.receiverCode = receiverInfo.receiverCode;
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmQuoteApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmQuoteApi.connect(getContext());
    }


    protected abstract void onSuccessDirectShipping(DataContainer response);


	//================
	//================
	//注文確認のストーク
	//見積確認のストーク
    //ストーク選択ダイアログの表示
    protected void doChangeExpressType(final ResponseCheck responseCheck, final ResponseCheck.ItemInfo itemInfoCheck) {

        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
            }
        });


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_express_type_layout, null, false);

		//-- ADD NT-LWL 17/08/22 Depo FR -
		if (SubsidiaryCode.isChinese()) {
			// 是否包含瞬达V标识
			boolean isDepo = false;
			TextView tv = (TextView) view.findViewById(R.id.confirm_express_tip);
			for (ExpressInfo expressInfo : itemInfoCheck.mExpressList){
				if (expressInfo.expressType.equals("V0")){
					isDepo = true;
					break;
				}
			}

			// 有瞬达V 显示闪达注意事项
			if (isDepo){
				tv.setText(R.string.confirm_express_type_caution);
			}else {
				tv.setText(R.string.confirm_express_type_caution_normal);
			}
		}
		//-- ADD NT-LWL 17/08/22 QR Depo TO -

		//見積差異
		boolean isQuote = responseCheck.isQuote();

		if (isQuote) {
			TextView textConfirmExpress = (TextView)view.findViewById(R.id.textConfirmExpress);
			textConfirmExpress.setText(R.string.quote_confirm_express_type_select_info);
		}


		//ストーク数量制限
		final TextView textLimit = (TextView)view.findViewById(R.id.textLimit);
		textLimit.setVisibility(View.GONE);
		if (itemInfoCheck.expressMaxQuantity != null) {
            String str = String.format(getResourceString(R.string.confirm_express_type_limit), itemInfoCheck.expressMaxQuantity);
	        textLimit.setText(str);
			textLimit.setVisibility(View.VISIBLE);
		}

        //項目を動的に追加する
		int idx = 1;
        LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);

		//選択中のストーク
		String expressType = itemInfoCheck.expressType;
		if (android.text.TextUtils.isEmpty(expressType)) {
			expressType = STOKE_NO_SELECT;
		}

		// List expressList				//　ストーク情報リスト
		ArrayList<ExpressInfo> expressList = new ArrayList<>();
		ExpressInfo expressInfoN = new ExpressInfo();
		expressInfoN.expressType = STOKE_NO_SELECT;
		expressInfoN.enableFlag = "1";
		expressInfoN.charge = 0.0;
		expressList.add(expressInfoN);
		expressList.addAll(itemInfoCheck.mExpressList);

        for (final ExpressInfo expressInfo : expressList) {

			//【32.ストーク選択】【Android】ストークリストにA早割が存在していても、表示しないことにする対応
			if (STOKE_SPEED_A.equals(expressInfo.expressType)) {
				continue;
			}

            final ExpressInfo expressInfoF = expressInfo;

			View childView = getChildViewExpress(itemListLayout, idx, expressInfo, expressType, isQuote);
			++idx;

            //選択ボタン
            childView.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelectedExpressTypeCommon(responseCheck, itemInfoCheck, expressInfoF);
                }
            });

            itemListLayout.addView(childView);
        }

        mMessageDialog.show(view, 0, R.string.dialog_button_close);
    }


	//================
    protected void doSelectedExpressTypeCommon(ResponseCheck responseCheck, ResponseCheck.ItemInfo itemInfoCheck, ExpressInfo expressInfo) {

		if (responseCheck.isQuote()) {
			doSelectedExpressType((ResponseCheckQuotation)responseCheck, (ResponseCheckQuotation.ItemInfo)itemInfoCheck, expressInfo);
		} else {
			doSelectedExpressType((ResponseCheckOrder)responseCheck, (ResponseCheckOrder.ItemInfo)itemInfoCheck, expressInfo);
		}
	}


	//注文確認のストーク
    protected void doSelectedExpressType(ResponseCheckOrder response, ResponseCheckOrder.ItemInfo itemInfoA, ExpressInfo expressInfo) {

        //ストークが選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

    	RequestCheckOrderFromOrder request = new RequestCheckOrderFromOrder();
        request.receptionCode = response.receptionCode;	//○受付番号

		//入力欄情報も送る
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;

        for (ResponseCheckOrder.ItemInfo itemInfo : response.mItemList){

			if (itemInfo.orderItemNo == null) {
				continue;
			}

			if (!itemInfo.orderItemNo.equals(itemInfoA.orderItemNo)) {
				//素通り
				continue;
			}

			//対象商品のストーク情報を更新設定する
            RequestCheckOrderFromOrder.ItemInfo reqestItemInfo = new RequestCheckOrderFromOrder.ItemInfo();
			//必須項目のみに変更した
            reqestItemInfo.orderItemNo = itemInfo.orderItemNo;
			reqestItemInfo.expressType = expressInfo.expressType;
            request.mItemList.add(reqestItemInfo);
        }

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmOrderExpressApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmOrderExpressApi.connect(getContext());
    }


	//見積確認のストーク
    protected void doSelectedExpressType(ResponseCheckQuotation response, ResponseCheckQuotation.ItemInfo itemInfoA, ExpressInfo expressInfo) {

        //ストークが選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

        RequestCheckQuotationFromQuote request = new RequestCheckQuotationFromQuote();
        request.receptionCode = response.receptionCode;	//○受付番号

		//入力欄情報も送る
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;

        for (ResponseCheckQuotation.ItemInfo itemInfo : response.mItemList){

			if (itemInfo.quotationItemNo == null) {
				continue;
			}

			if (!itemInfo.quotationItemNo.equals(itemInfoA.quotationItemNo)) {
				//素通り
				continue;
			}

			//対象商品のストーク情報を更新設定する
            RequestCheckQuotationFromQuote.ItemInfo reqestItemInfo = new RequestCheckQuotationFromQuote.ItemInfo();
			//必須項目のみに変更した
            reqestItemInfo.quotationItemNo = itemInfo.quotationItemNo;
			reqestItemInfo.expressType = expressInfo.expressType;
            request.mItemList.add(reqestItemInfo);
        }

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmQuoteExpressApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmQuoteExpressApi.connect(getContext());
    }


    protected void onSuccessExpressType(DataContainer response) {
	}


	//================
	//================
	//
	private View getChildViewExpress(LinearLayout itemListLayout, int idx, ExpressInfo expressInfo, String expressType, boolean isQuote)
	{
		String str = null;

        final LayoutInflater inflater = getActivity().getLayoutInflater();

            View childView = inflateLayout(inflater, R.layout.list_item_express_type_item, itemListLayout, false);

            TextView tv;

			//連番
            tv = (TextView) childView.findViewById(R.id.textView1);
	        tv.setText(""+ idx);

			//
			final View layoutError = childView.findViewById(R.id.layoutError);

			boolean errorMessageFlag = false;

			if (expressInfo.errorList != null && !expressInfo.errorList.ErrorInfoList.isEmpty()) {
				errorMessageFlag = true;
			}

			//エラー
			if (!errorMessageFlag) {

				layoutError.setVisibility(View.GONE);
			} else {

				layoutError.setVisibility(View.GONE);

				final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
				layoutErrorItemList.removeAllViews();

				//
				int errorCnt = 0;
				for (ErrorList.ErrorInfo errorInfo: expressInfo.errorList.ErrorInfoList) {

					String errorStr = errorInfo.getErrorMessage(getScreenId());
			        if (android.text.TextUtils.isEmpty(errorStr)) {
						continue;
					}

					++errorCnt;


					View subView = inflateLayout(inflater, R.layout.include_express_item_error, layoutErrorItemList, false);

					tv = (TextView) subView.findViewById(R.id.textMessage);
					tv.setText(errorStr);

					layoutErrorItemList.addView(subView);
				}

				if (errorCnt > 0) {
					layoutError.setVisibility(View.VISIBLE);
				}
			}

			//
	        SpannableStringBuilder ssb;
	        SpannableString ss;

			//ストーク
			str = MsmFormat.convertExpressType(getContext(), expressInfo.expressType, true);
			//課題 #4520
	        if (android.text.TextUtils.isEmpty(str)) {
				str = getResourceString(R.string.label_hyphen);   //ハイフン化
			}
			setIncludeItemText(childView.findViewById(R.id.expressType), getResourceString(R.string.confirm_express_type_express) + str, String.valueOf(Character.toChars(0x200B)));


			//出荷日対応
			//出荷日/実働日のパラメータが0で「0日間」と表示されていること
			str = MsmFormat.convertDaysToShipUnit2_00(getContext(), expressInfo.daysToShip, isQuote);

			if (android.text.TextUtils.isEmpty(str)) {

				str = getResourceString(R.string.label_hyphen);	//ハイフン化
			}

			int redId = (isQuote)? R.string.confirm_express_type_days_quote: R.string.confirm_express_type_days;
			//-- ADD NT-LWL 17/08/09 Depo FR -
			if (SubsidiaryCode.isChinese()){
				if ("V0".equals(expressInfo.expressType) && str.equals("0天")){
					str = "当天";
				}
			}
			//-- ADD NT-LWL 17/08/09 Depo TO -
			setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(redId), str);


			//
			if (android.text.TextUtils.isEmpty(expressInfo.expressDeadline)) {

				str = getResourceString(R.string.label_hyphen);	//ハイフン化
			} else {

				str = expressInfo.expressDeadline;
			}

			setIncludeItemText(childView.findViewById(R.id.expressDeadline), getResourceString(R.string.confirm_express_type_dead), str);

			//追加料金
			ssb = new SpannableStringBuilder();
			if (expressInfo.charge == null) {
				str = getResourceString(R.string.label_hyphen);	//ハイフン化
				ssb.append(str);
			} else {
				str = Format.formatWithSign(expressInfo.charge);
				if (AppConfig.getInstance().isDollar()){
					ssb.append(getResourceString(R.string.confirm_express_type_charge_unit_dollar));
				}
				ss = SpannableUtil.newSpannableString(str, 17, true, true);
				ssb.append(ss);
				if (!AppConfig.getInstance().isDollar()) {
					ssb.append(getResourceString(R.string.confirm_express_type_charge_unit));
				}
			}

			setIncludeItemText(childView.findViewById(R.id.charge), getResourceString(R.string.confirm_express_type_charge), ssb);


            //選択ボタン
            tv = (TextView) childView.findViewById(R.id.textView3);
			boolean isSel = false;
			if ( (!android.text.TextUtils.isEmpty(expressType))
				&& (!android.text.TextUtils.isEmpty(expressInfo.expressType)) ) {

	            //同じ物を選択中にする
				//MISUMI_MOBILE_APP-587
				if (STOKE_SPEED_A.equals(expressType)) {
					expressType = STOKE_NORMAL_A;
				}

	            if (expressType.equals(expressInfo.expressType)) {
	                tv.setEnabled(false);
	                tv.setText(R.string.confirm_express_type_selected);
	                childView.setSelected(true);
					isSel = true;
            	}
			}

			if (!isSel && !"1".equals(expressInfo.enableFlag)) {

	            //選択不可なら無効
                tv.setEnabled(false);
                tv.setText(R.string.confirm_express_type_disable);
                childView.setEnabled(false);
			}

		return childView;
	}


	protected void setTextEmptyGone(TextView tv, String str) {
		if (str == null || str.isEmpty()) {
			tv.setText("");
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}


	protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, boolean doHidden) {


		if (android.text.TextUtils.isEmpty(str2)) {

			if (doHidden) {
				subView.setVisibility(View.GONE);
				return;
			}

			str2 = getResourceString(R.string.label_hyphen);	//ハイフン化
		}

		subView.setVisibility(View.VISIBLE);

		((TextView)subView.findViewById(R.id.textView2)).setText(str2);
		((TextView)subView.findViewById(R.id.textView1)).setText(str1);
	}


	//================
	//================
	//注文確認の当日出荷
	//見積確認の当日出荷
    //当日出荷選択ダイアログの表示
    protected void doChangeTodayType(final ResponseCheck responseCheck, final ResponseCheck.ItemInfo itemInfoCheck) {

        mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
            }
        });


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_today_type_layout, null, false);

		String str = null;

        //項目を動的に追加する
		int idx = 1;
        LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);

		//当日出荷選択中？
		String expressType = itemInfoCheck.todayShipSelectedFlag;
		if (!TODAY_SELECT.equals(expressType)) {
			expressType = TODAY_NO_SELECT;
		}

		ArrayList<ExpressInfo> expressList = getTodayList(itemInfoCheck);

        for (final ExpressInfo expressInfo : expressList) {

            final ExpressInfo expressInfoF = expressInfo;

			View childView = getChildViewToday(itemListLayout, idx, expressInfo, expressType, false);
			++idx;

            //選択ボタン
            childView.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelectedTodayTypeCommon(responseCheck, itemInfoCheck, expressInfoF);
                }
            });

            itemListLayout.addView(childView);
        }

        mMessageDialog.show(view, 0, R.string.dialog_button_cancel);
    }


    protected void doSelectedTodayTypeCommon(ResponseCheck responseCheck, ResponseCheck.ItemInfo itemInfoCheck, ExpressInfo expressInfo) {

		if (responseCheck.isQuote()) {
			doSelectedTodayType((ResponseCheckQuotation)responseCheck, (ResponseCheckQuotation.ItemInfo)itemInfoCheck, expressInfo);
		} else {
			doSelectedTodayType((ResponseCheckOrder)responseCheck, (ResponseCheckOrder.ItemInfo)itemInfoCheck, expressInfo);
		}
	}


	//注文確認の当日出荷
    protected void doSelectedTodayType(ResponseCheckOrder response, ResponseCheckOrder.ItemInfo itemInfoA, ExpressInfo expressInfo) {

        //当日出荷が選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

    	RequestCheckOrderFromOrder request = new RequestCheckOrderFromOrder();
		//○受付番号
        request.receptionCode = response.receptionCode;	//○受付番号
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;


        for (ResponseCheckOrder.ItemInfo itemInfo : response.mItemList){

			if (itemInfo.orderItemNo == null) {
				continue;
			}

			if (!itemInfo.orderItemNo.equals(itemInfoA.orderItemNo)) {
				//素通り
				continue;
			}

			//対象商品の当日出荷情報を更新設定する
            RequestCheckOrderFromOrder.ItemInfo reqestItemInfo = new RequestCheckOrderFromOrder.ItemInfo();
			//必須項目のみに変更した
            reqestItemInfo.orderItemNo = itemInfo.orderItemNo;
			reqestItemInfo.todayShipFlag = expressInfo.expressType;
            request.mItemList.add(reqestItemInfo);
        }

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmOrderExpressApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmOrderExpressApi.connect(getContext());
    }

	//見積確認の当日出荷
    protected void doSelectedTodayType(ResponseCheckQuotation response, ResponseCheckQuotation.ItemInfo itemInfoA, ExpressInfo expressInfo) {

        //当日出荷が選択されたとき
		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

    	RequestCheckQuotationFromQuote request = new RequestCheckQuotationFromQuote();
		//○受付番号
        request.receptionCode = response.receptionCode;	//○受付番号
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;

        for (ResponseCheckQuotation.ItemInfo itemInfo : response.mItemList){

			if (itemInfo.quotationItemNo == null) {
				continue;
			}

			if (!itemInfo.quotationItemNo.equals(itemInfoA.quotationItemNo)) {
				//素通り
				continue;
			}

			//対象商品の当日出荷情報を更新設定する
            RequestCheckQuotationFromQuote.ItemInfo reqestItemInfo = new RequestCheckQuotationFromQuote.ItemInfo();
			//必須項目のみに変更した
            reqestItemInfo.quotationItemNo = itemInfo.quotationItemNo;
			reqestItemInfo.todayShipFlag = expressInfo.expressType;
            request.mItemList.add(reqestItemInfo);
        }

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmQuoteExpressApi.setParameter(isFromCart, request, mMessageDialog);
        mCartConfirmQuoteExpressApi.connect(getContext());
    }


	//
	private View getChildViewToday(LinearLayout itemListLayout, int idx, ExpressInfo expressInfo, String expressType, boolean isQuote)
	{
		String str = null;

        final LayoutInflater inflater = getActivity().getLayoutInflater();

            View childView = inflateLayout(inflater, R.layout.list_item_express_type_item, itemListLayout, false);

            TextView tv;

			//連番
            tv = (TextView) childView.findViewById(R.id.textView1);
	        tv.setText(""+ idx);

			//
			final View layoutError = childView.findViewById(R.id.layoutError);

			boolean errorMessageFlag = false;

			if (expressInfo.errorList != null && !expressInfo.errorList.ErrorInfoList.isEmpty()) {
				errorMessageFlag = true;
			}

			//エラー
			if (!errorMessageFlag) {

				layoutError.setVisibility(View.GONE);
			} else {

				layoutError.setVisibility(View.GONE);

				final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
				layoutErrorItemList.removeAllViews();

				//
				int errorCnt = 0;
				for (ErrorList.ErrorInfo errorInfo: expressInfo.errorList.ErrorInfoList) {

					String errorStr = errorInfo.getErrorMessage(getScreenId());
			        if (android.text.TextUtils.isEmpty(errorStr)) {
						continue;
					}

					++errorCnt;


					View subView = inflateLayout(inflater, R.layout.include_express_item_error, layoutErrorItemList, false);

					tv = (TextView) subView.findViewById(R.id.textMessage);
					tv.setText(errorStr);

					layoutErrorItemList.addView(subView);
				}

				if (errorCnt > 0) {
					layoutError.setVisibility(View.VISIBLE);
				}
			}

			//
	        SpannableStringBuilder ssb;
	        SpannableString ss;

			//当日出荷
			if (TODAY_SELECT.equals(expressInfo.expressType)) {

				str = getResourceString(R.string.today_ship_dialog_select_1);		//リソース化
			} else {

				str = getResourceString(R.string.today_ship_dialog_select_2);		//リソース化
			}
			setIncludeItemText(childView.findViewById(R.id.expressType), str, "");


			//出荷日対応
			if (TODAY_NO_SELECT.equals(expressInfo.expressType)) {
				//標準出荷日のパラメータが99で「都度お見積り」と表示されていること
				str = MsmFormat.convertDaysToShipUnit3_99(getContext(), expressInfo.daysToShip, isQuote);
			} else {
				str = MsmFormat.convertDaysToShipUnit3(getContext(), expressInfo.daysToShip, isQuote);
			}

			if (android.text.TextUtils.isEmpty(str)) {

				str = getResourceString(R.string.label_hyphen);	//ハイフン化
			}

			int redId = (isQuote)? R.string.confirm_express_type_days_quote: R.string.confirm_express_type_days;

			setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(redId), str);


			//
			if (android.text.TextUtils.isEmpty(expressInfo.expressDeadline)) {

				str = getResourceString(R.string.label_hyphen);	//ハイフン化
			} else {

				str = expressInfo.expressDeadline;
			}

			setIncludeItemText(childView.findViewById(R.id.expressDeadline), getResourceString(R.string.confirm_express_type_dead), str);

			//追加料金
			ssb = new SpannableStringBuilder();
			if (expressInfo.charge == null) {
				str = getResourceString(R.string.label_hyphen);	//ハイフン化
				ssb.append(str);
			} else {
				str = Format.formatWithSign(expressInfo.charge);
				if (AppConfig.getInstance().isDollar()){
					ssb.append(getResourceString(R.string.confirm_express_type_charge_unit_dollar));
				}
				ss = SpannableUtil.newSpannableString(str, 17, true, true);
				ssb.append(ss);
				if (!AppConfig.getInstance().isDollar()) {
					ssb.append(getResourceString(R.string.confirm_express_type_charge_unit));
				}
			}
			setIncludeItemText(childView.findViewById(R.id.charge), getResourceString(R.string.confirm_express_type_charge), ssb);


            //選択ボタン
            tv = (TextView) childView.findViewById(R.id.textView3);
			boolean isSel = false;
			if ( (!android.text.TextUtils.isEmpty(expressType))
				&& (!android.text.TextUtils.isEmpty(expressInfo.expressType)) ) {

	            //同じ物を選択中にする
	            if (expressType.equals(expressInfo.expressType)) {
	                tv.setEnabled(false);
	                tv.setText(R.string.confirm_express_type_selected);
	                childView.setSelected(true);
					isSel = true;
            	}
			}

			if (!isSel && !"1".equals(expressInfo.enableFlag)) {

	            //選択不可なら無効
                tv.setEnabled(false);
                tv.setText(R.string.confirm_express_type_disable);
                childView.setEnabled(false);
			}

		return childView;
	}


	//当日出荷用選択リスト
	private ArrayList<ExpressInfo> getTodayList(ResponseCheck.ItemInfo itemInfoCheck) {

		// List expressList				//　ストーク情報リスト
		ArrayList<ExpressInfo> expressList = new ArrayList<>();
		ExpressInfo expressInfoN = new ExpressInfo();
		expressInfoN.expressType = TODAY_NO_SELECT;
		expressInfoN.enableFlag = "1";
		expressInfoN.daysToShip = itemInfoCheck.standardDaysToShip; //素の日数
		expressInfoN.charge = 0.0;
		expressList.add(expressInfoN);

		expressInfoN = new ExpressInfo();
		expressInfoN.expressType = TODAY_SELECT;
		expressInfoN.enableFlag = "1";
		expressInfoN.daysToShip = 0;
		//中国は固定で17:00、日本は18:00固定
//		expressInfoN.expressDeadline = getResourceString(R.string.today_express_deadline);
		String todayShipDeadline = itemInfoCheck.todayShipDeadline;
		if (android.text.TextUtils.isEmpty(todayShipDeadline)) {
			todayShipDeadline = "";
		}
		expressInfoN.expressDeadline = todayShipDeadline;
		expressInfoN.charge = 0.0;
		expressList.add(expressInfoN);

		return expressList;
	}


	//アンフィット確認ダイアログの表示処理
    protected void doDispComfirmDialog(final String unfitConfirmType, ResponseCheck responseCheck) {

        hideKeyboard();

//		final String unfitConfirmType = responseCheck.unfitConfirmType;

		//アンフィット確認ダイアログの表示処理
		if ("1".equals(unfitConfirmType)) {
			//素通し可？
			doSubmit();
			return;
		}

		//見積差異
		final boolean isQuote = responseCheck.isQuote();


		//アンフィット確認ダイアログの表示処理
        mMessageDialog	= new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){

					doSubmitUnfit(isQuote, unfitConfirmType, which);
                } else
                if (which == DialogInterface.BUTTON_NEUTRAL){

					doSubmitUnfit(isQuote, unfitConfirmType, which);
                } else {

					//閉じるだけ
					mMessageDialog.hide();
                }
            }
        }).setAutoClose(false);


		final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_unfit_layout, null, false);

		ListView listView = (ListView)view.findViewById(R.id.listView);

        View viewH = inflateLayout(inflater, R.layout.dialog_unfit_layout_h, listView, false);

		listView.addHeaderView(viewH, null, false);


		int orderableCount = 0;

		//見積差異
		if (isQuote) {

			//見積{{{
			TextView textUnfitType = (TextView)viewH.findViewById(R.id.textUnfitType);
			textUnfitType.setText(R.string.unfit_type_dialog_titlle_quote);
			//見積}}}

			//見積{{{
			MyUnfitAdapterCom listAdapter = new MyUnfitAdapterCom(getContext(), R.layout.list_item_unfit_item, new ArrayList<ResponseCheck.ItemInfo>(), getScreenId());
			listView.setAdapter(listAdapter);

			//アンフィットの商品
			//ダイアログに表示する中身を作成
			for (ResponseCheckQuotation.ItemInfo itemInfo: ((ResponseCheckQuotation)responseCheck).mItemList) {

				if ("0".equals(itemInfo.unfitFlag)) {
					//アンフィット素通し可
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

			listAdapter.notifyDataSetChanged();
			//見積}}}

		} else {

			//注文{{{
			//文言分け
			if ("3".equals(unfitConfirmType)) {
	            TextView textView = (TextView) viewH.findViewById(R.id.textMessage);
	            textView.setText(getResourceString(R.string.unfit_type_dialog_info_3));
			}
			if ("4".equals(unfitConfirmType)) {
	            TextView textView = (TextView) viewH.findViewById(R.id.textMessage);
	            textView.setText(getResourceString(R.string.unfit_type_dialog_info_4));
			}
			//注文}}}

			//注文{{{
			MyUnfitAdapterCom listAdapter = new MyUnfitAdapterCom(getContext(), R.layout.list_item_unfit_item, new ArrayList<ResponseCheck.ItemInfo>(), getScreenId());
			listView.setAdapter(listAdapter);

			//アンフィットの商品
			//ダイアログに表示する中身を作成
			for (ResponseCheckOrder.ItemInfo itemInfo: ((ResponseCheckOrder)responseCheck).mItemList) {

				if ("0".equals(itemInfo.unfitFlag)) {
					//アンフィット素通し可
					continue;
				}

				++orderableCount;

				listAdapter.add(itemInfo);
			}

			listAdapter.notifyDataSetChanged();
			//注文}}}

		}


		{
			String str = ""+ orderableCount;
	        SpannableStringBuilder ssb = new SpannableStringBuilder();

	        ssb.append(getResourceString(R.string.unfit_type_dialog_list_title));
			SpannableString ss = SpannableUtil.newSpannableString(str, 15, true, true);
	        ssb.append(ss);

	        ssb.append(getResourceString(R.string.unfit_type_dialog_list_title_unit));

			TextView tv = (TextView) viewH.findViewById(R.id.textCount);
			tv.setText(ssb);
		}


		//見積、注文共通の異常時
		if (android.text.TextUtils.isEmpty(unfitConfirmType)) {
			mMessageDialog.showCart(view, 0, R.string.unfit_type_dialog_no);
		}

		//見積、注文共通
		if ("2".equals(unfitConfirmType)) {
			mMessageDialog.showCart(view, R.string.unfit_type_dialog_yes_quote, R.string.unfit_type_dialog_no);
		}

		//見積差異
		if (isQuote) {
		} else {

			//注文
			if ("3".equals(unfitConfirmType)) {
				mMessageDialog.showCart(view, R.string.unfit_type_dialog_yes_order, R.string.unfit_type_dialog_no);
			}

			//ボタン 3個
			if ("4".equals(unfitConfirmType)) {
				mMessageDialog.showCart(view, R.string.unfit_type_dialog_yes_order, R.string.unfit_type_dialog_no, R.string.unfit_type_dialog_yes_quote);
			}
		}
    }



	//430確認ダイアログの表示処理
    protected void doDisp430Dialog(ErrorList errorList) {

        hideKeyboard();

		//null避け
		if (errorList == null) {
			errorList = new ErrorList();
		}
		if (errorList.ErrorInfoList == null) {
			errorList.ErrorInfoList = new ArrayList<>();
		}

		//430確認ダイアログの表示処理
        mMessageDialog	= new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {

				doSubmit430();
            }
        }).setAutoClose(false);


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_so430_layout, null, false);

        //項目を動的に追加する
        LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);

		for (ErrorList.ErrorInfo errorInfo: errorList.ErrorInfoList) {

            View childView = inflateLayout(inflater, R.layout.include_item_text_so430, itemListLayout, false);

 			String str = errorInfo.errorMessage;

			//
			TextView tv;
			tv = (TextView) childView.findViewById(R.id.textMessage);
			setTextEmptyGone(tv, str);

			//
            itemListLayout.addView(childView);
        }

        mMessageDialog.show(view, 0, R.string.so430_dialog_button);

    }


    protected void doSubmit430() {}

    protected abstract void doSubmit();
    protected abstract void doSubmitQuote();

	//アンフィット解決経由の時
    protected abstract void doSubmitUnfit();
    protected abstract void doSubmitUnfitQuote();


	protected void doSubmitUnfit(boolean isQuote, String unfitConfirmType, int which) {

		//見積差異
		if (isQuote) {

			//
			if ("2".equals(unfitConfirmType)) {
	            if (which == DialogInterface.BUTTON_POSITIVE){
					//見積
					doSubmitUnfit();
	            }
			}

		} else {

			//
			if ("2".equals(unfitConfirmType)) {
	            if (which == DialogInterface.BUTTON_POSITIVE){
					//見積
					doSubmitUnfitQuote();
	            }
			}

			//注文
			if ("3".equals(unfitConfirmType)) {
	            if (which == DialogInterface.BUTTON_POSITIVE){
					//注文
					doSubmitUnfit();
	            }
			}

			//ボタン 3個
			if ("4".equals(unfitConfirmType)) {

	            if (which == DialogInterface.BUTTON_POSITIVE){
					//注文
					doSubmitUnfit();

	            } else
	            if (which == DialogInterface.BUTTON_NEUTRAL){
					//見積
					doSubmitUnfitQuote();
	            }
			}

		}
	}


	//見積確認のストークＡ早ラジオ
    protected void doSelectedExpressA(ResponseCheckQuotation response, boolean expressADiscountFlag) {

		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

        RequestCheckQuotationFromQuote request = new RequestCheckQuotationFromQuote();

		//○受付番号
        request.receptionCode = response.receptionCode;
		//入力欄情報も送る
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;

		//expressADiscountFlag (早割Aフラグ)情報を更新設定
		if (expressADiscountFlag) {
			request.expressADiscountFlag = "1";
		} else {
			request.expressADiscountFlag = "0";
		}

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmQuoteApi.setParameter(isFromCart, request, null);
        mCartConfirmQuoteApi.connect(getContext());
    }


	//注文確認の出荷オプションラジオ
    protected void doSelectedShipOption(ResponseCheckOrder response, boolean shipOptionFlag) {

		//ここでリクエストを生成する
		boolean isFromCart = response.isFromCart();

        RequestCheckOrderFromOrder request = new RequestCheckOrderFromOrder();

		//○受付番号
        request.receptionCode = response.receptionCode;
		//入力欄情報も送る
		request.billingUserName = response.purchaser.userName;
		request.billingDepartmentName = response.purchaser.userDepartmentName;
		request.receiverUserName = response.receiver.receiverUserName;
		request.receiverDepartmentName = response.receiver.receiverDepartmentName;

		//shipOption (出荷オプション)情報を更新設定
		if (shipOptionFlag) {
			request.shipOption = "C";
		} else {
			request.shipOption = "P";
		}

		//-- ADD NT-LWL 16/12/05 AliPay Payment FR -
		if (SubsidiaryCode.isChinese()&&selectPaymentGroup!=null){
			request.paymentGroup=selectPaymentGroup.paymentGroup;
		}
		// -- ADD NT-LWL 16/12/05 AliPay Payment TO -
		mCartConfirmOrderApi.setParameter(isFromCart, request, null);
        mCartConfirmOrderApi.connect(getContext());
    }
	//-- ADD NT-LWL 17/07/14 3小时闪达 FR -
	//判断是否是3小时闪达对象中 是返回1  不是返回0
	private String immediateDeliveryFlag(String immediateDeliveryFlag){
		String returnFlag = "0";
		if(!android.text.TextUtils.isEmpty(immediateDeliveryFlag)&&immediateDeliveryFlag.equals("1")){
			returnFlag = "1";
		}
		return returnFlag;
	}
	//-- ADD NT-LWL 17/07/14 3小时闪达 TO -

}


