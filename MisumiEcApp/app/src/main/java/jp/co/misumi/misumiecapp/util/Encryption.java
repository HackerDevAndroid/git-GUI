package jp.co.misumi.misumiecapp.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 暗号化クラス
 */
public class Encryption {

    private final static String AES_TYPE = "AES/ECB/PKCS5Padding";

    /**
     * AES128暗号化
     * @param key キー
     * @param value 暗号化する文字列
     * @return 暗号化された文字列
     */
    public static String toAES128(String key, String value) {
        AppLog.d("toAES128=[" + value + "]");
        try {
            byte[] rawKey = getRawKey(key.getBytes("UTF-8"));
            SecretKey secretKey = new SecretKeySpec(rawKey, "AES");

            Cipher c = Cipher.getInstance(AES_TYPE);
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedString = c.doFinal(value.getBytes("UTF-8"));
            return toHex(encryptedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * AES128複合化
     * @param key キー
     * @param value 複合化する文字列
     * @return 複合化された文字列
     */
    public static String fromAES128(String key, String value) {
        AppLog.d("fromAES=[" + value + "]");
        try {
            byte[] rawKey = getRawKey(key.getBytes("UTF-8"));
            byte[] byteValue = toByte(value);
            SecretKey secretKey = new SecretKeySpec(rawKey, "AES");
            Cipher c = Cipher.getInstance(AES_TYPE);
            c.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedString = c.doFinal(byteValue);
            return new String(decryptedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 指定したバイト配列を16バイトで取得する
     * @param key バイト配列
     * @return 16バイトのバイト配列
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] key) {
        byte[] raw = new byte[16];
        for (int i=0; i<raw.length; i++) {
            if (key.length >= i+1) {
                raw[i] = key[i];
            } else {
                raw[i] = '0';
            }
        }
        return raw;
    }
    /**
     * 16進文字列をバイト配列に変換する
     * @param hexString 文字列
     * @return バイト配列
     */
    private static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        }
        return result;
    }
    /**
     * 指定したバイト配列を16進文字列に変換する
     * @param buf バイト配列
     * @return 文字列
     */
    private static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2*buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    /**
     * 指定したバイトを文字列として追加します
     * @param sb 追加先の文字列バッファ
     * @param b バイト
     */
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
