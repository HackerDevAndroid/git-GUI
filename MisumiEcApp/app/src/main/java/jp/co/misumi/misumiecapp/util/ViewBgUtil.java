package jp.co.misumi.misumiecapp.util;

import android.view.View;
import android.widget.TextView;


/**
 * ViewBgUtil
 */
public class ViewBgUtil {

    public static void requestLayout(View parentView, int baseId, int leftId, int rightId) {

        final View viewBase = parentView.findViewById(baseId);
        final View viewLeft = parentView.findViewById(leftId);
        final View viewRight = parentView.findViewById(rightId);

		viewBase.post(new Runnable() {

			@Override
			public void run() {

				final int height = viewBase.getHeight();

				viewLeft.getLayoutParams().height = height;
				viewRight.getLayoutParams().height = height;
				viewLeft.requestLayout();
				viewRight.requestLayout();
			}
		}); 
	}

	public static void requestLayout(View parentView, TextView textView1, TextView textView2) {

		new Layout(parentView, textView1, textView2).calc();
	}
	public static void requestLayout(View parentView, int textView1Id, int textView2Id) {

		TextView textView1 = (TextView) parentView.findViewById(textView1Id);
		TextView textView2 = (TextView) parentView.findViewById(textView2Id);
		new Layout(parentView, textView1, textView2).calc();
	}

	private static class Layout {
		private View parentView;
		private TextView textView1;
		private TextView textView2;

		public Layout(View parentView, TextView textView1, TextView textView2) {
			this.parentView = parentView;
			this.textView1 = textView1;
			this.textView2 = textView2;
		}

		public void calc() {

			if (textView1 == null || textView2 == null || parentView == null) {
				return;
			}

			parentView.post(new Runnable() {
				@Override
				public void run() {
					if (textView1.getLineCount() > textView2.getLineCount()) {
						textView2.setMinLines(textView1.getLineCount());
					} else if (textView1.getLineCount() < textView2.getLineCount()) {
						textView1.setMinLines(textView2.getLineCount());
					}
				}
			});
		}
	}

}

