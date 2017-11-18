package jp.co.misumi.misumiecapp.autocomplete;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kawanobe on 15/09/09.
 */
public abstract class AutoCompleteAdapter<OBJ> extends BaseAdapter implements Filterable {

    private List<OBJ> allObjects;

    public AutoCompleteAdapter(List<OBJ> filterList) {
        allObjects = filterList;
    }

    /**
     * add
     *
     * @param obj
     */
    public void add(OBJ obj) {
        allObjects.add(obj);
    }

    /**
     * addAll
     *
     * @param collection
     */
    public void addAll(Collection<? extends OBJ> collection) {
        allObjects.addAll(collection);
    }

    protected abstract View createView(int position);


    protected abstract void refreshView(int position, View view);

    protected abstract boolean isShowData(CharSequence input, OBJ obj);

    /**
     * getCount
     *
     * @return
     */
    public int getCount() {
        return allObjects.size();
    }

    /**
     * getItem
     *
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return allObjects.get(position);
    }

    /**
     * getItemId
     *
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * getView
     *
     * @param posision
     * @param view
     * @param viewgroup
     * @return
     */
    public View getView(int posision, View view, ViewGroup viewgroup) {
        if (view == null) {
            view = createView(posision);
        }
        refreshView(posision, view);
        return view;
    }

    /**
     * getFilter
     */
    public Filter getFilter() {
        return new Filter() {

            /**
             * performFiltering
             * @param charsequence
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence charsequence) {
                FilterResults ret = new FilterResults();
                List<OBJ> list = new ArrayList<>();
                if (charsequence == null) {
                    list.addAll(allObjects);
                } else {
                    for (OBJ item : allObjects) {
                        if (isShowData(charsequence, item)) {
                            list.add(item);
                        }
                    }
                }
                ret.values = list;
                ret.count = list.size();
                return ret;
            }

            /**
             * publishResults
             * @param charsequence
             * @param filterresults
             */
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charsequence,
                                          FilterResults filterresults) {
                if (filterresults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    void clear() {
        allObjects.clear();
    }
}
