package jp.co.misumi.misumiecapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.uuzuche.lib_zxing.DisplayUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.SearchKeywordProcess;
import jp.co.misumi.misumiecapp.adapter.Element;
import jp.co.misumi.misumiecapp.adapter.MyExpandListAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.autocomplete.AutoCompleteProcess;
import jp.co.misumi.misumiecapp.data.Brand;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.ResponseKeywordSearch;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.data.SearchSuggest;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.BrandSearchUtils;
import jp.co.misumi.misumiecapp.util.ListViewUtil;
import jp.co.misumi.misumiecapp.widget.FooterView;


/**
 * キーワード検索結果画面
 */
//TODO:表示項目の内容が暫定
public class SearchResultKeywordFragment extends BaseGetSpProductApi {


    //検索リクエスト用（逐次読み込み）
    private RequestKeywordSearch mRequestSearch;
    //画面表示用
    private ResponseKeywordSearch mResponseSearch;

    private MyExpandListAdapter mListAdapter;
    private ExpandableListView mListView;
    private ListViewUtil mListViewUtil;
    private FooterView mFooterView;
	private AutoCompleteProcess autoCompleteProcess;
	private AutoCompleteTextView mEditPartNumber;

//    private View mCountView;
//	private TextView mTextListCount;

//	private View		mFolderView;

    //API
    private MySearchApi mMySearchApi;
    private SearchKeywordProcess searchKeywordProcess;
	private SeriesSearchApi mSeriesSearchApi;
    private CategorySearchApi mCategoryApi;
    private GetSpProductApi mGetSpProductApi;
    //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
    private boolean isFromSuggest = false;  //是否来自 点击suggest列表
    //-- ADD NT-LWL 17/03/23 AliPay Payment TO -

    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    private BrandSearchUtils mBrandSearchUtils;
    private View searchLayoutLine;
    // 品牌筛选对应的位置
    private int brandGroupPosition ;
    // 件数对应位置
    private int countPosition;
    private RequestKeywordSearch mBrandRequestSearch;
    private MyBrandSearchApi mMyBrandSearchApi;
    // 是否点击并选中品牌检索
    private boolean isBrandSearch = false;
    // 是否点击确定
    private boolean isClickBrandOk;
    //--ADD NT-LWL 17/09/08 BrandSearch TO -

    //--ADD NT-LWL 17/09/28 Series FR -
    //复杂品数
    private int complexFlagCount = 0;
    // view是否销毁过
    private boolean isDestroyView = false;
    //--ADD NT-LWL 17/09/28 Series TO -

    private LostSession lostSession = null;


    private View mRootView;

