package jp.co.misumi.misumiecapp.fragment;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;

/**
 * Created by ost000422 on 2015/10/09.
 */
public abstract class BaseGetSpProductApi extends BaseFragment {

    protected abstract class GetSpProductApi extends ApiAccessWrapper {

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

/*
        //派生先で実装する
        @Override
        protected String getScreenId() {
            return SearchResultCategoryFragment.this.getScreenId();
        }
*/

        @Override
        protected boolean getMethod() {

            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {

            Integer page = 1;
            Integer pageSize = AppConst.PART_NUMBER_LIST_REQUEST_COUNT + 1;

            return ApiBuilder.createGetSpProduct(mCompleteType, mSeriesCode, mInnerCode, mPartNumber, mQuantity, page, pageSize);
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseGetSpProduct response = new ResponseGetSpProduct();
            boolean pars = response.setData(result);
            if (!pars) {

                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    onSuccess(response);
                    break;

                default:

                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected void onNetworkError(int responseCode) {

            super.onNetworkError(responseCode);
        }

        @Override
        protected void onTimeout() {

            super.onTimeout();
        }

        //派生先で実装する
        protected abstract void onSuccess(ResponseGetSpProduct response);
    }
}
