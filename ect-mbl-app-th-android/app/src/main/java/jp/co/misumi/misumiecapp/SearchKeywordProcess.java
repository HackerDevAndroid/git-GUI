package jp.co.misumi.misumiecapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.ResponseKeywordSearch;
import jp.co.misumi.misumiecapp.fragment.SearchResultKeywordFragment;
import jp.co.misumi.misumiecapp.observer.ApiAccessObserver;

public class SearchKeywordProcess {

    private String screenId;
    //    private Context context;
    private FragmentController fragmentController = null;
    private DataCallback dataCallback = null;
    private SearchBar searchBar;
    KeywordSearchApi keywordSearchApi = null;
    //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
    //默认为点击按钮
    private String clickSource = GoogleAnalytics.searchBtn;

    public void setClickSource(String clickSource) {
        this.clickSource = clickSource;
    }

    //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
    public interface DataCallback {
        void noticeKeyword(ResponseKeywordSearch response);
    }

    public void runDefault(Context context, FragmentController fragmentController, RequestKeywordSearch request, String screenId) {
        this.screenId = screenId;
        this.fragmentController = fragmentController;

        keywordSearchApi = new KeywordSearchApi();
        keywordSearchApi.setParameter(true, request);
        keywordSearchApi.connect(context);

        this.searchBar = null;
    }

    public void run(Context context, FragmentController fragmentController, RequestKeywordSearch request, String screenId) {
        run(context, fragmentController, request, screenId, null);
    }

    public void run(Context context, FragmentController fragmentController, RequestKeywordSearch request, String screenId, SearchBar searchBar) {
        this.searchBar = searchBar;

        this.screenId = screenId;
//        this.context = context;
        this.fragmentController = fragmentController;

        keywordSearchApi = new KeywordSearchApi();
        keywordSearchApi.setParameter(false, request);
        keywordSearchApi.connect(context);
    }

    public void run(Context context, DataCallback callback, RequestKeywordSearch request, String screenId) {
        this.screenId = screenId;
        this.dataCallback = callback;

        keywordSearchApi = new KeywordSearchApi();
        keywordSearchApi.setParameter(false, request);
        keywordSearchApi.connect(context);
    }


    public void close() {
        if (keywordSearchApi != null) {
            keywordSearchApi.close();
        }
    }

    /**
     * キーワード検索
     */
    protected class KeywordSearchApi extends ApiAccessWrapper {

        private RequestKeywordSearch mKeywordSearch;
        private boolean runDefault;

        public void setParameter(boolean runDefault, RequestKeywordSearch keywordSearch) {
            mKeywordSearch = keywordSearch;
            this.runDefault = runDefault;
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
            if (runDefault) {
                return ApiBuilder.createKeywordSearchDefaut(mKeywordSearch);
            } else {
                return ApiBuilder.createKeywordSearch(mKeywordSearch);
            }
        }

        @Override
        protected String getScreenId() {
            return screenId;
        }

        @Override
        protected boolean getMethod() {
            return ApiAccessObserver.API_GET;
        }

        /**
         * onResult
         *
         * @param responseCode
         * @param result
         */
        @Override
        public void onResult(int responseCode, String result) {

            ResponseKeywordSearch response = new ResponseKeywordSearch();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            //検索キーワードを次画面へ引き継ぎしたい為代入する
            response.keyword = mKeywordSearch.keyword;
            //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
            setSearchGA(responseCode, response);
            //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
            switch (responseCode) {
                case NetworkInterface.STATUS_OK:
                    if (fragmentController != null) {

                        if (searchBar != null) {
                            searchBar.closeBar();
                        }

                        fragmentController.stackFragment(new SearchResultKeywordFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    } else if (dataCallback != null) {
                        dataCallback.noticeKeyword(response);
                    }
                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;

            }
        }

        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        @Override
        protected void onLostSession(int responseCode, String result) {
            super.onLostSession(responseCode, result);
            setSearchGA(responseCode, null);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            super.onNetworkError(responseCode);
            setSearchGA(responseCode, null);
        }

        private void setSearchGA(int responseCode, ResponseKeywordSearch rp) {
            ResponseKeywordSearch response = rp;
            if (response == null) {
                response = new ResponseKeywordSearch();
                response.totalCount = 0;
            }
            boolean showdata = true;
            if (response.mSeriesList == null || response.mSeriesList.isEmpty()) {
                showdata = false;
            } else if (response.totalCount == null || response.totalCount == 0) {
                showdata = false;
            }
            MisumiEcApp misumiEcApp = (MisumiEcApp) getContext().getApplicationContext();
            String totalCount = showdata ? response.totalCount + "" : "NotFound";
            String errorCode = responseCode == NetworkInterface.STATUS_OK ? "success" : responseCode + "";
            JSONObject object = new JSONObject();
            try {
                object.put("clickSource", clickSource);
                object.put("keyword", mKeywordSearch.keyword);
                object.put("totalCount", totalCount);
                object.put("errorCode", errorCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            String lable=object.toString();
            GoogleAnalytics.sendProductTrack(misumiEcApp.getDefaultTracker(), null, GoogleAnalytics.CATEGORY_SEARCH, object);
        }
        //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
    }
}
