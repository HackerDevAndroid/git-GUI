package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ArrayAdapter
 */
public class CategorySearchAdapter extends ArrayAdapter<CategoryList.Category> {

    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    //タイムアウトの時間
//	private final int loadImageTimeout = AppConst.ConnectTimeout;


    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
    }


    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public CategorySearchAdapter(Context context, int resource, List<CategoryList.Category> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }


    /* (非 Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            ViewUtil.setSplitMotionEventsToAll(convertView);

            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.image = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
            holder.pv = convertView.findViewById(R.id.progressView);
            holder.frameLayout = convertView.findViewById(R.id.frameLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 1行分のアイテムデータを取得
        final CategoryList.Category itemData = getItem(position);

        //読み込み処理
//		holder.image.setVisibility(View.VISIBLE);
        if (SubsidiaryCode.isJapan()) {
            if (itemData.categoryImageUrl == null || itemData.categoryImageUrl.isEmpty()) {
                holder.frameLayout.setVisibility(View.GONE);

//			holder.categoryName.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            } else {
                holder.frameLayout.setVisibility(View.VISIBLE);

                //UDP NT-LWL 16/11/28 AliPay Payment FR
//			PicassoUtil.PicassoLoad(holder.image, holder.pv, itemData.categoryImageUrl);
                if (SubsidiaryCode.isJapan()) {
                    PicassoUtil.PicassoLoad(holder.image, holder.pv, itemData.categoryImageUrl);
                } else {
                    PicassoUtil.PicassoLoadCategory(holder.image, holder.pv, itemData.categoryImageUrl);
                }
                //UDP NT-LWL 16/11/28 AliPay Payment TO
//			holder.categoryName.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            }
        } else {
            //-- ADD NT-LWL 17/07/3 Category FR -
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.categoryName.getLayoutParams();
//            if (position%2 == 0){
//				layoutParams.rightMargin = DensityUtil.dip2px(parent.getContext(),7);
//				holder.frameLayout.setPadding(DensityUtil.dip2px(parent.getContext(),15),DensityUtil.dip2px(parent.getContext(),10),
//											  DensityUtil.dip2px(parent.getContext(),7),DensityUtil.dip2px(parent.getContext(),10));
//            }else {
//				layoutParams.leftMargin = DensityUtil.dip2px(parent.getContext(),7);
//				holder.frameLayout.setPadding(DensityUtil.dip2px(parent.getContext(),7),DensityUtil.dip2px(parent.getContext(),10),
//											  DensityUtil.dip2px(parent.getContext(),15),DensityUtil.dip2px(parent.getContext(),10));
//            }

            //-- ADD NT-LWL 17/07/3 Category TO -

            //-- UDP NT-LWL 17/09/25 Category FR -
//            if(itemData.categoryCode.equals("MECH0200000")){//螺钉/螺栓/垫圈/螺帽
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_bolt);
//			}else if(itemData.categoryCode.equals("EL010000000")){//接线
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_connection);
//			}else if(itemData.categoryCode.equals("FS020000000")){//生产加工用品
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_produce);
//			}else if(itemData.categoryCode.equals("FS040000000")) {//安全用品/办公用品
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_safe);
//			}else if (itemData.categoryCode.equals("FS030000000")){//捆包用品/物流保管用品
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_bale);
//			}else if (itemData.categoryCode.equals("MECH0100000")){//工厂自动化零件
//				itemData.categoryName = "设备维护用品";
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_plant);
//			}else if (itemData.categoryCode.equals("EL020000000")){//控制
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_control);
//			}else if (itemData.categoryCode.equals("FS010000000")){//切削工具
//				holder.pv.setVisibility(View.GONE);
//				holder.image.setImageResource(R.drawable.category_cut);
//			}else{
//				PicassoUtil.PicassoLoadCategory(holder.image, holder.pv, itemData.categoryImageUrl);
//			}
            if (itemData.categoryCode.equals("mech_screw")) {//螺钉/螺栓/垫圈/螺帽
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_bolt);
            } else if (itemData.categoryCode.equals("el_wire")) {//接线
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_connection);
            } else if (itemData.categoryCode.equals("fs_processing")) {//生产加工用品
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_produce);
            } else if (itemData.categoryCode.equals("fs_health")) {//安全用品/办公用品
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_safe);
            } else if (itemData.categoryCode.equals("fs_logistics")) {//捆包用品/物流保管用品
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_bale);
            } else if (itemData.categoryCode.equals("mech")) {//工厂自动化零件
                itemData.categoryName = "设备维护用品";
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_plant);
            } else if (itemData.categoryCode.equals("el_control")) {//控制
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_control);
            } else if (itemData.categoryCode.equals("fs_machining")) {//切削工具
                holder.pv.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.category_cut);
            } else {
                PicassoUtil.PicassoLoadCategory(holder.image, holder.pv, itemData.categoryImageUrl);
            }
            //-- UDP NT-LWL 17/09/25 Category TO -
        }

        if (itemData.categoryName == null || itemData.categoryName.isEmpty()) {
            holder.categoryName.setText("");
        } else {
            holder.categoryName.setText(itemData.categoryName);
        }

//		if (itemData.categoryImageUrl == null || itemData.categoryImageUrl.isEmpty()){
//			convertView.findViewById(R.id.imageViewThumbnail).setVisibility(View.GONE);
//			convertView.findViewById(R.id.textViewName).setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
//		} else {
//			ImageView imageThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
//			PicassoUtil.PicassoLoad(imageThumbnail, itemData.categoryImageUrl);
//		}

        //項目を動的に追加する
//		LinearLayout itemListLayout = (LinearLayout) convertView.findViewById(R.id.itemListLayout);
//		itemListLayout.removeAllViews();
//
//
//		for (CategoryList.Category itemInfo: itemData.mItemList) {
//			View childView = mInflater.inflate(R.layout.z_list_item_estimate_list_item, itemListLayout, false);
//
//
//			ImageView iv = (ImageView) childView.findViewById(R.id.imageView);
//
//			String imageUrl = itemInfo.productImageUrl;
//			PicassoUtil.PicassoLoad(iv, imageUrl);
//
//			itemListLayout.addView(childView);
//		}
//
//		final View buttonDetail = convertView.findViewById(R.id.buttonDetail);
//		buttonDetail.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				AppLog.e("position: "+ position);
////				convertViewF.performClick();
//
//				mOnItemClickListener.onItemClick(CategorySearchAdapter.this, null, position, getItemId(position));
//
//			}
//		});
//		buttonDetail.setText("");

        return convertView;
    }

    class ViewHolder {

        TextView categoryName;
        ImageView image;
        View pv;
        View frameLayout;
    }

}
