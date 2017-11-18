package jp.co.misumi.misumiecapp;


/**
 * ApiResponseDispatcher
 */
public class ApiResponseDispatcher {


    /**
     * CallBackDispatcher
     */
    public interface CallBackDispatcher{
        void onResult(int responseCode, String result);
        void onNetworkError(int responseCode);
        void onLostSession(int responseCode, String result);
        void onTimeOut();
    }

    /**
     * CallBackDispatcherLight
     */
    public interface CallBackDispatcherLight{
        void onResult(int responseCode, String result);
        //void onProcessed();
    }


    /**
     * dispatch
     * @param responseCode
     * @param resultString
     * @param callBackDispatcher
     */
    public void dispatch(int responseCode, String resultString, CallBackDispatcher callBackDispatcher){
        assert callBackDispatcher != null;

        if (responseCode == NetworkInterface.STATUS_OK) {
            callBackDispatcher.onResult(responseCode, resultString);
        } else if (responseCode == NetworkInterface.NETWORK_ERROR) {
            callBackDispatcher.onNetworkError(responseCode);
        } else if (responseCode == NetworkInterface.SESSION_ERROR) {
            callBackDispatcher.onLostSession(responseCode, resultString);
        }else if (responseCode == NetworkInterface.TIMEOUT_ERROR){
            callBackDispatcher.onTimeOut();
        } else {
            AppLog.e("unknown error network interface(api)");
            callBackDispatcher.onResult(responseCode, resultString);
        }
    }

//    /**
//     * dispatch
//     * @param fragment
//     * @param responseCode
//     * @param resultString
//     * @param callBackDispatcherLight
//     */
//    public void dispatch(BaseFragment fragment, int responseCode, String resultString, CallBackDispatcherLight callBackDispatcherLight){
//        if (responseCode == NetworkInterface.STATUS_OK) {
//            callBackDispatcherLight.onResult(responseCode, resultString);
//            return;
//
//
//        } else if (responseCode == NetworkInterface.NETWORK_ERROR) {
//            new MessageDialog(fragment.getContext(), null).show(R.string.message_network_error, R.string.dialog_button_ok);
//        } else if (responseCode == NetworkInterface.SESSION_ERROR) {
//            new LostSessionProcess().run(fragment.getContext());
//        } else {
//            AppLog.e("unknown error network interface(api)");
//            new MessageDialog(fragment.getContext(), null).show(R.string.message_unknown_error, R.string.dialog_button_ok);
//        }
//    }

}
