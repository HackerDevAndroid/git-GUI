package jp.co.misumi.misumiecapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.uuzuche.lib_zxing.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.SearchSeriesAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.Brand;
import jp.co.misumi.misumiecapp.data.RequestSearchSeries;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.util.BrandSearchUtils;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.ListViewUtil;
import jp.co.misumi.misumiecapp.util.ShareUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.widget.FooterView;


/**
 * 商品検索結果画面  商品系列画面
 */
public class SearchResultCategoryFragment extends BaseGetSpProductApi {

    //リクエスト
    private RequestSearchSeries mRequestSearch;

    private TextView category;
    private SearchSeriesList mResponseSearch;
    private SearchSeriesAdapter mListAdapter;
    private ListView mListView;
    private ListViewUtil mListViewUtil;
    private FooterView mFooterView;

    private TextView mTextListCount;

    //API
    private SearchSeriesApi mSearchSeriesApi;
    private GetSpProductApi mGetSpProductApi;

    private String code;

    //--ADD NT-LWL 17/09/07 BrandSearch FR -
    private View btnBrandSearch;
    private TextView tvBrandSearch;
    private ImageView slideSwitch;
    private BrandSearchUtils mBrandSearchUtils;
    private boolean isExpanded;
    private boolean isClickBrandOk;
    private boolean isDestroyView = false;
    // 复杂品数
    private int complexFlagCount = 0;
    //--ADD NT-LWL 17/09/07 BrandSearch TO -


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroyView = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("RequestSearch", mRequestSearch);
        outState.putSerializable("ResponseSearch", mResponseSearch);
    }


    public SearchResultCategoryFragment() {
        mSearchSeriesApi = new SearchSeriesApi();
        mGetSpProductApi = new GetSpProductApi() {

            @Override
            protected String getScreenId() {
                //画面IDを返す
                return SearchResultCategoryFragment.this.getScreenId();
            }

            protected void onSuccess(ResponseGetSpProduct response) {

                //画面遷移する
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

        };
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            mResponseSearch = (SearchSeriesList) savedInstanceState.getSerializable("ResponseSearch");
            mRequestSearch = (RequestSearchSeries) savedInstanceState.getSerializable("RequestSearch");

        } else {

            if (mResponseSearch == null) {
                mResponseSearch = (SearchSeriesList) getParameter();
            }

            if (mRequestSearch == null) {
                mRequestSearch = new RequestSearchSeries();
                mRequestSearch.page = 2;
                if (!(mResponseSearch.mSeriesList == null) && !(mResponseSearch.mSeriesList.isEmpty())) {
                    mRequestSearch.categoryCode = mResponseSearch.mSeriesList.get(0).categoryCode;
                }
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflateLayout(inflater, R.layout.fragment_search_category_result, container, false);

        View header = inflateLayout(inflater, R.layout.list_item_series_search_header, null, false);
        mTextListCount = (TextView) header.findViewById(R.id.total);

        mListAdapter = new SearchSeriesAdapter(getContext(), R.layout.list_item_keyword_search, new ArrayList<SearchSeriesList.Series>());

        //
        mFooterView = new FooterView(inflater, new FooterView.OnViewListener() {
            @Override
            public void onReadList() {

                doReadList();
            }
        });

        mListView = (ListView) view.findViewById(R.id.seriesList);

        category = (TextView) view.findViewById(R.id.categoryName);

        //--ADD NT-LWL 17/09/07 BrandSearch FR -
        //获取状态栏高度
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        View title = view.findViewById(R.id.layout);
        title.measure(0, 0);
        isExpanded = false;
        mBrandSearchUtils = new BrandSearchUtils(mParent, mResponseSearch.mSeriesBrandList);
        btnBrandSearch = view.findViewById(R.id.layoutSlideTrigger);
        // 测量高度
        btnBrandSearch.measure(0, 0);
        tvBrandSearch = (TextView) view.findViewById(R.id.textSlideSwitch);
        setSelectedCount(mBrandSearchUtils.getSelectFlagBrands());
        slideSwitch = (ImageView) view.findViewById(R.id.imageSlideSwitch);
        mBrandSearchUtils.setBaseView(view.findViewById(R.id.line));
        // 设置显示高度
        mBrandSearchUtils.setPopupWindowHeight(DisplayUtil.screenhightPx - mParent.getHeaderView().getHeaderView().getHeight() - statusBarHeight - title.getMeasuredHeight() - btnBrandSearch.getMeasuredHeight() - DisplayUtil.dip2px(mParent, 1));
        // 设置消失监听
        mBrandSearchUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isExpanded = false;
                slideSwitch.setSelected(isExpanded);
                setSelectedCount(mBrandSearchUtils.getSelectFlagBrands());
            }
        });
        // 点击监听
        btnBrandSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    isExpanded = false;
                } else {
                    isExpanded = true;
                }
                slideSwitch.setSelected(isExpanded);
                if (isExpanded) {
                    mBrandSearchUtils.showBrandSearchDialog();
                    setSelectedCount(null);
                } else {
                    mBrandSearchUtils.dismissBrandSearchDialog();
                }
            }
        });
        // 点击确定回调
        mBrandSearchUtils.setOnBrandSelectOkListener(new BrandSearchUtils.OnBrandSelectOkListener() {
            @Override
            public void onSelectOk(List<Brand> selectedBrands) {
                // 组装参数
                String brand = "";
                for (int i = 0; i < selectedBrands.size(); i++) {
                    if (i != 0) {
                        brand += "," + selectedBrands.get(i).brandCode;
                    } else {
                        brand += selectedBrands.get(i).brandCode;
                    }
                }
                // 判断是否重复
                String oldBrand = mRequestSearch.brandCode;
                if (brand.equals(oldBrand)) {
                    return;
                }

                isClickBrandOk = true;
                mRequestSearch.brandCode = brand;
                mRequestSearch.page = 1;
                mListAdapter.clear();
                // 设置已选数量
                setSelectedCount(selectedBrands);


                // 开始API 品牌检索
                mSearchSeriesApi.setParameter(mRequestSearch);
                mSearchSeriesApi.connect(mParent);
            }

            @Override
            public void onReset() {
                // 重置
//                setSelectedCount(null);
            }
        });
        // 选择数量变化监听
        mBrandSearchUtils.setOnSelectedBrandcCountListen(new BrandSearchUtils.OnSelectedBrandcCountListen() {
            @Override
            public void onChange(List<Brand> brands) {
                // 设置已选数量
//                setSelectedCount(brands);
            }
        });
        //--ADD NT-LWL 17/09/07 BrandSearch TO -

        //seriesListのnullチェック
        View emptyList = header.findViewById(R.id.emptyList);
        View line = header.findViewById(R.id.category_diviver);
        if ((mResponseSearch.totalCount != null && mResponseSearch.totalCount != 0) &&
                (mResponseSearch.mSeriesList != null && !(mResponseSearch.mSeriesList.isEmpty()))) {

            emptyList.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

//            category.setText(mResponseSearch.mSeriesList.get(0).categoryName);
        } else {
            TextView textView = (TextView) emptyList.findViewById(R.id.textMessage);
            textView.setText(getResourceString(R.string.search_series_empty_list_item));

            emptyList.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

//            category.setText(mResponseSearch.getCategoryName);
        }

        //常に前画面のデータを表示
        //スキーマ起動時はレスポンスを細工する
        category.setText(mResponseSearch.getCategoryName);

        mListView.addHeaderView(header, null, false);

        mListView.addFooterView(mFooterView.getFooterView(), null, false);
        mListView.setAdapter(mListAdapter);

        mListViewUtil = new ListViewUtil(mListView, new ListViewUtil.OnListViewListener() {
            @Override
            public boolean canMoreRead() {

                //エラー状態
                return !mFooterView.isFooterError();

            }

            @Override
            public void onAdditionalReading(int totalItemCount) {
                //--UDP NT-LWL 17/09/07 BrandSearch FR -
//                    doAdditionalReading(totalItemCount);
                if (!isClickBrandOk) {
                    doAdditionalReading(totalItemCount);
                }
                //--UDP NT-LWL 17/09/07 BrandSearch TO -
            }

            @Override
            public void onSetFooterViewPc() {

                mFooterView.setFooterViewPc();
            }

            @Override
            public void onRemoveFooterView() {

                mListView.removeFooterView(mFooterView.getFooterView());
            }
        });

        addListAdapter(mResponseSearch);


