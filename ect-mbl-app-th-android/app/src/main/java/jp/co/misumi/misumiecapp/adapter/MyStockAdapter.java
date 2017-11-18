package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * (3)在庫切れ
 */
public class MyStockAdapter extends CommonAdapter<ResponseCheckOrder.ItemInfo> {

    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    private final String mScreenId;

    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public MyStockAdapter(Context context, int resource, List<ResponseCheckOrder.ItemInfo> objects, String screenId) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        mScreenId = screenId;
    }


    /* (非 Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            ViewUtil.setSplitMotionEventsToAll(convertView);
        }

        // 1行分のアイテムデータを取得
        ResponseCheckOrder.ItemInfo itemInfo = getItem(position);

        makeChildView(convertView, itemInfo, position);

        return convertView;
    }


    private void makeChildView(View childView, final ResponseCheckOrder.ItemInfo itemInfo, final int position) {

        final LayoutInflater inflater = mInflater;


        View layoutError = childView.findViewById(R.id.layoutError);
        boolean errorMessageFlag = false;

        if (itemInfo.errorList != null && !itemInfo.errorList.ErrorInfoList.isEmpty()) {
            errorMessageFlag = true;
        }

        //エラー
        if (!errorMessageFlag) {

            layoutError.setVisibility(View.GONE);
        } else {


/*
            layoutError.setVisibility(View.GONE);

			final LinearLayout layoutErrorItemList = (LinearLayout) layoutError.findViewById(R.id.layoutSubItemList);
			layoutErrorItemList.removeAllViews();

			//
			int errorCnt = 0;
			for (ErrorList.ErrorInfo errorInfo: itemInfo.errorList.ErrorInfoList) {

				String errorStr = errorInfo.getErrorMessage(mScreenId);
		        if (android.text.TextUtils.isEmpty(errorStr)) {
					continue;
				}

				++errorCnt;


				View subView = inflateLayout(inflater, R.layout.include_item_stock_err, layoutErrorItemList, false);

				TextView errorMessage = (TextView) subView.findViewById(R.id.textMessage);
				errorMessage.setText(errorStr);

				layoutErrorItemList.addView(subView);
			}

			if (errorCnt > 0) {
				layoutError.setVisibility(View.VISIBLE);
			}
*/

            makeErrorItemList(inflater, mScreenId, layoutError, R.layout.include_item_stock_err, itemInfo.errorList.ErrorInfoList);

        }

        //
        TextView tv;

        tv = (TextView) childView.findViewById(R.id.orderDetailPartNumber);
        setTextEmptyGone(tv, itemInfo.partNumber);

        tv = (TextView) childView.findViewById(R.id.orderDetailProductName);
        setTextEmptyGone(tv, itemInfo.productName);

        tv = (TextView) childView.findViewById(R.id.orderDetailBrandName);
        setTextEmptyGone(tv, itemInfo.brandName);

        //出荷日赤文字
        setIncludeItemText(childView.findViewById(R.id.earliestShipDate),
                getResourceString(R.string.out_of_stock_dialog_list_day_to_ship), itemInfo.nextArrivalDate, false);


    }


    //以下2つをfalseで返すと選択が行えなくなる
    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return false;
    }

}
