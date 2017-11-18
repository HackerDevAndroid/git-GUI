package jp.co.misumi.misumiecapp.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.NetworkInterface;

/**
 * ApiAccessObserver
 */
public class ApiAccessObserver implements NetworkInterface.NetworkInterfaceListener {

    public static final boolean API_POST = true;
    public static final boolean API_GET = false;

    private List observers = new ArrayList();

    private static ApiAccessObserver instance = null;

    private Long requestIdWork = 0L;
    private final Object object = new Object();

    /**
     * ObserverManageData
     */
    private class ObserverManageData {
        Long requestId;
        NetworkObserver observer;

        ObserverManageData(NetworkObserver observer) {
            this.observer = observer;
            if (requestIdWork == Long.MAX_VALUE) {
                requestIdWork = 0L;
            }
            requestIdWork++;
            this.requestId = requestIdWork;
        }
    }

    /**
     * ApiAccessObserver
     */
    private ApiAccessObserver() {
    }

    /**
     * createInstance
     */
    public static void createInstance() {
        instance = new ApiAccessObserver();
    }

    /**
     * getInstance
     *
     * @return
     */
    public static ApiAccessObserver getInstance() {
        return instance;
    }

    /**
     * removeObserver
     *
     * @param o
     */
    public void removeObserver(NetworkObserver o) {
        synchronized (object) {
            ObserverManageData observer = getObserverManageDataNoLock(o);
            if (observer != null) {
                if (observers.remove(observer)) {
                    AppLog.v("remove observer " + o.toString());
                } else {
                    AppLog.v("observer is non? " + o.toString());
                }
            }
        }
    }

    /**
     * requestApi
     *
     * @param post
     * @param param
     * @param observer
     */
    public void requestApi(boolean post, HashMap<String, String> param, NetworkObserver observer) {

        assert observer != null;

        synchronized (object) {

            ObserverManageData o = getObserverManageDataNoLock(observer);
            if (o == null) {
                o = new ObserverManageData(observer);
                observers.add(o);
            }

            if (post) {
                NetworkInterface.getInstance().postRequest(o.requestId, param, this);
            } else {
                NetworkInterface.getInstance().getRequest(o.requestId, param, this);
            }
        }
    }

    /**
     * onResult
     *
     * @param requestId
     * @param responseCode
     * @param result
     */
    @Override
    public void onResult(Long requestId, int responseCode, String result) {

        synchronized (object) {
            ObserverManageData o = getObserverManageDataNoLock(requestId);
            if (o != null) {
                o.observer.notice(responseCode, result);
            }
        }
    }

    /**
     * getObserverManageData
     *
     * @param observer
     * @return
     */
    private ObserverManageData getObserverManageDataNoLock(NetworkObserver observer) {
        for (int ii = 0; ii < observers.size(); ii++) {
            ObserverManageData o = (ObserverManageData) observers.get(ii);
            if (o.observer.equals(observer)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @param requestId
     * @return
     */
    private ObserverManageData getObserverManageDataNoLock(Long requestId) {
        for (int ii = 0; ii < observers.size(); ii++) {
            ObserverManageData o = (ObserverManageData) observers.get(ii);
            if (o.requestId.equals(requestId)) {
                return o;
            }
        }
        return null;
    }
}

