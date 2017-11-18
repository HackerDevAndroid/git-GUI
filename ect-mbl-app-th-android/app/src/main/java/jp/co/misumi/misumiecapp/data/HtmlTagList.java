package jp.co.misumi.misumiecapp.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import jp.co.misumi.misumiecapp.AppLog;

public class HtmlTagList {

    public ArrayList<HtmlTag> HtmlTagList = new ArrayList<>();
    private Context context;
    public String jsonData;

    private static HtmlTagList instance = null;

    public HtmlTagList(Context context) {
        this.context = context;
    }

    //インスタンスの作成
    public static HtmlTagList createInstance(Context context) {
        if (instance == null) {
            instance = new HtmlTagList(context);
        }
        return instance;
    }

    public static HtmlTagList getInstance() {
        return instance;
    }

//    private final String mFileName = "htmltag";

    /**
     *
     */
    public class HtmlTag {
        public String tagString;
        public String replaceString;


        HtmlTag(String tagString, String replaceString) {
            this.tagString = tagString;
            this.replaceString = replaceString;
        }
    }

    /**
     * @param src
     * @return
     */
    public boolean setData(String src) {

        if (!HtmlTagList.isEmpty()) {
            HtmlTagList.clear();
        }

        jsonData = src;
        JSONObject json;

        try {
            json = new JSONObject(src);
            JSONObject htmlTags = json.getJSONObject("htmlTags");
            Iterator<String> keys = htmlTags.keys();
            while (keys.hasNext()) {
                String tagString = keys.next();
                String replaceString = htmlTags.getString(tagString);
                HtmlTagList.add(new HtmlTag(tagString, replaceString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * @param inputString
     * @return
     */
    static public String replaceTagString(String inputString) {
        String result = null;


        return result;
    }

}
