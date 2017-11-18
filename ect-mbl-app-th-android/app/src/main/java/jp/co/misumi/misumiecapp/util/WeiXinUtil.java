//--ADD NT-LWL 17/05/20 Share FR -
package jp.co.misumi.misumiecapp.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import java.io.ByteArrayOutputStream;

public class WeiXinUtil {

    // 图片转为字节
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        bmp.compress(CompressFormat.JPEG, 100, output);

        byte[] result = output.toByteArray();

        try {
            output.close();

            if (needRecycle) {
                bmp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

}
//--ADD NT-LWL 17/05/20 Share TO -