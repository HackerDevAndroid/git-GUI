package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class CartDetailAdapter extends ArrayAdapter<GetCart.Product> {


    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    private final OnItemClickListener mOnItemClickListener;

    private boolean isFocusEditText;

    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemCheck(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemUpdate(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemDelete(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemAddMy(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemEdit(ArrayAdapter<?> adapter, View view, int position, long id);
    }

    private static StrikethroughSpan sStrikethroughSpan = new StrikethroughSpan();

    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public CartDetailAdapter(Context context, int resource, List<GetCart.Product> objects, OnItemClickListener onItemClickListener) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;

        mOnItemClickListener = onItemClickListener;

    }


    /* (非 Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {


//		if (convertView == null) {
        convertView = mInflater.inflate(mResource, parent, false);
        ViewUtil.setSplitMotionEventsToAll(convertView);
//		}

        //先頭は区切りを消す
//		if (position == 0) {
//			convertView.findViewById(R.id.viewDivider).setVisibility(View.GONE);
//		}

        // 1行分のアイテムデータを取得
        GetCart.Product itemInfo = getItem(position);

        makeChildView(convertView, itemInfo, position);

        return convertView;
    }


    private void makeChildView(View childView, final GetCart.Product itemInfo, final int position) {

        final LayoutInflater inflater = mInflater;

        //
        TextView tv;

        //TODO:キャンペーン終了フラグで表示
        final View layoutError = childView.findViewById(R.id.layoutError);

        boolean campaignEndFlag = false;
        boolean errorMessageFlag = false;

        if (itemInfo.campaignEndFlag != null && itemInfo.campaignEndFlag.equals("1")) {
            campaignEndFlag = true;
        }

        if (itemInfo.errorMessage != null && !itemInfo.errorMessage.isEmpty()) {
            errorMessageFlag = true;
        }


        //エラー
        if (!campaignEndFlag && !errorMessageFlag) {

            layoutError.setVisibility(View.GONE);
        } else {

            layoutError.setVisibility(View.VISIBLE);

            final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
            layoutErrorItemList.removeAllViews();

            if (campaignEndFlag) {

                View subView;
                subView = inflater.inflate(R.layout.include_change_unit_price_1k, layoutErrorItemList, false);
                ViewUtil.setSplitMotionEventsToAll(subView);
                layoutErrorItemList.addView(subView);

                subView = inflater.inflate(R.layout.include_change_unit_price_2k, layoutErrorItemList, false);
                ViewUtil.setSplitMotionEventsToAll(subView);
                layoutErrorItemList.addView(subView);
            }

            //
            if (errorMessageFlag) {

                View subView = inflater.inflate(R.layout.include_change_unit_price_i, layoutErrorItemList, false);
                ViewUtil.setSplitMotionEventsToAll(subView);

                tv = (TextView) subView.findViewById(R.id.textMessage);
                tv.setText(itemInfo.errorMessage);

                layoutErrorItemList.addView(subView);
            }

        }


        //
        tv = (TextView) childView.findViewById(R.id.textPartNumber);
        setTextEmptyGone(tv, itemInfo.partNumber);

        tv = (TextView) childView.findViewById(R.id.textProductName);
        setTextEmptyGone(tv, itemInfo.productName);

        tv = (TextView) childView.findViewById(R.id.textMakerName);
        setTextEmptyGone(tv, itemInfo.brandName);

        String str;


        //単価表示
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        SpannableString ss;

        if ((itemInfo.unitPrice == null) && (itemInfo.standardUnitPrice == null || itemInfo.standardUnitPrice < 1.0)) {
            ;
        } else if (itemInfo.unitPrice == null) {

            //単価無しは標準単価
            ssb = new SpannableStringBuilder(Format.formatAmountWithUnitNoRed(itemInfo.standardUnitPrice, null));

        } else if (itemInfo.standardUnitPrice == null) {

            //単価のみは単価
            ssb = new SpannableStringBuilder(Format.formatAmountWithUnitNoRed(itemInfo.unitPrice, null));

        } else if (itemInfo.unitPrice.equals(itemInfo.standardUnitPrice)
                || (itemInfo.unitPrice > itemInfo.standardUnitPrice)
                || (itemInfo.standardUnitPrice < 1.0)) {

            //同じは単価
            //標準単価 < 単価は単価
            ssb = new SpannableStringBuilder(Format.formatAmountWithUnitNoRed(itemInfo.unitPrice, null));

        } else {

            //標準単価 > 単価 は単価は並べて表示
            //取り消し線
            //全角空白
            CharSequence string = Format.formatAmountWithUnit(itemInfo.standardUnitPrice, null);
            ss = SpannableUtil.newSpannableString(string.toString(), 15, false, false);
            ss.setSpan(sStrikethroughSpan, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);
            ssb.append("　");
            ssb.append(Format.formatAmountWithUnit(itemInfo.unitPrice, null));

        }
        setIncludeItemText(childView.findViewById(R.id.unitPrice), getResourceString(R.string.cart_unit_price), ssb, false);


        //SALEアイコン
        tv = (TextView) childView.findViewById(R.id.textSale);
        String campainEndDate = MsmFormat.convertCampainEndDate(getContext(), itemInfo.campaignEndDate);

        if (!android.text.TextUtils.isEmpty(campainEndDate)) {
            tv.setText(campainEndDate);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }

        tv = (TextView) childView.findViewById(R.id.textUpdateDate);
        if (android.text.TextUtils.isEmpty(itemInfo.updateDateTime)) {

            str = getResourceString(R.string.label_hyphen);
        } else {

            str = itemInfo.updateDateTime;
        }
        tv.setText(str);


        //
        str = null;
        if (itemInfo.piecesPerPackage != null && itemInfo.piecesPerPackage != 0) {
            str = String.format(getResourceString(R.string.cart_pack_quantity), Format.formatCount(itemInfo.piecesPerPackage));

        } else {

        }
        setIncludeItemText(childView.findViewById(R.id.perPack), "", str, true);


        if (SubsidiaryCode.isJapan()) {

            //日本外税

            childView.findViewById(R.id.layoutSubtotalWithTax).setVisibility(View.GONE);

            tv = (TextView) childView.findViewById(R.id.textSubtotal);

            ssb = new SpannableStringBuilder();

            if (itemInfo.totalPrice == null) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化

                ssb.append(str);
            } else {

                ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, null));

            }

            tv.setText(ssb);
        } else {

            //中国外税
            tv = (TextView) childView.findViewById(R.id.textSubtotal);

            ssb = new SpannableStringBuilder();

            if (itemInfo.totalPrice == null) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化

                ssb.append(str);
            } else {

                ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, null));

            }

            tv.setText(ssb);


            //中国税込み
            tv = (TextView) childView.findViewById(R.id.textSubtotalWithTax);

            ssb = new SpannableStringBuilder();

            if (itemInfo.totalPriceWithTax == null) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化

                ssb.append(str);
            } else {

                ssb.append(Format.formatAmountWithUnitNoRed(itemInfo.totalPriceWithTax, null));

            }

            tv.setText(ssb);

        }


        //出荷日数の表示
        {
            //出荷日数
            //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
            str = MsmFormat.convertShip(getContext(), itemInfo.daysToShip, itemInfo.shipType);
//	        str = convertShip(itemInfo.daysToShip, itemInfo.shipType);

            setIncludeItemText(childView.findViewById(R.id.daysToShip), getResourceString(R.string.cart_ship_date), str, false);
        }

        setIncludeItemText(childView.findViewById(R.id.quantity), getResourceString(R.string.cart_quantity), "", false);


        //数量更新
//	    final EditText editQuantity = (EditText) childView.findViewById(R.id.editQuantity);
        final TextView editQuantity = (TextView) childView.findViewById(R.id.editQuantityT);


        editQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemEdit(CartDetailAdapter.this, v, position, getItemId(position));

            }
        });


        final View buttonMinus = childView.findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = editQuantity.getText().toString();

                if (android.text.TextUtils.isEmpty(str)) {

					/*
                    数量入力欄が空欄の時の＋－ボタンの挙動について
					動作仕様
					・両方とも押せる
					・押した場合は0が入る
					*/
                    editQuantity.setText("0");
                    //--ADD NT-LWL 17/06/26 Share FR -
                    // 记录变化数量
                    itemInfo.editedQuantity = "0";
                    //--ADD NT-LWL 17/06/26 Share TO -
                } else {

                    int qty = getQuantity(str);
                    --qty;
                    if (qty < 1) {
                        qty = 1;
                    }

                    editQuantity.setText("" + qty);
                    //--ADD NT-LWL 17/06/26 Share FR -
                    // 记录变化数量
                    itemInfo.editedQuantity = "0";
                    //--ADD NT-LWL 17/06/26 Share TO -
                }
            }
        });

        final View buttonPlus = childView.findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = editQuantity.getText().toString();

                if (android.text.TextUtils.isEmpty(str)) {

					/*
					数量入力欄が空欄の時の＋－ボタンの挙動について
					動作仕様
					・両方とも押せる
					・押した場合は0が入る
					*/
                    editQuantity.setText("0");
                } else {

                    int qty = getQuantity(str);
                    if (qty < 0) {
                        qty = 0;
                    }
                    ++qty;
                    if (qty > 99999) {
                        qty = 99999;
                    }

                    editQuantity.setText("" + qty);
                }
            }
        });


        final View imageReload = childView.findViewById(R.id.imageReload);
        imageReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemUpdate(CartDetailAdapter.this, null, position, getItemId(position));

            }
        });


        final View buttonDelete = childView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemDelete(CartDetailAdapter.this, null, position, getItemId(position));

            }
        });


        final View buttonAddMy = childView.findViewById(R.id.buttonAddMy);
        buttonAddMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemAddMy(CartDetailAdapter.this, null, position, getItemId(position));

            }
        });


        //

        //途中で編集している場合
        String quantity = itemInfo.editedQuantity;

        //EditText監視
        editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = s.toString();

                //編集途中を保持
                itemInfo.editedQuantity = str;

                int qty = getQuantity(str);

                if (qty < 1) {
                    imageReload.setEnabled(false);
                } else {
                    imageReload.setEnabled(true);
                }

                if (qty >= 99999) {
                    buttonPlus.setEnabled(false);
                } else if (qty <= 1 && !str.isEmpty()) {
                    buttonMinus.setEnabled(false);
                } else {
                    buttonPlus.setEnabled(true);
                    buttonMinus.setEnabled(true);
                }
            }
        });

        //更新ボタン制御も同時に行う
        editQuantity.setText(quantity);


