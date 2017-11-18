package jp.co.misumi.misumiecapp.util;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

/**
 * SpannableUtil
 */
public class SpannableUtil {

	public static StyleSpan getBoldStyleSpan() {

		return new StyleSpan(Typeface.BOLD);
	}

	public static ForegroundColorSpan getRedColorSpan() {

		return new ForegroundColorSpan(0xFFDD0000);
	}

	public static ForegroundColorSpan getForegroundColorSpan(int color) {

		return new ForegroundColorSpan(color);
	}

	public static StrikethroughSpan getStrikethroughSpan() {

		return new StrikethroughSpan();
	}

	public static AbsoluteSizeSpan getAbsoluteSizeSpan(int size) {

		return new AbsoluteSizeSpan(size, true);
	}

	public static RelativeSizeSpan getRelativeSizeSpan(float size) {

		return new RelativeSizeSpan(size);
	}


	//getAbsoluteSizeSpan
	public static void setSpan(SpannableString ss, int size, boolean isRed, boolean isBold) {

		if (size > 0) {
	        ss.setSpan(getAbsoluteSizeSpan(size), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (isRed) {
	        ss.setSpan(getRedColorSpan(), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (isBold) {
	        ss.setSpan(getBoldStyleSpan(), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}


	public static void setSpan(SpannableString ss, int size, int color, boolean isBold) {

		if (size > 0) {
	        ss.setSpan(getAbsoluteSizeSpan(size), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

        ss.setSpan(getForegroundColorSpan(color), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		if (isBold) {
	        ss.setSpan(getBoldStyleSpan(), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}


	public static SpannableString newSpannableString(String str, int size, boolean isRed, boolean isBold) {

		SpannableString ss = new SpannableString(str);
		setSpan(ss, size, isRed, isBold);

		return ss;
	}


	public static SpannableString newSpannableString(String str, int size, int color, boolean isBold) {

		SpannableString ss = new SpannableString(str);
		setSpan(ss, size, color, isBold);

		return ss;
	}


	//getRelativeSizeSpan
	public static void setSpan(SpannableString ss, float size, boolean isRed, boolean isBold) {

		if (size > 0) {
	        ss.setSpan(getRelativeSizeSpan(size), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (isRed) {
	        ss.setSpan(getRedColorSpan(), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (isBold) {
	        ss.setSpan(getBoldStyleSpan(), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}


	public static SpannableString newSpannableString(String str, float size, boolean isRed, boolean isBold) {

		SpannableString ss = new SpannableString(str);
		setSpan(ss, size, isRed, isBold);

		return ss;
	}

}
