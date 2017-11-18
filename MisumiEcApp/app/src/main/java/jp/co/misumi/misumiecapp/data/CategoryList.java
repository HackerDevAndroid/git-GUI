package jp.co.misumi.misumiecapp.data;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.util.BlackListUtils;
import jp.co.misumi.misumiecapp.util.FileUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

/**
 * CategoryList
 */
public class CategoryList extends DataContainer {


    public String updateDateTime;
    public ArrayList<Category> categoryList = new ArrayList<>();

    public ErrorList errorList;

    public static class Category extends DataContainer{
        // カテゴリコード
        public String categoryCode;
        // カテゴリ名称
        public String categoryName;
        // 画像URL
        public String categoryImageUrl;
        // カテゴリ情報リスト
        public ArrayList<Category> categoryList = null;

        // 子カテゴリ有りフラグ 0: 子カテゴリ無し、1: 子カテゴリ有り
        public String hasChildCategoryFlag;
        //-- ADD NT-LWL 17/07/26 TopCategory FR -
        // 排序位置
        public int position;
        //-- ADD NT-LWL 17/07/26 TopCategory TO -


        public boolean setData(JSONObject json){

            categoryList = new ArrayList<>();
            try {
                categoryCode = getJsonString(json, "categoryCode");
                categoryName = getJsonString(json, "categoryName");
                categoryImageUrl = getJsonString(json, "categoryImageUrl");

                if (!TextUtils.isEmpty(categoryCode) && categoryCode.equals("mech")){
                    categoryName = "设备维护用品";
                }

                hasChildCategoryFlag = getJsonString(json, "hasChildCategoryFlag");


                if (!json.has("categoryList")){
                    return true;
                }


                JSONArray categories = getJsonArray(json, "categoryList");
                for (int ii = 0; ii < categories.length(); ii++) {
                    Category category = new Category();
                    if (!category.setData(categories.getJSONObject(ii))){
                        return false;
                    } else {
                        categoryList.add(category);
                    }
                    //-- ADD NT-LWL 17/09/25 Category FR -
                    if (SubsidiaryCode.isChinese()){
                        // 子分类黑名单检查
                        if (BlackListUtils.getInstance().getBlackList().contains(category.categoryCode)){
                            categoryList.remove(category);
                        }
                    }
                    //-- ADD NT-LWL 17/09/25 Category TO -
                }


            } catch (JSONException e) {
                e.printStackTrace();
                AppLog.e(e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return "{" +
                    ((categoryCode != null) ? categoryCode : "null") +
                    "," +
                    ((categoryName != null) ? categoryName : "null") +
                    "," +
                    ((categoryImageUrl != null) ? categoryImageUrl : "null") +
                    "}";
        }

        public JSONObject toJson() throws JSONException {

            JSONObject json = new JSONObject();

            if (categoryCode != null) {
                json.put("categoryCode", categoryCode);
            }
            if (categoryName != null) {
                json.put("categoryName", categoryName);
            }
            if (categoryImageUrl != null) {
                json.put("categoryImageUrl", categoryImageUrl);
            }
            if (categoryList != null){
                JSONArray categories = new JSONArray();
                for (Category category : categoryList){
                    categories.put(category.toJson());
                }
                json.put("categoryList", categories);
            }

            return json;
        }
    }

    public boolean setData(String src){

        if (!categoryList.isEmpty()){
            categoryList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            updateDateTime = getJsonString(json, "updateDateTime");

            if (!json.has("categoryList")){
                return true;
            }

            JSONArray categories = getJsonArray(json, "categoryList");

            for (int ii = 0; ii < categories.length(); ii++) {
                Category category = new Category();
                if (!category.setData(categories.getJSONObject(ii))){
                    return false;
                } else {
                    categoryList.add(category);
                }
                //-- ADD NT-LWL 17/09/25 Category FR -
                if (SubsidiaryCode.isChinese()){
                    // 大分类黑名单检查
                    if (BlackListUtils.getInstance().getBlackList().contains(category.categoryCode)){
                        categoryList.remove(category);
                    }
                }
                //-- ADD NT-LWL 17/09/25 Category TO -
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }

    public String toJson(){
        JSONObject json = new JSONObject();
        try {
            JSONArray categories = new JSONArray();

            for (Category category : categoryList){
                categories.put(category.toJson());
            }
            json.put("categoryList", categories);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    /**
     * isEmpty
     * @return
     */
    public boolean isEmpty(){
        return categoryList == null || categoryList.size() == 0;
    }

    /**
     * exportFile
     * @param context
     * @return
     */
    public boolean exportFile(Context context){
        if (categoryList == null || categoryList.size() == 0){
            return true;
        }

        try {
            if (errorList == null) {
                FileUtil.exportCategory(context, this);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * importFile
     * @param context
     * @return
     */
    public boolean importFile(Context context){

        AppLog.d("category import file.");
        errorList = null;
        categoryList.clear();

        CategoryList cate = FileUtil.importCategory(context);

        if (cate == null){
            return false;
        }
        if (cate.categoryList != null){
            categoryList = cate.categoryList;
        }
        if (cate.errorList != null){
            errorList = cate.errorList;
        }

        updateDateTime = cate.updateDateTime;

//        cate = null;

        return true;
    }

}
