package jp.co.misumi.misumiecapp.api;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.data.RequestCheckQuotationFromQuote;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotation;
import jp.co.misumi.misumiecapp.data.ResponseCheckQuotationFromQuote;


/**
 * 見積確認のAPI FROM 見積確認
 */
public abstract class QuoteConfirmQuoteBaseApi extends ApiAccessWrapper {

    private boolean mIsFromCart;
    private RequestCheckQuotationFromQuote mRequest;
    private MessageDialog mMessageDialog;

    public void setParameter(boolean isFromCart, RequestCheckQuotationFromQuote request, MessageDialog messageDialog) {
        mIsFromCart = isFromCart;
        mRequest = request;
        mMessageDialog = messageDialog;
    }


    @Override
    public HashMap<String, String> getParameter() {

        return ApiBuilder.createCheckQuotationFromQuote(mRequest);
    }

    @Override
    public void onResult(int responseCode, String result) {

        ResponseCheckQuotationFromQuote response = new ResponseCheckQuotationFromQuote();
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
                }

                onSuccess(response);
                break;

            default:
                showErrorMessage(response.errorList);
                break;
        }
    }

    protected void onSuccess(ResponseCheckQuotation response) {
    }

}
