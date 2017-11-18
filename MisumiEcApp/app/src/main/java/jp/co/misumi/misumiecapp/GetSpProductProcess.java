package jp.co.misumi.misumiecapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.fragment.ItemDetailFragment;
import jp.co.misumi.misumiecapp.observer.ApiAccessObserver;


public class GetSpProductProcess {

    private String screenId;
//    private Context context;
    private FragmentController fragmentController = null;
    private DataCallback dataCallback = null;
    GetSpProductApi keywordSearchApi = null;

    public interface DataCallback{
        void noticeKeyword(ResponseGetSpProduct response);
    }


    public void run(Context context, FragmentController fragmentController, String completeType, String seriesCode, String innerCode, String partNumber, Integer quantity, String screenId){
        this.screenId = screenId;
//        this.context = context;
        this.fragmentController = fragmentController;

        keywordSearchApi = new GetSpProductApi();
        keywordSearchApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        keywordSearchApi.connect(context);
    }

    public void run(Context context, DataCallback callback, RequestKeywordSearch request, String screenId){
        this.screenId = screenId;
        this.dataCallback = callback;

        keywordSearchApi = new GetSpProductApi();
//        keywordSearchApi.setParameter(false,request);
        keywordSearchApi.connect(context);
    }


    public void close(){
        if (keywordSearchApi != null) {
            keywordSearchApi.close();
        }
    }

    /**
     * キーワード検索
     */
    protected class GetSpProductApi extends ApiAccessWrapper {

        String mCompleteType;
        String mSeriesCode;
        String mInnerCode;
        String mPartNumber;
        Integer mQuantity;

        public void setParameter(String completeType, String seriesCode, String innerCode, String partNumber, Integer quantity) {

            mCompleteType = completeType;
            mSeriesCode = seriesCode;
            mInnerCode = innerCode;
            mPartNumber = partNumber;
            mQuantity = quantity;
        }

        /**
         * getParameter
         *
         * @return
         */
        @Override
        public HashMap<String, String> getParameter() {
 
            Integer page = 1;
            Integer pageSize = AppConst.PART_NUMBER_LIST_REQUEST_COUNT + 1;

            return ApiBuilder.createGetSpProduct(mCompleteType, mSeriesCode, mInnerCode, mPartNumber, mQuantity, page, pageSize);
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

            ResponseGetSpProduct response = new ResponseGetSpProduct();
            boolean pars = response.setData(result);
            if (!pars){
                showErrorMessage(null);
                return;
            }
            //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
            setSearchGA(responseCode);
            //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
            //検索キーワードを次画面へ引き継ぎしたい為代入する
//            response.keyword = mKeywordSearch.keyword;

            switch (responseCode){
                case NetworkInterface.STATUS_OK:
                    if (fragmentController != null) {
                        fragmentController.stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    } else if(dataCallback != null){
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
            setSearchGA(responseCode);
        }

        @Override
        protected void onNetworkError(int responseCode) {
            super.onNetworkError(responseCode);
            setSearchGA(responseCode);
        }

        private void setSearchGA(int responseCode) {
            MisumiEcApp misumiEcApp= (MisumiEcApp) getContext().getApplicationContext();
            // 产品存在时 定义totalCount=1，反之NotFound
            String totalCount= responseCode== NetworkInterface.STATUS_OK ? "1":"NotFound";
            String errorCode=responseCode==NetworkInterface.STATUS_OK ? "success" : responseCode+"";
            // 标签拼接规则 来源+型号+数量+responseCode
            JSONObject object=new JSONObject();
            try {
                object.put("clickSource", GoogleAnalytics.suggest);
                object.put("partNumber",mPartNumber);
                object.put("totalCount",totalCount);
                object.put("errorCode",errorCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            String lable = object.toString();
            // 搜索 建议列表中 跟踪用户点击的产品
            GoogleAnalytics.sendProductTrack(misumiEcApp.getDefaultTracker(), null, GoogleAnalytics.CATEGORY_SEARCH, object);
        }
        //-- ADD NT-LWL 17/03/23 AliPay Payment TO -
    }
}
