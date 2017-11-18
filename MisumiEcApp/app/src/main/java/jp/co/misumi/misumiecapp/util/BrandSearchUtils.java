//--ADD NT-LWL 17/09/07 BrandSearch FR -
package jp.co.misumi.misumiecapp.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.adapter.BrandAdapter;
import jp.co.misumi.misumiecapp.data.Brand;

/**
 * Created date: 2017/9/7 13:43
 * Description: 品牌检索工具
 */

public class BrandSearchUtils implements BrandAdapter.OnOperateBrandListen {
    // 品牌筛选框
    private PopupWindow popupWindow;
    // 筛选框显示的基础view
    private View baseView;
    // y 轴偏移方向
    private int y;
    // 上下文
    private Context mContext;
    // 弹框根view
    private View rootView;
    // 品牌列表
    private ListView mListView;
    // 品牌列表适配器
    private BrandAdapter mBrandAdapter;
    // 已选品牌容器
    private RecyclerView selectedLayout;
    // 已选品牌标题
    private TextView selectedTitle;
    // 红色提示文字
    private TextView tvTip;
    private SelectedBrandAdapter selectedBrandAdapter;
    // 最新数据
    private List<Brand> brands = new ArrayList<>();
    // 布局解析器
    private LayoutInflater inflater;
    // 点击确定监听
    private OnBrandSelectOkListener onBrandSelectOkListener;
    // 数量变化 监听
    private OnSelectedBrandcCountListen onSelectedBrandcCountListen;

    public void setBrands(List<Brand> brands) {
        if (brands != null && !brands.isEmpty()) {
            this.brands.clear();
            this.brands.addAll(brands);
            if (mBrandAdapter != null) {
                mBrandAdapter.updateDatas(brands);
                mBrandAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setOnSelectedBrandcCountListen(OnSelectedBrandcCountListen onSelectedBrandcCountListen) {
        this.onSelectedBrandcCountListen = onSelectedBrandcCountListen;
    }

    public void setOnBrandSelectOkListener(OnBrandSelectOkListener onBrandSelectOkListener) {
        this.onBrandSelectOkListener = onBrandSelectOkListener;
    }

    public void setBaseView(View baseView) {
        this.baseView = baseView;
    }

    // 关闭监听
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        popupWindow.setOnDismissListener(onDismissListener);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public BrandAdapter getmBrandAdapter(){
        return mBrandAdapter;
    }

    public BrandSearchUtils(Context context, List<Brand> brands) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        rootView = inflater.inflate(R.layout.dialog_brand_search, null);
        tvTip = (TextView) rootView.findViewById(R.id.brand_selected_tip);
        tvTip.setVisibility(View.GONE);
        selectedTitle = (TextView) rootView.findViewById(R.id.selected_brand_title);
        setSelectedCount(0);
        // 已选择品牌容器
        selectedLayout = (RecyclerView) rootView.findViewById(R.id.selected_brand_layout);
        selectedLayout.setLayoutManager(new GridLayoutManager(mContext,2));
        DividerGridItemDecoration dividerGridItemDecoration = new DividerGridItemDecoration(mContext);
        dividerGridItemDecoration.setDivider(mContext.getResources().getDrawable(R.drawable.divider_line));
        selectedLayout.addItemDecoration(dividerGridItemDecoration);
//        selectedLayout.setItemAnimator(new DefaultItemAnimator());
        selectedBrandAdapter = new SelectedBrandAdapter(mContext);
        selectedBrandAdapter.setOnDeleteItemListen(new SelectedBrandAdapter.OnDeleteItemListen() {
            @Override
            public void onDelete(Brand brand, int position) {
                // 顶部移除
                selectedBrandAdapter.removeItem(position);

                // 列表已选择中移除
                mBrandAdapter.unSelect(brand);
                // 选中状态为false
                brand.isCheck = false;
                // 列表添加
                mBrandAdapter.getList().add(brand);
                mBrandAdapter.notifyDataSetChanged();

                // 选择提示文字 隐藏
                if (mBrandAdapter.getSelectedBrands().size()<6){
                    if (tvTip.getVisibility() == View.VISIBLE) {
                        tvTip.setVisibility(View.GONE);
                    }
                    // 变为加号
                    for (Brand b : mBrandAdapter.getList()){
                        b.isEnabled = true;
                    }
                    mBrandAdapter.notifyDataSetChanged();
                }
                // 设置已选数量
                setSelectedCount(mBrandAdapter.getSelectedBrands().size());

                if (onSelectedBrandcCountListen != null){
                    onSelectedBrandcCountListen.onChange(mBrandAdapter.getSelectedBrands());
                }
            }
        });
        selectedLayout.setAdapter(selectedBrandAdapter);

        // 品牌列表
        mListView = (ListView) rootView.findViewById(R.id.brandList);
        mBrandAdapter = new BrandAdapter(mContext,R.layout.item_list_brand);
        // 添加数据
        mBrandAdapter.updateDatas(brands);
        setBrands(brands);
        mListView.setAdapter(mBrandAdapter);
        mBrandAdapter.setOnOperateBrandListen(this);
        // 确定按钮
        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭筛选框
                if (popupWindow != null){
                    popupWindow.dismiss();
                }
                // 调用监听处理逻辑
                if (onBrandSelectOkListener != null){
                    onBrandSelectOkListener.onSelectOk(mBrandAdapter.getSelectedBrands());
                }
            }
        });
        // 重置 清除按钮
        rootView.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移除顶部选择视图
                selectedBrandAdapter.datas.clear();
                selectedBrandAdapter.notifyDataSetChanged();