    //--ADD NT-LWL 17/09/28 Series FR -
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroyView = true;
    }
    //--ADD NT-LWL 17/09/28 Series TO -

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("RequestSearch", mRequestSearch);
        outState.putSerializable("ResponseSearch", mResponseSearch);

        //--ADD NT-LWL 17/09/08 BrandSearch FR -
        outState.putSerializable("BrandRequestSearch",mBrandRequestSearch);
        //--ADD NT-LWL 17/09/08 BrandSearch TO -
    }


    public SearchResultKeywordFragment() {

        //--ADD NT-LWL 17/09/08 BrandSearch FR -
        mMyBrandSearchApi = new MyBrandSearchApi();
        //--ADD NT-LWL 17/09/08 BrandSearch TO -
        mMySearchApi = new MySearchApi();
        searchKeywordProcess = new SearchKeywordProcess();

		mSeriesSearchApi = new SeriesSearchApi();
        mCategoryApi = new CategorySearchApi();

        mGetSpProductApi = new GetSpProductApi() {

	        @Override
	        protected String getScreenId() {
				//画面IDを返す
	            return SearchResultKeywordFragment.this.getScreenId();
	        }

	        protected void onSuccess(ResponseGetSpProduct response) {

				//画面遷移する
	            getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
			}
            //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
            @Override
            public void onResult(int responseCode, String result) {
                super.onResult(responseCode, result);
                setSearchGA(responseCode);


            }
            @Override
            protected void onLostSession(int responseCode, String result) {
                super.onLostSession(responseCode, result);
                setSearchGA(responseCode);
            }
            @Override
            protected void onNetworkError(int responseCode) {
                super.onNetworkError(responseCode);
                setSearchGA(responseCode);
            }

            private void setSearchGA(int responseCode) {
                if (isFromSuggest) {
                    // 产品存在时 定义totalCount=1，反之NotFound
                    String totalCount = responseCode == NetworkInterface.STATUS_OK ? "1" : "NotFound";
                    String errorCode = responseCode == NetworkInterface.STATUS_OK ? "success" : responseCode + "";
                    // 标签拼接规则 来源+型号+数量+responseCode
                    JSONObject object = new JSONObject();
                    try {
                        object.put("clickSource", GoogleAnalytics.suggest);
                        object.put("partNumber", mPartNumber);
                        object.put("totalCount", totalCount);
                        object.put("errorCode", errorCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    String lable = object.toString();
                    // 搜索 建议列表中 跟踪用户点击的产品
                    GoogleAnalytics.sendProductTrack(mTracker, null, GoogleAnalytics.CATEGORY_SEARCH, object);
                }
            }
            //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
        };
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

/*
        // 現地法人判別用
        if (AppConst.LocalSubsidiary == AppConst.LOCAL_SUBSIDIARY_CHN) {
        }
*/

        //リクエストデータを最初に作成して、このデータクラスを画面編集と同期させる
        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

            mResponseSearch = (ResponseKeywordSearch) savedInstanceState.getSerializable("ResponseSearch");
            mRequestSearch = (RequestKeywordSearch) savedInstanceState.getSerializable("RequestSearch");

            mBrandRequestSearch = (RequestKeywordSearch) savedInstanceState.getSerializable("BrandRequestSearch");

        } else {

            if (mResponseSearch == null) {
		        mResponseSearch = (ResponseKeywordSearch) getParameter();
            }

            if (mRequestSearch == null) {
                mRequestSearch = new RequestKeywordSearch();
                mRequestSearch.page = 2;    //２ページ目を初期値
                mRequestSearch.keyword = mResponseSearch.keyword;
            }

            //--ADD NT-LWL 17/09/08 BrandSearch FR -
            if (mBrandRequestSearch == null){
                mBrandRequestSearch = new RequestKeywordSearch();
                mBrandRequestSearch.page = 1;
                mBrandRequestSearch.keyword = mResponseSearch.keyword;
            }
            //--ADD NT-LWL 17/09/08 BrandSearch TO -

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflateLayout(inflater, R.layout.fragment_search_result_keyword, container, false);


        mListView = (ExpandableListView) mRootView.findViewById(R.id.seriesList);

		mListView.setGroupIndicator(getContext().getResources().getDrawable(android.R.color.transparent));

		//ヘッダー情報
		//Myフォルダ選択
//		mFolderView = inflateLayout(inflater, R.layout.list_item_keyword_header_folder, mListView, false);

		//
//		mCountView = inflateLayout(inflater, R.layout.list_item_keyword_sub, mListView, false);
//        mTextListCount = (TextView) mCountView.findViewById(R.id.textCount);


		//
    	mFooterView = new FooterView(inflater, new FooterView.OnViewListener() {
			@Override
		    public void onReadList() {

				doReadList();
			}
		});

//		mListView.addHeaderView(mFolderView, null, false);
//		mListView.addHeaderView(mCountView, null, false);
        mListView.addFooterView(mFooterView.getFooterView(), null, false);


		//
        View emptyList = mRootView.findViewById(R.id.emptyList);

        boolean showdata = true;
        if (mResponseSearch.mSeriesList == null || mResponseSearch.mSeriesList.isEmpty()){
            showdata = false;
        } else if (mResponseSearch.totalCount == null || mResponseSearch.totalCount == 0){
            showdata = false;
        }

        if (showdata){
            mRootView.findViewById(R.id.LinearCategory).setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            mRootView.findViewById(R.id.LinearCategory).setVisibility(View.GONE);
            TextView textView = (TextView) emptyList.findViewById(R.id.textMessage);
            textView.setText(getResourceString(R.string.search_keyword_empty_list_item));
            emptyList.setVisibility(View.VISIBLE);
        }

		//
		mListAdapter = new MyExpandListAdapter(getContext(), mResponseSearch, mOnItemClickListener);

//        mListAdapter = new SearchSeriesAdapter(getContext(), R.layout.list_item_keyword_search, new ArrayList<SearchSeriesList.Series>());
        mListView.setAdapter(mListAdapter);

        //--ADD NT-LWL 17/09/04 BrandSearch FR -
        searchLayoutLine = mRootView.findViewById(R.id.category_diviver);
        initBrandDialog();

        //--ADD NT-LWL 17/09/04 BrandSearch TO -


		mListViewUtil = new ListViewUtil(mListView, new ListViewUtil.OnListViewListener() {
			    @Override
			    public boolean canMoreRead() {

	                //エラー状態
                    return !mFooterView.isFooterError();

                }

			    @Override
			    public void onAdditionalReading(int totalItemCount) {
                    //--UDP NT-LWL 17/09/12 BrandSearch FR -
                    // doAdditionalReading(totalItemCount);
                    if (!isClickBrandOk){
                        doAdditionalReading(totalItemCount);
                    }
                    //--UDP NT-LWL 17/09/12 BrandSearch TO -
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

//        mListView.setOnItemClickListener(onItemClickListener);


		// 商品検索
		final View buttonSearch = mRootView.findViewById(R.id.buttonSearch);
		buttonSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                //设置 点击来源为点击 搜索按钮
                searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
				doSearchItem();
			}
		});

		//クリアボタン
		final View mKeywordClear = mRootView.findViewById(R.id.buttonKeywordClear);
		mKeywordClear.setVisibility(View.INVISIBLE);
		mKeywordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPartNumber.setText("");
            }
        });


	    mEditPartNumber = (AutoCompleteTextView) mRootView.findViewById(R.id.editPartNumber);
		//EditText監視
        mEditPartNumber.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {}

		    @Override
		    public void afterTextChanged(Editable s) {

				String str = s.toString();
				if (str.isEmpty()) {
					buttonSearch.setEnabled(false);
					mKeywordClear.setVisibility(View.INVISIBLE);
				} else {
					buttonSearch.setEnabled(true);
					mKeywordClear.setVisibility(View.VISIBLE);
				}
		    }
		});
		//サジェストを選択した時のリスナー
		mEditPartNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AutoCompleteProcess.FilterObject filterObject = (AutoCompleteProcess.FilterObject) parent.getItemAtPosition(position);
