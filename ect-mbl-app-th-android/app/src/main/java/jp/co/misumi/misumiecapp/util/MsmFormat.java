package jp.co.misumi.misumiecapp.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.R;


/**
 * Created by sakamoto on 15/10/13.
 */
public class MsmFormat {


    private static String getResourceString(Context context, int id) {

        if (id == 0) {
            return null;
        }

        return context.getString(id);
    }


    public static String convertShipDateTime(Context context, String shipDateTime) {//出荷日

        String date;

        if (shipDateTime == null) {
            date = getResourceString(context, R.string.label_hyphen);
        } else {
            String[] result = shipDateTime.split(" ");
            date = result[0];
        }
        return date;
    }


    public static String convertExpressType(Context context, String expressType, boolean dialog) {//緊急出荷サービス

        int resId;

        if (expressType == null) expressType = "";

        switch (expressType) {
            case "T0":
                resId = R.string.label_stoke_t0;
                break;
            case "A0":
                resId = R.string.label_stoke_a0;
                break;
            case "B0":
                resId = R.string.label_stoke_b0;
                break;
            case "C0":
                resId = R.string.label_stoke_c0;
                break;
            case "0Z":
                resId = R.string.label_stoke_0z;
                break;
            case "0L":
                resId = R.string.label_stoke_0l;
                break;
//			case "0V":
//				resId = R.string.label_stoke_0v;
//				break;
            case "0A":
                resId = R.string.label_stoke_0a;
                break;
            //-- ADD NT-SLJ 17/07/18 3小时闪达 FR –
            case "V0":
                resId = R.string.label_stoke_v0;
                break;
            //-- ADD NT-SLJ 17/07/18 3小时闪达 TO
            default:
                if (dialog) {
                    resId = R.string.label_stoke_etc;
                } else {
                    resId = R.string.label_stoke_etc2;
                }
                break;
        }
        return getResourceString(context, resId);
    }


    public static String convertExpressTypeFull(Context context, String expressType) {//緊急出荷サービス

/*
SP10_API外部設計書(価格チェック)_20151106.xlsx API設計書より
"ストーク
  ""T0"": Tストーク
  ""A0"": Aストーク
  ""B0"": Bストーク
  ""C0"": Cストーク
  ""0Z"": Zストーク メモ："0Z"が正しい。（API設計書の記載はバグ）
  ""0A"": 早割Aストーク"
*/

        int resId;

        if (expressType == null) expressType = "";

        switch (expressType) {
            case "T0":
                resId = R.string.label_stoke_t;
                break;
            case "A0":
                resId = R.string.label_stoke_a;
                break;
            case "B0":
                resId = R.string.label_stoke_b;
                break;
            case "C0":
                resId = R.string.label_stoke_c;
                break;
            case "0Z":
                resId = R.string.label_stoke_z;
                break;
//			case "0L":
//				resId = R.string.label_stoke_l;
//				break;
            case "0A":
                resId = R.string.label_stoke_a_rapid;
                break;
            //-- ADD NT-SLJ 17/07/18 3小时闪达 FR –
            case "V0":
                resId = R.string.label_stoke_v;
                break;
            //-- ADD NT-SLJ 17/07/18 3小时闪达 TO
            default:
                //その他はハイフン
                resId = R.string.label_hyphen;
                break;
        }
        return getResourceString(context, resId);
    }


