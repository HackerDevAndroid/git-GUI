package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.Brand;

/**
 * Created date: 2017/9/5 15:22
 * Description: 品牌适配器
 */

public class BrandAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Brand> selectedBrands = new ArrayList<>();
    private List<Brand> list = new ArrayList<>();
    private int res;
    private OnOperateBrandListen onOperateBrandListen;


    public void addAll(List<Brand> datas) {
        list.addAll(datas);
    }

    public List<Brand> getList() {
        return list;
    }

    public void setOnOperateBrandListen(OnOperateBrandListen onOperateBrandListen) {
        this.onOperateBrandListen = onOperateBrandListen;
    }

    public interface OnOperateBrandListen {
        void onSelect(Brand brand);

        void onRemove(Brand brand);
    }

    public BrandAdapter(@NonNull Context context, @LayoutRes int resource) {
        inflater = LayoutInflater.from(context);
        this.res = resource;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Brand item = (Brand) getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(res, parent, false);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.name = (TextView) convertView.findViewById(R.id.brandName);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(item.brandName);
        viewHolder.checkBox.setChecked(item.isCheck);
        viewHolder.checkBox.setEnabled(item.isEnabled);
        convertView.setOnClickListener(new OnBrandClick(viewHolder, item));

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(list, new Comparator<Brand>() {
            @Override
            public int compare(Brand lhs, Brand rhs) {
                // 升序排列
                if (lhs.position > rhs.position) {
                    return 1;
                }
                if (lhs.position == rhs.position) {
                    return 0;
                }
                return -1;
            }
        });
        super.notifyDataSetChanged();
    }

    // 获取已选中品牌
    public List<Brand> getSelectedBrands() {
        return selectedBrands;
    }


    // 更新数据
    public void updateDatas(List<Brand> datas) {
        if (datas != null && !datas.isEmpty()) {
            list.clear();
            list.addAll(datas);

            selectedBrands.clear();

            Iterator<Brand> iterator = list.iterator();
            while (iterator.hasNext()) {
                Brand brand = iterator.next();
                if (brand.selectedFlag.equals("1")) {
                    brand.isCheck = true;
                    selectedBrands.add(brand);
                    iterator.remove();
                } else {
                    brand.isCheck = false;
                }
            }

        }
    }

    // 取消选中
    public void unSelect(Brand brand) {
        selectedBrands.remove(brand);
    }

    // 品牌选中 与取消选中
    class OnBrandClick implements View.OnClickListener {
        ViewHolder viewHolder;
        Brand brand;

        public OnBrandClick(ViewHolder viewHolder, Brand brand) {
            this.viewHolder = viewHolder;
            this.brand = brand;
        }

        @Override
        public void onClick(View v) {
            if (brand.isCheck) {
                brand.isCheck = false;
                selectedBrands.remove(brand);
                if (onOperateBrandListen != null) {
                    onOperateBrandListen.onRemove(brand);
                }
            } else {
                if (selectedBrands.size() >= 6) {
                    return;
                }
                brand.isCheck = true;
                selectedBrands.add(brand);
                if (onOperateBrandListen != null) {
                    onOperateBrandListen.onSelect(brand);
                }
            }
            viewHolder.checkBox.setChecked(brand.isCheck);
        }
    }

    class ViewHolder {
        TextView name;
        CheckBox checkBox;
    }
}
