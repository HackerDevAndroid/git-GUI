package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ErrorList;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.ViewUtil;

/**
 * ArrayAdapter
 */
public class CommonAdapter<T> extends ArrayAdapter<T> {


    public CommonAdapter(Context context, int resource, List<T> objects) {

        super(context, resource, objects);
    }

    protected String getResourceString(int id) {
        return getContext().getString(id);
    }


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, boolean doHidden) {


        if (android.text.TextUtils.isEmpty(str2)) {

            if (doHidden) {
                subView.setVisibility(View.GONE);
                return;
            }

            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        subView.setVisibility(View.VISIBLE);

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }


    protected void setIncludeItemText(View subView, String str1, boolean doHidden) {
        subView.setVisibility(View.VISIBLE);

        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }


/*
    protected void setIncludeItemText(View subView, String str1, String str2, String str3) {

		if (str2 == null || str2.isEmpty()) {

			str2 = getResourceString(R.string.label_hyphen);	//ハイフン化
		}

		((TextView)subView.findViewById(R.id.textView3)).setText(str3);
		((TextView)subView.findViewById(R.id.textView2)).setText(str2);
		((TextView)subView.findViewById(R.id.textView1)).setText(str1);
	}
*/

    protected void setIncludeItemText(View subView, String str1, String str2, boolean doHidden) {

        if (str2 == null || str2.isEmpty()) {

            if (doHidden) {
                subView.setVisibility(View.GONE);
                return;
            }

            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        subView.setVisibility(View.VISIBLE);

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }

    protected void setIncludeItemTextRed(View subView, String str1, String str2, boolean doHidden) {
        setIncludeItemText(subView, str1, str2, doHidden);
        int color = ContextCompat.getColor(getContext(), R.color.color_error_frame_red_DD);
        ((TextView) subView.findViewById(R.id.textView2)).setTextColor(color);
        //((TextView)subView.findViewById(R.id.textView1)).setTextColor(color);
    }

    protected void setIncludeItemText(View subView, String str1, String str2, String format) {

        if (str2 == null) {
            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(String.format(format, str2));
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
    }


    protected String getUnitPriceString(String currencyFormat, Double unitPrice) {

        if (unitPrice == null) {
            return null;
        }

        String str = Format.formatAmount(unitPrice);
        str = String.format(currencyFormat, str);
        return str;
    }


    protected void setTextEmptyGone(TextView tv, String str) {
        if (str == null || str.isEmpty()) {
            tv.setText("");
            tv.setVisibility(View.GONE);
            return;
        }

        tv.setText(str);
        tv.setVisibility(View.VISIBLE);
    }


    //
    protected String convertShipDateTime(String shipDateTime) {//出荷日

        return MsmFormat.convertShipDateTime(getContext(), shipDateTime);
    }

    protected String convertExpressType(String expressType) {//緊急出荷サービス

        return MsmFormat.convertExpressType(getContext(), expressType, false);
    }

    protected String convertStatusQuote(String status) {//status

        return MsmFormat.convertStatusQuote(getContext(), status);
    }

    protected String convertStatusOrder(String status) {//status

        return MsmFormat.convertStatusOrder(getContext(), status);
    }

    protected String convertShip(Integer daysToShip, String shipType) {//daysToShip

        return MsmFormat.convertShip(getContext(), daysToShip, shipType);
    }

    protected String convertShipQ(Integer daysToShip, String shipType) {//daysToShip

        return MsmFormat.convertShip(getContext(), daysToShip, shipType, true);
    }

    //
    protected String convertDaysToShipUnit2(Integer daysToShip, boolean isQuote) {//daysToShip

        return MsmFormat.convertDaysToShipUnit2(getContext(), daysToShip, isQuote);
    }

    protected String convertDaysToShipUnit3(Integer daysToShip, boolean isQuote) {//daysToShip

        return MsmFormat.convertDaysToShipUnit3(getContext(), daysToShip, isQuote);
    }


    protected String convertShipType(String shipType) {//shipType

        return MsmFormat.convertShipType(getContext(), shipType);
    }


    protected View inflateLayout(LayoutInflater inflater, int resource, ViewGroup root, boolean attachToRoot) {

        View view = inflater.inflate(resource, root, attachToRoot);
        ViewUtil.setSplitMotionEventsToAll(view);
        return view;
    }


    //エラー
    protected int makeErrorItemList(LayoutInflater inflater, String screenId, View layoutError, int resId, ArrayList<ErrorList.ErrorInfo> errorInfoList) {

        layoutError.setVisibility(View.GONE);

        final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
        layoutErrorItemList.removeAllViews();

        //
        int errorCnt = 0;
        for (ErrorList.ErrorInfo errorInfo : errorInfoList) {

            String errorStr = errorInfo.getErrorMessage(screenId);
            if (android.text.TextUtils.isEmpty(errorStr)) {
                continue;
            }

            ++errorCnt;


            View subView = inflateLayout(inflater, resId, layoutErrorItemList, false);

            TextView errorMessage = (TextView) subView.findViewById(R.id.textMessage);
            errorMessage.setText(errorStr);

            layoutErrorItemList.addView(subView);
        }

        if (errorCnt > 0) {
            layoutError.setVisibility(View.VISIBLE);
        }

        return errorCnt;
    }

}
