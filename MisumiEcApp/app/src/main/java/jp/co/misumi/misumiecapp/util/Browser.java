package jp.co.misumi.misumiecapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.MisumiEcApp;


/**
 * Browser
 */
public class Browser {

    public static void run(Context context, String url){

        String addr;
        try {
            addr = new URL(url).toString();
        } catch (MalformedURLException e) {
            addr = "http://" + url;
        }


		//サイカタ
        if (!addr.contains("?")){
            addr += "?";
        }else{
            addr += "&";
        }
		addr += MisumiEcApp.getWebUrlStrExt();
        AppLog.d("addr=" + addr);

        Uri uri = Uri.parse(addr);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(i);
    }
}
