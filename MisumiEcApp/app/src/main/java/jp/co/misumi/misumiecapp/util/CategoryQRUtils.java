//-- ADD NT-LWL 17/09/27 Category FR -
package jp.co.misumi.misumiecapp.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created date: 2017/9/27 11:33
 * Description: 分类code 替换工具
 */

public class CategoryQRUtils {

    private CategoryQRUtils() {
    }

    /**
     * 保存到文件
     * @param context
     * @param json
     */
    public static void saveCategoryQRData(Context context,String json){
        try {
            FileUtil.writeString(context,json,FileUtil.CategoryQRMapFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件
     * @param context
     * @return
     */
    public static HashMap<String,String> readCategoryQRData(Context context){
        HashMap<String,String> categoryMap = new HashMap<>();
        String json = FileUtil.readString(context,FileUtil.CategoryQRMapFile);
        try {
            JSONArray array = new JSONArray(json);
            int n = array.length();
            for (int i=0; i<n;i++){
                JSONObject object = array.getJSONObject(i);
                String oldCode = object.getString("fcd");
                String newCode = object.getString("tcd");
                categoryMap.put(oldCode,newCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return categoryMap;
        }
        return categoryMap;
    }

}
//-- ADD NT-LWL 17/09/27 Category TO -