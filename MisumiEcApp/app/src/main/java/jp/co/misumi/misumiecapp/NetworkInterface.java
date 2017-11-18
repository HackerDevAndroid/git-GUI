package jp.co.misumi.misumiecapp;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



/**
 * ネットワークアクセス
 */
public class NetworkInterface {


    // ------------
    public static final int INFORMATIONAL = 100;
    public static final int STATUS_OK = 200;
    public static final int REDIRECTION = 300;
    public static final int CLIENT_ERROR = 400;
    public static final int SESSION_ERROR = 401;	//2015/11/24 403から 401に変更
    public static final int VALIDATION_ERROR = 418;
    public static final int UNFIT_ERROR = 422;
    public static final int SERVER_ERROR = 500;
    public static final int NETWORK_ERROR = -1;
    public static final int UNKNOWN_ERROR = -2;
    public static final int TIMEOUT_ERROR = -3;

    // ------------

    HandlerThread mNetworkThread;
    Handler mNetworkHandler;
    Handler mMainHandler;

    Context mContext;

    private static NetworkInterface _instance = null;

//    private final int POST_REQUEST = 1;
//    private final int GET_REQUEST = 2;
    private final int RESULT = 3;

    private final int REQUEST = 1;

    private final int POST_METHOD = 1;
    private final int GET_METHOD = 2;

    private boolean mQueueReading = false;


    private BlockingQueue<RequestParam> mRequestQueue = new LinkedBlockingDeque<>();


    private class RequestParam{
        Long requestId;
        int method;
        String url;
        String body;
        String content_type;
        NetworkInterfaceListener listener;
        public RequestParam(Long id, int method1, String url1, String body1, String content1, NetworkInterfaceListener p2){
            requestId = id;
            method = method1;
            url = url1;
            body = body1;
            content_type = content1;
            listener = p2;
        }
    }

    private class ResponseParam{
        Long requestId;
        String body;
        int responsecode;
        NetworkInterfaceListener listener;
        public ResponseParam(Long id, String p1, int p2,NetworkInterfaceListener p3){
            requestId = id;
            body = p1;
            responsecode = p2;
            listener = p3;
        }
    }

    private NetworkInterface(Context context){
        mNetworkThread = new HandlerThread("network");
        mNetworkThread.start();
        mNetworkHandler = new Handler(mNetworkThread.getLooper(),mNetworkThreadCallback);
//        mMainHandler = new Handler(context.getMainLooper(),mMainThreadCallback);
        mMainHandler = new Handler(mMainThreadCallback);
        mContext = context;
    }

    public static NetworkInterface createInstance(Context context){
        if (_instance == null){
            _instance = new NetworkInterface(context);
        }
        return _instance;
    }

    public static NetworkInterface getInstance(){
        return _instance;
    }