//				showToast("CCC: "+ position);
//				showToast("CCC: "+ filterObject.suggestString);

                doSuggest(filterObject);
            }

        });



/*
		String str = mEditPartNumber.getText().toString();
		if (str.isEmpty()) {
			buttonSearch.setEnabled(false);
		} else {
			buttonSearch.setEnabled(true);
		}
*/
		mEditPartNumber.setText(mResponseSearch.keyword);


		autoCompleteProcess = new AutoCompleteProcess(getContext(), mEditPartNumber, new AutoCompleteProcess.KeyEnter() {
			@Override
			public void codeEnter() {
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                //设置 点击来源为点击 搜索按钮
                searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
				doSearchItem();
			}
		});


        // フォルダ一覧スライド
//        makeFolderListView();

        return mRootView;
    }
    //--ADD NT-LWL 17/09/04 BrandSearch FR -
    private void initBrandDialog() {
        //isBrandSearch = false;
        // 创建品牌选择对话框
        View v1 = mListView.getExpandableListAdapter().getGroupView(0, false, null, mListView);
        v1.measure(0,0);
        int n = mListAdapter.getGroupCount();
        for (int i=0 ;i<n;i++){
            Element element = (Element) mListAdapter.getGroup(i);
            if (element.getObjectType()==1||element.getObjectType()==4){
                if (element.getObjectType() == 4){
                    brandGroupPosition = i;
                }
            }
            if (element.getObjectType() == 2){
                countPosition = i;
            }
        }
        //获取状态栏高度
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // 测量高度
        int h = DisplayUtil.screenhightPx - DisplayUtil.dip2px(mParent, 64) - statusBarHeight - mParent.getHeaderView().getHeaderView().getHeight();
        mBrandSearchUtils = new BrandSearchUtils(mParent,mResponseSearch.mSeriesBrandList);
        mBrandSearchUtils.setBaseView(searchLayoutLine);
        mBrandSearchUtils.setPopupWindowHeight(h);
        // 点击确定回调
        mBrandSearchUtils.setOnBrandSelectOkListener(new BrandSearchUtils.OnBrandSelectOkListener() {
            @Override
            public void onSelectOk(List<Brand> selectedBrands) {
                // 调用品牌检索API
                String brand ="";
                for (int i=0; i<selectedBrands.size(); i++){
                    if (i != 0) {
                        brand += ","+selectedBrands.get(i).brandCode;
                    }else {
                        brand += selectedBrands.get(i).brandCode;
                    }
                }
                // 判断是否重复
                String oldBrand = mBrandRequestSearch.brandCode;
                if (brand.equals(oldBrand)){
                    return;
                }
                isClickBrandOk = true;

                // 设置已选品牌数
                setBrandCount(brandGroupPosition,selectedBrands.size(),true);

                mListAdapter.clearSeriesList();
                mListAdapter.notifyDataSetChanged();
                mListView.removeFooterView(mFooterView.getFooterView());

                isBrandSearch = true;
                mBrandRequestSearch.page = 1;
                mBrandRequestSearch.brandCode = brand;
                mMyBrandSearchApi.setParameter(mBrandRequestSearch);
                mMyBrandSearchApi.connect(mParent);




            }

            @Override
            public void onReset() {
            }
        });
        // 设置品牌数
        setBrandCount(brandGroupPosition, mResponseSearch.mSeriesBrandList.size(),false);
        mListAdapter.setBrandSearchUtils(mBrandSearchUtils);
    }

    /**
     * 设置品牌数
     * @param brandGroupPosition  组序号
     * @param size  个数
     */
    private void setBrandCount(int brandGroupPosition, int size ,boolean isSelect) {
        Element element = (Element) mListAdapter.getGroup(brandGroupPosition);

        if (isSelect){
            if (size == 0){
                element.setObject(mResponseSearch.mSeriesBrandList.size());
            }else {
                element.setObject("已选" + size);
            }
        }else {
            int n = 0;
            if (mBrandSearchUtils != null && mBrandSearchUtils.getmBrandAdapter() != null) {
                n = mBrandSearchUtils.getmBrandAdapter().getSelectedBrands().size();
            }
            if (n == 0) {
                element.setObject(size);
            }else {
                // 默认选择
                element.setObject("已选" + n);
            }
        }
    }
    //--ADD NT-LWL 17/09/04 BrandSearch TO -

    // リストビューのアイテムがクリックされた時
    MyExpandListAdapter.OnItemClickListener	mOnItemClickListener	= new MyExpandListAdapter.OnItemClickListener() {
		public void onItemClick(SearchSeriesList.Series itemData, View view, int position) {

//			showToast("onItemClick: ");

			doSpProduct(itemData);
		}

    	public void onItemCategory(CategoryList.Category info) {

//			showToast("onItemCategory: ");

			//子カテゴリ有りフラグ 0: 子カテゴリ無し
			if ("0".equals(info.hasChildCategoryFlag)) {
				//子カテゴリなしの場合にシリーズ検索
				doSearchSeries(info);
			} else {
                doCategorySearch(info);
			}

		}

	};




    /**
     * updateListView
     */
    private void updateListView(){

        if (lostSession != null){
            lostSession.close();
            lostSession = null;
        }

        View emptyList = mRootView.findViewById(R.id.emptyList);


        boolean showdata = true;
        if (mResponseSearch.mSeriesList == null || mResponseSearch.mSeriesList.isEmpty()){
            showdata = false;
        } else if (mResponseSearch.totalCount == null || mResponseSearch.totalCount == 0){
            showdata = false;
        }

        if (showdata){
            mRootView.findViewById(R.id.LinearCategory).setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            mRootView.findViewById(R.id.LinearCategory).setVisibility(View.GONE);
            TextView textView = (TextView) emptyList.findViewById(R.id.textMessage);
            textView.setText(getResourceString(R.string.search_keyword_empty_list_item));
            emptyList.setVisibility(View.VISIBLE);
        }

		//
        mListView.setAdapter((MyExpandListAdapter)null);
		mListView.removeFooterView(mFooterView.getFooterView());
		mFooterView.init();
        mListView.addFooterView(mFooterView.getFooterView(), null, false);

//        mListAdapter = new SearchSeriesAdapter(getContext(), R.layout.list_item_keyword_search, new ArrayList<SearchSeriesList.Series>());
//        mListView.setAdapter(mListAdapter);

		mListAdapter = new MyExpandListAdapter(getContext(), mResponseSearch, mOnItemClickListener);
        mListView.setAdapter(mListAdapter);
        //--ADD NT-LWL 17/09/04 BrandSearch FR -
        initBrandDialog();
        //--ADD NT-LWL 17/09/04 BrandSearch TO -

		mListViewUtil.init();	//逐次読み込み修正

        addListAdapter(mResponseSearch);

        // フォルダ一覧スライド
//        makeFolderListView();
    }


    // フォルダ一覧スライド
