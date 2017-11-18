package jp.co.misumi.misumiecapp.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.data.CategoryList;

public class FileUtil {

    private static final String CategoryFile = "Category.json";
    //-- ADD NT-LWL 17/09/25 Category FR -
    public static final String ExcludeCategoryListFile = "excludeCategoryList.json";
    public static final String CategoryQRMapFile = "CategoryQRMap.json";
    //-- ADD NT-LWL 17/09/25 Category TO -

    private FileUtil(){
    }

    /**
     * 写入字符串
     * @param context
     * @param json
     * @param fileName
     * @throws Exception
     */
    //-- ADD NT-LWL 17/09/25 Category FR -
    public static void writeString(Context context, String json, String fileName)throws Exception {
        try {
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(json.getBytes());
            fo.close();
            fo.flush();
        } catch (FileNotFoundException e) {
            throw new Exception(fileName+" file not found.");
        } catch (IOException e) {
            throw new Exception(fileName+" file write error.");
        }
    }

    /**
     * 读取字符串
     * @param context
     * @param fileName
     * @return
     */
    public static String readString(Context context,String fileName){
        try {
            FileInputStream fi = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader( new InputStreamReader(fi, "UTF-8" ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
            }
            if (br != null) {
                br.close();
            }
            if (fi != null) {
                fi.close();
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * excludeCategoryList
     * @param context
     * @param excludeCategoryList
     * @return
     */
    public static void exportExcludeCategoryList(Context context, String excludeCategoryList) throws Exception {
        try {
            FileOutputStream fo = context.openFileOutput(ExcludeCategoryListFile, Context.MODE_PRIVATE);
            fo.write(excludeCategoryList.getBytes());
            fo.close();
            fo.flush();
        } catch (FileNotFoundException e) {
            throw new Exception("excludeCategoryList file not found.");
        } catch (IOException e) {
            throw new Exception("excludeCategoryList file write error.");
        }
    }
    /**
     * importExcludeCategoryList
     * @param context
     * @return
     */
    public static List<String> importExcludeCategoryList(Context context){

        List<String> list = new ArrayList<>();

        try {
            FileInputStream fi = context.openFileInput(ExcludeCategoryListFile);
            BufferedReader br = new BufferedReader( new InputStreamReader(fi, "UTF-8" ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
            }
            if (br != null) {
                br.close();
            }
            if (fi != null) {
                fi.close();
            }

            JSONArray array = new JSONArray(sb.toString());
            for (int i=0;i<array.length();i++){
                list.add(array.getString(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return list;
        }

        return list;
    }
    //-- ADD NT-LWL 17/09/25 Category TO -
    /**
     * exportCategory
     * @param context
     * @param categoryList
     * @return
     */
    public static void exportCategory(Context context, CategoryList categoryList) throws Exception {
        try {
            FileOutputStream fo = context.openFileOutput(CategoryFile, Context.MODE_PRIVATE);
            fo.write(categoryList.toJson().getBytes());
            fo.close();
            fo.flush();
        } catch (FileNotFoundException e) {
            throw new Exception("category file not found.");
        } catch (IOException e) {
            throw new Exception("category file write error.");
        }
    }

    /**
     * importCategory
     * @param context
     * @return
     */
    public static CategoryList importCategory(Context context){

        CategoryList categoryList = new CategoryList();

        try {
            FileInputStream fi = context.openFileInput(CategoryFile);
            BufferedReader br = new BufferedReader( new InputStreamReader(fi, "UTF-8" ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
            }
            if (!categoryList.setData(sb.toString())){
                return null;
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return categoryList;
    }

    /**
     * existingCategory
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean existingCategory(Context context){
        try {
            context.openFileInput(CategoryFile).close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * deleteCategory
     * @param context
     */
    public static void deleteCategory(Context context){
        boolean ret = context.deleteFile(CategoryFile);
        if (ret){
            AppLog.d("category file delete");
        }else{
            AppLog.d("category file not delete");
        }
    }

}