    Handler.Callback mNetworkThreadCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return NetworkInterface.this.handleMessage(msg);
        }
    };

    Handler.Callback mMainThreadCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return NetworkInterface.this.handleMessage(msg);
        }
    };

    /**
     * handleMessage
     * @param msg
     * @return
     */
    public boolean handleMessage(Message msg) {

        switch (msg.what){
            case REQUEST:
                if (!mQueueReading) {
                    next();
                }
                break;
            case RESULT:
                result(msg);
                Message m = mNetworkHandler.obtainMessage();
                m.what = REQUEST;
                m.sendToTarget();
                break;
        }
        return false;
    }


    /**
     * next
     */
    private void next(){
        RequestParam param;
        synchronized (mRequestQueue) {
            param = mRequestQueue.poll();
        }
        if (param == null){
            mQueueReading = false;
            return;
        }
        if (param.method == POST_METHOD){
            post(param);
        }else{
            get(param);
        }
    }


    private HttpURLConnection createHttpURLConnection(URL url) throws Exception {

        HttpURLConnection urlCon; // httpのコネクションを管理するクラス

		if (url.toString().startsWith("https://")) {

			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(
			        null,
			        new TrustManager[]{new AppTrustManager()},
			        new SecureRandom()
			);

			HttpsURLConnection.setDefaultSSLSocketFactory(
			        sslcontext.getSocketFactory()
			);

		    urlCon = (HttpsURLConnection) url.openConnection();
		}else{
		    urlCon = (HttpURLConnection) url.openConnection();
		}

		return urlCon;
	}

    /**
     * post
     * @param param
     */
    private void post(RequestParam param) {

        int responsecode = NETWORK_ERROR;
        HttpURLConnection urlCon = null; // httpのコネクションを管理するクラス
        InputStream in = null; // URL連携した戻り値を取得して保持する用
        String result = "";
        try {
            // httpコネクションを確立し、urlを叩いて情報を取得
            String u = param.url;
            URL url = new URL(u);

            urlCon = createHttpURLConnection(url);

            urlCon.setRequestMethod("POST");
            urlCon.setDoOutput(true);
            urlCon.setConnectTimeout(AppConst.ConnectTimeout);
            urlCon.setReadTimeout(AppConst.ConnectTimeout);
            if (param.content_type != null) {
                urlCon.setRequestProperty("Content-Type", param.content_type);
            }

            // POSTパラメータ
            String postDataSample = param.body;
//            AppLog.d("post data=" + postDataSample);

            // POSTパラメータを設定（方法２）PrintStreamを利用
            PrintStream ps = new PrintStream(urlCon.getOutputStream());
            ps.print(postDataSample);
            ps.close();
            Map<String, List<String>> headers = urlCon.getHeaderFields();   //この一文入れると401を一部の端末で補足できる
            responsecode = urlCon.getResponseCode();
            AppLog.v("ResponseCode = " + responsecode);

            // データを取得
            try {
                in = urlCon.getInputStream();
            }catch (Exception e){
                in = urlCon.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // InputStreamからbyteデータを取得するための変数
            StringBuilder bufStr = new StringBuilder();
            String temp;

            // InputStreamからのデータを文字列として取得する
            while ((temp = br.readLine()) != null) {
                bufStr.append(temp);
            }

//            AppLog.d(bufStr.toString());
            result = bufStr.toString();
            AppLog.d("result = " + result);
            AppLog.file(result);


        }catch (java.net.SocketTimeoutException te){
            responsecode = TIMEOUT_ERROR;
            AppLog.e(te.toString());
        } catch (ConnectException  | UnknownHostException ce) {
            AppLog.e(ce.toString());
            responsecode = NETWORK_ERROR;
        } catch (Exception e) {
            AppLog.e(e.toString());
            if (e.toString().matches(".*authentication.*")){
                responsecode = SESSION_ERROR;
            }else{
                responsecode = UNKNOWN_ERROR;
            }
        } finally {
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

            Message result_msg = mMainHandler.obtainMessage();
            result_msg.what = RESULT;
            result_msg.obj = new ResponseParam(param.requestId, result, responsecode, param.listener);
            result_msg.sendToTarget();
        }
    }


    /**
     * get
     * @param param
     */
    private void get(RequestParam param){

        HttpURLConnection urlCon = null;
        URL url;
        int responsecode = NETWORK_ERROR;
        String result = "";
        InputStream in = null;

        try {
            url = new URL(param.url);
            urlCon = createHttpURLConnection(url);

            urlCon.setRequestMethod("GET");
            urlCon.setInstanceFollowRedirects(false);
            urlCon.setConnectTimeout(AppConst.ConnectTimeout);
            urlCon.setReadTimeout(AppConst.ConnectTimeout);
            urlCon.connect();
            Map<String, List<String>> headers = urlCon.getHeaderFields();   //この一文入れると401を一部の端末で補足できる
            responsecode = urlCon.getResponseCode();
            AppLog.v("ResponseCode = " + responsecode);

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

            AppLog.d("result = " + result);
            AppLog.file(result);
//            AppLog.d(result);

        }catch (java.net.SocketTimeoutException te){
            AppLog.e(te.toString());
            responsecode = TIMEOUT_ERROR;
        } catch (ConnectException | UnknownHostException ce) {
            AppLog.e(ce.toString());
            responsecode = NETWORK_ERROR;
        } catch (Exception e) {
            AppLog.e(e.toString());
            if (e.toString().matches(".*authentication.*")){
                responsecode = SESSION_ERROR;
            }else{
                responsecode = UNKNOWN_ERROR;
            }
        }finally {
            if (urlCon != null) {
                urlCon.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Message result_msg = mMainHandler.obtainMessage();
            result_msg.what = RESULT;
            result_msg.obj = new ResponseParam(param.requestId, result, responsecode, param.listener);
            result_msg.sendToTarget();
        }

    }


    /**
     * result
     * @param msg
     */
    private void result(Message msg){
        ResponseParam param = (ResponseParam)msg.obj;
        if (param.listener != null){
            int responsecode = param.responsecode;
            param.listener.onResult(param.requestId, responsecode, param.body);
        }
    }


    public interface NetworkInterfaceListener{
        void onResult(Long requestId, int responseCode, String result);
    }

    /**
     * postRequest
     * @param param
     * @param listener
     */
    public void postRequest(Long requestId, HashMap<String, String> param, NetworkInterfaceListener listener){
        logData(false, param);
        synchronized (mRequestQueue) {
            mRequestQueue.offer(new RequestParam(requestId, POST_METHOD, param.get("url"), param.get("body"), param.get("content-type"), listener));
        }

        Message msg = mNetworkHandler.obtainMessage();
        msg.what = REQUEST;
//        msg.obj = new RequestParam(POST_METHOD, param.get("url"), param.get("body"), param.get("content-type"), listener);
        msg.sendToTarget();
    }

    /**
     * getRequest
     * @param param
     * @param listener
     */
    public void getRequest(Long requestId, HashMap<String, String> param, NetworkInterfaceListener listener){
        logData(true, param);
        synchronized (mRequestQueue) {
            mRequestQueue.offer(new RequestParam(requestId, GET_METHOD, param.get("url"), param.get("body"), param.get("content-type"), listener));
        }

        Message msg = mNetworkHandler.obtainMessage();
        msg.what = REQUEST;
//        msg.obj = new RequestParam(GET_METHOD, param.get("url"), param.get("body"), param.get("content-type"), listener);
        msg.sendToTarget();
    }

    void logData(boolean method_get, HashMap<String, String> param){
        AppLog.d(" --- RequestData Log --- ");
        AppLog.d("METHOD=" + ((method_get) ? "GET":"POST"));
        AppLog.d("url=" + param.get("url"));
        AppLog.d("content-type=" + param.get("content-type"));
        AppLog.d("body=" + param.get("body"));
        AppLog.d(" ------ ");
    }


	//TODO:オレオレ証明書でエラーが出ない様に対応、リリース時には本物の証明書なので削除する
    private static class AppTrustManager implements X509TrustManager {

        private final String trustApiBase64 =
             "KStRAUEuhZRqWOBCkNkef+/kPM46yTp8Ezg8EyNd/vE=\n";

        private final String trustNasBase64 =
             "QBsorfi+XtW7MjDcmcAeBbnnk3Kk+hfcmelBuWqSjWk=\n";

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            X509Certificate cert = chain[0];
            checkTrustCert(cert);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }

        private void checkTrustCert(X509Certificate cert) throws CertificateException {

//			AppLog.e("=== checkTrustCert: "+ cert);

            String base64 = toBase64(cert.getPublicKey().getEncoded());
			AppLog.d("base64: "+ base64);
			AppLog.d("trust : "+ trustApiBase64);

            if (trustApiBase64.equals(base64)) {
				return;
            }

            if (trustNasBase64.equals(base64)) {
				return;
            }

            AppLog.e("!trust.equals");
            //throw new CertificateException("cert doesn`t match " + base64);
        }

        // X509Certificate.getPublicKey().getEncoded()
        private String toBase64(byte[] b) {
            String base64 = null;

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(b);

                byte[] sha256byte = md.digest();
                base64 = Base64.encodeToString(sha256byte, Base64.DEFAULT);
            } catch (NoSuchAlgorithmException e) {

	            AppLog.e(e);
            }

            return base64;
        }
    }


}
