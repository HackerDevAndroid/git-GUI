package jp.co.misumi.misumiecapp.util;

import android.widget.AbsListView;
import android.widget.ListView;


/**
 * UrlEncoder
 */
public class ListViewUtil {

    private ListView mListView;
    private OnListViewListener mListener;
    private boolean mNoMoreRead;
    private boolean mIsTriggered;

    public interface OnListViewListener {
        boolean canMoreRead();

        void onAdditionalReading(int totalItemCount);

        void onRemoveFooterView();

        void onSetFooterViewPc();
    }

    public void init() {

        mNoMoreRead = false;
        mIsTriggered = false;
    }


    public ListViewUtil(ListView listView, OnListViewListener listener) {

        mListView = listView;
        mListener = listener;

        init();

        //スクロール
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                //まだ読み込み？
                if (mNoMoreRead) {
                    return;
                }

                //まだ読み込み？
                if (!mListener.canMoreRead()) {
                    return;
                }

//	            AppLog.e("totalItemCount: " + totalItemCount +", "+ firstVisibleItem +", "+ visibleItemCount +"/ "+ mIsTriggered);

                //二重に呼ばない様にガードする
                //追加読み込みが発生して totalItemCountが更新されたらクリアする
                if (mIsTriggered) {

                    if (totalItemCount > firstVisibleItem + visibleItemCount) {
                        mIsTriggered = false;
                    }
                    return;
                }

                //逐次読み込み判定
                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    mIsTriggered = true;

                    mListener.onAdditionalReading(totalItemCount);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    public void updateListData(int listSize, int totalCount, int requestPage, int adapterCount, int LIST_MAX_COUNT, int LIST_REQUEST_COUNT) {

        int maxTotal = Math.min(totalCount, LIST_MAX_COUNT);
        int maxPage = (int) Math.ceil((double) maxTotal / LIST_REQUEST_COUNT);

//        AppLog.e("maxPage: " + totalCount +"/ "+ maxPage);

        //ListViewの件数が 0件かページ数を読み込んだら終わり
        if ((listSize == 0) || (requestPage > maxPage)) {

            mNoMoreRead = true;
        }

        //PCから参照するUI、実データと総件数
        //総件数がPC最大件数より多い場合
        if ((adapterCount >= LIST_MAX_COUNT)
                && (totalCount > LIST_MAX_COUNT)) {

            //PCから参照するUI
//				setFooterViewPc();
            mListener.onSetFooterViewPc();
            mNoMoreRead = true;

        } else {
            if (mNoMoreRead) {
//				mListView.removeFooterView(getFooterView());
                mListener.onRemoveFooterView();
            }
        }

        if (!mNoMoreRead) {
            mIsTriggered = false;
        }
    }

}

