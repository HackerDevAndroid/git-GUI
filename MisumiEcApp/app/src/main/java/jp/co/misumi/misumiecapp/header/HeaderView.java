package jp.co.misumi.misumiecapp.header;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.co.misumi.misumiecapp.activity.AppActivity;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * ヘッダ
 */
public abstract class HeaderView {

    private Context mContext;
    private AppActivity mActivity;
    private ActionBar mActionBar;

    private List mHeaderEventListeners = new ArrayList();
    public interface HeaderEventListener {
        void onHeaderEvent(int event, Objects objects);
    }


    HeaderView(AppActivity activity){
        mContext = activity;
        mActivity = activity;

        mActionBar = mActivity.getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = mActivity.getLayoutInflater().inflate(getHeaderViewResource(), null);
		ViewUtil.setSplitMotionEventsToAll(customView);


        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(customView, lp);
        Toolbar parent =(Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

    }

    public View getHeaderView(){

        if (mActionBar != null){
            return mActionBar.getCustomView();
        }

        return null;
    }

    public Context getContext(){
        return this.mContext;
    }

    public void hideHeader(){
        mActionBar.hide();
    }
    public void showHeader(){
         mActionBar.show();
    }

    public AppActivity getActivity(){
        return mActivity;
    }

    protected abstract int getHeaderViewResource();


    protected void sendEvent(int event, Objects objects){
        for (Object mHeaderEventListener : mHeaderEventListeners) {
            HeaderEventListener o = (HeaderEventListener) mHeaderEventListener;
            o.onHeaderEvent(event, objects);
        }
    }

    public void addHeaderEventListener(HeaderEventListener listener){
        mHeaderEventListeners.add(listener);
    }
    public void removeHeaderEventListener(HeaderEventListener listener){
        mHeaderEventListeners.remove(listener);
    }


}
