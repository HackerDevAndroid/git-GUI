package jp.co.misumi.misumiecapp.autocomplete;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.SearchSuggest;



/**
 * SuggestManager
 */
public class SuggestManager{

    private static int counter = 0;

    Context context;

    interface SuggestManagerListener{
        void onResult(String keyword, SearchSuggest searchSuggest);
    }

    private static final Object lockObj = new Object();
    private final int delayTime = 100;

    private SuggestManagerListener listener;
    private List<Suggest> listRequestSuggest;
    private Handler threadHandler;
    private HandlerThread handlerThread;
    private boolean processed;
    private Handler mainHandler;

    /**
     * SuggestManager
     */
    public SuggestManager(Context context){
        this.context = context;
        listRequestSuggest = new ArrayList<>();


        handlerThread = new HandlerThread("suggest_getter");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper(), handlerCallback);
        mainHandler = new Handler(mainThreadCallback);
        processed = false;
    }

    /**
     * setListener
     * @param listener
     */
    public void setListener(SuggestManagerListener listener){
        this.listener = listener;
    }

    /**
     * requestSuggest
     * @param keyword
     */
    public void requestSuggest(String keyword, Integer size){
        synchronized (lockObj){
            for (int ii = 0 ; ii < listRequestSuggest.size() ; ii++){
                listRequestSuggest.get(ii).disable();
            }
            listRequestSuggest.add(new Suggest(keyword, size));
        }

        threadHandler.sendEmptyMessageDelayed(0, delayTime);
    }
    public void requestClearSuggest(){
        synchronized (lockObj){
            for (int ii = 0 ; ii < listRequestSuggest.size() ; ii++){
                listRequestSuggest.get(ii).disable();
            }
            listRequestSuggest.add(new Suggest());
        }
        threadHandler.sendEmptyMessage(0);
    }

    Handler.Callback mainThreadCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Suggest suggest = (Suggest) msg.obj;
            if (!suggest.isDisable()) {
                AppLog.d("Suggest not cancel [" + suggest.counter + "]");
                listener.onResult(suggest.keyword, suggest.searchSuggest);
            }else{
                AppLog.d("Suggest canceled after communication[" + suggest.counter + "]");
            }

            return false;
        }
    };

    Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (processed){
                return false;
            }

            while (true){
                processed = true;
                Suggest suggest;

                synchronized (lockObj){
                    if (listRequestSuggest.size() == 0){
                        break;
                    }
                    suggest = listRequestSuggest.get(0);
                    if (listRequestSuggest.remove(suggest)) {
                        AppLog.d("Suggest removed success [" + suggest.counter + "]");
                    }
                }

                if (suggest.isDisable() || listener == null){
                    AppLog.d("Suggest canceled before communication[" + suggest.counter + "]");
                }else {
                    if (suggest.get()) {
                        Message m = mainHandler.obtainMessage();
                        m.obj = suggest;
                        m.sendToTarget();
                    }
                }
            }
            processed = false;

            return false;
        }
    };

    /**
     * Suggest
     */
    private class Suggest {

        // Context mContext;
        SearchSuggest searchSuggest;
        String keyword;
        Integer size;
        int counter;
        boolean clear_mode;

        private boolean disable = false;

        private Suggest(String keyword, Integer size) {
            this.keyword = keyword;
            this.size = size;
            this.counter = SuggestManager.counter;
            SuggestManager.counter++;
            clear_mode = false;
            //AppLog.d("Suggest new [" + this.counter + "]");
        }

        private Suggest(){
            clear_mode = true;
        }

        /**
         * disable
         */
        public void disable(){
            this.disable = true;
            //AppLog.d("Suggest disabled [" + this.counter + "]");
        }

        /**
         * isDisable
         * @return boolean
         */
        public boolean isDisable(){
            return this.disable;
        }


        /**
         * get
         */
        public boolean get(){

            //AppLog.d("Suggest get [" + this.counter + "]");

            if (clear_mode){
                searchSuggest = new SearchSuggest();
                return true;
            }

            HttpURLConnection urlCon = null;
            InputStream in = null;

            try {
                String result;

                URL url = new URL(ApiBuilder.createSearchSuggest(keyword, size));
                AppLog.d("URL=" + url.toString());
                if (url.toString().startsWith("https://")) {
                    urlCon = (HttpsURLConnection) url.openConnection();
                }else{
                    urlCon = (HttpURLConnection) url.openConnection();
                }
                urlCon.setRequestMethod("GET");
                urlCon.setInstanceFollowRedirects(false);
                urlCon.setConnectTimeout(AppConst.ConnectTimeout);
                urlCon.setReadTimeout(AppConst.ConnectTimeout);
                urlCon.connect();
                int responseCode = urlCon.getResponseCode();
                //AppLog.v("ResponseCode = " + responseCode);

            	if (NetworkInterface.STATUS_OK == responseCode) {

	                try {
	                    in = urlCon.getInputStream();
	                }catch (Exception e){
	                    in = urlCon.getErrorStream();
	                }

	                BufferedReader br = new BufferedReader(new InputStreamReader(in));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line);
	                }
	                br.close();
	                result = sb.toString();
	                AppLog.d("Result=" + result);

	                searchSuggest = new SearchSuggest();
	                if (!searchSuggest.setData(result)) {

		                searchSuggest = null;
					}

				} else {

	                searchSuggest = null;
				}


            } catch (Exception e) {
                AppLog.e(e.toString());
                searchSuggest = null;
            }finally {
                try {
                    if (urlCon != null) {
                        urlCon.disconnect();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ioe ) {
                    ioe.printStackTrace();
                }
            }

            //AppLog.d("Suggest get result[" + (searchSuggest != null) + "]");
            return (searchSuggest != null);
        }
    }

}

