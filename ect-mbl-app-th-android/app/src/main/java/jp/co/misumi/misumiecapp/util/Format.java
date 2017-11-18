package jp.co.misumi.misumiecapp.util;

import android.text.SpannableStringBuilder;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;

/**
 * Created by kawanobe on 15/06/26.
 */
public class Format {

    public static final String OKptn = "^[\\x20-\\x7E]+$";
    public static final String NUMptn = "^[0-9]+$";

    /**
     * @param value
     * @return
     */
    public static String formatAmount(Double value) {

        //コンパイル時のオプションで金額フォーマットを切り替える
        //BuildConfig.subsidiaryCode
        if (SubsidiaryCode.isJapan()) {
            //日本の時は無条件で整数のみ"MJP"
            return String.format("%,.0f", value);
        }

        //それ以外は小数２桁
        return String.format("%,.2f", value);
    }

    /**
     * @param value
     * @return
     */
    public static CharSequence formatAmountWithUnit(Double value, String addString) {

        return formatAmountWithUnit(value, false, true, true, addString);
    }


    public static CharSequence formatAmountWithUnitNoRed(Double value, String addString) {

        return formatAmountWithUnit(value, false, false, true, addString);
    }


    /**
     * @param value
     * @return
     */
    public static CharSequence formatAmountWithUnit(Double value, boolean nosize, boolean red, boolean bold, String addString) {

        AppConfig config = AppConfig.getInstance();

        String valueResult = formatAmount(value);

        float valueSize = nosize ? 1.0f : 1.3f;

        SpannableStringBuilder sb = new SpannableStringBuilder();
        String cur = AppConfig.getInstance().getCurrencyCode();
        switch (cur) {
            case AppConst.CURRENCY_CODE_JPY:
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                break;
            case AppConst.CURRENCY_CODE_RMB:
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                break;
            case AppConst.CURRENCY_CODE_USD:
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                break;
        }
        if (addString != null) {
            sb.append(SpannableUtil.newSpannableString(addString, 1.0f, false, false));
        }


        return sb;
    }

    public static CharSequence formatAmountWithUnit(Double value, float valueSize, boolean red, boolean bold, String addString) {

        AppConfig config = AppConfig.getInstance();

        String valueResult = formatAmount(value);

//        float valueSize = nosize ? 1.0f:1.3f;

        SpannableStringBuilder sb = new SpannableStringBuilder();
        String cur = AppConfig.getInstance().getCurrencyCode();
        switch (cur) {
            case AppConst.CURRENCY_CODE_JPY:
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                break;
            case AppConst.CURRENCY_CODE_RMB:
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                break;
            case AppConst.CURRENCY_CODE_USD:
                sb.append(SpannableUtil.newSpannableString(config.getCurrencyString(cur), 1.0f, false, false));
                sb.append(SpannableUtil.newSpannableString(valueResult, valueSize, red, bold));
                break;
        }
        if (addString != null) {
            sb.append(SpannableUtil.newSpannableString(addString, 1.0f, false, false));
        }


        return sb;
    }


    /**
     * getHyphenWithUnit
     *
     * @return String
     */
    public static String getHyphenWithUnit() {
        String cur = AppConfig.getInstance().getCurrencyCode();
        String unit = AppConfig.getInstance().getCurrencyString(cur);
        switch (cur) {
            case AppConst.CURRENCY_CODE_JPY:
            case AppConst.CURRENCY_CODE_RMB:
                return "-" + unit;
            case AppConst.CURRENCY_CODE_USD:
                return unit + "-";
        }

        return null;
    }

    /**
     * @param value
     * @return
     */
    public static String formatWithSign(Double value) {

        //コンパイル時のオプションで金額フォーマットを切り替える
        //BuildConfig.subsidiaryCode
        if (SubsidiaryCode.isJapan()) {
            //日本の時は無条件で整数のみ"MJP"
            if (value == 0.0) {
                return String.format("%,.0f", value);
            } else {
                return String.format("%+,.0f", value);
            }
        }

        //それ以外は小数２桁
        if (value == 0.0) {
            return String.format("%,.2f", value);
        } else {
            return String.format("%+,.2f", value);
        }
    }

    /**
     * @param value
     * @return
     */
    public static String formatCount(Integer value) {
        //３桁区切りの整数
        //String result = String.format("%,3d", value).trim();
        return String.valueOf(value);
    }

    public static boolean isAsciiFormat(String value) {
        return value.matches(OKptn);
    }

    public static boolean isNumberFormat(String value) {
        return value.matches(NUMptn);
    }

}

