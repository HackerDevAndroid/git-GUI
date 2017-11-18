package jp.co.misumi.misumiecapp.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;


/**
 * AutoFitTextView
 */
public class AutoFitTextView extends TextView {

    private int min_size = 1;
    private boolean mSpannableFlag;

    public AutoFitTextView(Context context) {
        super(context);
        init();
    }

    public AutoFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoFitTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
//        float scale = getContext().getResources().getDisplayMetrics().density;
//        int dp = (int) (1 * scale + 0.5f);
//        min_size = dp * 5;

        mSpannableFlag = false;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        autoFit();
    }

    /**
     * autoFit
     */
    private void autoFit() {

        int height, width;


        if (getText() instanceof Spanned) {
            mSpannableFlag = true;
        }
        Typeface type = getTypeface();
        if (type != null && type.isBold()) {
            mSpannableFlag = false;
        }

        height = getHeight();
        width = getWidth();

        float size = getTextSize();

        Paint paint = new Paint();
        paint.setTextSize(size);

        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = (Math.abs(fm.top)) + (Math.abs(fm.descent));
        float textWidth = paint.measureText(getText().toString());

        if (mSpannableFlag) {
            textWidth += paint.measureText("0");
        }

        while (height < textHeight | width < textWidth) {
            if (min_size >= size) {
                size = min_size;
                break;
            }
//            size--;
            size = size - 0.5f;

            paint.setTextSize(size);

            fm = paint.getFontMetrics();
            textHeight = (Math.abs(fm.top)) + (Math.abs(fm.descent));

            textWidth = paint.measureText(this.getText().toString());
            if (mSpannableFlag) {
                textWidth += paint.measureText("0");
            }
        }

//        String log = getText().toString();
//        AppLog.d("log = " + log);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

//	public void setSpannableFlag() {
//		mSpannableFlag = true;
//        autoFit();
//	}

}

