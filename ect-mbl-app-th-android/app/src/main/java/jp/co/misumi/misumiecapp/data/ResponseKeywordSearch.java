package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.util.BlackListUtils;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;

/**
 * キーワード検索
 */
public class ResponseKeywordSearch extends SearchSeriesList {

    public String keyword;                        //

//	ブランドは不要
//    public ArrayList<Brand> mBrandList = new ArrayList<>();

    public ArrayList<CategoryList.Category> mCategoryList = new ArrayList<>();

    public ErrorList errorList;

    public boolean setData(String src) {

        if (!mCategoryList.isEmpty()) {
            mCategoryList.clear();
        }

        if (!super.setData(src)) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(src);

/*
            //商品件数
            totalCount = getJsonInteger(json, "totalCount");

            mSeriesList = new ArrayList<>();
            JSONArray serieses = json.getJSONArray("seriesList");

            for (int num=0; num<serieses.length(); num++){
                SearchSeriesList.Series series = new SearchSeriesList.Series();
                if (!series.setData(serieses.getJSONObject(num))){
                    return false;
                } else {
                    mSeriesList.add(series);
                }
            }
*/
            // エラーリスト
            errorList = getErrorList(json);

            if (!json.has("categoryList")) {
                return true;
            }
            JSONArray categories = getJsonArray(json, "categoryList");

            for (int ii = 0; ii < categories.length(); ii++) {
                CategoryList.Category category = new CategoryList.Category();
                if (!category.setData(categories.getJSONObject(ii))) {
                    return false;
                } else {
                    mCategoryList.add(category);
                }
                //-- ADD NT-LWL 17/09/25 Category FR -
                if (!SubsidiaryCode.isJapan()) {
                    // 关键字检索分类黑名单检查
                    if (BlackListUtils.getInstance().getBlackList().contains(category.categoryCode)) {
                        mCategoryList.remove(category);
                    }
                }
                //-- ADD NT-LWL 17/09/25 Category TO -
            }

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
    }
}
