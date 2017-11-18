package jp.co.misumi.misumiecapp;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.data.Login;
import jp.co.misumi.misumiecapp.observer.AppNotifier;

public class LoginState {

    public void login(Login login, String user, String pass, boolean checked, boolean isnew) throws Exception {
        HashMap<String, Object> param = new HashMap<>();
        param.put(AppConfig.KEY_LOGIN_ID, user);
        param.put(AppConfig.KEY_LOGIN_PASS, pass);
        param.put(AppConfig.KEY_ENABLE_IDPASS, checked);
        param.put(AppConfig.KEY_SESSION_ID, login.sessionId);
        param.put(AppConfig.KEY_USRNAME, login.userName);
        param.put(AppConfig.KEY_USRCODE, login.userCode);
        param.put(AppConfig.KEY_CUSTOMER_NAME, login.customerName);
        param.put(AppConfig.KEY_CUSTOMER_CODE, login.customerCode);
        param.put(AppConfig.KEY_CURRENCY_CODE, login.currencyCode);
        param.put(AppConfig.KEY_PAYMENT_TYPE, login.paymentType);
        param.put(AppConfig.KEY_SETTLEMENT_TYPE, login.settlementType);
        param.put(AppConfig.KEY_QUOTAION_UNFIT_COUNT, login.quotationUnfitCount);
        param.put(AppConfig.KEY_ORDER_UNFIT_COUNT, login.orderUnfitCount);
        param.put(AppConfig.KEY_PERMISSION_LIST, login.permissionList);
        param.put(AppConfig.KEY_IMMEDIATEDELIVERY_FLAG,login.immediateDeliveryFlag);

        AppConfig.getInstance().setConfigData(param);
        if (isnew){
            AppNotifier.getInstance().setNewLogin();
        }else {
            AppNotifier.getInstance().setLogin(true);
        }
    }

    public void logout(){
        AppConfig config = AppConfig.getInstance();
        config.setSessionId("");
        config.setCustomerCode("");
        config.setCustomerName("");
        config.setUserCode("");
        config.setUserName("");

        if (!config.getEnableIDandPassward()) {
            config.setLoginId("");
        }
        config.setLoginPassword("");
        config.setCurrencyCode("");
        config.setPaymentType("");
        config.setSettlementType("");
        config.setQuotationUnfitCount(-1);
        config.setOrderUnfitCount(-1);
        config.setPermissionList(null);

        //--ADD NT-SLJ 17/07/14 3小时必达 FR -
        config.setImmediateDeliveryFlag("");
        //--ADD NT-SLJ 17/07/14 3小时必达 TO -

        AppNotifier.getInstance().setLogin(false);
    }

    public void sessionLost(){
        AppConfig config = AppConfig.getInstance();
        config.setSessionId("");
        config.setCustomerCode("");
        config.setCustomerName("");
        config.setUserCode("");
        config.setUserName("");
        config.setCurrencyCode("");
        config.setPaymentType("");
        config.setSettlementType("");
        config.setQuotationUnfitCount(-1);
        config.setOrderUnfitCount(-1);
        config.setPermissionList(null);
        //--ADD NT-SLJ 17/07/14 3小时必达 FR -
        config.setImmediateDeliveryFlag("");
        //--ADD NT-SLJ 17/07/14 3小时必达 TO -
    }

}
