package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.ResponseKeywordSearch;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.util.BrandSearchUtils;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;
import jp.co.misumi.misumiecapp.widget.DayToShipLayout;
import jp.co.misumi.misumiecapp.widget.UnitPriceLayout;


/**
 * Created by ost000422 on 2015/08/31.
 */
public class MyExpandListAdapter extends BaseExpandableListAdapter {

    private List<Element> mElements;
    private Context mContext;
    //--ADD NT-LWL 17/09/06 BrandSearch FR -
    private BrandSearchUtils brandSearchUtils;

    public void setBrandSearchUtils(BrandSearchUtils brandSearchUtils) {
        this.brandSearchUtils = brandSearchUtils;
    }

    //--ADD NT-LWL 17/09/06 BrandSearch TO -

    private final LayoutInflater mInflater;

//    private final int mResource;

	private final OnItemClickListener	mOnItemClickListener;

	private Element elementCount;

    private boolean sale;

    private Context getContext() {
		return mContext;
	}

    public interface OnItemClickListener {
        void onItemClick(SearchSeriesList.Series itemData, View view, int position);
	    void onItemCategory(CategoryList.Category info);
    }

    public MyExpandListAdapter(Context context, ResponseKeywordSearch response, OnItemClickListener onItemClickListener) {
//        super(context, resource, objects);
        mContext = context;

		//ここでElement形式に変換する
		mElements = new ArrayList<>();
		ArrayList<CategoryList.Category> mCategoryList = response.mCategoryList;
		if (mCategoryList != null && !mCategoryList.isEmpty()) {
			Element element = new Element(1, mCategoryList);
			mElements.add(element);
		}

        //--ADD NT-LWL 17/09/04 BrandSearch FR -
        // 添加品牌检索按钮
        Element e1 = new Element(4,20);
        mElements.add(e1);
        //--ADD NT-LWL 17/09/04 BrandSearch TO -

		//件数
		{
	        int totalCount = (response.totalCount != null) ? response.totalCount : 0;
			Element element = new Element(2, totalCount);
			mElements.add(element);
			elementCount = element;
		}

/*
		//商品はここでは足さない
		List<SearchSeriesList.Series> list = response.mSeriesList;
		if (list != null && !list.isEmpty()) {
			for (SearchSeriesList.Series item: list) {
				Element element = new Element(3, item);
				mElements.add(element);
			}
		}
*/

		//
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mResource = resource;

		mOnItemClickListener = onItemClickListener;

    }

    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    public void clearSeriesList(){
        Element e1 = null;
        Element e2 = null;
        Element e3 = null;
        if (mElements.size()>=1) {
            e1 = mElements.get(0);
        }
        if (mElements.size()>=2) {
            e2 = mElements.get(1);
        }
        if (mElements.size()>=3) {
            e3 = mElements.get(2);
        }
        mElements.clear();
        if (e1 != null) {
            mElements.add(e1);
        }
        if (e2 != null) {
            mElements.add(e2);
        }
        if (e3 != null && e3.getObjectType() == 2) {
            mElements.add(e3);
        }
    }
    //--ADD NT-LWL 17/09/08 BrandSearch TO -
    public void addAllAndUpdate(ResponseKeywordSearch response) {

		//件数
		{
	        int totalCount = (response.totalCount != null) ? response.totalCount : 0;
			if (elementCount != null) {
				elementCount.setObject(totalCount);
			}
		}

		//商品
		List<SearchSeriesList.Series> list = response.mSeriesList;
		if (list != null && !list.isEmpty()) {
			for (SearchSeriesList.Series item: list) {
				Element element = new Element(3, item);
				mElements.add(element);
			}
		}

    }