    /*
    共通
    ・1: 処理中
    ・3: 注文済
    ・4: 出荷済
    ・z: ミスミ確認中

    見積履歴詳細のみ
    ・2: 見積済
    ・f: 失敗
    ・c: 有効期限切れ
    ・e: エラー

    注文履歴詳細のみ
    ・x: キャンセル済
    ・w: 入金待ち
    */
    public static String convertStatusQuote(Context context, String status) {//status

        int resId = R.string.common_status_unknown;

        //共通
        if ("1".equals(status)) {
            resId = R.string.common_status_1;
        } else if ("3".equals(status)) {
            resId = R.string.common_status_3;
        } else if ("4".equals(status)) {
            resId = R.string.common_status_4;
        } else if ("z".equals(status)) {
            resId = R.string.common_status_z;
        } else

            //見積履歴詳細のみ
            if ("2".equals(status)) {
                resId = R.string.common_status_2;
            } else if ("f".equals(status)) {
                resId = R.string.common_status_f;
            } else if ("c".equals(status)) {
                resId = R.string.common_status_c;
            } else if ("e".equals(status)) {
                resId = R.string.common_status_e;
            }

        return getResourceString(context, resId);
    }


    public static String convertStatusOrder(Context context, String status) {//status

        int resId = R.string.common_status_unknown;

        //共通
        if ("1".equals(status)) {
            resId = R.string.common_status_1;
        } else if ("3".equals(status)) {
            resId = R.string.common_status_3;
        } else if ("4".equals(status)) {
            resId = R.string.common_status_4;
        } else if ("z".equals(status)) {
            resId = R.string.common_status_z;
        } else
            //注文履歴詳細のみ
            if ("x".equals(status)) {
                resId = R.string.common_status_x;
            } else if ("w".equals(status)) {
                resId = R.string.common_status_w;
            } else if ("a1".equals(status)) {
                resId = R.string.common_status_a1;
            } else if ("a2".equals(status)) {
                resId = R.string.common_status_a2;
            } else if ("a3".equals(status)) {
                resId = R.string.common_status_a3;
            }

        return getResourceString(context, resId);
    }