                // 清除已选择集合
                int n =mBrandAdapter.getSelectedBrands().size();
                for (int i=0;i<n;i++){
                    mBrandAdapter.getSelectedBrands().get(i).isCheck = false;
                }
                mBrandAdapter.addAll(mBrandAdapter.getSelectedBrands());
                mBrandAdapter.getSelectedBrands().clear();

                // 变为加号
                for (Brand b : mBrandAdapter.getList()){
                    b.isEnabled = true;
                }
                mBrandAdapter.notifyDataSetChanged();

                // 选择提示文字 隐藏
                if (tvTip.getVisibility() == View.VISIBLE) {
                    tvTip.setVisibility(View.GONE);
                }
                // 设置已选数量
                setSelectedCount(0);
                // 调用监听处理逻辑
                if (onBrandSelectOkListener != null){
                    onBrandSelectOkListener.onReset();
                }
            }
        });
        // 关闭
        rootView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭筛选框
                if (popupWindow != null){
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow = new PopupWindow(rootView);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.rgb(0xee, 0xee, 0xee)));
        // 设置获取焦点
        popupWindow.setFocusable(true);
        // 设置外部可点击
        popupWindow.setOutsideTouchable(true);
    }
    // 获取实际选中的
    public List<Brand> getSelectFlagBrands(){
        List<Brand> brands = new ArrayList<>();
        for (Brand brand : this.brands){
            if (brand.selectedFlag.equals("1")){
                brands.add(brand);
            }
        }
        return brands;
    }
    // 列表选中
    @Override
    public void onSelect(final Brand brand) {
        selectedBrandAdapter.addItem(brand);

        // 列表移除
        mBrandAdapter.getList().remove(brand);
        mBrandAdapter.notifyDataSetChanged();

        // 选择提示文字 显示
        if (mBrandAdapter.getSelectedBrands().size()>=6){
            if (tvTip.getVisibility() == View.GONE) {
                tvTip.setVisibility(View.VISIBLE);
            }
            for (Brand b : mBrandAdapter.getList()){
                b.isEnabled = false;
            }
            mBrandAdapter.notifyDataSetChanged();
        }
        // 设置已选数量
        setSelectedCount(mBrandAdapter.getSelectedBrands().size());

        if (onSelectedBrandcCountListen != null){
            onSelectedBrandcCountListen.onChange(mBrandAdapter.getSelectedBrands());
        }
    }
    // 列表移除选中
    @Override
    public void onRemove(Brand brand) {
        // 选择提示文字 隐藏
        if (mBrandAdapter.getSelectedBrands().size()<6){
            if (tvTip.getVisibility() == View.VISIBLE) {
                tvTip.setVisibility(View.GONE);
            }
        }
        // 设置已选数量
        setSelectedCount(mBrandAdapter.getSelectedBrands().size());

        if (onSelectedBrandcCountListen != null){
            onSelectedBrandcCountListen.onChange(mBrandAdapter.getSelectedBrands());
        }
    }
    public void showBrandSearchDialog(){
        if (popupWindow != null){

            popupWindow.showAsDropDown(baseView,0,y);

            // 更新数据
            mBrandAdapter.updateDatas(brands);
            mBrandAdapter.notifyDataSetChanged();
            // 设置已选数量
            setSelectedCount(mBrandAdapter.getSelectedBrands().size());
            // 选择提示文字 隐藏
            if (mBrandAdapter.getSelectedBrands().size()<6){
                if (tvTip.getVisibility() == View.VISIBLE) {
                    tvTip.setVisibility(View.GONE);
                }
                for (Brand b : mBrandAdapter.getList()){
                    b.isEnabled = true;
                }
                mBrandAdapter.notifyDataSetChanged();
            }else {
                if (tvTip.getVisibility() == View.GONE) {
                    tvTip.setVisibility(View.VISIBLE);
                }
                for (Brand b : mBrandAdapter.getList()){
                    b.isEnabled = false;
                }
                mBrandAdapter.notifyDataSetChanged();
            }
            // 初始化
            selectedBrandAdapter.datas.clear();
            selectedBrandAdapter.datas.addAll(mBrandAdapter.getSelectedBrands());
            selectedBrandAdapter.notifyDataSetChanged();
        }
    }
    public void dismissBrandSearchDialog(){
        if (popupWindow != null){
            // 更新数据
            popupWindow.dismiss();
        }
    }
    public void setY(int y) {
        this.y = y;
    }

    /**
     * 设置高度
     * @param height
     */
    public void setPopupWindowHeight(int height){
        if (popupWindow != null){
            popupWindow.setHeight(height);
        }
    }

    private void setSelectedCount(int count){
        String s = String.valueOf(count);
        selectedTitle.setText(String.format(mContext.getString(R.string.selected_brands), s));
        selectedTitle.setTextColor(Color.BLACK);
    }

    // 品牌选择点击 确定 重置
    public interface OnBrandSelectOkListener{
        void onSelectOk(List<Brand> selectedBrands);
        void onReset();
    }
    // 已选数量变化监听
    public interface OnSelectedBrandcCountListen{
        void onChange(List<Brand> brands);
    }

    public static class SelectedBrandAdapter extends RecyclerView.Adapter<SelectedBrandAdapter.ViewHolder>{
        private List<Brand> datas = new ArrayList<>();
        private Context context;
        private OnDeleteItemListen onDeleteItemListen;

        public void setOnDeleteItemListen(OnDeleteItemListen onDeleteItemListen) {
            this.onDeleteItemListen = onDeleteItemListen;
        }

        public SelectedBrandAdapter(Context context) {
            this.context = context;
        }

        public List<Brand> getDatas() {
            return datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =LayoutInflater.from(context).inflate(R.layout.item_selected_brand,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
        public void addItem(Brand brand){
            datas.add(brand);
            notifyDataSetChanged();
//            notifyItemInserted(datas.size()-1);
        }
        public void removeItem(int position){
            datas.remove(position);
            notifyDataSetChanged();
//            notifyItemRemoved(position);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Brand brand = datas.get(position);
            ClickListen clickListen = new ClickListen(holder,brand,onDeleteItemListen);

            holder.tvName.setText(brand.brandName);
            // 点击后删除
            holder.view.setOnClickListener(clickListen);
        }


        @Override
        public int getItemCount() {
            if (datas !=null ){
                return datas.size();
            }
            return 0;
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvName;
            private View view;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.brandName);
                view = itemView.findViewById(R.id.item_view);
            }
        }

        static class ClickListen implements View.OnClickListener{
            private Brand brand;
            private OnDeleteItemListen onDeleteItemListen;
            private ViewHolder viewHolder;

            public ClickListen(ViewHolder viewHolder, Brand brand,OnDeleteItemListen onDeleteItemListen) {
                this.viewHolder = viewHolder;
                this.brand = brand;
                this.onDeleteItemListen = onDeleteItemListen;
            }

            @Override
            public void onClick(View v) {
                if (onDeleteItemListen != null){
                    onDeleteItemListen.onDelete(brand,viewHolder.getAdapterPosition());
                }
            }
        }

        public interface OnDeleteItemListen{
            void onDelete(Brand brand,int position);
        }
    }
}
//--ADD NT-LWL 17/09/07 BrandSearch TO -