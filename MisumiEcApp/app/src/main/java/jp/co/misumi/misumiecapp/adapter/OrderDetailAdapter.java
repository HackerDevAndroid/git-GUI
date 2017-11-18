package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseGetOrderDetail;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class OrderDetailAdapter extends CommonAdapter<ResponseGetOrderDetail.ItemInfo> {

	/** LayoutInflator. */
	private final LayoutInflater mInflater;

	/** 行レイアウトリソースID. */
	private final int mResource;
	private final OnItemClickListener	mOnItemClickListener;

	private String mScreenId;

	//	12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
	private final boolean mIsCodUser;

	public interface OnItemClickListener {
	    void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
	    void onItemAddMy(ArrayAdapter<?> adapter, View view, int position, long id);
	    void onItemAddCart(ArrayAdapter<?> adapter, View view, int position, long id);
	}


	/**
	 * コンストラクタ.
	 * 
	 * @param context
	 *            コンテキスト
	 * @param resource
	 *            行レイアウトリソースID
	 * @param objects
	 *            一覧データ
	 */
	public OrderDetailAdapter(Context context, int resource, List<ResponseGetOrderDetail.ItemInfo> objects, OnItemClickListener onItemClickListener, String screenId) {
		super(context, resource, objects);

		mScreenId = screenId;

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;

		mOnItemClickListener = onItemClickListener;

//		12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
		mIsCodUser = AppConfig.getInstance().isCodUser();

//		mMoneyFormat = context.getString(R.string.money_format);

//		int resId = R.string.format_currency_none; //通貨単位無し
//		String currencyCode = AppConfig.getInstance().getCurrencyCode();
//		if (AppConst.CURRENCY_CODE_JPY.equals(currencyCode)) {
//			resId = R.string.format_currency_jpy;
//		} else if (AppConst.CURRENCY_CODE_RMB.equals(currencyCode)) {
//			resId = R.string.format_currency_rmb;
//		} else if (AppConst.CURRENCY_CODE_USD.equals(currencyCode)) {
//			resId = R.string.format_currency_usd;
//		}
//		mCurrencyFormat = getResourceString(resId);



	}


	/* (非 Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(mResource, parent, false);
			ViewUtil.setSplitMotionEventsToAll(convertView);

			holder = new ViewHolder();
			holder.statasView = (ViewGroup) convertView.findViewById(R.id.layoutError);
			holder.partNumber = (TextView) convertView.findViewById(R.id.orderDetailPartNumber);
			holder.productName = (TextView) convertView.findViewById(R.id.orderDetailProductName);
			holder.brandName = (TextView) convertView.findViewById(R.id.orderDetailBrandName);
			holder.productImage = (ImageView) convertView.findViewById(R.id.orderDetailProductImage);
            holder.sale = (TextView) convertView.findViewById(R.id.textSale);
			holder.pv = convertView.findViewById(R.id.progressView);
//			holder.standardunitPrice = (TextView) convertView.findViewById(R.id.orderDetailStandardUnitPrice);
			holder.unitPrice = (TextView) convertView.findViewById(R.id.orderDetailUnitPrice);
			holder.quantity = (TextView) convertView.findViewById(R.id.orderDetailQuantity);
            holder.pack = (TextView) convertView.findViewById(R.id.textPerPack);
			holder.shipDateTime = (TextView) convertView.findViewById(R.id.orderDetailShipDateTime);
			holder.expressType = (TextView) convertView.findViewById(R.id.textEmerg);
			holder.totalPrice = (TextView) convertView.findViewById(R.id.orderDetailTotalPrice);
            holder.totalPriceWithTax = (TextView) convertView.findViewById(R.id.orderDetailTotalPriceWithTax);
            holder.totalPriceWithTaxlabel = (ViewGroup) convertView.findViewById(R.id.orderDetailTotalPriceWithTaxlabel);
			holder.status = (TextView) convertView.findViewById(R.id.orderDetailStatus);
			holder.invoiceNo = (TextView) convertView.findViewById(R.id.orderDetailInvoiceNo);
			holder.addMyPart = (ViewGroup) convertView.findViewById(R.id.orderDetailAddMyPart);
			holder.addCart = (ViewGroup) convertView.findViewById(R.id.orderDetailAddCart);

            //ミスミ受付番号追加
            holder.misumiNo = (TextView) convertView.findViewById(R.id.orderDetailMisumiNo);

			if (SubsidiaryCode.isChinese()){
				convertView.findViewById(R.id.misumiNoArea).setVisibility(View.GONE);
			}else{
				convertView.findViewById(R.id.misumiNoArea).setVisibility(View.VISIBLE);
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 1行分のアイテムデータを取得
		final ResponseGetOrderDetail.ItemInfo itemInfo = getItem(position);
        //エラーリスト追加
        makeChildView(convertView, itemInfo, position);
		//型番
		holder.partNumber.setText(itemInfo.partNumber);
		//商品名
		holder.productName.setText(itemInfo.productName);
		//ブランド名
		holder.brandName.setText(itemInfo.brandName);
        //SALEアイコン
        {
			String campainEndDate = MsmFormat.convertCampainEndDate(getContext(), itemInfo.campainEndDate);
			boolean saleEnable = true;
//			if (itemInfo.unitPrice == null || itemInfo.unitPrice == 0){
//				saleEnable = false;
//			}
			if (android.text.TextUtils.isEmpty(campainEndDate)){
				saleEnable = false;
			}
            if (saleEnable) {
                holder.sale.setText(campainEndDate);
                holder.sale.setVisibility(View.VISIBLE);
            } else {
                holder.sale.setVisibility(View.GONE);
            }
        }
		//単価
        {
            SpannableStringBuilder ssbUnitPrice = new SpannableStringBuilder();
            if (itemInfo.unitPrice == null) {

				ssbUnitPrice.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                if (SubsidiaryCode.isJapan()) {
					ssbUnitPrice.append(Format.formatAmountWithUnit(itemInfo.unitPrice, getResourceString(R.string.order_detail_unit_price_unit_main)));
                } else {
					ssbUnitPrice.append(Format.formatAmountWithUnit(itemInfo.unitPrice, getResourceString(R.string.order_detail_unit_price_unit_main_CHN)));
                }
            }
            holder.unitPrice.setText(ssbUnitPrice);
        }
		//数量
		holder.quantity.setText(convertQuantity(itemInfo.quantity));

        //入り数
        {
            String str = null;

			if (itemInfo.piecesPerPakage != null && itemInfo.piecesPerPakage != 0) {

                str = String.format(getResourceString(R.string.order_detail_pack_quantity), Format.formatCount(itemInfo.piecesPerPakage));

            }

			holder.pack.setText(str);
        }

        //ミスミ受付番号追加
        {
            String misumiNo;
            if (itemInfo.misumiNo != null && !(itemInfo.misumiNo.isEmpty())){
                //正常
                misumiNo = itemInfo.misumiNo;
            } else {
                //無しor空の場合はハイフン表示
                misumiNo = getResourceString(R.string.label_hyphen);
            }
            holder.misumiNo.setText(misumiNo);
        }

		//出荷日
		holder.shipDateTime.setText(convertShipDateTime(itemInfo.shipDateTime));

		{
			//緊急出荷サービス
	//		holder.expressType.setText(convertExpressType(itemInfo.expressType));

	//		12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
			if (mIsCodUser){
				holder.expressType.setVisibility(View.GONE);
			} else {
				//課題 #4520
				String str = convertExpressType(itemInfo.expressType);
				if (android.text.TextUtils.isEmpty(str)) {
					str = getResourceString(R.string.label_hyphen);   //ハイフン化
				}

				holder.expressType.setText(getResourceString(R.string.order_detail_emarg_main) + str);
			}
		}

		//小計
        if (SubsidiaryCode.isJapan()) {

            //日本外税
            holder.totalPriceWithTaxlabel.setVisibility(View.GONE);
            SpannableStringBuilder ssbTotalPrice = new SpannableStringBuilder();

            if (itemInfo.totalPrice == null) {

				ssbTotalPrice.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

				ssbTotalPrice.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.order_detail_unit_price_unit_main)));
            }

            holder.totalPrice.setText(ssbTotalPrice);
        } else {

            //中国外税
            SpannableStringBuilder ssbTotalPriceCHN = new SpannableStringBuilder();


            if (itemInfo.totalPrice == null) {

				ssbTotalPriceCHN.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

				ssbTotalPriceCHN.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.order_detail_unit_price_unit_main_CHN)));
            }

            holder.totalPrice.setText(ssbTotalPriceCHN);


            //中国税込み
            SpannableStringBuilder ssbTax = new SpannableStringBuilder();


            if (itemInfo.totalPriceWithTax == null) {

				ssbTax.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

				ssbTax.append(Format.formatAmountWithUnit(itemInfo.totalPriceWithTax, getResourceString(R.string.quote_hist_detail_subtotal_unit_main_tax)));
            }

            holder.totalPriceWithTax.setText(ssbTax);
        }
		//ステータス
        {
            SpannableStringBuilder ssbStatus = new SpannableStringBuilder();
            String strStatus;
//			ssb.append(getResourceString(R.string.quote_hist_detail_status_main));

            strStatus = convertStatusOrder(itemInfo.status);
            SpannableString ssStatus = new SpannableString(strStatus);

            //ミスミ確認中なら赤色
            if ("z".equals(itemInfo.status)) {
				ssStatus = SpannableUtil.newSpannableString(strStatus, 17, true, true);
            }
            ssbStatus.append(ssStatus);

            holder.status.setText(ssbStatus);
        }
		//送り状No.
        {
			String invoiceResult;
			String deliveryCompany;
            //配送会社
            if (itemInfo.deliveryCompanyAbbrName==null || itemInfo.deliveryCompanyAbbrName.isEmpty()){
				if (itemInfo.invoiceNo == null || itemInfo.invoiceNo.isEmpty()){
					invoiceResult = getResourceString(R.string.label_hyphen);
					deliveryCompany = "";
					holder.invoiceNo.setText(deliveryCompany + invoiceResult);
				} else {
					invoiceResult = convertInvoiceNo(itemInfo.invoiceNo);
					deliveryCompany = "";
					holder.invoiceNo.setText(deliveryCompany + invoiceResult);
				}
            } else {
				invoiceResult =  " " + convertInvoiceNo(itemInfo.invoiceNo);
				deliveryCompany = "["+itemInfo.deliveryCompanyAbbrName+"]";
                holder.invoiceNo.setText(deliveryCompany + invoiceResult);
            }
            //配送状況URL
			final String strUrl = itemInfo.deliveryStatusUrl;
			if (!android.text.TextUtils.isEmpty(strUrl)) {
				if (!(itemInfo.deliveryCompanyAbbrName==null || itemInfo.deliveryCompanyAbbrName.isEmpty()) ||
					!(itemInfo.invoiceNo == null || itemInfo.invoiceNo.isEmpty())) {

					Pattern pattern = Pattern.compile(".*");

					Linkify.TransformFilter filter = new Linkify.TransformFilter() {
					    @Override
					    public String transformUrl(Matcher match, String url) {
					        return strUrl;
					    }
					};

					Linkify.addLinks(holder.invoiceNo, pattern, strUrl, null, filter);
				}
            }
        }

		String imageUrl = itemInfo.productImageUrl;
		PicassoUtil.PicassoLoad(holder.productImage, holder.pv, imageUrl);

        //商品詳細
        final View layoutTapItem = convertView.findViewById(R.id.layoutTapItem);
        layoutTapItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(OrderDetailAdapter.this, null, position, getItemId(position));
            }
        });

		//カートへ追加
		holder.addCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mOnItemClickListener.onItemAddCart(OrderDetailAdapter.this, null, position, getItemId(position));
            }
		});

		//My部品表へ追加
		holder.addMyPart.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){

				mOnItemClickListener.onItemAddMy(OrderDetailAdapter.this, null, position, getItemId(position));
			}
		});

		return convertView;
	}


	private void makeChildView(View childView, final ResponseGetOrderDetail.ItemInfo itemInfo, final int position) {

		final LayoutInflater inflater = mInflater;

		View layoutError = childView.findViewById(R.id.layoutError);
		boolean errorMessageFlag = false;
		if (itemInfo.errorList != null && !itemInfo.errorList.ErrorInfoList.isEmpty()) {
			errorMessageFlag = true;
		}

		if (!errorMessageFlag) {

			layoutError.setVisibility(View.GONE);
		} else {

/*
			layoutError.setVisibility(View.GONE);

			final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
			layoutErrorItemList.removeAllViews();

			//
			int errorCnt = 0;
			for (ErrorList.ErrorInfo errorInfo: itemInfo.errorList.ErrorInfoList) {

				String errorStr = errorInfo.getErrorMessage(mScreenId);
		        if (android.text.TextUtils.isEmpty(errorStr)) {
					continue;
				}

				++errorCnt;


				View subView = inflater.inflate(R.layout.include_confirm_item_error_info, layoutErrorItemList, false);
				ViewUtil.setSplitMotionEventsToAll(subView);

				errorMessage = (TextView) subView.findViewById(R.id.textMessage);
				errorMessage.setText(errorStr);

				layoutErrorItemList.addView(subView);
			}

			if (errorCnt > 0) {
				layoutError.setVisibility(View.VISIBLE);
			}
*/

				makeErrorItemList(inflater, mScreenId, layoutError, R.layout.include_confirm_item_error_info, itemInfo.errorList.ErrorInfoList);

		}
	}


	class ViewHolder {

		ViewGroup statasView;

		TextView partNumber;
		TextView productName;
		TextView brandName;
		ImageView productImage;
        TextView sale;
//		TextView standardUnitPrice;
		TextView unitPrice;
		TextView quantity;
        TextView pack;
		TextView totalPrice;
        TextView totalPriceWithTax;
        ViewGroup totalPriceWithTaxlabel;
		TextView shipDateTime;
		TextView expressType;
		TextView status;
		TextView invoiceNo;
		View pv;

		ViewGroup addMyPart;
		ViewGroup addCart;

		//ミスミ受付番号の追加
		TextView misumiNo;
	}

	public String convertQuantity (Integer quantity) {//数量
		String q;

		if (quantity==null) {
			q = "1";
		} else {
			q = quantity.toString();
		}

		return q;
	}


	public int counterZ(ArrayList<ResponseGetOrderDetail.ItemInfo> info){
		Integer counter = 0;
		ResponseGetOrderDetail.ItemInfo itemInfo;
		for (int num=0; num<info.size(); num++){
			itemInfo = info.get(num);
			if ("z".equals(itemInfo.status)) {
				counter = counter + 1;
			}
		}
		return counter;
	}

	public String convertInvoiceNo(String invoiceNo){
		String invoice;

		if (invoiceNo==null){
			invoice = "";
		} else {
			invoice = invoiceNo;
        }
		return invoice;
	}


	protected String getResourceString(int id){
		return getContext().getString(id);
	}
}
