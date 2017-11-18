//package jp.co.misumi.misumiecapp.util;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import jp.co.misumi.misumiecapp.AppLog;
//
///**
// * UrlEncoder
// */
//public class UrlEncoder {
//
//    public static final String ENCODE_CHAR = "utf-8";
//
//	/**
//	 * encodeUrl
//	 * @param s
//	 * @return
//	 */
////	public static String encode(String s) {
////		StringBuilder builder = new StringBuilder();
////		char[] chars = s.toCharArray();
////		for (int i = 0; i < chars.length; i++) {
////			String temp = String.valueOf(chars[i]);
////			try {
////				String enc = URLEncoder.encode(temp, ENCODE_CHAR);
////				if (enc.length() < 4) {
////					// 半角
////					builder.append(temp);
////				} else {
////					// 全角
////					builder.append(enc);
////				}
////			} catch (UnsupportedEncodingException e) {
////				e.printStackTrace();
////			}
////		}
////		return builder.toString().replace(" ", "%20");
////	}
//
//	public static String encode(String s){
//
//
//		String test_url = "https://aaaaaa/get?p=ああああ&d=$$%&data=123&234";
//
//		int pos = s.indexOf("?");
//		if (pos > 0){
//
//			String url,param;
//			url = s.substring(0, pos);
//			param = s.substring(pos+1);
//
//			try {
//				return url + URLEncoder.encode(param, ENCODE_CHAR);
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return s;
//	}
//
//	public static String encode2(){
//
//
//		String test_url = "https://aaaaaa/get?p=ああああ&d=$$%&data=123&234";
//
//		int pos = test_url.indexOf("?");
//		if (pos > 0){
//
//			String url,param;
//			url = test_url.substring(0, pos);
//			param = test_url.substring(pos+1);
//
//			char[] chars = param.toCharArray();
//			for (int ii = 0 ; ii < chars.length ; ii++ ){
//
//			}
//
//			try {
//				return url + URLEncoder.encode(param, ENCODE_CHAR);
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return test_url;
//	}
//
//	public static String encode3(){
//
//
//		String test_url = "https://aaaaaa/get?p=ああああ&d=$$%&data=123&234";
//
//		int pos = test_url.indexOf("?");
//		if (pos > 0){
//
//			String url,param;
//			url = test_url.substring(0, pos);
//			param = test_url.substring(pos+1);
//
//			Pattern pattern = Pattern.compile("([a-z]*)=&");
//			Matcher m = pattern.matcher(param);
//			while (m.find()) {
//				AppLog.d("group=" + m.group());
//				AppLog.d("count=" + m.groupCount());
//				for (int i = 1; i <= m.groupCount(); i++) {
//					AppLog.d("[" + m.group(i) + "]");
//				}
//			}
//
//		}
//
//		return test_url;
//	}
//
//
//}
//
