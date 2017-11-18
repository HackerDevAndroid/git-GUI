package jp.co.misumi.misumiecapp.util;

import jp.co.misumi.misumiecapp.AppConst;


public class SubsidiaryCode {

    public static boolean isJapan() {
        return AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_MJP);
    }

    public static boolean isChinese() {
        return AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_CHN);
    }

    public static boolean isThailand() {
        return AppConst.subsidiaryCode.equals(AppConst.SUBSIDIARY_CODE_CHN);
    }
}

