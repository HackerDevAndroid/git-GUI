package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotation;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class QuotationConfirmAdapter extends CommonAdapter<ResponseCheckQuotation.ItemInfo> {
	private static final String TODAY_NO_SELECT = "0";
	private static final String TODAY_SELECT = "1";

	/** LayoutInflator. */
	private final LayoutInflater mInflater;

	/** 行レイアウトリソースID. */
	private final int mResource;

	private final String mScreenId;

	private final OnItemClickListener	mOnItemClickListener;

	private final boolean mIsCodUser;


	public interface OnItemClickListener {
	    void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
	    void onItemChange(ArrayAdapter<?> adapter, View view, int position, long id);
	    void onItemToday(ArrayAdapter<?> adapter, View view, int position, long id);
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
	public QuotationConfirmAdapter(Context context, int resource, List<ResponseCheckQuotation.ItemInfo> objects, OnItemClickListener onItemClickListener, String screenId) {
		super(context, resource, objects);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
		mScreenId = screenId;

		mOnItemClickListener = onItemClickListener;

//		12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
		mIsCodUser = AppConfig.getInstance().isCodUser();
	}


	/* (非 Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {


		if (convertView == null) {
			convertView = mInflater.inflate(mResource, parent, false);
			ViewUtil.setSplitMotionEventsToAll(convertView);
		}

		// 1行分のアイテムデータを取得
		ResponseCheckQuotation.ItemInfo itemInfo = getItem(position);

		makeChildView(convertView, itemInfo, position);

		return convertView;
	}


	private void makeChildView(View childView, final ResponseCheckQuotation.ItemInfo itemInfo, final int position) {

		final LayoutInflater inflater = mInflater;

		//
		TextView tv;

		//エラー表示
		final View layoutError = childView.findViewById(R.id.layoutError);
		boolean errorMessageFlag = false;

		if (itemInfo.errorList != null && !itemInfo.errorList.ErrorInfoList.isEmpty()) {
			errorMessageFlag = true;
		}

		//エラー
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


				View subView = inflater.inflate(R.layout.include_confirm_item_error, layoutErrorItemList, false);
				ViewUtil.setSplitMotionEventsToAll(subView);

					TextView errorMessage = (TextView) subView.findViewById(R.id.textMessage);
					errorMessage.setText(errorStr);

				layoutErrorItemList.addView(subView);
			}

			if (errorCnt > 0) {
				layoutError.setVisibility(View.VISIBLE);
			}
*/

				makeErrorItemList(inflater, mScreenId, layoutError, R.layout.include_confirm_item_error, itemInfo.errorList.ErrorInfoList);

		}



		//
		tv = (TextView) childView.findViewById(R.id.textPartNumber);
		setTextEmptyGone(tv, itemInfo.partNumber);

		tv = (TextView) childView.findViewById(R.id.textProductName);
		setTextEmptyGone(tv, itemInfo.productName);

		tv = (TextView) childView.findViewById(R.id.textMakerName);
		setTextEmptyGone(tv, itemInfo.brandName);


		String	str;

		//単価表示
		SpannableStringBuilder ssb = new SpannableStringBuilder();

		if (itemInfo.unitPrice == null) {

			ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化

		} else {

			ssb.append(Format.formatAmountWithUnit(itemInfo.unitPrice, null));
		}

		setIncludeItemText(childView.findViewById(R.id.unitPrice), getResourceString(R.string.confirm_unit_price), ssb, false);


        //SALEアイコン
		tv = (TextView) childView.findViewById(R.id.textSale);
		String campainEndDate = MsmFormat.convertCampainEndDate(getContext(), itemInfo.campaignEndDate);

        if (!android.text.TextUtils.isEmpty(campainEndDate)) {
            tv.setText(campainEndDate);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }


		//数量
		if (itemInfo.quantity != null) {
			str = Format.formatCount(itemInfo.quantity);
		} else {
			//デフォルト数量＝1
			str = "1";
		}

		//入り数
		if (itemInfo.piecesPerPackage != null && itemInfo.piecesPerPackage != 0) {
			str += String.format(getResourceString(R.string.my_parts_pack_quantity), Format.formatCount(itemInfo.piecesPerPackage));
		}

		setIncludeItemText(childView.findViewById(R.id.quantity), getResourceString(R.string.confirm_quantity), str, false);


        //出荷日数の表示
		{
	        //出荷日数
	        //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
	        str = convertShipQ(itemInfo.daysToShip, itemInfo.shipType);

			setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(R.string.confirm_ship_date_quote), str, false);
		}


		//緊急出荷ストーク
		{
			//		12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
			if (mIsCodUser){
				childView.findViewById(R.id.expressType).setVisibility(View.GONE);
			} else {
				str = convertExpressType(itemInfo.expressType);

//			setIncludeItemText(childView.findViewById(R.id.expressType), getResourceString(R.string.confirm_emarg_main), str, false);
				//課題 #4520
				if (android.text.TextUtils.isEmpty(str)) {
					str = getResourceString(R.string.label_hyphen);   //ハイフン化
				}
				setIncludeItemText(childView.findViewById(R.id.expressType), getResourceString(R.string.confirm_emarg_main) + str, false);
			}
		}


		//
		tv = (TextView) childView.findViewById(R.id.textSubtotal);

		if (SubsidiaryCode.isJapan()) {

			//日本外税

			tv = (TextView) childView.findViewById(R.id.textSubtotal);

			ssb = new SpannableStringBuilder();

//			str = null;

			if (itemInfo.totalPrice == null) {

				ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
			} else {

//				//赤色
				ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.confirm_subtotal_unit_main)));
			}

			tv.setText(ssb);


			//中国税込み消し
			childView.findViewById(R.id.layoutSubtotalWithTax).setVisibility(View.GONE);

		} else {

			//中国税込み
			tv = (TextView) childView.findViewById(R.id.textSubtotal);

			ssb = new SpannableStringBuilder();

//			str = null;

			if (itemInfo.totalPrice == null) {

				ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
			} else {

//				//赤色
				ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.confirm_subtotal_unit_main)));
			}

			tv.setText(ssb);


			//中国税込み
			tv = (TextView) childView.findViewById(R.id.textSubtotalWithTax);

			ssb = new SpannableStringBuilder();