/*
		editQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		@Override  
		public void onFocusChange(View v, boolean hasFocus) {

				AppLog.e("onFocusChange: "+ hasFocus);

				if(hasFocus) {
					isFocusEditText = true;
				}
			}
		});

		if (isFocusEditText) {
			editQuantity.requestFocus();

			isFocusEditText = false;
		}
*/

/*
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (Format.isNumberFormat(source.toString())) {
                    return source;
                } else {
                    return "";
                }
            }
        };
        InputFilter inputFilterLength = new InputFilter.LengthFilter(5);
        editQuantity.setFilters(new InputFilter[]{inputFilter, inputFilterLength});
*/

        final View viewSelectBg = childView.findViewById(R.id.viewSelectBg);

        CheckBox checkBox = (CheckBox) childView.findViewById(R.id.checkBox);
        //リスナをクリアしないと再利用時に誤動作する
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(itemInfo.checked);
        viewSelectBg.setSelected(itemInfo.checked);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                itemInfo.checked = isChecked;

                viewSelectBg.setSelected(isChecked);

                mOnItemClickListener.onItemCheck(CartDetailAdapter.this, null, position, getItemId(position));

            }
        });


        View layoutTapItem = childView.findViewById(R.id.layoutTapItem);
        layoutTapItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(CartDetailAdapter.this, null, position, getItemId(position));
            }
        });

        //
        ImageView iv = (ImageView) childView.findViewById(R.id.imageView);
        View pv = childView.findViewById(R.id.progressView);

        String imageUrl = itemInfo.productImageUrl;
        PicassoUtil.PicassoLoad(iv, pv, imageUrl);


        //モジュラ部品

		/*
		"商品のタイプ
		1: 通常商品
		2: モジュラアセンブラ
		3: モジュラ品の構成部品
		となるように修正いたします。

		修正後は0が返ることはありませんので、1の場合のみ商品詳細呼出をして下さい。
		*/
        //モジュラ部品判定
        if ("1".equals(itemInfo.productType)) {

            //モジュラ以外
            return;
        }

        //モジュラ部品はタップで遷移しない
        layoutTapItem.setOnClickListener(null);
        layoutTapItem.setClickable(false);

    }


    private void setTextEmptyGone(TextView tv, String str) {

        if (android.text.TextUtils.isEmpty(str)) {
            tv.setText(null);
            tv.setVisibility(View.GONE);
            return;
        }

        tv.setText(str);
        tv.setVisibility(View.VISIBLE);
    }


    protected String getResourceString(int id) {
        return getContext().getString(id);
    }


    private void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, boolean doHidden) {


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


    private void setIncludeItemText(View subView, String str1, String str2, boolean doHidden) {

        if (str2 == null) {

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


    private void setIncludeItemText(View subView, String str1, String str2, String format) {

        if (str2 == null) {
            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(String.format(format, str2));
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }


    private String getUnitPriceString(String currencyFormat, Double unitPrice) {

        if (unitPrice == null) {
            return null;
        }

        String str = Format.formatAmount(unitPrice);
        str = String.format(currencyFormat, str);
        return str;
    }


//	private String convertShip(Integer daysToShip, String shipType) {//daysToShip
//
//        //出荷日数
//        //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
//        String str = null;
//        String daytoship;
//        String shiptype = "";
//        String shiptype2 = "";
//        if (daysToShip == null || (daysToShip != 0 && daysToShip != 99)){
//
//            //　フォーマット例：10日目(土曜含まず)
//            if (daysToShip == null) {
//			}else{
//                daytoship = daysToShip.toString();
//				shiptype = getResourceString(R.string.label_day_to_ship_1ro98_1);
//				shiptype += daytoship;
//				shiptype += getResourceString(R.string.label_day_to_ship_1ro98_2);
//            }
//
//        } else if (daysToShip == 0) {
//            // 当日出荷
//			shiptype = getResourceString(R.string.label_day_to_ship_0);
//        } else if (daysToShip == 99) {
//            // 都度お見積り
//			shiptype = getResourceString(R.string.label_day_to_ship_99);
//        } else {
//			shiptype = getResourceString(R.string.label_hyphen);
//        }
//
//        if (shipType != null) {
//            //・1の場合：在庫品　　　・2の場合：在庫手配中
//            //　・3の場合：土祝含まず　・4の場合：土曜含まず
//            //　・5の場合：祝日含まず
//            switch (shipType) {
//                case "1":
//                    shiptype2 += getResourceString(R.string.label_shiptype_1);
//                    break;
//                case "2":
//                    shiptype2 += getResourceString(R.string.label_shiptype_2);
//                    break;
//                case "3":
//                    shiptype2 += getResourceString(R.string.label_shiptype_3);
//                    break;
//                case "4":
//                    shiptype2 += getResourceString(R.string.label_shiptype_4);
//                    break;
//                case "5":
//                    shiptype2 += getResourceString(R.string.label_shiptype_5);
//                    break;
//            }
//        }
//
//
//		if ((daysToShip == null) && (shipType == null)) {
//
//			str = getResourceString(R.string.label_hyphen);
//		} else {
//
//			if (shiptype2.length() != 0) {
//				shiptype2 += " ";
//			}
//
//			str = shiptype2 + shiptype;
//		}
//
//		return str;
//	}


    private int getQuantity(String str) {

        int qty = 0;
        if (str != null && !str.isEmpty()) {
            try {
                qty = Integer.parseInt(str);
            } catch (Exception e) {
            }
        }

        return qty;
    }


}
