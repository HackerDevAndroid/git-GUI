package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.ExpandAnimator;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class MyPartsDetailAdapter extends ArrayAdapter<ResponseGetMyComponents.Product> {

	/** LayoutInflator. */
	private final LayoutInflater mInflater;

	/** 行レイアウトリソースID. */
	private final int mResource;

	private final String mCurrencyFormat;

	private final OnItemClickListener	mOnItemClickListener;


	public interface OnItemClickListener {
	    void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
	    void onItemAdd(ArrayAdapter<?> adapter, View view, int position, long id);
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
	public MyPartsDetailAdapter(Context context, int resource, List<ResponseGetMyComponents.Product> objects, OnItemClickListener onItemClickListener) {
		super(context, resource, objects);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;

		mOnItemClickListener = onItemClickListener;

		//言語設定で単価表示形式を切り替える
		int resId = R.string.format_currency_none; //通貨単位無し
		String currencyCode = AppConfig.getInstance().getCurrencyCode();
		if (AppConst.CURRENCY_CODE_JPY.equals(currencyCode)) {
			resId = R.string.format_currency_jpy;
		} else if (AppConst.CURRENCY_CODE_RMB.equals(currencyCode)) {
			resId = R.string.format_currency_rmb;
		} else if (AppConst.CURRENCY_CODE_USD.equals(currencyCode)) {
			resId = R.string.format_currency_usd;
		}
		mCurrencyFormat = getResourceString(resId);

	}


	/* (非 Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {


//		if (convertView == null) {
			//無条件で生成する
			convertView = mInflater.inflate(mResource, parent, false);
			ViewUtil.setSplitMotionEventsToAll(convertView);
//		}

		//先頭は区切りを消す
//		if (position == 0) {
//			convertView.findViewById(R.id.viewDivider).setVisibility(View.GONE);
//		}

		// 1行分のアイテムデータを取得
		ResponseGetMyComponents.Product itemInfo = getItem(position);

		makeChildView(convertView, itemInfo, position);

		return convertView;
	}


	private void makeChildView(View childView, final ResponseGetMyComponents.Product itemInfo, final int position) {

        //
        TextView tv;

//		//エラー分岐処理がここにありました！

        //
        tv = (TextView) childView.findViewById(R.id.textPartNumber);
        setTextEmptyGone(tv, itemInfo.partNumber);

        tv = (TextView) childView.findViewById(R.id.textProductName);
        setTextEmptyGone(tv, itemInfo.productName);

        tv = (TextView) childView.findViewById(R.id.textMakerName);
        setTextEmptyGone(tv, itemInfo.brandName);

        //数量
        String str;
        if (itemInfo.quantity != null) {
            str = Format.formatCount(itemInfo.quantity);
        } else {
            //デフォルト数量＝1
            str = "1";
        }
        tv = (TextView) childView.findViewById(R.id.textQuantity);
        setTextEmptyGone(tv, str);

        //入り数
        str = null;
        if (itemInfo.piecesPerPackage != null && itemInfo.piecesPerPackage != 0) {
            str = String.format(getResourceString(R.string.my_parts_pack_quantity), Format.formatCount(itemInfo.piecesPerPackage));
        }
        tv = (TextView) childView.findViewById(R.id.textPerPack);
        setTextEmptyGone(tv, str);

        //
        View layoutTapItem = childView.findViewById(R.id.layoutTapItem);
        layoutTapItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(MyPartsDetailAdapter.this, null, position, getItemId(position));
            }
        });

        View buttonAddCart = childView.findViewById(R.id.buttonAddCart);
        buttonAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemAdd(MyPartsDetailAdapter.this, null, position, getItemId(position));
            }
        });


        //
        ImageView iv = (ImageView) childView.findViewById(R.id.imageView);
        View pv = childView.findViewById(R.id.progressView);

        String imageUrl = itemInfo.productImageUrl;
        PicassoUtil.PicassoLoad(iv, pv, imageUrl);


        //モジュラ部品
        final View layoutSlideTrigger = childView.findViewById(R.id.layoutSlideTrigger);

        final View viewNext = childView.findViewById(R.id.viewNext);

        final View layoutModule = childView.findViewById(R.id.layoutModule);

        final LinearLayout layoutSubItemList = (LinearLayout) layoutModule.findViewById(R.id.layoutSubItemList);
        layoutSubItemList.removeAllViews();

        View slideSwitch = childView.findViewById(R.id.imageSlideSwitch);

        //モジュラ部品判定
		/*
		商品タイプ		productType
		"商品のタイプ
		""0"": 通常商品
		""1"": モジュラアセンブラ"
		*/
        if (("0".equals(itemInfo.productType))) {

            viewNext.setVisibility(View.VISIBLE);
            layoutModule.setVisibility(View.GONE);
            return;
        }

        //モジュラ部品はタップで遷移しない
        viewNext.setVisibility(View.INVISIBLE);
        layoutTapItem.setOnClickListener(null);
//		layoutTapItem.setClickable(false);

        layoutTapItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
				return !"0".equals(itemInfo.productType);
			}
        });

        //モジュラ部品リスト数判定
        if ((itemInfo.componentItemList.isEmpty())) {

			layoutModule.setVisibility(View.GONE);
			return;
		}

		layoutModule.setVisibility(View.VISIBLE);

        addExpandAnimator2(layoutSlideTrigger, slideSwitch, layoutSubItemList, itemInfo);
	}

    private void addModuleChild(LinearLayout layoutSubItemList, ResponseGetMyComponents.Product itemInfo) {

		if (layoutSubItemList.getChildCount() > 0) {
			return;
		}

		final LayoutInflater inflater = mInflater;
		String str;

		for (ResponseGetMyComponents.Modula item : itemInfo.componentItemList) {

			View subView = inflater.inflate(R.layout.list_item_myparts_module_item, layoutSubItemList, false);
			ViewUtil.setSplitMotionEventsToAll(subView);

			setIncludeItemText(subView.findViewById(R.id.partNumber), getResourceString(R.string.my_parts_parts_number), item.partNumber, false);
			setIncludeItemText(subView.findViewById(R.id.productName), getResourceString(R.string.my_parts_product_name), item.productName, false);

			str	= null;
			if (item.unitPrice != null) {
				str = getUnitPriceString(mCurrencyFormat, item.unitPrice);
			}
			setIncludeItemText(subView.findViewById(R.id.unitPrice), getResourceString(R.string.my_parts_unit_price), str, false);

			str	= null;
			if (item.quantity != null) {
				str = ""+ item.quantity + getResourceString(R.string.my_parts_quantity_unit);
			}
			setIncludeItemText(subView.findViewById(R.id.quantity), getResourceString(R.string.my_parts_quantity), str, false);

			layoutSubItemList.addView(subView);
		}
	}

    private void addExpandAnimator2(View triggerView, final View viewSlideSwitch, final View containerView, final ResponseGetMyComponents.Product itemInfo) {

		final ExpandAnimator animator = createAnimator2(triggerView, null, containerView);

        triggerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!animator.isAnimating()) {
                    if (animator.isExpand()) {
                        animator.unexpand();
						viewSlideSwitch.setSelected(false);

						itemInfo.expanded = false;
                  } else {

						addModuleChild((LinearLayout)containerView, itemInfo);

                        animator.expand();
						viewSlideSwitch.setSelected(true);

						itemInfo.expanded = true;
                  }
                }
            }
        });

		//開いている？
		if (itemInfo.expanded) {

			addModuleChild((LinearLayout)containerView, itemInfo);

			//画面サイズより大きい場合に伸びきらない対応
			animator.adjustSizeImmediately();
			viewSlideSwitch.setSelected(true);
		}

    }


    private ExpandAnimator createAnimator2(View triggerView, final TextView textModuleTab, View containerView) {

        ExpandAnimator animator = new ExpandAnimator(containerView, new ExpandAnimator.OnAnimationListener() {

            @Override
            public void onExpanded(ExpandAnimator e) {
            }

            @Override
            public void onStartExpand(ExpandAnimator e) {
            }

            @Override
            public void onStartUnexpand(ExpandAnimator e) {
            }

            @Override
            public void onUnexpanded(ExpandAnimator e) {
            }
        });

        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setWrapContent(true);
        return animator;
    }

    protected String getResourceString(int id){
        return getContext().getString(id);
    }



	private void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, boolean doHidden) {


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


	private void setIncludeItemText(View subView, String str1, String str2, boolean doHidden) {

		if (str2 == null || str2.isEmpty()) {

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


	private void setIncludeItemText(View subView, String str1, String str2, String format) {

		if (str2 == null) {
			str2 = getResourceString(R.string.label_hyphen);	//ハイフン化
		}

		((TextView)subView.findViewById(R.id.textView2)).setText(String.format(format, str2));
		((TextView)subView.findViewById(R.id.textView1)).setText(str1);
	}


	private String getUnitPriceString(String currencyFormat, Double unitPrice) {

		if (unitPrice == null) {
			return null;
		}

		String str = Format.formatAmount(unitPrice);
		str = String.format(currencyFormat, str);
		return str;
	}


	private void setTextEmptyGone(TextView tv, String str) {
		if (str == null || str.isEmpty()) {
			tv.setText("");
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}


}
