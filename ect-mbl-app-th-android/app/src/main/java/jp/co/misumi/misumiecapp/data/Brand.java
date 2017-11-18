//--ADD NT-LWL 17/09/04 BrandSearch TO -
package jp.co.misumi.misumiecapp.data;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;

import static android.R.attr.src;

/**
 * Created date: 2017/9/5 14:55
 * Description: 品牌
 */

public class Brand extends DataContainer {
    // 品牌code
    public String brandCode;
    // 品牌名称
    public String brandName;
    // 是否选中标识
    public String selectedFlag;
    // 是否选中
    public boolean isCheck;
    // listView 中位置
    public int position;
    // 是否可添加
    public boolean isEnabled = true;


    public boolean setData(JSONObject json) {


        try {
            brandCode = getJsonString(json, "brandCode");
            brandName = getJsonString(json, "brandName");
            selectedFlag = getJsonString(json, "selectedFlag");
            if (!TextUtils.isEmpty(selectedFlag) && selectedFlag.equals("1")) {
                isCheck = true;
            } else {
                isCheck = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }
        return true;
    }
}
//--ADD NT-LWL 17/09/04 BrandSearch TO -