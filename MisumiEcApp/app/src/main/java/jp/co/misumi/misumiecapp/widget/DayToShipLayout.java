package jp.co.misumi.misumiecapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;


/**
 * DayToShipLayout
 */
public class DayToShipLayout extends LinearLayout{

    public TextView textViewLabel;
    public TextView textViewColon;
    public AutoFitTextView textViewDay;
    public TextView textViewPeriod;
    public TextView textViewTilde;

    Float labelFontSize;
    Float labelColonSize;
    Float dayFontSize;
    Float periodFontSize;
    Float tildFontSize;

    Context context;

    protected String getResourceString(int id){
        return getContext().getString(id);
    }

    public DayToShipLayout(Context context) {
        super(context);
        init(context, null);
    }

    public DayToShipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DayToShipLayout(Context context, AttributeSet attrs, int defStyle) {
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

        this.context = context;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_to_ship_layout, this);
        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayToShipLayout);
        labelFontSize = typedArray.getFloat(R.styleable.DayToShipLayout_labelFontSize, 10);
        labelColonSize = typedArray.getFloat(R.styleable.DayToShipLayout_colonFontSize, 8);
        dayFontSize = typedArray.getFloat(R.styleable.DayToShipLayout_dayFontSize, 12);
        periodFontSize = typedArray.getFloat(R.styleable.DayToShipLayout_periodFontSize, 8);
        tildFontSize = typedArray.getFloat(R.styleable.DayToShipLayout_dayFontSize, 8);
        typedArray.recycle();

        textViewLabel = (TextView) findViewById(R.id.textViewLabel);
        textViewColon = (TextView) findViewById(R.id.textViewColon);
        textViewDay = (AutoFitTextView) findViewById(R.id.textViewDay);
        textViewPeriod = (TextView) findViewById(R.id.textViewPeriod);
        textViewTilde = (TextView) findViewById(R.id.textViewTilde);

        textViewLabel.setTextSize(labelFontSize);
        textViewColon.setTextSize(labelColonSize);
        textViewDay.setTextSize(dayFontSize);
        textViewPeriod.setTextSize(periodFontSize);
        textViewTilde.setTextSize(tildFontSize);
    }

    /**
     * setFontSize
     * @param label
     * @param day
     * @param period
     */
    public void setFontSize(Float label, Float colon, Float day, Float period, Float tilde){
        if (label != null){
            labelFontSize = label;
            textViewLabel.setTextSize(label);
        }
        if (colon != null){
            labelColonSize = colon;
            textViewLabel.setTextSize(colon);
        }
        if (day != null){
            dayFontSize = day;
            textViewDay.setTextSize(day);
        }
        if (period != null){
            periodFontSize = period;
            textViewPeriod.setTextSize(period);
        }
        if (tilde != null){
            tildFontSize = tilde;
            textViewTilde.setTextSize(tilde);
        }
    }

    /**
     * SetDate
     * @param minValue
     * @param maxValue
     */
    public void SetDate(Integer minValue, Integer maxValue) {

		//両方無しは表示しない
        if (minValue==null && maxValue==null){
            this.setVisibility(View.GONE);
            return;
        }

		//min無しは表示しない
        if (minValue==null){
            this.setVisibility(View.GONE);
            return;
        }

        //デフォルト
        this.setVisibility(View.VISIBLE);

        //デフォルト
        textViewLabel.setText(R.string.daytoship_label_title);//通常出荷日
        textViewColon.setText(R.string.daytoship_label_colon_day); //：
        textViewPeriod.setText(R.string.daytoship_label_days);   //日目
        textViewTilde.setText(R.string.daytoship_label_tilde);     //～

//        AppLog.d("min=" + minValue + " max=" + maxValue);

        if (maxValue == null || minValue.equals(maxValue) ) {
            //最小通常出荷日(minStandardDaysToShip)のみ
            //最小通常出荷日(minStandardDaysToShip)と最大通常出荷日(maxStandardDaysToShip)が同一
            if (minValue == 0) {
                //0の場合：当日出荷
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.GONE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.GONE);
                textViewTilde.setVisibility(View.GONE);

            } else if (minValue == 99) {
                //99の場合：都度お見積り
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.GONE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.GONE);
                textViewTilde.setVisibility(View.GONE);

            } else {
                //それ以外の場合：対応パラメータ＋日目
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.VISIBLE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.VISIBLE);
                textViewTilde.setVisibility(View.GONE);
            }
        } else
		{

			//大小関係を修正
	        //if (maxValue < minValue) {
            if (maxValue.compareTo(minValue) < 0) {
	        	minValue = maxValue;
			}

            //最小通常出荷日(minStandardDaysToShip)<最大通常出荷日(maxStandardDaysToShip)
            if (minValue == 0){
                //0の場合：当日出荷
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.GONE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.GONE);
                textViewTilde.setVisibility(View.GONE);

            } else if (minValue == 99) {
                //99の場合：都度お見積り
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.GONE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.GONE);
                textViewTilde.setVisibility(View.GONE);

            } else {
                //1～98の場合：対応パラメータ＋日目
                textViewDay.setVisibility(View.VISIBLE);
                textViewColon.setVisibility(View.VISIBLE);
                textViewLabel.setVisibility(View.VISIBLE);
                textViewPeriod.setVisibility(View.VISIBLE);
                textViewTilde.setVisibility(View.VISIBLE);
	        }
        }


		final Integer minValueF = minValue;


        //文字のサイズ調整
        this.post(new Runnable() {
            @Override
            public void run() {

                int dayWidth;
                //                AppLog.d("parentWidth=" + parentWidth);
                dayWidth = DayToShipLayout.this.getWidth();

                if (textViewLabel.getVisibility() == View.VISIBLE){
                    int textViewLabelWidth = textViewLabel.getWidth();
//                    AppLog.d("textViewLabelWidth=" + textViewLabelWidth);
                    dayWidth = dayWidth - textViewLabelWidth;
                }
                if (textViewColon.getVisibility() == View.VISIBLE){
                    int textViewColonWidth = textViewColon.getWidth();
//                    AppLog.d("textViewColonWidth=" + textViewColonWidth);
                    dayWidth = dayWidth - textViewColonWidth;
                }
                if (textViewPeriod.getVisibility() == View.VISIBLE){
                    int textViewPeriodWidth = textViewPeriod.getWidth();
//                    AppLog.d("textViewPeriodWidth=" + textViewPeriodWidth);
                    dayWidth = dayWidth - textViewPeriodWidth;
                }
                if (textViewTilde.getVisibility() == View.VISIBLE){
                    int textViewTildeWidth = textViewTilde.getWidth();
//                    AppLog.d("textViewTildeWidth=" + textViewTildeWidth);
                    dayWidth = dayWidth - textViewTildeWidth;
                }
                dayWidth = dayWidth - (5* AppConfig.getInstance().dp)*2;
//                AppLog.d("dayWidth=" + dayWidth);
                textViewDay.setMinWidth(1);
                textViewDay.setMaxWidth(dayWidth);

                textViewDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
                textViewDay.setText(getDayToShip(minValueF));

                textViewDay.post(new Runnable() {
                    @Override
                    public void run() {
//                        int size = textViewDay.getWidth();
//                        AppLog.d("daySize=" + size);

                        textViewDay.requestLayout();
                    }
                });
            }
        });
    }

    private String getDayToShip(Integer min) {
        String result;

        if (min == 0) {
            result = getResourceString(R.string.daytoship_label_day);

        } else if (min == 99) {

            result = getResourceString(R.string.daytoship_label_estimate);
        } else {

            result = min.toString();
        }

//        AppLog.d("result=" + result);
        return result;
    }
}

