package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import jp.co.misumi.misumiecapp.util.ViewUtil;

/**
 * Created by ost000422 on 2015/08/25.
 */
public class CategorySearchListAdapter extends ArrayAdapter<CategoryList.Category> {

    private final LayoutInflater mInflater;

    private final int mResource;
    //--ADD NT-LWL 17/07/06 Category FR -
    private List<CategoryList.Category> replaceList;
    // 设置数据
    public void setReplaceList(List<CategoryList.Category> replaceList) {
        this.replaceList = replaceList;
    }
    //--ADD NT-LWL 17/07/06 Category TO -

    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);
    }

    public CategorySearchListAdapter(Context context, int resource, List<CategoryList.Category> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }

    public View getView(final int position, View convertView, ViewGroup parent){

        final CategoryList.Category itemData = getItem(position);

		if (itemData == null) {
            convertView = new View(getContext());
	        return convertView;
		}

        //--ADD NT-LWL 17/07/06 Category FR -
        // 不为空时才作判断
        if (replaceList != null){
            // categoryCode相等时替换图片地址
            for (CategoryList.Category category : replaceList){
                if (!TextUtils.isEmpty(itemData.categoryCode)&&!TextUtils.isEmpty(category.categoryCode)) {
                    if (itemData.categoryCode.equals(category.categoryCode)) {
                        itemData.categoryImageUrl = category.categoryImageUrl;
                        itemData.categoryName = category.categoryName;
                    }
                }
            }
        }
        //--ADD NT-LWL 17/07/06 Category TO -
        ViewHolder holder;

//        if (convertView == null){
            convertView = mInflater.inflate(mResource, parent, false);
			ViewUtil.setSplitMotionEventsToAll(convertView);

            holder = new ViewHolder();
            holder.subCategoryName = (TextView) convertView.findViewById(R.id.subcategoryName);
            holder.image = (ImageView) convertView.findViewById(R.id.subCategoryImage);
			holder.pv = convertView.findViewById(R.id.progressView);

            holder.arrow = convertView.findViewById(R.id.list_arrorw_right);

            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        holder.subCategoryName.setText(itemData.categoryName);

        PicassoUtil.PicassoLoad(holder.image, holder.pv, itemData.categoryImageUrl);

        return convertView;
    }

    class ViewHolder {

        TextView subCategoryName;
        ImageView image;
        View arrow;
        View pv;
    }
}
