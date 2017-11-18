package jp.co.misumi.misumiecapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * UnitPriceLayout
 */
public class UnitPriceLayout extends LinearLayout{

    private static StyleSpan sBoldStyleSpan = new StyleSpan(Typeface.BOLD);
//    private static AbsoluteSizeSpan sAbsoluteSizeSpan = new AbsoluteSizeSpan(12, true);

    TextView textViewLabel;
    AutoFitTextView textPrice;
    TextView textViewTilde;

    Float labelFontSize;
    Float priceFontSize;
    Float tildeFontSize;


    public UnitPriceLayout(Context context) {
        super(context);
        init(context, null);
    }

    public UnitPriceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UnitPriceLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * init
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.unit_price_layout, this);
        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UnitPriceLayout);
        labelFontSize = typedArray.getFloat(R.styleable.UnitPriceLayout_priceLabelFontSize, 10);
        priceFontSize = typedArray.getFloat(R.styleable.UnitPriceLayout_priceFontSize, 12);
        tildeFontSize = typedArray.getFloat(R.styleable.UnitPriceLayout_tildeFontSize, 8);
        typedArray.recycle();

        textViewLabel = (TextView) findViewById(R.id.textViewLabel);
        textPrice = (AutoFitTextView) findViewById(R.id.autoFitTextPrice);
        textViewTilde = (TextView) findViewById(R.id.textViewTilde);

        textViewLabel.setTextSize(labelFontSize);
        textPrice.setTextSize(priceFontSize);
        textViewTilde.setTextSize(tildeFontSize);
    }

    /**
     * setFontSize
     * @param label
     * @param price
     * @param tilde
     */
    public void setFontSize(Float label, Float price, Float tilde){
        if (label != null){
            labelFontSize = label;
            textViewLabel.setTextSize(label);
        }
        if (price != null){
            priceFontSize = price;
            textPrice.setTextSize(price);
        }
        if (tilde != null){
            tildeFontSize = tilde;
            textViewTilde.setTextSize(tilde);
        }
    }

    /**
     * SetDate
     * @param
     */
    public void SetPrice(Double minValue, Double maxValue, boolean sale){

        if (sale){
        } else {
            //両方無しは表示しない
            if (minValue==null && maxValue==null){
                this.setVisibility(View.GONE);
                return;
            }

            //デフォルト
            if (minValue == null){
                //最大通常価格(maxValue)のみ
                this.setVisibility(View.GONE);
                return;
            }

        }

        this.setVisibility(View.VISIBLE);

        boolean isDollar = AppConfig.getInstance().isDollar();
        boolean isJapan = SubsidiaryCode.isJapan();

        if (sale){
            textViewLabel.setText(R.string.unitprice_label_title);
			textPrice.setTextSize(priceFontSize);
            textPrice.setText(R.string.unitprice_label_title_sale);
            textViewTilde.setText("");
            return;
        } else 
        if (maxValue == null || minValue.equals(maxValue)) {
            //最小通常価格(minStandardUnitPrice)と最大通常価格(maxValue)が同一
            //最小通常価格(minStandardUnitPrice)のみ
            {
                if (isJapan){
                    textViewLabel.setText(R.string.unitprice_label_title);
                    textViewTilde.setText(R.string.unitprice_label_period);
                }else {
                    if (isDollar){
                        textViewLabel.setText(R.string.unitprice_label_title_usd);
                        textViewTilde.setText("");
                    }else {
                        textViewLabel.setText(R.string.unitprice_label_title);
                        textViewTilde.setText(R.string.unitprice_label_period_gen);
                    }
                }
            }
        } else
		{

			//大小関係を修正
			if (maxValue < minValue) {
				minValue = maxValue;
			}

            //最小通常価格(minStandardUnitPrice)<最大通常価格(maxValue)

            {
                if (isJapan) {
                    textViewLabel.setText(R.string.unitprice_label_title);
                    textViewTilde.setText(R.string.unitprice_label_period_tilde);
                } else {
                    if (isDollar) {
                        textViewLabel.setText(R.string.unitprice_label_title_usd);
                        textViewTilde.setText(R.string.unitprice_label_period_usd_tilde);
                    } else {
                        textViewLabel.setText(R.string.unitprice_label_title);
                        textViewTilde.setText(R.string.unitprice_label_period_gen_tilde);
                    }
                }
            }
        }


		final Double minValueF = minValue;


        //文字のサイズ調整
        this.post(new Runnable() {
            @Override
            public void run() {
                int textViewLabelWidth = textViewLabel.getWidth();
                int textViewTildeWidth = textViewTilde.getWidth();
                int parentWidth = UnitPriceLayout.this.getWidth();

                int priceWidth = parentWidth - textViewLabelWidth - textViewTildeWidth - (5* AppConfig.getInstance().dp)*2;
                textPrice.setMinWidth(1);
                textPrice.setMaxWidth(priceWidth);

                textPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
                textPrice.setText(getUnitPriceString(minValueF));

                textPrice.post(new Runnable() {
                    @Override
                    public void run() {
//                        int size = textPrice.getWidth();
//                        AppLog.d("size=" + size);

                        textPrice.requestLayout();
                    }
                });
            }
        });

    }

    private String getUnitPriceString( Double unitPrice) {

        if (unitPrice == null) {
            return null;
        }


//        str = String.format(currencyFormat, str);
        return Format.formatAmount(unitPrice);
    }

}