/*
	private void makeFolderListView() {

        LinearLayout slideTrigger = (LinearLayout) mFolderView.findViewById(R.id.layoutSlideTrigger);
        LinearLayout slideContents = (LinearLayout) mFolderView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = mFolderView.findViewById(R.id.imageSlideSwitch);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        int categoryCount = mResponseSearch.mCategoryList.size();
        if (categoryCount == 0) {

            slideTrigger.setVisibility(View.GONE);
            slideContents.setVisibility(View.GONE);
        } else {

            slideTrigger.setVisibility(View.VISIBLE);
            slideContents.setVisibility(View.VISIBLE);

            ((TextView) mFolderView.findViewById(R.id.textSlideSwitch)).setText(String.format(getResourceString(R.string.search_keyword_category_count),
                    Format.formatCount(categoryCount)));

            for (CategoryList.Category info : mResponseSearch.mCategoryList) {

                View tableRow = inflateLayout(mParent.getLayoutInflater(), R.layout.list_item_category_search_sub2, slideContents, false);

                TextView textView1 = (TextView) tableRow.findViewById(R.id.subcategoryName);
                textView1.setText(info.categoryName);

				//画像
				ImageView iv = (ImageView) tableRow.findViewById(R.id.subCategoryImage);
				View pv = tableRow.findViewById(R.id.progressView);

				String imageUrl = info.categoryImageUrl;
				PicassoUtil.PicassoLoad(iv, pv, imageUrl);

				//
                slideContents.addView(tableRow);

                final CategoryList.Category infoF = info;

                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

						//子カテゴリ有りフラグ 0: 子カテゴリ無し
						if ("0".equals(infoF.hasChildCategoryFlag)) {
							//子カテゴリなしの場合にシリーズ検索
							doSearchSeries(infoF);
						} else {
	                        doCategorySearch(infoF);
						}

                    }
                });

            }

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);
        }
	}
*/


    /**
     * dispTotalCount
     * @param totalCount
     */
