package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.widget.DayToShipLayout;
import jp.co.misumi.misumiecapp.widget.UnitPriceLayout;
import jp.co.misumi.misumiecapp.util.ViewUtil;
import jp.co.misumi.misumiecapp.util.MsmFormat;

/**
 * Created by ost000422 on 2015/08/31.
 */
public class SearchSeriesAdapter extends ArrayAdapter<SearchSeriesList.Series> {

    private final LayoutInflater mInflater;

    private final int mResource;

//    MISUMI_MOBILE_APP-559 【検索結果】【シリーズ一覧】SALEの場合は価格表示を「特別価格」とする
    private boolean sale;


    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
    }

    public SearchSeriesAdapter (Context context, int resource, List<SearchSeriesList.Series> objects){
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
			ViewUtil.setSplitMotionEventsToAll(convertView);

            holder = new ViewHolder();
            holder.textPartsName = (TextView) convertView.findViewById(R.id.textPartsName);
            holder.seriesName = (TextView) convertView.findViewById(R.id.seriesName);
            holder.brandCode = (TextView) convertView.findViewById(R.id.brandName);
//            holder.catchCopy = (TextView) convertView.findViewById(R.id.catchCopy);
            holder.dayToShipLayout = (DayToShipLayout) convertView.findViewById(R.id.viewDayToShip);
            holder.unitPriceLayout = (UnitPriceLayout) convertView.findViewById(R.id.viewUnitPrice);
            holder.imageView = (ImageView) convertView.findViewById(R.id.productImage);
            holder.progressView = convertView.findViewById(R.id.progressView);

            holder.picts[0] = (TextView) convertView.findViewById(R.id.textViewPict1);
            holder.picts[1] = (TextView) convertView.findViewById(R.id.textViewPict2);
            holder.picts[2] = (TextView) convertView.findViewById(R.id.textViewPict3);
            holder.layoutPictArea = (LinearLayout) convertView.findViewById(R.id.layoutPictArea);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchSeriesList.Series itemData = getItem(position);

        if (!android.text.TextUtils.isEmpty(itemData.partNumber)) {
            holder.textPartsName.setText(itemData.partNumber);
            holder.textPartsName.setVisibility(View.VISIBLE);
        } else {
			//空とnullはトルツメにするに修正する。
            holder.textPartsName.setText(null);
            holder.textPartsName.setVisibility(View.GONE);
        }

        holder.seriesName.setText(itemData.seriesName);

        holder.brandCode.setText(itemData.brandName);

//        holder.catchCopy.setText(itemData.catchCopy);

        AppLog.v("position=" + position);

        holder.dayToShipLayout.SetDate(itemData.minStandardDaysToShip, itemData.maxStandardDaysToShip);


//        MISUMI_MOBILE_APP-559 【検索結果】【シリーズ一覧】SALEの場合は価格表示を「特別価格」とする
        //アイコン最大３個表示
        int pict_idx = 0;


        if (itemData.productImageUrlList.size() > 0) {
            PicassoUtil.PicassoLoad(holder.imageView, holder.progressView, itemData.productImageUrlList.get(0));
        }else{
            PicassoUtil.PicassoLoad(holder.imageView, holder.progressView, null);
        }

        //おすすめアイコン
        if ((itemData.recommendFlag != null) && (!itemData.recommendFlag.isEmpty())){
            if (itemData.recommendFlag.equals("1")){
                holder.picts[pict_idx].setText(getResourceString(R.string.search_series_recommend));
				setIconDesign(holder.picts[pict_idx], 1);

                holder.picts[pict_idx].setVisibility(View.VISIBLE);
                pict_idx++;
            }
        }

        //SALEアイコン
        String campainEndDate = MsmFormat.convertCampainEndDate(getContext(), itemData.campainEndDate);

        if (!android.text.TextUtils.isEmpty(campainEndDate)) {
            holder.picts[pict_idx].setText(campainEndDate);
            setIconDesign(holder.picts[pict_idx], 2);

            holder.picts[pict_idx].setVisibility(View.VISIBLE);
            pict_idx++;

            sale = true;
        } else {
            sale = false;
        }

        holder.unitPriceLayout.SetPrice(itemData.minStandardUnitPrice, itemData.maxStandardUnitPrice, sale);

        //エコノミーアイコン
        if ((itemData.gradeType !=null) && (!itemData.gradeType.isEmpty()) ) {
            if (itemData.gradeType.equals("1")) {
                holder.picts[pict_idx].setText(getResourceString(R.string.search_series_grade));
				setIconDesign(holder.picts[pict_idx], 1);

                holder.picts[pict_idx].setVisibility(View.VISIBLE);
                pict_idx++;
            }
        }

        if (pict_idx == 0){
            // ピクトない場合はエリアごと非表示
            holder.layoutPictArea.setVisibility(View.GONE);
        }else {
            holder.layoutPictArea.setVisibility(View.VISIBLE);
            // 使用しなかったピクトは非表示
            for (int ii = pict_idx; ii < holder.picts.length; ii++) {
                holder.picts[ii].setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }



	private void setIconDesign(TextView textIcon, int type) {

		textIcon.setBackgroundDrawable(null);

		switch (type) {
		case 1:
			textIcon.setTextColor(0xFFDD0000);
			textIcon.setBackgroundResource(R.drawable.text_frame_red_1dp);
			break;

		case 2:
			textIcon.setTextColor(0xFFFFFFFF);
			textIcon.setBackgroundResource(R.drawable.text_frame_red_fill);
			break;

		case 3:
			textIcon.setTextColor(0xFFFFFFFF);
			textIcon.setBackgroundColor(0xFF749D9D);
			break;
		}

		textIcon.setPadding(0, 0, 0, 0);
	}


    class ViewHolder {

        TextView textPartsName;
        TextView seriesName;
        TextView brandCode;
        UnitPriceLayout unitPriceLayout;
        DayToShipLayout dayToShipLayout;
        ImageView imageView;
        View progressView;
        TextView picts[] = {
                null,null,null
        };
        LinearLayout layoutPictArea;
    }

    protected String getResourceString(int id){
        return getContext().getString(id);
    }
}
