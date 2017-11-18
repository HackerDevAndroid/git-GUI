package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseSearchInfo;
import jp.co.misumi.misumiecapp.data.ResponseSearchQuotation;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * 見積履歴一覧
 */
public class QuotationSearchAdapter extends CommonAdapter<ResponseSearchInfo> {


    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    private final boolean mIsIncludeTax;

    private final OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
    }


    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public QuotationSearchAdapter(Context context, int resource, List<ResponseSearchInfo> objects, OnItemClickListener onItemClickListener) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;

        mOnItemClickListener = onItemClickListener;

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
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
        final ResponseSearchQuotation.ListInfo itemData = (ResponseSearchQuotation.ListInfo) getItem(position);

		//
        setIncludeItemText(convertView.findViewById(R.id.textSlipNo), getResourceString(R.string.quote_hist_slip_no), itemData.infoSlipNo, false);
        setIncludeItemText(convertView.findViewById(R.id.textDateTime), getResourceString(R.string.quote_hist_date), itemData.infoDateTime, false);

        setIncludeItemText(convertView.findViewById(R.id.textExpire), getResourceString(R.string.quote_hist_expire), itemData.quotationExpireDateTime, false);


        String str;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        if (itemData.mItemList == null || itemData.mItemList.size() == 0) {
            ;
        } else {
            int itemCount = itemData.mItemList.size();

            str = Format.formatCount(itemCount);

			SpannableString ss = SpannableUtil.newSpannableString(str, 0, true, false);
            ssb.append(ss);

            ss = new SpannableString(getResourceString(R.string.quote_hist_count_unit));
            ssb.append(ss);

            //ミスミ確認中
            int misumiCheckCount = 0;
            for (int idx = 0; idx < itemCount; ++idx) {

                ResponseSearchQuotation.ItemInfo itemInfo = (ResponseSearchQuotation.ItemInfo) itemData.mItemList.get(idx);

                if ("z".equals(itemInfo.status)) {
                    ++misumiCheckCount;
                }
            }

            if (misumiCheckCount > 0) {

                ss = new SpannableString("（");
                ssb.append(ss);

                str = String.format(getResourceString(R.string.quote_hist_misumi_check_str), Format.formatCount(misumiCheckCount));
				ss = SpannableUtil.newSpannableString(str, 0, true, false);
                ssb.append(ss);

                ss = new SpannableString("）");
                ssb.append(ss);
            }
        }

        setIncludeItemText(convertView.findViewById(R.id.textItemCount), getResourceString(R.string.quote_hist_item_count), ssb, false);


		//
        ssb = new SpannableStringBuilder();


		if (!mIsIncludeTax) {

			//こっちは非表示
	        View textTotalPriceWithTax = convertView.findViewById(R.id.textTotalPriceWithTax);
            textTotalPriceWithTax.setVisibility(View.GONE);

			//日本外税
            if (itemData.totalPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(itemData.totalPrice, true, true, false, getResourceString(R.string.quote_hist_total_price_unit)));
            }

	        setIncludeItemText(convertView.findViewById(R.id.textTotalPrice), getResourceString(R.string.quote_hist_total_price), ssb, false);
        }


		if (mIsIncludeTax) {

			//こっちは非表示
	        View textTotalPrice = convertView.findViewById(R.id.textTotalPrice);
            textTotalPrice.setVisibility(View.GONE);

			//中国税込み
            ssb = new SpannableStringBuilder();
            if (itemData.totalPriceIncludingTax == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(itemData.totalPriceIncludingTax, true, true, false, getResourceString(R.string.quote_hist_total_price_tax_unit)));
            }

            setIncludeItemText(convertView.findViewById(R.id.textTotalPriceWithTax), getResourceString(R.string.quote_hist_total_price_tax), ssb, false);
        }


//        final View imageNext = convertView.findViewById(R.id.imageNext);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(QuotationSearchAdapter.this, null, position, getItemId(position));
            }
        });

        return convertView;
    }

}
