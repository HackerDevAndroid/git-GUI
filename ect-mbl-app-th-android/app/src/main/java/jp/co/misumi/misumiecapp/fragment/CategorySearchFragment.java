package jp.co.misumi.misumiecapp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.CategorySearchAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * カテゴリ検索（大）画面  分类检索
 */
public class CategorySearchFragment extends BaseFragment {


    private CategoryList mCategoryList;
    private CategorySearchAdapter mListAdapter;
    private GridView mGridView;

    private SeriesSearchApi mSeriesSearchApi;

    /**
     *
     */
    public CategorySearchFragment() {

        mSeriesSearchApi = new SeriesSearchApi();
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflateLayout(inflater, R.layout.fragment_category_search, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridCategory);

        mCategoryList = (CategoryList) getParameter();
        //-- ADD NT-SLJ 16/11/3 AliPay Payment FR -
        if (SubsidiaryCode.isJapan()) {
            mGridView.setNumColumns(3);
            mListAdapter = new CategorySearchAdapter(getContext(), R.layout.list_item_category_search1, mCategoryList.categoryList);
        } else {
            mGridView.setNumColumns(2);
            // 设置垂直分割线
            mGridView.setVerticalSpacing(1);
            // 设置水平分割线
            mGridView.setHorizontalSpacing(1);
            // 设置背景
            mGridView.setBackgroundColor(Color.GRAY);

            // 添加末尾横线
            View endLineView = new View(mParent);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            endLineView.setBackgroundColor(Color.GRAY);
            endLineView.setLayoutParams(lineParams);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.rootView);
            layout.addView(endLineView);

            // 设置GridView高度
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mGridView.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            //-- ADD NT-LWL 17/07/26 TopCategory FR -
            // 排好顺序的数组

            //-- UDP NT-LWL 17/09/25 Category FR -
//            String [] categoryCodes = {"FS040000000", //安全用品/办公用品
//                                       "EL010000000", //接线
//                                       "FS030000000", //捆包用品/物流保管用品
//                                       "EL020000000", //控制
//                                       "FS020000000", //生产加工用品
//                                       "MECH0200000", //螺钉/螺栓/垫圈/螺帽
//                                       "MECH0100000", //设备维护用品
//                                       "FS010000000"};//切削工具
            String[] categoryCodes = {"fs_health", //安全用品/办公用品
                    "el_wire", //接线
                    "fs_logistics", //捆包用品/物流保管用品
                    "el_control", //控制
                    "fs_processing", //生产加工用品
                    "mech_screw", //螺钉/螺栓/垫圈/螺帽
                    "mech", //设备维护用品
                    "fs_machining"};//切削工具
            //-- UDP NT-LWL 17/09/25 Category TO -
            // 超过8个后的 顺序值
            int length = categoryCodes.length;
            // 遍历赋值
            for (CategoryList.Category category : mCategoryList.categoryList) {
                if (category.categoryCode.equals(categoryCodes[0])) {
                    category.position = 0;
                } else if (category.categoryCode.equals(categoryCodes[1])) {
                    category.position = 1;
                } else if (category.categoryCode.equals(categoryCodes[2])) {
                    category.position = 2;
                } else if (category.categoryCode.equals(categoryCodes[3])) {
                    category.position = 3;
                } else if (category.categoryCode.equals(categoryCodes[4])) {
                    category.position = 4;
                } else if (category.categoryCode.equals(categoryCodes[5])) {
                    category.position = 5;
                } else if (category.categoryCode.equals(categoryCodes[6])) {
                    category.position = 6;
                } else if (category.categoryCode.equals(categoryCodes[7])) {
                    category.position = 7;
                } else {
                    category.position = length;
                    length++;
                }
            }
            // 按 position 值升序排序
            Collections.sort(mCategoryList.categoryList, new Comparator<CategoryList.Category>() {
                @Override
                public int compare(CategoryList.Category lhs, CategoryList.Category rhs) {
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
            //-- ADD NT-LWL 17/07/26 TopCategory TO -

            mListAdapter = new CategorySearchAdapter(getContext(), R.layout.list_item_category_search2, mCategoryList.categoryList);
        }
//        mListAdapter = new CategorySearchAdapter(getContext(), R.layout.list_item_category_search1, mCategoryList.categoryList);
        //-- ADD NT-SLJ 16/11/3 AliPay Payment TO -


        mGridView.setAdapter(mListAdapter);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        View emptyList = view.findViewById(R.id.emptyList);
        if (mCategoryList.isEmpty() || mCategoryList == null) {

            TextView textView = (TextView) emptyList.findViewById(R.id.textMessage);
            textView.setText(getResourceString(R.string.search_category_empty_list_item));

            emptyList.setVisibility(View.VISIBLE);
        } else {

            emptyList.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     *
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            CategoryList.Category cate = mListAdapter.getItem(position);
            if (cate.categoryList == null || cate.categoryList.isEmpty()) {
                doSearchSeries(cate);
            } else {
                AppLog.d("category name=" + cate.categoryName);
                getFragmentController().stackFragment(new CategorySearchListFragment(), FragmentController.ANIMATION_SLIDE_IN, cate);
            }
            //-- ADD NT-LWL 17/07/25 GA追加 FR -
            // 分类名称点击事件
            String label = cate.categoryCode + " " + cate.categoryName;
            GoogleAnalytics.sendAction(mTracker, GoogleAnalytics.CATEGORY_TOPCATEGORY, label);
            //-- ADD NT-LWL 17/07/25 GA追加 TO -
        }
    };

    private void doSearchSeries(CategoryList.Category cate) {
        hideKeyboard();
        mSeriesSearchApi.connect(getContext(), cate);
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.CategoryTop;
    }


    private class SeriesSearchApi extends ApiAccessWrapper {
        CategoryList.Category category;

        @Override
        protected String getScreenId() {
            return CategorySearchFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            Integer page = 1;
            Integer pageSize = AppConst.SERIES_LIST_REQUEST_COUNT;
            return ApiBuilder.createGetSeries(category.categoryCode, page, pageSize);
        }

        public void connect(Context context, CategoryList.Category cate) {
            category = cate;
            super.connect(context);
        }

        @Override
        public void onResult(int responseCode, String result) {
            SearchSeriesList response = new SearchSeriesList();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    response.getCategoryName = category.categoryName;
                    getFragmentController().stackFragment(new SearchResultCategoryFragment(),
                            FragmentController.ANIMATION_SLIDE_IN, response);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }

        }
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.CategoryTop;
    }
}