    public static String convertShip(Context context, Integer daysToShip, String shipType) {//daysToShip

        //出荷日数
        //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
        String str;
        String daytoship;
        String shiptype;
        String shiptype2 = "";
        if (daysToShip == null || (daysToShip > 0 && daysToShip < 99)) {

            //　フォーマット例：10日目(土曜含まず)
            if (daysToShip == null) {
                shiptype = "";
            } else {
                daytoship = daysToShip.toString();
                shiptype = getResourceString(context, R.string.label_day_to_ship_1ro98_1)
                        + daytoship + getResourceString(context, R.string.label_day_to_ship_1ro98_2);
            }

        } else if (daysToShip == 0) {
            // 当日出荷
            shiptype = getResourceString(context, R.string.label_day_to_ship_0);
        } else if (daysToShip == 99) {
            // 都度お見積り
            shiptype = getResourceString(context, R.string.label_day_to_ship_99);
        } else {
//            daytoship = getResourceString(context, R.string.label_hyphen);
/*
【21.見積内容確認】【Android】出荷日数が0～99以外で"-"表示になってしまう
*/
            daytoship = daysToShip.toString();
            shiptype = getResourceString(context, R.string.label_day_to_ship_1ro98_1)
                    + daytoship + getResourceString(context, R.string.label_day_to_ship_1ro98_2);
        }

        if (shipType != null) {
            //・1の場合：在庫品　　　・2の場合：在庫手配中
            //　・3の場合：土祝含まず　・4の場合：土曜含まず
            //　・5の場合：祝日含まず
            switch (shipType) {
                case "1":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_1);
                    break;
                case "2":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_2);
                    break;
                case "3":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_3);
                    break;
                case "4":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_4);
                    break;
                case "5":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_5);
                    break;
            }
        }


        if ((android.text.TextUtils.isEmpty(shiptype)) && (android.text.TextUtils.isEmpty(shiptype2))) {

            str = getResourceString(context, R.string.label_hyphen);
        } else {

            if (shiptype2.length() != 0) {
                shiptype2 += " ";
            }

            str = shiptype2 + shiptype;
        }

        return str;
    }


    public static String convertShip(Context context, Integer daysToShip, String shipType, boolean isQuote) {//daysToShip

        //出荷日数
        //出荷日数(daysToShip)＋(＋出荷区分(shipType)＋)
        String str;
        String daytoship;
        String shiptype;
        String shiptype2 = "";
        if (daysToShip == null || (daysToShip > 0 && daysToShip < 99)) {

            //　フォーマット例：10日目(土曜含まず)
            if (daysToShip == null) {
                shiptype = "";
            } else {
                daytoship = daysToShip.toString();

                int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
                int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

                shiptype = getResourceString(context, redId)
                        + daytoship + getResourceString(context, redId2);
            }

        } else if (daysToShip == 0) {
            // 当日出荷
            shiptype = getResourceString(context, R.string.label_day_to_ship_0);
        } else if (daysToShip == 99) {
            // 都度お見積り
            shiptype = getResourceString(context, R.string.label_day_to_ship_99);
        } else {
//            daytoship = getResourceString(context, R.string.label_hyphen);
/*
【21.見積内容確認】【Android】出荷日数が0～99以外で"-"表示になってしまう
*/
            daytoship = daysToShip.toString();

            int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
            int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

            shiptype = getResourceString(context, redId)
                    + daytoship + getResourceString(context, redId2);
        }

        if (shipType != null) {
            //・1の場合：在庫品　　　・2の場合：在庫手配中
            //　・3の場合：土祝含まず　・4の場合：土曜含まず
            //　・5の場合：祝日含まず
            switch (shipType) {
                case "1":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_1);
                    break;
                case "2":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_2);
                    break;
                case "3":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_3);
                    break;
                case "4":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_4);
                    break;
                case "5":
                    shiptype2 += getResourceString(context, R.string.label_shiptype_5);
                    break;
            }
        }


        if ((daysToShip == null) && (shipType == null)) {

            str = getResourceString(context, R.string.label_hyphen);
        } else {

            if (shiptype2.length() != 0) {
                shiptype2 += " ";
            }

            str = shiptype2 + shiptype;
        }

        return str;
    }


    public static String convertDaysToShip(Context context, Integer daysToShip) {//daysToShip

        //出荷日数
        String daysToShipStr = "";

        if (daysToShip == null) {

        } else if (daysToShip == 0) {

            // 当日出荷
            daysToShipStr = getResourceString(context, R.string.label_day_to_ship_0);
        } else if (daysToShip == 99) {

            // 都度お見積り
            daysToShipStr = getResourceString(context, R.string.label_day_to_ship_99);
        } else {

            //　フォーマット例：10日目(土曜含まず)
            daysToShipStr = daysToShip.toString();
        }

        return daysToShipStr;
    }


    public static String convertDaysToShipUnit(Context context, Integer daysToShip) {//daysToShip

        //出荷日数
        String daysToShipUnitStr = "";

        if (daysToShip == null) {

        } else if (daysToShip == 0) {

        } else if (daysToShip == 99) {

        } else {

            daysToShipUnitStr = getResourceString(context, R.string.label_day_to_ship_1ro98_1)
                    + daysToShip.toString() + getResourceString(context, R.string.label_day_to_ship_1ro98_2);
        }

        return daysToShipUnitStr;
    }

    public static String convertDaysToShipUnit2(Context context, Integer daysToShip) {//daysToShip

        return convertDaysToShipUnit2(context, daysToShip, false);
    }


    public static String convertDaysToShipUnit2(Context context, Integer daysToShip, boolean isQuote) {//daysToShip

        //出荷日数
        String daysToShipUnitStr = "";

        if (daysToShip == null) {

        } else if (daysToShip == 0) {

            // 当日出荷
            daysToShipUnitStr = getResourceString(context, R.string.label_day_to_ship_0);
        } else if (daysToShip == 99) {

            // 都度お見積り
            daysToShipUnitStr = getResourceString(context, R.string.label_day_to_ship_99);
        } else {

            int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
            int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

            daysToShipUnitStr = getResourceString(context, redId)
                    + daysToShip.toString() + getResourceString(context, redId2);
        }

        return daysToShipUnitStr;
    }


    public static String convertDaysToShipUnit2_00(Context context, Integer daysToShip, boolean isQuote) {//daysToShip

        //出荷日数
        String daysToShipUnitStr = "";

        if (daysToShip == null) {

        } else if (daysToShip == 99) {

            // 都度お見積り
            daysToShipUnitStr = getResourceString(context, R.string.label_day_to_ship_99);
        } else {

            int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
            int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

            daysToShipUnitStr = getResourceString(context, redId)
                    + daysToShip.toString() + getResourceString(context, redId2);
        }

        return daysToShipUnitStr;
    }


    public static String convertDaysToShipUnit3(Context context, Integer daysToShip, boolean isQuote) {//daysToShip

        //出荷日数
        String daysToShipUnitStr = "";

        if (daysToShip == null) {

        } else {

            int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
            int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

            //　フォーマット例：10日目
            daysToShipUnitStr = getResourceString(context, redId)
                    + daysToShip.toString() + getResourceString(context, redId2);

        }

        return daysToShipUnitStr;
    }

    public static String convertDaysToShipUnit3_99(Context context, Integer daysToShip, boolean isQuote) {//daysToShip

        //出荷日数
        String daysToShipUnitStr = "";

        if (daysToShip == null) {

        } else if (daysToShip == 99) {

            // 都度お見積り
            daysToShipUnitStr = getResourceString(context, R.string.label_day_to_ship_99);
        } else {

            int redId = (isQuote) ? R.string.label_day_to_ship_1ro98_1_quote : R.string.label_day_to_ship_1ro98_1;
            int redId2 = (isQuote) ? R.string.label_day_to_ship_1ro98_2_quote : R.string.label_day_to_ship_1ro98_2;

            //　フォーマット例：10日目
            daysToShipUnitStr = getResourceString(context, redId)
                    + daysToShip.toString() + getResourceString(context, redId2);

        }

        return daysToShipUnitStr;
    }


    public static CharSequence convertDaysToShipUnitWithSpaned(Context context, Integer daysToShip) {//daysToShip

        //出荷日数
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        SpannableString ss;

        if (daysToShip == null) {

        } else if (daysToShip == 0) {

//			ssb.append("（");
            // 当日出荷
            ssb.append(getResourceString(context, R.string.label_day_to_ship_0));
//			ssb.append("）");

        } else if (daysToShip == 99) {

//			ssb.append("（");
            // 都度お見積り
            ssb.append(getResourceString(context, R.string.label_day_to_ship_99));
//			ssb.append("）");

        } else {

            //　フォーマット例：10日目(土曜含まず)
//			ssb.append("（");
            ssb.append(getResourceString(context, R.string.label_day_to_ship_1ro98_1));
            ss = SpannableUtil.newSpannableString(daysToShip.toString(), 20, true, true);
            ssb.append(ss);
            //日目
            ssb.append(getResourceString(context, R.string.label_day_to_ship_1ro98_2));
//			ssb.append("）");

        }

        return ssb;
    }


    public static String convertShipType(Context context, String shipType) {//shipType

        String shipTypeStr = "";

        if (shipType != null) {
            //・1の場合：在庫品　　　・2の場合：在庫手配中
            //　・3の場合：土祝含まず　・4の場合：土曜含まず
            //　・5の場合：祝日含まず
            switch (shipType) {
                case "1":
                    shipTypeStr += getResourceString(context, R.string.label_shiptype_1);
                    break;
                case "2":
                    shipTypeStr += getResourceString(context, R.string.label_shiptype_2);
                    break;
                case "3":
                    shipTypeStr += getResourceString(context, R.string.label_shiptype_3);
                    break;
                case "4":
                    shipTypeStr += getResourceString(context, R.string.label_shiptype_4);
                    break;
                case "5":
                    shipTypeStr += getResourceString(context, R.string.label_shiptype_5);
                    break;
            }
        }

        return shipTypeStr;
    }


    public static String convertCampainEndDate(Context context, String campainEndDate) {//campainEndDate

        String campainEndDateStr = "";

        if (!android.text.TextUtils.isEmpty(campainEndDate)) {

            if (campainEndDate.matches("^\\d{4}/\\d{1,2}/\\d{1,2}")) {

                campainEndDateStr = String.format(getResourceString(context, R.string.common_sale_format), campainEndDate.substring(5));

            } else {

                campainEndDateStr = String.format(getResourceString(context, R.string.common_sale_format), campainEndDate);

            }
        }

        return campainEndDateStr;
    }


    //通貨関係
    public static boolean isUsa(Context context) {

        if (AppConfig.getInstance().hasSessionId()) {

            //言語設定
            String currencyCode = AppConfig.getInstance().getCurrencyCode();
            if (AppConst.CURRENCY_CODE_USD.equals(currencyCode)) {
                return true;
            }
        }

        return false;
    }

    //通貨記号（通常、後付、ただし米国は前付）
    public static String getCurrencyCode(Context context) {

        return getCurrencyCode(context, false);
    }

    public static String getCurrencyCodeWithLogin(Context context) {

        return getCurrencyCode(context, true);
    }

    private static String getCurrencyCode(Context context, boolean useLogin) {

        int resId = 0;

        if (useLogin) {

            //言語設定で単価表示形式を切り替える
            String currencyCode = AppConfig.getInstance().getCurrencyCode();
            if (AppConst.CURRENCY_CODE_JPY.equals(currencyCode)) {
                resId = R.string.label_currency_jpy;
            } else if (AppConst.CURRENCY_CODE_RMB.equals(currencyCode)) {
                resId = R.string.label_currency_rmb;
            } else if (AppConst.CURRENCY_CODE_USD.equals(currencyCode)) {
                resId = R.string.label_currency_usd;
            }
        }

        if (resId == 0) {

            if (SubsidiaryCode.isJapan()) {

                resId = R.string.label_currency_jpy;
            } else {

                resId = R.string.label_currency_rmb;
            }
        }

        return getResourceString(context, resId);
    }


    //通貨記号（前付）
    public static String getCurrencyCodePre(Context context) {

        return getCurrencyCodePre(context, false);
    }

    public static String getCurrencyCodePreWithLogin(Context context) {

        return getCurrencyCodePre(context, true);
    }

    private static String getCurrencyCodePre(Context context, boolean useLogin) {

        int resId = 0;

        if (useLogin) {

            //言語設定で単価表示形式を切り替える
            String currencyCode = AppConfig.getInstance().getCurrencyCode();
            if (AppConst.CURRENCY_CODE_JPY.equals(currencyCode)) {
                resId = R.string.label_currency_jpy_pre;
            } else if (AppConst.CURRENCY_CODE_RMB.equals(currencyCode)) {
                resId = R.string.label_currency_rmb_pre;
            } else if (AppConst.CURRENCY_CODE_USD.equals(currencyCode)) {
                resId = R.string.label_currency_usd_pre;
            }
        }

        if (resId == 0) {

            if (SubsidiaryCode.isJapan()) {

                resId = R.string.label_currency_jpy_pre;
            } else {

                resId = R.string.label_currency_rmb_pre;
            }
        }

        return getResourceString(context, resId);
    }


    //
    public static String getExpressConfirmTypeString(Context context, String expressConfirmType) {

        int resId = 0;
        if ("1".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_1;
        }
        if ("2".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_2;
        }
        if ("3".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_3;
        }
        if ("4".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_4;
        }
        if ("5".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_5;
        }
        //--ADD NT-LWL 17/08/22 Depo FR -
        if (!SubsidiaryCode.isJapan() && "6".equals(expressConfirmType)) {
            resId = R.string.so_dialog_info_6;
        }
        //--ADD NT-LWL 17/08/22 Depo TO -
        return getResourceString(context, resId);
    }


}

