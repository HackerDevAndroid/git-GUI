package jp.co.misumi.misumiecapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AliPaymentInfo;
import jp.co.misumi.misumiecapp.data.ResponseConfirm;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

/**
 * 注文完了画面
 */
//TODO:表示項目の内容が暫定
//TODO:レスポンス内容から画面を作る（画面表示）
//TODO:画面下部の「商品変更～」「電話」「キャンセルポリシー」の処理
public class OrderCompleteFragment extends CartCompleteFragment {

    private final boolean mIsIncludeTax;
	//-- ADD NT-SLJ 16/10/25 AliPay Payment FR -
	private MyOnlinePaymentApi mMyOnlinePaymentApi;
	//-- ADD NT-SLJ 16/10/25 AliPay Payment TO -
    public OrderCompleteFragment() {
		//-- ADD NT-SLJ 16/10/25 AliPay Payment FR -
		mMyOnlinePaymentApi = new MyOnlinePaymentApi();
		//-- ADD NT-SLJ 16/10/25 AliPay Payment TO -
		mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
    }

    @Override
	protected int getLayoutId() {

		return R.layout.fragment_order_complete;
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
    }


	private ResponseConfirm getData() {

		return (ResponseConfirm)getDataContainer();
	}


    @Override
	protected void makeDataView(View rootView) {

		//ResponseConfirmOrder getData()で画面を作る
		ResponseConfirm response = getData();
		//進行ゲージ
		TextView textProgress1 = (TextView) rootView.findViewById(R.id.textProgress1);
		TextView textProgress2 = (TextView) rootView.findViewById(R.id.textProgress2);
		TextView textProgress3 = (TextView) rootView.findViewById(R.id.textProgress3);
		if (response.isFromCart()) {

			textProgress1.setText(getResourceString(R.string.progress_order_from_cart));
		} else {

			textProgress1.setText(getResourceString(R.string.progress_order_from_quote));
		}

		textProgress2.setText(getResourceString(R.string.progress_order_2));
		textProgress3.setSelected(true);
		textProgress3.setText(getResourceString(R.string.progress_order_3));


		//
		TextView buttonClose = (TextView) rootView.findViewById(R.id.buttonClose);
		if (response.isFromCart()) {

			buttonClose.setText(getResourceString(R.string.confirm_back_from_cart));
		} else {

			buttonClose.setText(getResourceString(R.string.confirm_back_from_quote));
		}


		//
		String str;

		String itemCountMisumi = null;
		String totalPriceMisumi = null;
		boolean isHyphenPrice = false;
		if (response.itemCountInChecking == null || response.itemCountInChecking <= 0) {
		} else {

			itemCountMisumi = String.format(getResourceString(R.string.order_complete_misumi_check_str), Format.formatCount(response.itemCountInChecking));

			totalPriceMisumi = getResourceString(R.string.order_complete_price_checking);
		}

		setIncludeItemText(rootView.findViewById(R.id.infoSlipNo), getResourceString(R.string.order_complete_slip_no), response.infoSlipNo, null);


		//
		if (response.itemCount == null) {

			str = getResourceString(R.string.label_hyphen);	//ハイフン化
		} else {

			str = Format.formatCount(response.itemCount);
			str += getResourceString(R.string.order_complete_count_unit);
		}
		setIncludeItemText(rootView.findViewById(R.id.itemCount), getResourceString(R.string.order_complete_item_count), str, itemCountMisumi);


		//
		if (response.totalPrice == null || response.totalPrice.equals(0.0)) {

			str = getResourceString(R.string.label_hyphen);	//ハイフン化
			isHyphenPrice = true;
		} else {

			str = Format.formatAmount(response.totalPrice);

			// 通貨を追加
			if ( SubsidiaryCode.isJapan() ){
				str += getResourceString(R.string.order_complete_total_price_unit_yen);
			}else{
				if (AppConfig.getInstance().isDollar()){
					str = getResourceString(R.string.order_complete_total_price_unit_dollar) + str;
				}else{
					str += getResourceString(R.string.order_complete_total_price_unit_gen);
				}
			}
		}

        if (SubsidiaryCode.isJapan() && mIsIncludeTax) {

			//日本非表示
			rootView.findViewById(R.id.totalPrice).setVisibility(View.GONE);
		} else {

			if (isHyphenPrice){
				setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.order_complete_total_price), str, null);
			}else {
				setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.order_complete_total_price), str, totalPriceMisumi);
			}
		}

		//
		isHyphenPrice = false;
		if (SubsidiaryCode.isJapan()) {

            if (!mIsIncludeTax){
				//日本非表示
				rootView.findViewById(R.id.totalPriceIncludingTax).setVisibility(View.GONE);
			} else {

				//合計金額(税込)
				if (response.totalPriceIncludingTax == null || response.totalPriceIncludingTax.equals(0.0)) {

					str = getResourceString(R.string.label_hyphen);	//ハイフン化
					isHyphenPrice = true;
				} else {

					str = Format.formatAmount(response.totalPriceIncludingTax);
					str += getResourceString(R.string.order_complete_total_price_unit_yen);

				}

				if (isHyphenPrice){
					setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.order_complete_total_price_tax), str, null);
				}else {
					setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.order_complete_total_price_tax), str, totalPriceMisumi);
				}
			}

		} else {

			//中国版は表示

			//合計金額(税込)
			if (response.totalPriceIncludingTax == null || response.totalPriceIncludingTax.equals(0.0)) {

				str = getResourceString(R.string.label_hyphen);	//ハイフン化
				isHyphenPrice = true;
			} else {


				str = Format.formatAmount(response.totalPriceIncludingTax);

				if (AppConfig.getInstance().isDollar()){
					str = getResourceString(R.string.order_complete_total_price_unit_dollar) + str;
				}else{
					str += getResourceString(R.string.order_complete_total_price_unit_gen);
				}

				//str += getResourceString(R.string.order_complete_total_price_tax_unit);
			}
			if (isHyphenPrice){
				setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.order_complete_total_price_tax), str, null);
			}else {
				setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.order_complete_total_price_tax), str, totalPriceMisumi);
			}

		}


		//
		if (android.text.TextUtils.isEmpty(response.infoDatetime)) {

			str = getResourceString(R.string.label_hyphen);	//ハイフン化
		} else {


			str = response.infoDatetime;
		}
		setIncludeItemText(rootView.findViewById(R.id.infoDatetime), getResourceString(R.string.order_complete_date), str, null);

		//-- UPD NT-LWL 16/11/15 AliPay Payment FR -
		/*if (SubsidiaryCode.isJapan()){
			rootView.findViewById(R.id.textDeadlineInfo).setVisibility(View.GONE);
		}else{
			TextView deadLine = (TextView) rootView.findViewById(R.id.textDeadlineInfo);
			String paymentType = AppConfig.getInstance().getPaymentType();

			deadLine.setVisibility(View.GONE);
			if ("ADV".equals(AppConfig.getInstance().getSettlementType())){
				//前金 + 振込 + ステータス＝入金待ち
				if ( "10".equals(paymentType) ){
					String deadlineInfo = getResourceString(R.string.order_detail_deadline_info1) + "\n"
							+ getResourceString(R.string.order_detail_deadline_info2) + "\n"
							+ getResourceString(R.string.order_detail_deadline_info3);
					deadLine.setVisibility(View.VISIBLE);
					deadLine.setText(deadlineInfo);
				}
			}
		}*/
		if (SubsidiaryCode.isJapan()){
			rootView.findViewById(R.id.textDeadlineInfo).setVisibility(View.GONE);
			rootView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
			rootView.findViewById(R.id.paymentType).setVisibility(View.GONE);
		}else{

			AppLog.d("login.settlementType:" + AppConfig.getInstance().getSettlementType());
			AppLog.d("paymentDeadlineDateTime:"+response.paymentDeadlineDateTime);
			AppLog.d("paymentGroup:"+response.paymentGroup);
			AppLog.d("paymentGroupName:"+response.paymentGroupName);
			AppLog.d("itemCountInChecking:"+response.itemCountInChecking);

			TextView deadLine = (TextView) rootView.findViewById(R.id.textDeadlineInfo);

			deadLine.setVisibility(View.GONE);
			//状态为adv 显示支付方式
			if ("ADV".equals(AppConfig.getInstance().getSettlementType())){
				rootView.findViewById(R.id.paymentType).setVisibility(View.VISIBLE);
				//线上支付限制订单有效期提示
				if(response.paymentGroup!=null&&"1".equals(response.paymentGroup)){
					String paymentDeadlineDateTime = response.paymentDeadlineDateTime;
					//米思米确认中件数小于等于0 显示有效期
					if (paymentDeadlineDateTime != null&&response.itemCountInChecking<=0){
						if(paymentDeadlineDateTime.isEmpty()){
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
							AppLog.d("systemTime:"+sDateFormat.format(nowdate));
							d = sDateFormat.parse(response.paymentDeadlineDateTime);
							compareFlag = d.before(nowdate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//有效期未过期显示支付按钮
						if(!compareFlag){
								rootView.findViewById(R.id.buttonPay).setVisibility(View.VISIBLE);
								final ResponseConfirm finalResponse = response;
								rootView.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										GoogleAnalytics.sendAction(mTracker,getSaicataId(),GoogleAnalytics.CATEGORY_ALIPAY,getResources().getString(R.string.order_list_go_to_pay));
										mMyOnlinePaymentApi.setParameter("1", finalResponse.infoSlipNo, "60");
										mMyOnlinePaymentApi.connect(getContext());
									}
								});
						}
					}
				}//支付类型为银行转账 paymentGroup等于2 并且确认中件数小于等于0 银行转账说明
				else if(response.paymentGroup!=null&&"2".equals(response.paymentGroup)&&response.itemCountInChecking<=0){
					String deadlineInfo = getResourceString(R.string.order_detail_deadline_info1) + "\n"
							+ getResourceString(R.string.order_detail_deadline_info2) + "\n"
							+ getResourceString(R.string.order_detail_deadline_info3);
					deadLine.setVisibility(View.VISIBLE);
					deadLine.setText(deadlineInfo);
				}
				else{}
				//支付方式显示
				if(response.paymentGroupName!=null&&!response.paymentGroupName.isEmpty()){
					setIncludeItemText(rootView.findViewById(R.id.paymentType), getResourceString(R.string.order_list_payment_type),
									   response.paymentGroupName, null);
				}else{
					setIncludeItemText(rootView.findViewById(R.id.paymentType), getResourceString(R.string.order_list_payment_type),
									   getResourceString(R.string.label_hyphen), null);
				}

			}else{
				rootView.findViewById(R.id.paymentType).setVisibility(View.GONE);
			}
		}
		//-- UPD NT-LWL 16/11/15 AliPay Payment TO -

		//素通しエリアの表示判定
		if (response.itemCountInChecking == null || response.itemCountInChecking <= 0) {
            rootView.findViewById(R.id.misumiCheckLayout).setVisibility(View.GONE);
        }else{
            rootView.findViewById(R.id.misumiCheckLayout).setVisibility(View.VISIBLE);

            ((TextView)rootView.findViewById(R.id.misumiCheckLayout).findViewById(R.id.textMessage)).setText(R.string.order_complete_confirm_info_memo3);

		}


		//画面下部の「商品変更～」「電話」「キャンセルポリシー」のリンク
		TextView textCancelPolicy = (TextView)rootView.findViewById(R.id.textCancelPolicy);

