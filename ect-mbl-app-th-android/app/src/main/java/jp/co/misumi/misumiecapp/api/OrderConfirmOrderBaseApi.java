package jp.co.misumi.misumiecapp.api;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.data.RequestCheckOrderFromOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrder;
import jp.co.misumi.misumiecapp.data.ResponseCheckOrderFromOrder;


/**
 * 注文確認のAPI FROM 注文確認
 */
public abstract class OrderConfirmOrderBaseApi extends ApiAccessWrapper {

    private boolean mIsFromCart;
    private RequestCheckOrderFromOrder mRequest;
    private MessageDialog mMessageDialog;

    public void setParameter(boolean isFromCart, RequestCheckOrderFromOrder request, MessageDialog messageDialog) {
        mIsFromCart = isFromCart;
        mRequest = request;
        mMessageDialog = messageDialog;
    }

    @Override
    public HashMap<String, String> getParameter() {

        return ApiBuilder.createCheckOrderFromOrder(mRequest);
    }

    @Override
    public void onResult(int responseCode, String result) {

        ResponseCheckOrderFromOrder response = new ResponseCheckOrderFromOrder();
        boolean pars = response.setData(result);
        if (!pars) {
            showErrorMessage(null);
            return;
        }

        switch (responseCode) {
            case NetworkInterface.STATUS_OK:

                //ダイアログ閉じる
                if (mMessageDialog != null) {
                    mMessageDialog.hide();
                    mMessageDialog = null;
                }

                //カートから、見積履歴からの区別をレスポンスに設定
                if (mIsFromCart) {

                    response.setFromCart();
                } else {

                    response.setFromQuote();
                }

                onSuccess(response);
                break;

            default:
                showErrorMessage(response.errorList);
                break;
        }
    }

    protected void onSuccess(ResponseCheckOrder response) {
    }

}