//        String count = Format.formatCount(mResponseSearch.totalCount);
//        mTextListCount.setText(String.valueOf(count));

        mListView.setOnItemClickListener(onItemClickListener);


        //
        View imageShare = header.findViewById(R.id.imageShare);
        if (SubsidiaryCode.isJapan()) {
            imageShare.setVisibility(View.GONE);
        } else {
            imageShare.setVisibility(View.VISIBLE);
            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //SNS連携
                    String categoryCode = mRequestSearch.categoryCode;

                    String url = ShareUtil.makeShareUrl(getContext(), "slist", categoryCode, null, null);
                    ShareUtil.doShareUrl(getContext(), url);

                }
            });
        }
        if (BuildConfig.hideWechatShare) {
            imageShare.setVisibility(View.GONE);
        }

        return view;
    }

    //--ADD NT-LWL 17/09/12 BrandSearch FR -
    // 设置已选 数量
    private void setSelectedCount(List<Brand> selectedBrands) {
        if (selectedBrands != null && selectedBrands.size() > 0) {
            // 设置已选数量
            String str = "已选" + selectedBrands.size();
            tvBrandSearch.setText(String.format(mParent.getString(R.string.branc_search_title), str));
            tvBrandSearch.setTextColor(Color.BLACK);
            SpannableStringBuilder spannable = new SpannableStringBuilder(tvBrandSearch.getText());
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 6, 6 + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvBrandSearch.setText(spannable);
        } else {
            // 重置
            String s = "共" + mResponseSearch.mSeriesBrandList.size();
            tvBrandSearch.setText(String.format(mParent.getString(R.string.branc_search_title), s));
            tvBrandSearch.setTextColor(Color.BLACK);

        }
    }
    //--ADD NT-LWL 17/09/12 BrandSearch TO -

    private void dispTotalCount(int totalCount) {

        String count = Format.formatCount(totalCount);
        mTextListCount.setText(count);
    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.SearchResult;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        mSearchSeriesApi.close();
        mGetSpProductApi.close();

        super.onPause();
    }


    private void doAdditionalReading(int totalItemCount) {

        doReadList();
    }


    private void doReadList() {

        hideKeyboard();
        mSearchSeriesApi.setParameter(mRequestSearch);
        mSearchSeriesApi.connect(getContext());
    }


    private void addListAdapter(SearchSeriesList response) {

        mListAdapter.addAll(response.mSeriesList);

        //JSONリストが0件の場合
        int listSize = response.mSeriesList.size();
        //総件数
        int totalCount = (response.totalCount != null) ? response.totalCount : 0;

        //--ADD NT-LWL 17/09/28 Series FR -
        if (!isDestroyView) {
            complexFlagCount += response.complexFlagCount;
            // 依次减去复杂品数
            mResponseSearch.totalCount = response.totalCount - complexFlagCount;
            response.totalCount = mResponseSearch.totalCount;
        }
        isDestroyView = false;
        //--ADD NT-LWL 17/09/28 Series TO -

        //--UDP NT-LWL 17/09/28 Series FR -
        //mListViewUtil.updateListData(listSize, totalCount, mRequestSearch.page, mListAdapter.getCount(), AppConst.SERIES_LIST_MAX_COUNT, AppConst.SERIES_LIST_REQUEST_COUNT);
        mListViewUtil.updateListData(listSize, totalCount, mRequestSearch.page, mListAdapter.getCount() + complexFlagCount, AppConst.SERIES_LIST_MAX_COUNT, AppConst.SERIES_LIST_REQUEST_COUNT);
        //--UDP NT-LWL 17/09/28 Series TO -

        mListAdapter.notifyDataSetChanged();

        //--UDP NT-LWL 17/09/28 Series FR -
        //dispTotalCount(totalCount);
        dispTotalCount(response.totalCount);
        //--UDP NT-LWL 17/09/28 Series FR -
    }


    private void updateListAdapter(SearchSeriesList response) {

        //更新 2015/09/22
        mResponseSearch.totalCount = response.totalCount;


        //既存のデータに足す
        mResponseSearch.mSeriesList.addAll(response.mSeriesList);

        addListAdapter(response);
    }

    private class SearchSeriesApi extends ApiAccessWrapper {

        RequestSearchSeries mRequest;
        //--ADD NT-LWL 17/09/08 BrandSearch FR -
        String brand = "";
        //--ADD NT-LWL 17/09/08 BrandSearch TO -

        public void setParameter(RequestSearchSeries request) {

            mRequest = request;
            //--ADD NT-LWL 17/09/08 BrandSearch FR -
            brand = request.brandCode;
            //--ADD NT-LWL 17/09/08 BrandSearch TO -
        }

        @Override
        protected String getScreenId() {
            return SearchResultCategoryFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            //--UDP NT-LWL 17/09/08 BrandSearch FR -
            //return ApiBuilder.createGetSeries(mRequest.categoryCode, mRequest.page, mRequest.pageSize);
            if (TextUtils.isEmpty(brand)) {
                return ApiBuilder.createGetSeries(mRequest.categoryCode, mRequest.page, mRequest.pageSize);
            } else {
                return ApiBuilder.createGetSeriesBrand(mRequest.categoryCode, mRequest.seriesCode, mRequest.innerCode, mRequest.brandCode, mRequest.page, mRequest.pageSize);
            }
            //--UDP NT-LWL 17/09/08 BrandSearch TO -
        }

        @Override
        public void onResult(int responseCode, String result) {
            //--ADD NT-LWL 17/09/011 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/011 BrandSearch TO -
            SearchSeriesList response = new SearchSeriesList();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                mFooterView.setFooterViewError();
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    //--ADD NT-LWL 17/09/011 BrandSearch FR -
                    // 更新数据
                    if (response.mSeriesBrandList != null && !response.mSeriesBrandList.isEmpty()) {
                        mResponseSearch.mSeriesBrandList.clear();
                        mResponseSearch.mSeriesBrandList.addAll(response.mSeriesBrandList);
                        mBrandSearchUtils.setBrands(response.mSeriesBrandList);
                    }
                    // 清除重复数据
                    if (mRequestSearch.page == 1) {
                        mResponseSearch.mSeriesList.clear();
                        // 初始化
                        complexFlagCount = 0;
                        mListViewUtil.init();
                    }
                    //--ADD NT-LWL 17/09/011 BrandSearch TO -

                    ++mRequestSearch.page;
                    updateListAdapter(response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    mFooterView.setFooterViewError();
                    break;
            }
        }

        //バグ #3804対応
        @Override
        protected void onNetworkError(int responseCode) {
            //--ADD NT-LWL 17/09/011 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/011 BrandSearch TO -
            super.onNetworkError(responseCode);
            mFooterView.setFooterViewError();
        }

        //バグ #3809対応
        @Override
        protected void onTimeout() {
            //--ADD NT-LWL 17/09/011 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/011 BrandSearch TO -
            super.onTimeout();
            mFooterView.setFooterViewError();
        }

        //--ADD NT-LWL 17/09/011 BrandSearch FR -
        @Override
        protected void onLostSession(int responseCode, String result) {
            isClickBrandOk = false;
            super.onLostSession(responseCode, result);
        }
        //--ADD NT-LWL 17/09/011 BrandSearch TO -
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SearchSeriesList.Series seriesItem = (SearchSeriesList.Series) parent.getItemAtPosition(position);

            doSpProduct(seriesItem);

        }
    };


    private void doSpProduct(SearchSeriesList.Series seriesItem) {

        hideKeyboard();

        String completeType = seriesItem.completeType;
        String seriesCode = seriesItem.seriesCode;
        String innerCode = seriesItem.innerCode;
        String partNumber = seriesItem.partNumber;
        Integer quantity = 1;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.SeriesList;
    }

}


