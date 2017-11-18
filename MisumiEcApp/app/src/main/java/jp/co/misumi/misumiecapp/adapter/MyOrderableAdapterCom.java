package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseCheck;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * (1)素通し
 */
public class MyOrderableAdapterCom extends CommonAdapter<ResponseCheck.ItemInfo> {

	/** LayoutInflator. */
	private final LayoutInflater mInflater;

	/** 行レイアウトリソースID. */
	private final int mResource;

	private final String mScreenId;

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
	public MyOrderableAdapterCom(Context context, int resource, List<ResponseCheck.ItemInfo> objects, String screenId) {
		super(context, resource, objects);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
		mScreenId = screenId;
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
		ResponseCheck.ItemInfo itemInfo = getItem(position);

		makeChildView(convertView, itemInfo, position);

		return convertView;
	}


	private void makeChildView(View childView, final ResponseCheck.ItemInfo itemInfo, final int position) {

		final LayoutInflater inflater = mInflater;


			View layoutError = childView.findViewById(R.id.layoutError);
			boolean errorMessageFlag = false;

			if (itemInfo.errorList != null && !itemInfo.errorList.ErrorInfoList.isEmpty()) {
				errorMessageFlag = true;
			}

			//エラー
			if (!errorMessageFlag) {

				layoutError.setVisibility(View.GONE);
			} else {

				makeErrorItemList(inflater, mScreenId, layoutError, R.layout.include_item_stock_err, itemInfo.errorList.ErrorInfoList);

			}

			//
			TextView tv;

			tv = (TextView) childView.findViewById(R.id.orderDetailPartNumber);
			setTextEmptyGone(tv, itemInfo.partNumber);

			tv = (TextView) childView.findViewById(R.id.orderDetailProductName);
			setTextEmptyGone(tv, itemInfo.productName);

			tv = (TextView) childView.findViewById(R.id.orderDetailBrandName);
			setTextEmptyGone(tv, itemInfo.brandName);

			//
			String	str;

			//単価表示
			SpannableStringBuilder ssb = new SpannableStringBuilder();

			if (itemInfo.unitPrice == null) {

				ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化

			} else {

				//単価のみは単価
				ssb.append(Format.formatAmountWithUnit(itemInfo.unitPrice, null));
			}

			setIncludeItemText(childView.findViewById(R.id.unitPrice), getResourceString(R.string.unfit_type_dialog_list_unit_price_main), ssb, false);


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

			setIncludeItemText(childView.findViewById(R.id.quantity), getResourceString(R.string.unfit_type_dialog_list_quantity_main), str, false);


	        //出荷日数の表示
			{
		        //出荷日数
		        //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
//		        str = convertShip(itemInfo.daysToShip, itemInfo.shipType);
				String shipType = convertShipType(itemInfo.shipType);
		        str = convertDaysToShipUnit2(itemInfo.daysToShip, true);

				if (android.text.TextUtils.isEmpty(shipType)) {
					shipType = "";
				}

				if (android.text.TextUtils.isEmpty(str)) {
					str = "";
				}

				if (!android.text.TextUtils.isEmpty(shipType)) {
					str = shipType + str;
				}

				if (android.text.TextUtils.isEmpty(str)) {

					str = getResourceString(R.string.label_hyphen);	//ハイフン化
				}

				//見積差異
				if (itemInfo.isQuote()) {

					//見積
					setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(R.string.unfit_type_dialog_list_shipdate_main_quote), str, false);
				} else {

					//注文
					setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(R.string.unfit_type_dialog_list_shipdate_main), str, false);
				}

			}


			//緊急出荷ストーク（非表示）
			{
				setIncludeItemText(childView.findViewById(R.id.expressType), null, null, true);
			}

			//
			tv = (TextView) childView.findViewById(R.id.textSubtotal);

			if (SubsidiaryCode.isJapan()) {

				//日本外税

				tv = (TextView) childView.findViewById(R.id.textSubtotal);

				ssb = new SpannableStringBuilder();

				if (itemInfo.totalPrice == null) {

					ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
				} else {

					ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.unfit_type_dialog_list_unit_main)));
				}

				tv.setText(ssb);


				//中国税込み消し
				childView.findViewById(R.id.layoutSubtotalWithTax).setVisibility(View.GONE);

			} else {

				//中国税込み
				tv = (TextView) childView.findViewById(R.id.textSubtotal);

				ssb = new SpannableStringBuilder();

				if (itemInfo.totalPrice == null) {

					ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
				} else {

					ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.unfit_type_dialog_list_unit_main)));
				}

				tv.setText(ssb);


				//中国税込み
				tv = (TextView) childView.findViewById(R.id.textSubtotalWithTax);

				ssb = new SpannableStringBuilder();

				if (itemInfo.totalPriceIncludingTax == null) {

					ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
				} else {

					ssb.append(Format.formatAmountWithUnit(itemInfo.totalPriceIncludingTax, getResourceString(R.string.unfit_type_dialog_list_main_wtax)));
				}

				tv.setText(ssb);

			}

	}


	//以下2つをfalseで返すと選択が行えなくなる
	public boolean areAllItemsEnabled() {
		return false;
	}
	public boolean isEnabled(int position) {
		return false;
	}

}
