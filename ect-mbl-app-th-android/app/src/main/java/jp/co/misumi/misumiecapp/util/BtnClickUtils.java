//-- ADD NT-SLJ 16/11/12 Alipay payment FR -
package jp.co.misumi.misumiecapp.util;

/**
 * 按钮重复点击
 */
public class BtnClickUtils {
    private BtnClickUtils() {

    }

    public synchronized static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }

        mLastClickTime = time;

        return false;
    }

    /**
     * 重复点击判断
     *
     * @param waitTime
     * @return true表示重复点击
     */
    public synchronized static boolean isFastDoubleClick(long waitTime) {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < waitTime) {
            return true;
        }

        mLastClickTime = time;

        return false;
    }

    private static long mLastClickTime = 0;
}
//-- ADD NT-SLJ 16/11/12 Alipay payment TO -