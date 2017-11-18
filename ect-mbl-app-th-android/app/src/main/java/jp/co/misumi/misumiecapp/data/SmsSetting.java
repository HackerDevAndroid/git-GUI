package jp.co.misumi.misumiecapp.data;

/**
 * Created by BaoVT on 11/6/2017.
 */

public class SmsSetting {
    public static final int KEY_SEND_SMS_STATUS_CHANGE = 0;
    public static final int KEY_SETTING_TIME_CHANGE = 1;
    public static final int KEY_OTHER_CHANGE = 1;

    private boolean isSendSms;
    private String phoneNumber;
    private boolean isFinishOrder;
    private boolean isFinishExport;
    private boolean isFinishChange;
    private boolean isSettingTime;
    private int timeRange;

    SmsValueListener listener;

    public SmsSetting() {
    }

    public SmsSetting(boolean isSendSms, String phoneNumber, boolean isFinishOrder, boolean
            isFinishExport, boolean isFinishChange, boolean isSettingTime, int timeRange) {
        this.isSendSms = isSendSms;
        this.phoneNumber = phoneNumber;
        this.isFinishOrder = isFinishOrder;
        this.isFinishExport = isFinishExport;
        this.isFinishChange = isFinishChange;
        this.isSettingTime = isSettingTime;
        this.timeRange = timeRange;
    }

    public void setValueChangeListener(SmsValueListener listener) {
        this.listener = listener;
    }

    public boolean isSendSms() {
        return isSendSms;
    }

    public void setSendSms(boolean sendSms) {
        isSendSms = sendSms;
        listener.onValueChange(KEY_SEND_SMS_STATUS_CHANGE);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        listener.onValueChange(KEY_OTHER_CHANGE);
    }

    public boolean isFinishOrder() {
        return isFinishOrder;
    }

    public void setFinishOrder(boolean finishOrder) {
        isFinishOrder = finishOrder;
        listener.onValueChange(KEY_OTHER_CHANGE);
    }

    public boolean isFinishExport() {
        return isFinishExport;
    }

    public void setFinishExport(boolean finishExport) {
        isFinishExport = finishExport;
        listener.onValueChange(KEY_OTHER_CHANGE);
    }

    public boolean isFinishChange() {
        return isFinishChange;
    }

    public void setFinishChange(boolean finishChange) {
        isFinishChange = finishChange;
        listener.onValueChange(KEY_OTHER_CHANGE);
    }

    public boolean isSettingTime() {
        return isSettingTime;
    }

    public void setSettingTime(boolean settingTime) {
        isSettingTime = settingTime;
        listener.onValueChange(KEY_SETTING_TIME_CHANGE);
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
        listener.onValueChange(KEY_OTHER_CHANGE);
    }

    public static boolean isEquals(SmsSetting oldValue, SmsSetting newValue) {
        return oldValue.isSendSms() == newValue.isSendSms()
                && oldValue.getPhoneNumber().equals(newValue.getPhoneNumber())
                && oldValue.isFinishOrder() == newValue.isFinishOrder()
                && oldValue.isFinishExport() == newValue.isFinishExport()
                && oldValue.isFinishChange() == newValue.isFinishChange()
                && oldValue.isSettingTime() == newValue.isSettingTime()
                && oldValue.getTimeRange() == newValue.getTimeRange();
    }

    public static void resetValue(SmsSetting value) {
        value.setPhoneNumber("");
        value.setSendSms(false);
        value.setFinishOrder(false);
        value.setFinishExport(false);
        value.setFinishChange(false);
        value.setSettingTime(false);
        value.setTimeRange(-1);
    }

    public interface SmsValueListener {
        void onValueChange(int key);
    }
}
