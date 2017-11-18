
package jp.co.misumi.misumiecapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.CategorySearchListAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.RequestSearchSeries;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.util.ShareUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * カテゴリ検索画面  分类检索子画面
 */
public class CategorySearchListFragment extends BaseFragment {

    private TextView textView;
    private ListView listView;
    private CategorySearchListAdapter categorySearchListAdapter;
    private CategoryList.Category category;

    private RequestSearchSeries requestSearchSeries;
    private SeriesSearchApi mSeriesSearchApi;

    /**
     *
     */
    public CategorySearchListFragment() {
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

        View view = inflateLayout(inflater, R.layout.fragment_category_search_list, container, false);

        listView = (ListView) view.findViewById(R.id.listViewCategory);

        category = (CategoryList.Category) getParameter();
        textView = (TextView) view.findViewById(R.id.subCategoryTitle);
        if (category != null) {
            textView.setText(category.categoryName);

			if (category.categoryList == null) {
				category.categoryList = new ArrayList<>();
			}

        } else {
            textView.setText("");
        }

        categorySearchListAdapter = new CategorySearchListAdapter(getContext(),
                R.layout.list_item_category_search_sub, category.categoryList);

        //--ADD NT-LWL 17/07/06 Category FR -
        if (SubsidiaryCode.isChinese()&&category !=null){
            List<CategoryList.Category> replaceList;
            // 获取本地保存数据
            String json = AppConfig.getInstance().getUrlList().categoryImgURLReplaceList;
            JSONArray categories = null;
            try {
                if (!TextUtils.isEmpty(json)){
                    replaceList = new ArrayList<>();
                    categories = new JSONArray(json);
                    // 解析对象
                    for (int ii = 0; ii < categories.length(); ii++) {
                        CategoryList.Category category = new CategoryList.Category();
                        if (category.setData(categories.getJSONObject(ii))){
                            // 添加进入集合
                            replaceList.add(category);
                        }
                    }
                    // 设置需要替换的数据
                    categorySearchListAdapter.setReplaceList(replaceList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //--ADD NT-LWL 17/07/06 Category TO -

        listView.setAdapter(categorySearchListAdapter);
        listView.setOnItemClickListener(onItemClickListener);


		//
        View imageShare = view.findViewById(R.id.imageShare);
		if (SubsidiaryCode.isJapan()) {
			imageShare.setVisibility(View.GONE);
		} else {
			imageShare.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {

					//SNS連携
					String categoryCode = category.categoryCode;

					String url = ShareUtil.makeShareUrl(getContext(), "scate", categoryCode, null, null);
					ShareUtil.doShareUrl(getContext(), url);

	            }
	        });
		}
        if (BuildConfig.hideWechatShare){
            imageShare.setVisibility(View.GONE);
        }

        View emptyList = view.findViewById(R.id.emptyList);
        View line = view.findViewById(R.id.category_diviver);
        if (category.categoryList==null || category.categoryList.isEmpty()) {

            TextView tv = (TextView) emptyList.findViewById(R.id.textMessage);
            tv.setText(getResourceString(R.string.search_category_empty_list_item));

            emptyList.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else {

			//【08.カテゴリ一覧】【Android】一覧が画面内に収まる場合、最下部の区切り線がない
//			categorySearchListAdapter.add(null);

            emptyList.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

        }


        return view;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            category = categorySearchListAdapter.getItem(position);

			if (category == null) return;

            AppLog.d("category name=" + category.categoryName);

            if (category.categoryList == null || category.categoryList.isEmpty()){
                CategoryList.Category category = categorySearchListAdapter.getItem(position);
                doSearchSeries(category);

            } else {
                getFragmentController().stackFragment(new CategorySearchListFragment(),
                        FragmentController.ANIMATION_SLIDE_IN, category);
            }
        }
    };

    /**
     * シリーズ検索
     */
    private void doSearchSeries(CategoryList.Category cate){
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
        return ScreenId.CategorySub;
    }


    private class SeriesSearchApi extends ApiAccessWrapper {
        CategoryList.Category category;
        @Override
        protected String getScreenId() {
            return CategorySearchListFragment.this.getScreenId();
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

        public void connect(Context context, CategoryList.Category cate){
            category = cate;
            super.connect(context);
        }

        @Override
        public void  onResult(int responseCode, String result) {
            SearchSeriesList response = new SearchSeriesList();
            boolean pars = response.setData(result);
            if (!pars){
                showErrorMessage(null);
                return;
            }

            switch (responseCode){
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
		return jp.co.misumi.misumiecapp.SaicataId.CategorySub;
	}
}
