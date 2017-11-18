package jp.co.misumi.misumiecapp.observer;

/**
 * Created by kawanobe on 15/07/31.
 */
public interface NetworkObserver {
    void notice(int responseCode, String result);
}
