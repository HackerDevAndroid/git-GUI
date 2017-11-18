package jp.co.misumi.misumiecapp.util;

import android.view.View;
import android.view.ViewGroup;


/**
 * ViewUtil
 */
public class ViewUtil {

	public static void setSplitMotionEventsToAll(View view) {

		if (view instanceof ViewGroup) {
			ViewGroup g = (ViewGroup) view;
			// android:splitMotionEvents="false"
			g.setMotionEventSplittingEnabled(false);

			final int count = g.getChildCount();
			for (int i = 0; i < count; ++i) {
				setSplitMotionEventsToAll(g.getChildAt(i));
			}

		}
	}

}

