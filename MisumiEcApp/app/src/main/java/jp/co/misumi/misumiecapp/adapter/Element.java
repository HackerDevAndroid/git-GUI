package jp.co.misumi.misumiecapp.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.CategoryList;

/**
 * Created by ost000422 on 2015/08/31.
 */
public class Element {

    private int mObjectType;	// 1,2,3, 11
    private Object mObj;
    private int mResId;
    private List<Element> children = new ArrayList<>();

    public Element(int objectType, Object obj)
    {
        mObjectType = objectType;
        mObj = obj;

		if (mObj == null) {
			return;
		}

		//カテゴリ
		if (mObjectType == 1) {

			mResId = R.layout.list_item_keyword_header_folder;

			ArrayList<CategoryList.Category> categoryList = (ArrayList<CategoryList.Category>)mObj;
			for (CategoryList.Category info : categoryList) {
				Element element = new Element(11, info);
				addChild(element);
			}

		}

		//件数
		if (mObjectType == 2) {

			mResId = R.layout.list_item_keyword_sub;

		}

		//商品
		if (mObjectType == 3) {

			mResId = R.layout.list_item_keyword_search_tap;

		}

		//カテゴリの子供
		if (mObjectType == 11) {

			mResId = R.layout.list_item_category_search_sub2;

		}

        //品牌检索
        if (mObjectType == 4) {

            mResId = R.layout.list_item_keyword_header_folder;
        }

    }


    public int getObjectType()
    {
        return mObjectType;
    }

    public Object getObject()
    {
        return mObj;
    }

    public void setObject(Object obj)
    {
        mObj = obj;
    }

    public int getResId()
    {
        return mResId;
    }

    public boolean isParent()
    {
        return children.size() > 0;
    }

    public List<Element> getChildren()
    {
        return children;
    }

    public void addChild(Element element)
    {
        children.add(element);
    }

}