/*
	private void dispTotalCount(int totalCount) {
		String count = Format.formatCount(totalCount) + getResourceString(R.string.search_keyword_total);
//        mTextListCount.setText(count);
	}
*/


/*
	//
    private void addExpandAnimator(View triggerView, final View viewSlideSwitch, View containerView) {

		final ExpandAnimator animator = createAnimator(containerView);

        triggerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                if (!animator.isAnimating()) {
                    if (animator.isExpand()) {

                        animator.unexpand();
                        viewSlideSwitch.setSelected(false);
                    } else {

                        animator.expand();
                        viewSlideSwitch.setSelected(true);
                    }
                }
            }
        });
    }


    private ExpandAnimator createAnimator(View containerView) {


        ExpandAnimator animator = new ExpandAnimator(containerView, new ExpandAnimator.OnAnimationListener() {
            @Override
            public void onExpanded(ExpandAnimator e) {
            }

            @Override
            public void onStartExpand(ExpandAnimator e) {
            }

            @Override
            public void onStartUnexpand(ExpandAnimator e) {
            }

            @Override
            public void onUnexpanded(ExpandAnimator e) {
            }
        });

        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
		animator.setWrapContent(true);
        return animator;
    }
*/


/*
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            SearchSeriesList.Series series = (SearchSeriesList.Series)parent.getItemAtPosition(position);

//    		doSearchSeries(series);
			doSpProduct(series);
        }
    };
*/



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

        mMySearchApi.close();
