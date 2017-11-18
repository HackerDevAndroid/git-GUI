package jp.co.misumi.misumiecapp;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * ログ出力
 */
public class AppLog {

    private static final String TAG = "MisumiEcApp";
    private static int counter = 0;

    public static void v(String m) {
        if (BuildConfig.LogV) {
            Log.v(TAG, m);
        }
    }

    public static void d(String m) {
        if (BuildConfig.LogD) {
            Log.d(TAG, m);
        }
    }

    public static void e(String m) {
        if (BuildConfig.LogE) {
            Log.e(TAG, m);
        }
    }

    public static void e(Exception e) {
        if (BuildConfig.LogE) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    public static void file(String str) {

        if (BuildConfig.LogF) {
            String filename = Environment.getExternalStorageDirectory().getPath();
            if (!filename.substring(filename.length() - 1, filename.length()).equals("/")) {
                filename += "/misumi/misumi_data_log" + counter + ".txt";
            } else {
                filename += "misumi/misumi_data_log" + counter + ".txt";
            }


            counter++;
            if (counter > 9) {
                counter = 0;
            }

            File file = new File(filename);
            file.getParentFile().mkdir();

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);

                //デバグ用　512KB以上はカット
                if (str.length() > 1024 * 512) {
                    str = str.substring(0, 1024 * 512);
                }

                bw.write(str);
                bw.flush();
                bw.close();
            } catch (Exception e) {
            }

        }
    }

//    public static void println(String m) {
//
//        int length = m.length();
//        int total = 0;
//        while (true) {
//            total += Log.println(Log.DEBUG, TAG, m.substring(total));
//            if (total >= length){
//                break;
//            }
//        }
//    }

    public static void Config() {

        if (BuildConfig.ConfigLog) {
            Log.d(TAG, "--- config ---");
            Log.d(TAG, "API=" + AppConfig.getInstance().initApiBaseUrl);
            Log.d(TAG, "subsidiaryCode=" + AppConst.subsidiaryCode);
            Log.d(TAG, "ConnectTimeout=" + AppConst.ConnectTimeout);
            Log.d(TAG, "AppID=" + AppConst.AppID);
            Log.d(TAG, "LogV=" + BuildConfig.LogV);
            Log.d(TAG, "LogD=" + BuildConfig.LogD);
            Log.d(TAG, "LogE=" + BuildConfig.LogE);
            Log.d(TAG, "--------------");
        }
    }

}
