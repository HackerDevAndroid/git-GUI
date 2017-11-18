package jp.co.misumi.misumiecapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * An enhanced {@code CheckBox} that differentiates between user clicks and
 * programmatic clicks. In particular, the {@code OnCheckedChangeListener} is
 * <strong>not</strong> triggered when the state of the checkbox is changed
 * programmatically.
 */
public class CheckBoxEx extends CheckBox implements ProgrammaticallyCheckable {

    private CompoundButton.OnCheckedChangeListener mListener = null;

    public CheckBoxEx(Context context) {
        super(context);
    }

    public CheckBoxEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {

//    if (this.mListener == null) {this.mListener = listener;}

        mListener = listener;
        super.setOnCheckedChangeListener(listener);
    }

    /**
     * Set the checked state of the checkbox programmatically. This is to differentiate it from a user click
     *
     * @param checked Whether to check the checkbox
     */
    @Override
    public void setCheckedEx(boolean checked) {

        super.setOnCheckedChangeListener(null);
        super.setChecked(checked);
        super.setOnCheckedChangeListener(mListener);
    }
}

