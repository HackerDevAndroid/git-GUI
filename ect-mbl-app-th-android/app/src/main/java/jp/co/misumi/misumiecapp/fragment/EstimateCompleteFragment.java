package jp.co.misumi.misumiecapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.data.ResponseConfirm;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

/**
 * 見積完了画面
 */
//TODO:閉じるボタン処理が未実装
public class EstimateCompleteFragment extends CartCompleteFragment {

    private final boolean mIsIncludeTax;


    public EstimateCompleteFragment() {

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
    }

    @Override
    protected int getLayoutId() {

        return R.layout.fragment_estimate_complete;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private ResponseConfirm getData() {

        return (ResponseConfirm) getDataContainer();
    }


    @Override
    protected void makeDataView(View rootView) {

        //ResponseConfirmQuotation getData()で画面を作る
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

        if (isFromOrder()) {
            textProgress2.setText(getResourceString(R.string.progress_order_2));
        } else {
            textProgress2.setText(getResourceString(R.string.progress_quote_2));
        }

        textProgress3.setSelected(true);
        textProgress3.setText(getResourceString(R.string.progress_quote_3));


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

        if (response.itemCountInChecking == null || response.itemCountInChecking == 0) {
        } else {

            itemCountMisumi = String.format(getResourceString(R.string.quote_comp_misumi_check_str), Format.formatCount(response.itemCountInChecking));

            totalPriceMisumi = getResourceString(R.string.quote_comp_price_checking);
        }

        setIncludeItemText(rootView.findViewById(R.id.infoSlipNo), getResourceString(R.string.quote_comp_slip_no), response.infoSlipNo, null);


        //
        if (response.itemCount == null) {

            str = getResourceString(R.string.label_hyphen);    //ハイフン化
        } else {

            str = Format.formatCount(response.itemCount);
            str += getResourceString(R.string.quote_comp_count_unit);
        }
        setIncludeItemText(rootView.findViewById(R.id.itemCount), getResourceString(R.string.quote_comp_item_count), str, itemCountMisumi);


        //
        boolean isHyphenPrice = false;
        if (response.totalPrice == null || response.totalPrice.equals(0.0)) {

            str = getResourceString(R.string.label_hyphen);    //ハイフン化
            isHyphenPrice = true;
        } else {

            str = Format.formatAmount(response.totalPrice);

            // 通貨を追加
            if (SubsidiaryCode.isJapan()) {
                str += getResourceString(R.string.order_complete_total_price_unit_yen);
            } else {
                if (AppConfig.getInstance().isDollar()) {
                    str = getResourceString(R.string.order_complete_total_price_unit_dollar) + str;
                } else {
                    str += getResourceString(R.string.order_complete_total_price_unit_gen);
                }
            }
        }

        if (SubsidiaryCode.isJapan() && mIsIncludeTax) {

            //日本非表示
            rootView.findViewById(R.id.totalPrice).setVisibility(View.GONE);
        } else {
            if (isHyphenPrice) {
                setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.quote_comp_total_price), str, null);
            } else {
                setIncludeItemText(rootView.findViewById(R.id.totalPrice), getResourceString(R.string.quote_comp_total_price), str, totalPriceMisumi);
            }
        }

        //
        isHyphenPrice = false;
        if (SubsidiaryCode.isJapan()) {

            if (!mIsIncludeTax) {
                //日本非表示
                rootView.findViewById(R.id.totalPriceIncludingTax).setVisibility(View.GONE);
            } else {

                //合計金額(税込)
                if (response.totalPriceIncludingTax == null || response.totalPriceIncludingTax.equals(0.0)) {

                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                    isHyphenPrice = true;
                } else {

                    str = Format.formatAmount(response.totalPriceIncludingTax);
                    str += getResourceString(R.string.order_complete_total_price_unit_yen);
                }

                if (isHyphenPrice) {
                    setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.quote_comp_total_price_tax), str, null);
                } else {
                    setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.quote_comp_total_price_tax), str, totalPriceMisumi);
                }


            }

        } else {

            //中国版は表示

            //合計金額(税込)
            if (response.totalPriceIncludingTax == null) {

                str = getResourceString(R.string.label_hyphen);    //ハイフン化
                isHyphenPrice = true;
            } else {


                str = Format.formatAmount(response.totalPriceIncludingTax);

                if (AppConfig.getInstance().isDollar()) {
                    str = getResourceString(R.string.order_complete_total_price_unit_dollar) + str;
                } else {
                    str += getResourceString(R.string.order_complete_total_price_unit_gen);
                }

                //str += getResourceString(R.string.quote_comp_total_price_tax_unit);
            }

            if (isHyphenPrice) {
                setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.quote_comp_total_price_tax), str, null);
            } else {
                setIncludeItemText(rootView.findViewById(R.id.totalPriceIncludingTax), getResourceString(R.string.quote_comp_total_price_tax), str, totalPriceMisumi);
            }


        }


        //
        if (android.text.TextUtils.isEmpty(response.infoDatetime)) {

            str = getResourceString(R.string.label_hyphen);    //ハイフン化
        } else {


            str = response.infoDatetime;
        }
        setIncludeItemText(rootView.findViewById(R.id.infoDatetime), getResourceString(R.string.quote_comp_date), str, null);


        //素通しエリアの表示判定
        if (response.itemCountInChecking == null || response.itemCountInChecking == 0) {

            rootView.findViewById(R.id.misumiCheckLayout).setVisibility(View.GONE);
        } else {

            rootView.findViewById(R.id.misumiCheckLayout).setVisibility(View.VISIBLE);

            ((TextView) rootView.findViewById(R.id.misumiCheckLayout).findViewById(R.id.textMessage)).setText(R.string.order_complete_confirm_info_memo3);

        }


    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.EstimateComplete;
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.EstimateComplete;
    }
}