//        mMyDetailApi.close();
//		mKeywordSearchApi.close();
        searchKeywordProcess.close();
		mSeriesSearchApi.close();
        mGetSpProductApi.close();
        if (lostSession != null) {
            lostSession.close();
            lostSession = null;
        }
        super.onPause();
    }


	/**
	 * doSearchItem
	 */
	private void doSearchItem() {
        hideKeyboard();

		//フラグメント遷移をしない
		String str = mEditPartNumber.getText().toString();

		RequestKeywordSearch request = new RequestKeywordSearch();
		request.keyword = str;
        searchKeywordProcess.run(getContext(), new SearchKeywordProcess.DataCallback() {
            @Override
            public void noticeKeyword(ResponseKeywordSearch response) {

                mResponseSearch = response;

                mRequestSearch = new RequestKeywordSearch();
                mRequestSearch.page = 2;    //２ページ目を初期値
                mRequestSearch.keyword = mResponseSearch.keyword;

                //--ADD NT-LWL 17/09/08 BrandSearch FR -
                isBrandSearch = false;
                mBrandRequestSearch = new RequestKeywordSearch();
                mBrandRequestSearch.page = 1;
                mBrandRequestSearch.keyword = mResponseSearch.keyword;
                complexFlagCount = 0;
                //--ADD NT-LWL 17/09/08 BrandSearch TO -

                updateListView();

            }
        }, request, getScreenId());



	}



    private void doAdditionalReading(int totalItemCount) {

        doReadList();
    }



    private void doReadList() {
        hideKeyboard();

        if (lostSession != null) {
            lostSession.close();
            lostSession = null;
        }

        //--UDP NT-LWL 17/09/07 BrandSearch FR -
        //mMySearchApi.setParameter(mRequestSearch);
        //mMySearchApi.connect(getContext());

        if (isBrandSearch){
            mMyBrandSearchApi.setParameter(mBrandRequestSearch);
            mMyBrandSearchApi.connect(mParent);
        }else {
            mMySearchApi.setParameter(mRequestSearch);
            mMySearchApi.connect(getContext());
        }
        //--UDP NT-LWL 17/09/07 BrandSearch TO -
    }

    private void doCategorySearch(CategoryList.Category cate){
        hideKeyboard();
        mCategoryApi.connect(getContext(), cate);
    }

    private void doSearchSeries(CategoryList.Category cate){
        hideKeyboard();
        mSeriesSearchApi.connect(getContext(), cate);
    }


    private void addListAdapter(ResponseKeywordSearch response) {

        //総件数
        int totalCount = (response.totalCount != null) ? response.totalCount : 0;
        if (totalCount == 0){
            return;
        }
        //--ADD NT-LWL 17/09/28 Series FR -
        if (!isDestroyView){
                complexFlagCount += response.complexFlagCount;
                // 依次减去复杂品数
                mResponseSearch.totalCount = response.totalCount - complexFlagCount;
                response.totalCount = mResponseSearch.totalCount;
            }
        isDestroyView = false;
        //--ADD NT-LWL 17/09/28 Series TO -
//        mListAdapter.addAll(response.mSeriesList);

        mListAdapter.addAllAndUpdate(response);


		//JSONリストが0件の場合
		int listSize = response.mSeriesList.size();

        //--UDP NT-LWL 17/09/28 Series FR -
		//  isBrandSearch
        if (isBrandSearch){
            mListViewUtil.updateListData(listSize, totalCount, mBrandRequestSearch.page, mListAdapter.getGroupCount()+complexFlagCount, AppConst.KEYWORD_LIST_MAX_COUNT, AppConst.KEYWORD_LIST_REQUEST_COUNT);
        }else {
            mListViewUtil.updateListData(listSize, totalCount, mRequestSearch.page, mListAdapter.getGroupCount()+complexFlagCount, AppConst.KEYWORD_LIST_MAX_COUNT, AppConst.KEYWORD_LIST_REQUEST_COUNT);
        }
		//mListViewUtil.updateListData(listSize, totalCount, mRequestSearch.page, mListAdapter.getGroupCount(), AppConst.KEYWORD_LIST_MAX_COUNT, AppConst.KEYWORD_LIST_REQUEST_COUNT);
        isClickBrandOk = false;
        //--UDP NT-LWL 17/09/28 Series TO -

		mListAdapter.notifyDataSetChanged();

//		dispTotalCount(totalCount);
    }


    private void updateListAdapter(ResponseKeywordSearch response) {

		//更新 2015/09/22
		mResponseSearch.totalCount = response.totalCount;

        //既存のデータに足す
        mResponseSearch.mSeriesList.addAll(response.mSeriesList);

    	addListAdapter(response);
    }

    private class MySearchApi extends ApiAccessWrapper {

        RequestKeywordSearch mRequest;

        public void setParameter(RequestKeywordSearch request) {

            mRequest = request;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        protected String getScreenId() {
            return SearchResultKeywordFragment.this.getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createKeywordSearch(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseKeywordSearch response = new ResponseKeywordSearch();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                mFooterView.setFooterViewError();
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    ++mRequestSearch.page;    //読み込み成功時は次のページを設定
                    updateListAdapter(response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    mFooterView.setFooterViewError();
                    break;
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            lostSession = new LostSession();
            lostSession.run();
            super.onLostSession(responseCode, result);
        }

        //バグ #XXXX対応
        @Override
	    protected void onNetworkError(int responseCode){
	        super.onNetworkError(responseCode);
            mFooterView.setFooterViewError();
    }

		//バグ #XXXX対応
        @Override
	    protected void onTimeout(){
	        super.onTimeout();
            mFooterView.setFooterViewError();
	    }
    }

    //--ADD NT-LWL 17/09/08 BrandSearch FR -
    private class MyBrandSearchApi extends ApiAccessWrapper {

        RequestKeywordSearch mRequest;

        public void setParameter(RequestKeywordSearch request) {

            mRequest = request;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        protected String getScreenId() {
            return SearchResultKeywordFragment.this.getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createKeywordBrandSearch(mRequest,false);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseKeywordSearch response = new ResponseKeywordSearch();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                mFooterView.setFooterViewError();
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    // 更新数据
                    mBrandSearchUtils.setBrands(response.mSeriesBrandList);
                    if (response.mSeriesBrandList!=null && !response.mSeriesBrandList.isEmpty()){
                        mResponseSearch.mSeriesBrandList.clear();
                        mResponseSearch.mSeriesBrandList.addAll(response.mSeriesBrandList);
                    }
                    // 清除重复数据
                    if (mBrandRequestSearch.page == 1){
                        mResponseSearch.mSeriesList.clear();
                        complexFlagCount = 0;
                        mListViewUtil.init();
                        mFooterView.init();
                        mListView.addFooterView(mFooterView.getFooterView(),null,false);
                    }

                    ++mBrandRequestSearch.page;    //読み込み成功時は次のページを設定
                    updateListAdapter(response);

                    break;

                default:
                    Element element = (Element) mListAdapter.getGroup(countPosition);
                    element.setObject(0);
                    mListAdapter.notifyDataSetChanged();

                    showErrorMessage(response.errorList);
                    mFooterView.setFooterViewError();
                    break;
            }
        }

        @Override
        protected void onLostSession(int responseCode, String result) {
            //--ADD NT-LWL 17/09/12 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/12 BrandSearch TO -
            lostSession = new LostSession();
            lostSession.run();
            super.onLostSession(responseCode, result);
        }

        //バグ #XXXX対応
        @Override
        protected void onNetworkError(int responseCode){
            //--ADD NT-LWL 17/09/12 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/12 BrandSearch TO -
            super.onNetworkError(responseCode);
            mFooterView.setFooterViewError();
        }

        //バグ #XXXX対応
        @Override
        protected void onTimeout(){
            //--ADD NT-LWL 17/09/12 BrandSearch FR -
            isClickBrandOk = false;
            //--ADD NT-LWL 17/09/12 BrandSearch TO -
            super.onTimeout();
            mFooterView.setFooterViewError();
        }
    }
    //--ADD NT-LWL 17/09/08 BrandSearch TO -

    private class LostSession{

        public LostSession(){
        }
        public void run(){
            AppNotifier.getInstance().addListener(listener, AppNotifier.USER_LOGIN);
            mFooterView.setFooterViewError();
        }
        public void close(){
            AppNotifier.getInstance().removeListener(listener);
        }

        AppNotifier.AppNoticeListener listener = new AppNotifier.AppNoticeListener() {
            @Override
            public void appNotice(AppNotifier.AppNotice notice) {
                doReadList();
            }
        };

    }


    /**
     * カテゴリ検索
     */
    private class CategorySearchApi extends ApiAccessWrapper {

        CategoryList.Category cate;

        public void connect(Context context, CategoryList.Category cate) {
            this.cate = cate;
            super.connect(context);
        }

        @Override
        protected String getScreenId() {
            return SearchResultKeywordFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
			//この画面のカテゴリAPIにはupdateDateTimeを付与しない
            return ApiBuilder.createSearchCategory(cate.categoryCode, null, false);
        }

        @Override
        public void onResult(int responseCode, String result) {

            CategoryList categorylist = new CategoryList();
            if (!categorylist.setData(result)){
                showErrorMessage(null);
                return;
            }

            if (responseCode == NetworkInterface.STATUS_OK) {
				//null避け
                CategoryList.Category category = new CategoryList.Category();
                if (categorylist.categoryList != null && (!categorylist.categoryList.isEmpty())) {
                    category = categorylist.categoryList.get(0);
                }

				category.categoryName = cate.categoryName;

                getFragmentController().stackFragment(new CategorySearchListFragment(), FragmentController.ANIMATION_SLIDE_IN, category);

            } else {
                showErrorMessage(categorylist.errorList);
            }
        }
    }



    private class SeriesSearchApi extends ApiAccessWrapper {
        CategoryList.Category category;

        @Override
        protected String getScreenId() {
            return SearchResultKeywordFragment.this.getScreenId();
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


    private void doSpProduct(SearchSeriesList.Series seriesItem) {

        hideKeyboard();

        String completeType = seriesItem.completeType;
        String seriesCode = seriesItem.seriesCode;
        String innerCode = seriesItem.innerCode;
        String partNumber = seriesItem.partNumber;
		Integer quantity = 1;
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        isFromSuggest = false;
        //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


	private void  doSuggest(AutoCompleteProcess.FilterObject filterObject) {

//		showToast("CCC: "+ position);
//		showToast("CCC: "+ filterObject.suggestString);
//		doSuggest(filterObject);
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        //设置 点击来源为点击 推荐列表
        searchKeywordProcess.setClickSource(GoogleAnalytics.suggest);
        isFromSuggest = true;
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        SearchSuggest.PartNumber partNumber = filterObject.partNumber;

		//キーワード検索？
		if (partNumber == null) {
			//キーワード検索
	        doSearchItem();
			return;
		}

		//型番検索
		//BaseGetSpProductApi
        hideKeyboard();

        String completeType = partNumber.completeType;
        String seriesCode = partNumber.seriesCode;
        String innerCode = partNumber.innerCode;
        String partNumberStr = partNumber.partNumber;
		Integer quantity = 1;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumberStr, quantity);
        mGetSpProductApi.connect(getContext());

    }

	//サイカタ
	protected String getSaicataId() {
		return jp.co.misumi.misumiecapp.SaicataId.SearchKeyword;
	}



}


