package jp.co.misumi.misumiecapp.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClickableText
 * 文字列の任意の部分をクリック可能にする
 */
public class ClickableText {

    public interface OnClickListener {
        void onLinkClick(int textId);
    }

    private static class ClickableSpanEx extends ClickableSpan {

        private OnClickListener mListener;
        private int mTextId;

        public ClickableSpanEx(int textId, OnClickListener listener) {
            mTextId = textId;
            mListener = listener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) mListener.onLinkClick(mTextId);
        }
    }

    public static SpannableString getClickableText(String text, SparseArray<String> links, OnClickListener listener) {
        SpannableString spannable = new SpannableString(text);

        int size = links.size();
        for (int i = 0; i < size; i++) {
            int key = links.keyAt(i);
            String link = links.get(key);
            Pattern p = Pattern.compile(link);
            Matcher m = p.matcher(text);

            while (m.find()) {
                ClickableSpanEx span = new ClickableSpanEx(key, listener);
                spannable.setSpan(span, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannable;
    }
}