//			str = null;
			if (itemInfo.totalPriceIncludingTax == null) {

				ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
			} else {

				ssb.append(Format.formatAmountWithUnitNoRed(itemInfo.totalPriceIncludingTax, getResourceString(R.string.confirm_subtotal_unit_main_wtax)));
			}

			tv.setText(ssb);

		}



		//
		//ストーク変更ボタン
		View expressType = childView.findViewById(R.id.expressType);
		View button1 = expressType.findViewById(R.id.button1);

		if (itemInfo.mExpressList.size() > 0) {

            button1.setVisibility(View.VISIBLE);
        } else {

            button1.setVisibility(View.GONE);
		}

		button1.setOnClickListener(new View.OnClickListener() {
			private boolean isClicked;

            @Override
            public void onClick(View v) {

				// MISUMI_MOBILE_APP-567
				if (isClicked) return;
				isClicked = true;

				mOnItemClickListener.onItemChange(QuotationConfirmAdapter.this, null, position, getItemId(position));

				v.postDelayed(new Runnable() {
					@Override
					public void run() {
						isClicked = false;
					}}, 1000L);
            }
        });

		//お急ぎですか？ボタン
		View todayShip = childView.findViewById(R.id.daysToShip);
        Button btn = (Button)todayShip.findViewById(R.id.button1);

		if ("1".equals(itemInfo.todayShipFlag)){
			btn.setVisibility(View.VISIBLE);

			//当日出荷選択中はボタン文言
			//当日出荷選択中フラグを見る(API追加待ち)
			int resId;
			if (TODAY_SELECT.equals(itemInfo.todayShipSelectedFlag)) {
				resId = R.string.confirm_change_today_ship_2;
			} else {
				resId = R.string.confirm_change_today_ship;
			}
			btn.setText(getResourceString(resId));

		} else {
			btn.setVisibility(View.GONE);
		}
		btn.setOnClickListener(new View.OnClickListener() {
			private boolean isClicked;

            @Override
            public void onClick(View v) {

				// MISUMI_MOBILE_APP-567
				if (isClicked) return;
				isClicked = true;

				mOnItemClickListener.onItemToday(QuotationConfirmAdapter.this, null, position, getItemId(position));

				v.postDelayed(new Runnable() {
					@Override
					public void run() {
						isClicked = false;
					}}, 1000L);
            }
        });


		//
		ImageView iv = (ImageView) childView.findViewById(R.id.imageView);
		View pv = childView.findViewById(R.id.progressView);

//		String imageUrl = itemInfo.productImageUrl;
		String imageUrl = null;
		if (itemInfo.productImageUrlList.size() > 0) {
			imageUrl = itemInfo.productImageUrlList.get(0);
		}
		PicassoUtil.PicassoLoad(iv, pv, imageUrl);

	}



}
