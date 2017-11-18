package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseGetQuotation;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class QuotationDetailAdapter extends CommonAdapter<ResponseGetQuotation.ItemInfo> {


    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    private final String mScreenId;

    private final OnItemClickListener mOnItemClickListener;

    //	12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
    private final boolean mIsCodUser;


    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemCheck(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemAddCart(ArrayAdapter<?> adapter, View view, int position, long id);

        void onItemAddMy(ArrayAdapter<?> adapter, View view, int position, long id);
    }


    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public QuotationDetailAdapter(Context context, int resource, List<ResponseGetQuotation.ItemInfo> objects, OnItemClickListener onItemClickListener, String screenId) {
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
        final ResponseGetQuotation.ItemInfo itemInfo = getItem(position);
        //エラーリスト追加
        makeChildView(convertView, itemInfo, position);

        TextView tv;
        TextView tvWithTax;
        ViewGroup tvWithTaxlabel;

        //
        tv = (TextView) convertView.findViewById(R.id.textPartNumber);
        setTextEmptyGone(tv, itemInfo.partNumber);

        tv = (TextView) convertView.findViewById(R.id.textProductName);
        setTextEmptyGone(tv, itemInfo.productName);

        tv = (TextView) convertView.findViewById(R.id.textMakerName);
        setTextEmptyGone(tv, itemInfo.brandName);


        //SALEアイコン
        tv = (TextView) convertView.findViewById(R.id.textSale);
        String campainEndDate = MsmFormat.convertCampainEndDate(getContext(), itemInfo.campainEndDate);

        boolean saleEnable = true;
//		if (itemInfo.unitPrice == null || itemInfo.unitPrice == 0){
//			saleEnable = false;
//		}
        if (android.text.TextUtils.isEmpty(campainEndDate)) {
            saleEnable = false;
        }
        if (saleEnable) {
            tv.setText(campainEndDate);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }


        //日本も中国も外税外税
        tv = (TextView) convertView.findViewById(R.id.textUnitPrice);
        {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            if (itemInfo.unitPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));    //ハイフン化
            } else {

                if (SubsidiaryCode.isJapan()) {
                    ssb.append(Format.formatAmountWithUnit(itemInfo.unitPrice, getResourceString(R.string.quote_hist_detail_unit_price_unit_main)));
                } else {
                    ssb.append(Format.formatAmountWithUnit(itemInfo.unitPrice, getResourceString(R.string.quote_hist_detail_unit_price_unit_main_CHN)));
                }

            }

            tv.setText(ssb);
        }


        tv = (TextView) convertView.findViewById(R.id.textQuantity);
        {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String str;
//			ssb.append(getResourceString(R.string.quote_hist_detail_quantity_main));

            //赤色
            str = Format.formatCount(itemInfo.quantity);

            SpannableString ss = new SpannableString(str);
            ssb.append(ss);

            tv.setText(ssb);
        }

        //入り数
        tv = (TextView) convertView.findViewById(R.id.textPerPack);
        {
            String str = null;

            if (itemInfo.piecesPerPakage != null && itemInfo.piecesPerPakage != 0) {

                str = String.format(getResourceString(R.string.quote_hist_detail_pack_quantity), Format.formatCount(itemInfo.piecesPerPakage));

            }

            tv.setText(str);
        }

        //出荷日数の表示
        {
            //出荷日数
            //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
            String str = convertShipQ(itemInfo.daysToShip, itemInfo.shipType);

            tv = (TextView) convertView.findViewById(R.id.textShipdate);
            tv.setText(str);
        }


        //
        tv = (TextView) convertView.findViewById(R.id.textEmerg);
        {
//			SpannableStringBuilder ssb = new SpannableStringBuilder();
//			ssb.append(convertExpressType(itemInfo.expressType));
//
//			tv.setText(ssb);

//            12/10 MISUMI_MOBILE_APP-571 代引きユーザでは緊急出荷サービスをトルツメ
            if (mIsCodUser) {
                tv.setVisibility(View.GONE);
            } else {
                //課題 #4520
                String str = convertExpressType(itemInfo.expressType);
                if (android.text.TextUtils.isEmpty(str)) {
                    str = getResourceString(R.string.label_hyphen);   //ハイフン化
                }

                tv.setText(getResourceString(R.string.quote_hist_detail_emarg_main) + str);
            }
        }

        tv = (TextView) convertView.findViewById(R.id.textStatus);
        {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String str;
//			ssb.append(getResourceString(R.string.quote_hist_detail_status_main));

            str = convertStatusQuote(itemInfo.status);
            SpannableString ss = new SpannableString(str);

            //ミスミ確認中なら赤色
            if ("z".equals(itemInfo.status)) {
                ss = SpannableUtil.newSpannableString(str, 15, true, true);
            }
            ssb.append(ss);

            tv.setText(ssb);
        }

        tv = (TextView) convertView.findViewById(R.id.textSubtotal);
        tvWithTax = (TextView) convertView.findViewById(R.id.textSubtotalWithTax);
        tvWithTaxlabel = (ViewGroup) convertView.findViewById(R.id.textSubtotalWithTaxlabel);

        if (SubsidiaryCode.isJapan()) {

            //日本外税
            tvWithTax.setVisibility(View.GONE);
            tvWithTaxlabel.setVisibility(View.GONE);
            SpannableStringBuilder ssb = new SpannableStringBuilder();

            if (itemInfo.totalPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));
            } else {

                ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.quote_hist_detail_subtotal_unit_main)));
            }

            tv.setText(ssb);
        } else {

            //中国外税
            SpannableStringBuilder ssb = new SpannableStringBuilder();


            if (itemInfo.totalPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));
            } else {

                ssb.append(Format.formatAmountWithUnit(itemInfo.totalPrice, getResourceString(R.string.quote_hist_detail_subtotal_unit_main_CHN)));
            }

            tv.setText(ssb);


            //中国税込み
            SpannableStringBuilder ssbTax = new SpannableStringBuilder();

            if (itemInfo.totalPriceIncludingTax == null) {

                ssbTax.append(getResourceString(R.string.label_hyphen));
            } else {

                ssbTax.append(Format.formatAmountWithUnit(itemInfo.totalPriceIncludingTax, getResourceString(R.string.quote_hist_detail_subtotal_unit_main_tax)));
            }

            tvWithTax.setText(ssbTax);
        }


        //
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
        View pv = convertView.findViewById(R.id.progressView);

        String imageUrl = itemInfo.productImageUrl;
        PicassoUtil.PicassoLoad(iv, pv, imageUrl);


        //
        final View layoutTapItem = convertView.findViewById(R.id.layoutTapItem);
        layoutTapItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(QuotationDetailAdapter.this, null, position, getItemId(position));
            }
        });

        View buttonAddMy = convertView.findViewById(R.id.buttonAddMy);
        buttonAddMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemAddMy(QuotationDetailAdapter.this, null, position, getItemId(position));
            }
        });

        View buttonAddCart = convertView.findViewById(R.id.buttonAddCart);
        buttonAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemAddCart(QuotationDetailAdapter.this, null, position, getItemId(position));
            }
        });


        final View viewSelectBg = convertView.findViewById(R.id.viewSelectBg);

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox);
        //リスナをクリアしないと再利用時に誤動作する
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(itemInfo.checked);
        viewSelectBg.setSelected(itemInfo.checked);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                itemInfo.checked = isChecked;

                viewSelectBg.setSelected(isChecked);

                mOnItemClickListener.onItemCheck(QuotationDetailAdapter.this, null, position, getItemId(position));

            }
        });

        return convertView;
    }

    private void makeChildView(View childView, final ResponseGetQuotation.ItemInfo itemInfo, final int position) {

        final LayoutInflater inflater = mInflater;

        View layoutError = childView.findViewById(R.id.layoutError);
        boolean errorMessageFlag = false;
        if (itemInfo.errorList != null && !itemInfo.errorList.ErrorInfoList.isEmpty()) {
            errorMessageFlag = true;
        }

        if (!errorMessageFlag) {

            layoutError.setVisibility(View.GONE);
        } else {

            makeErrorItemList(inflater, mScreenId, layoutError, R.layout.include_confirm_item_error_info, itemInfo.errorList.ErrorInfoList);

        }
    }


    protected void setTextEmptyGone(TextView tv, String str) {

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

}