    private View getViewX(Element element, ViewGroup parent) {

        final ViewHolder holder;
		View convertView = null;

        if (convertView == null) {

			int resId = element.getResId();

            convertView = mInflater.inflate(resId, parent, false);
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

//        final SearchSeriesList.Series itemData = getItem(position);
        final SearchSeriesList.Series itemData = (SearchSeriesList.Series)element.getObject();

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

//        AppLog.v("position=" + position);

        holder.dayToShipLayout.SetDate(itemData.minStandardDaysToShip, itemData.maxStandardDaysToShip);

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

            //MISUMI_MOBILE_APP-559 【検索結果】【シリーズ一覧】SALEの場合は価格表示を「特別価格」とする
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


//		View layoutTapItem = childView.findViewById(R.id.layoutTapItem);
		convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

				mOnItemClickListener.onItemClick(itemData, null, 0);
            }
        });

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




	//
    @Override
    public Object getChild(int arg0, int arg1)
    {
        return mElements.get(arg0).getChildren().get(arg1);
    }

    @Override
    public long getChildId(int arg0, int arg1)
    {
        return arg1;
    }

    @Override
    public View getChildView(int arg0, int arg1, boolean arg2, View view, ViewGroup parent)
    {
         /**
        * 子アイテムの描画
         */
        Element element = (Element)getChild(arg0, arg1);
		CategoryList.Category info = (CategoryList.Category)element.getObject();

		int resId = element.getResId();
        view = mInflater.inflate(resId, parent, false);


        TextView textView1 = (TextView) view.findViewById(R.id.subcategoryName);
        textView1.setText(info.categoryName);

		//画像
		ImageView iv = (ImageView) view.findViewById(R.id.subCategoryImage);
		View pv = view.findViewById(R.id.progressView);

		String imageUrl = info.categoryImageUrl;
		PicassoUtil.PicassoLoad(iv, pv, imageUrl);

		//


        final CategoryList.Category infoF = info;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

				if (mOnItemClickListener == null) {
					return;
				}

				mOnItemClickListener.onItemCategory(infoF);

/*
				//子カテゴリ有りフラグ 0: 子カテゴリ無し
				if ("0".equals(infoF.hasChildCategoryFlag)) {
					//子カテゴリなしの場合にシリーズ検索
					doSearchSeries(infoF);
				} else {
                    doCategorySearch(infoF);
				}
*/
            }
        });



        return view;
    }

    @Override
    public int getChildrenCount(int arg0)
    {
        return mElements.get(arg0).getChildren().size();
    }

    @Override
    public Object getGroup(int arg0)
    {
        return mElements.get(arg0);
    }

    @Override
    public int getGroupCount()
    {
        return mElements.size();
    }

    @Override
    public long getGroupId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getGroupView(int arg0, boolean isExpanded, View view, ViewGroup parent)
    {
        Element element = mElements.get(arg0);

/*
        if(element.isParent())
        {}
        else
        {}
*/

		if (element.getObjectType() == 1) {
			//カテゴリ
			int resId = element.getResId();
            view = mInflater.inflate(resId, parent, false);

	        int categoryCount = element.getChildren().size();

        ((TextView) view.findViewById(R.id.textSlideSwitch)).setText(String.format(getResourceString(R.string.search_keyword_category_count),
                Format.formatCount(categoryCount)));

			//つまみ
	        View slideSwitch = view.findViewById(R.id.imageSlideSwitch);
			slideSwitch.setSelected(isExpanded);

		} else

		if (element.getObjectType() == 2) {
			//件数
			int resId = element.getResId();
            view = mInflater.inflate(resId, parent, false);
            // -- ADD NT-LWL 17/01/24 AliPay Payment FR -
            View tip = view.findViewById(R.id.search_keyword_tip);
            if (SubsidiaryCode.isChinese()){
                tip.setVisibility(View.VISIBLE);
            }else {
                tip.setVisibility(View.GONE);
            }
            // -- ADD NT-LWL 17/01/24 AliPay Payment FR -
	        TextView tv = (TextView) view.findViewById(R.id.textCount);

	        int totalCount = (int) element.getObject();
			String count = Format.formatCount(totalCount) + getResourceString(R.string.search_keyword_total);
			tv.setText(count);

		} else

		if (element.getObjectType() == 3) {
			//商品
			view = getViewX(element, parent);
		}
        //--ADD NT-LWL 17/09/04 BrandSearch FR -
         else if (element.getObjectType() == 4) {
            //カテゴリ
            int resId = element.getResId();
            view = mInflater.inflate(resId, parent, false);

            TextView title = (TextView) view.findViewById(R.id.textSlideSwitch);

                if (element.getObject() instanceof Integer) {
                    // 未选中时
                    int str = (int) element.getObject();
                    String s = "共"+str;
                    title.setText(String.format(mContext.getString(R.string.branc_search_title), s));
                    title.setTextColor(Color.BLACK);
                } else {
                    // 设置选中颜色
                    String str = (String) element.getObject();
                    title.setText(String.format(mContext.getString(R.string.branc_search_title), String.valueOf(str)));
                    title.setTextColor(Color.BLACK);
                    SpannableStringBuilder spannable = new SpannableStringBuilder(title.getText());
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), 6, 6 + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setText(spannable);
                }

            View slideSwitch = view.findViewById(R.id.imageSlideSwitch);
            slideSwitch.setSelected(isExpanded);

            if (brandSearchUtils != null) {
                view.setOnClickListener(new OnBrandSearchClick(view, brandSearchUtils));
            }

        }
        //--ADD NT-LWL 17/09/04 BrandSearch TO -

        return view;
    }


    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1)
    {
        return true;
    }



	//以下2つをfalseで返すと選択が行えなくなる
    @Override
	public boolean areAllItemsEnabled() {
		return false;
	}


/*
    @Override
	public boolean isEnabled(int position) {

        Element element = mElements.get(position);

		if (element.getObjectType() == 2) {
			return false;
		}

		return false;
//		return true;
	}
*/
    //--ADD NT-LWL 17/09/04 BrandSearch FR -
    static class OnBrandSearchClick implements View.OnClickListener,PopupWindow.OnDismissListener{
        // 是否展开
        boolean isExpanded = false;
        BrandSearchUtils brandSearchUtils;
        View view;

        public OnBrandSearchClick(View view,BrandSearchUtils brandSearchUtils) {
            this.view = view;
            this.brandSearchUtils = brandSearchUtils;
            if (this.brandSearchUtils != null) {
                this.brandSearchUtils.setOnDismissListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (isExpanded){
                brandSearchUtils.dismissBrandSearchDialog();
                isExpanded = false;
            }else {
                brandSearchUtils.showBrandSearchDialog();
                isExpanded = true;
            }

            View slideSwitch = v.findViewById(R.id.imageSlideSwitch);
            slideSwitch.setSelected(isExpanded);
        }

        @Override
        public void onDismiss() {
            isExpanded = false;
            View slideSwitch = view.findViewById(R.id.imageSlideSwitch);
            slideSwitch.setSelected(isExpanded);

        }
    }
    //--ADD NT-LWL 17/09/04 BrandSearch TO -

}
