package jp.co.misumi.misumiecapp.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.QuotationSearchAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.RequestGetQuotation;
import jp.co.misumi.misumiecapp.data.RequestSearchQuotation;
import jp.co.misumi.misumiecapp.data.ResponseGetQuotation;
import jp.co.misumi.misumiecapp.data.ResponseSearchInfo;
import jp.co.misumi.misumiecapp.data.ResponseSearchQuotation;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.ListViewUtil;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.widget.FooterView;


/**
 * SPv1_11 見積履歴一覧画面
 */
public class EstimateListFragment extends BaseFragment {

    //検索リクエスト用
    private RequestSearchQuotation mRequestSearch;
    //画面表示用
    private ResponseSearchQuotation mResponseSearch;

    private QuotationSearchAdapter mListAdapter;
    private ListView mListView;
    private ListViewUtil mListViewUtil;
    private FooterView mFooterView;

	private TextView mTextListCount;

    //API
    private MySearchApi mMySearchApi;
    private MyDetailApi mMyDetailApi;

    //セッション切れ
    private LostSession lostSession = null;


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
        outState.putSerializable("RequestSearch", mRequestSearch);
        outState.putSerializable("ResponseSearch", mResponseSearch);
    }


    public EstimateListFragment() {

        mMySearchApi = new MySearchApi();
        mMyDetailApi = new MyDetailApi();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //リクエストデータを最初に作成して、このデータクラスを画面編集と同期させる
        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

            mResponseSearch = (ResponseSearchQuotation) savedInstanceState.getSerializable("ResponseSearch");
            mRequestSearch = (RequestSearchQuotation) savedInstanceState.getSerializable("RequestSearch");

        } else {

            if (mResponseSearch == null) {
                mResponseSearch = (ResponseSearchQuotation) getParameter();
            }

            if (mRequestSearch == null) {
                mRequestSearch = new RequestSearchQuotation();
                mRequestSearch.page = 2;    //２ページ目で初期値
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateLayout(inflater, R.layout.fragment_estimate_list, container, false);

        //件数
        mTextListCount = (TextView) rootView.findViewById(R.id.textListCount);
//        View line = rootView.findViewById(R.id.divider);
        if (mResponseSearch.totalCount == null || mResponseSearch.totalCount == 0) {
//            line.setVisibility(View.VISIBLE);
        } else {
//            line.setVisibility(View.GONE);
        }

		//2015/10/13 0件時に表示の要件
		//虫眼鏡の 0件表示は totalCountが 0で orderListが空の時のみ表示する。
        View emptyList = rootView.findViewById(R.id.emptyList);
        if ((mResponseSearch.totalCount == null || mResponseSearch.totalCount == 0)
                && (mResponseSearch.mList.size() == 0)){

            TextView textMessage = (TextView) emptyList.findViewById(R.id.textMessage);
            textMessage.setText(getResourceString(R.string.quote_hist_empty_list_item));
            emptyList.setVisibility(View.VISIBLE);
        } else {
            emptyList.setVisibility(View.GONE);
        }

        mListAdapter = new QuotationSearchAdapter(getContext(), R.layout.list_item_estimate_list_header, new ArrayList<ResponseSearchInfo>(), mOnItemClickListener);

        //
    	mFooterView = new FooterView(inflater, new FooterView.OnViewListener() {
			@Override
		    public void onReadList() {

				doReadList();
			}
		});

        mListView = (ListView) rootView.findViewById(R.id.listView);
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
                    doAdditionalReading(totalItemCount);
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

        return rootView;
    }

//    public void onRemoveStack() {
//        super.onRemoveStack();
//        if (lostSession != null){
//            lostSession.close();
//            lostSession = null;
//        }
//    }


    private void dispTotalCount(int totalCount) {

        //件数
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        ssb.append(getResourceString(R.string.quote_hist_count_str1));
        String str = Format.formatCount(totalCount);

        SpannableString ss;
		ss = SpannableUtil.newSpannableString(str, 15, true, true);
        ssb.append(ss);

        ss = new SpannableString(getResourceString(R.string.quote_hist_count_str2));
        ssb.append(ss);

        mTextListCount.setText(ssb);
    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.EstimateList;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {

        mMySearchApi.close();
        mMyDetailApi.close();
        if (lostSession != null){
            lostSession.close();
            lostSession = null;
        }
        super.onPause();
    }



    // リストビューのアイテムがクリックされた時
    QuotationSearchAdapter.OnItemClickListener mOnItemClickListener = new QuotationSearchAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view,
                                int position, long id) {

            ResponseSearchQuotation.ListInfo info = (ResponseSearchQuotation.ListInfo) mResponseSearch.mList.get(position);

            doEstimateDetail(info);

        }
    };


    private void doAdditionalReading(int totalItemCount) {

        doReadList();
    }


    private void doReadList() {

        hideKeyboard();

        if (lostSession != null){
            lostSession.close();
            lostSession = null;
        }

        mMySearchApi.setParameter(mRequestSearch);
        mMySearchApi.connect(getContext());
    }


    private void addListAdapter(ResponseSearchQuotation response) {

        mListAdapter.addAll(response.mList);

		//JSONリストが0件の場合
		int listSize = response.mList.size();
        //総件数
        int totalCount = (response.totalCount != null) ? response.totalCount : 0;

		//
		mListViewUtil.updateListData(listSize, totalCount, mRequestSearch.page, mListAdapter.getCount(), AppConst.HISTORY_LIST_MAX_COUNT, AppConst.HISTORY_LIST_REQUEST_COUNT);

        mListAdapter.notifyDataSetChanged();

		dispTotalCount(totalCount);
    }


    private void updateListAdapter(ResponseSearchQuotation response) {

        if (lostSession != null){
            lostSession.close();
            lostSession = null;
        }

		//更新 2015/09/22
		mResponseSearch.totalCount = response.totalCount;

        //既存のデータに足す
        mResponseSearch.mList.addAll(response.mList);

    	addListAdapter(response);
    }


    private void doEstimateDetail(ResponseSearchQuotation.ListInfo info) {

        hideKeyboard();

        RequestGetQuotation request = new RequestGetQuotation();
        request.quotationSlipNo = info.infoSlipNo;

        mMyDetailApi.setParameter(request);
        mMyDetailApi.connect(getContext());
    }


    private class MySearchApi extends ApiAccessWrapper {

        RequestSearchQuotation mRequest;

        @Override
        protected String getScreenId() {
            return EstimateListFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        public void setParameter(RequestSearchQuotation request) {

            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createSearchQuotation(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseSearchQuotation response = new ResponseSearchQuotation();
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
        protected void onLostSession(int responseCode, String result){
            lostSession = new LostSession();
            lostSession.run();
            super.onLostSession(responseCode, result);
        }

		//バグ #3804対応
        @Override
	    protected void onNetworkError(int responseCode){
	        super.onNetworkError(responseCode);
            mFooterView.setFooterViewError();
 	    }

		//バグ #3809対応
        @Override
	    protected void onTimeout(){
	        super.onTimeout();
            mFooterView.setFooterViewError();
	    }


    }

    private class LostSession{

        public LostSession(){
        }
        public void run(){
            AppNotifier.getInstance().addListener(listner, AppNotifier.USER_LOGIN);
            mFooterView.setFooterViewError();
        }
        public void close(){
            AppNotifier.getInstance().removeListener(listner);
        }

        AppNotifier.AppNoticeListener listner = new AppNotifier.AppNoticeListener() {
            @Override
            public void appNotice(AppNotifier.AppNotice notice) {
                doReadList();
            }
        };
    }


    private class MyDetailApi extends ApiAccessWrapper {

        RequestGetQuotation mRequest;

        @Override
        protected String getScreenId() {
            return EstimateListFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        public void setParameter(RequestGetQuotation request) {

            mRequest = request;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createGetQuotation(mRequest);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseGetQuotation response = new ResponseGetQuotation();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    getFragmentController().stackFragment(new EstimateDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


	//サイカタ
	protected String getSaicataId() {
		return jp.co.misumi.misumiecapp.SaicataId.EstimateList;
	}
}


