//-- ADD NT-LWL 17/09/25 Category FR -
package jp.co.misumi.misumiecapp.util;

import android.app.Application;
import android.content.Context;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created date: 2017/9/25 12:18
 * Description: 分类黑名单检查
 */

public class BlackListUtils {
    private static BlackListUtils mBlackListUtils = null;
    private Context mContext;
    private List<String> blackList;

    private BlackListUtils(Application mContext) {
        this.mContext = mContext;
        blackList = new ArrayList<>();
    }

    public static BlackListUtils createBlackList(Application mContext) {
        if (mBlackListUtils == null) {
            mBlackListUtils = new BlackListUtils(mContext);
        }
        return mBlackListUtils;
    }

    public static BlackListUtils getInstance() {
        return mBlackListUtils;
    }

    /**
     * 获取 黑名单集合
     *
     * @return
     */
    public List<String> getBlackList() {
        if (blackList == null || blackList.isEmpty()) {
            blackList = FileUtil.importExcludeCategoryList(mContext);
        }
        return blackList;
    }

    // 保存数据
    public void saveBlackList(String blackListJson) {
        try {
            JSONArray array = new JSONArray(blackListJson);
            for (int i = 0; i < array.length(); i++) {
                blackList.add(array.getString(i));
            }
            FileUtil.exportExcludeCategoryList(mContext, blackListJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//-- ADD NT-LWL 17/09/25 Category TO -