/*
		String text = textCancelPolicy.getText().toString();
		SparseArray<String> links = new SparseArray<String>();
		//マッチした文字列
		links.append(0, "商品変更または、キャンセル");
		links.append(1, "00-0000-0000");				//
		links.append(2, "キャンセルポリシー");			//

		ClickableText.OnClickListener listener = new ClickableText.OnClickListener() {
		    @Override
		    public void onLinkClick(int textId) {

				showToast("TODO:商品変更または、キャンセルをクリック ID="+ textId);
		    }
		};

		textCancelPolicy.setText(ClickableText.getClickableText(text, links, listener));
		textCancelPolicy.setMovementMethod(LinkMovementMethod.getInstance());
//		textCancelPolicy.setFocusable(true);
//		textCancelPolicy.setClickable(true);
//		textCancelPolicy.setLongClickable(true);
*/

		//URLが有る場合
		final String strUrl = AppConfig.getInstance().getUrlList().cancelOrderUrl;
        textCancelPolicy.setText(R.string.order_complete_cancel_info_memo);
		if (android.text.TextUtils.isEmpty(strUrl)) {
		} else {

	        Pattern pattern = Pattern.compile((String)getText(R.string.order_complete_cancel_info_memo_link));

			Linkify.TransformFilter filter = new Linkify.TransformFilter() {
			    @Override
			    public String transformUrl(Matcher match, String url) {
			        return strUrl;
			    }
			};

			Linkify.addLinks(textCancelPolicy, pattern, strUrl, null, filter);
		}

	}


	/**
	 * getScreenId
	 *
	 * @return
	 */
	@Override
	public String getScreenId() {
		return ScreenId.OrderComplete;
	}


	//サイカタ
	protected String getSaicataId() {
		return jp.co.misumi.misumiecapp.SaicataId.OrderComplete;
	}


	//-- UPD NT-LWL 16/11/15 AliPay Payment FR -
	private class MyOnlinePaymentApi extends ApiAccessWrapper {

		String mcallerPage;
		String morderSlipNo;
		String mpaymentType;

		@Override
		protected String getScreenId() {
			return OrderCompleteFragment.this.getScreenId();
		}

		@Override
		protected boolean getMethod() {
			return API_GET;
		}

		public void setParameter(String callerPage, String orderSlipNo,String paymentType) {

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
					Intent intent = new Intent();
					intent.putExtra("flag","OrderComplete");
					response.isFromComplete = true;
					Bundle bundle = new Bundle();
					bundle.putSerializable("online_payment", response);
					intent.putExtras(bundle);
					intent.setClass(getActivity(),MainActivity.class);
					startActivity(intent);
					mParent.finish();
					break;

				default:
					showErrorMessage(response.errorList);
					break;
			}
		}

	}

	@Override
	public void onPause() {
		mMyOnlinePaymentApi.close();
		super.onPause();
	}

	//-- UPD NT-LWL 16/11/15 AliPay Payment TO -
}


