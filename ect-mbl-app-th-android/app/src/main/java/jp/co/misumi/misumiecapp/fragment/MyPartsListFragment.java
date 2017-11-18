package jp.co.misumi.misumiecapp.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.ExpandAnimator;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.adapter.MyPartsDetailAdapter;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.observer.AppNotifier;


/**
 * My部品表画面
 */
//TODO:表示項目の内容が暫定
//TODO:表示データの内容が暫定
//TODO:国毎の微妙なアプリ仕様の違い
//TODO:通信してアイテム詳細画面に遷移
public class MyPartsListFragment extends BaseGetSpProductApi {

    //検索リクエスト用
//	private RequestSearchOrder	mRequest;
    //画面表示用
    private ResponseGetMyComponents mResponse;

    private MyPartsDetailAdapter mListAdapter;
    private ListView mListView;
    private View mFolderView;
    private View mEmptyView;

    private View mView;

    //API
    private MyPartsApi mMyPartsApi; //フォルダ変更
    private AddToCartApi mAddToCartApi;
    //	private SeriesSearchApi mSeriesSearchApi;
    private GetSpProductApi mGetSpProductApi;

//    private Handler mMainHandler = new Handler() {
//	        @Override
//            public void handleMessage(Message msg) {
//
//            }
//		};

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        //アプリ内で変更するので保存
//        outState.putSerializable("RequestSearch", mRequestSearch);
        outState.putSerializable("ResponseGetMyComponents", mResponse);
    }


    public MyPartsListFragment() {

        mMyPartsApi = new MyPartsApi() {
            @Override
            protected void onSuccess(ResponseGetMyComponents response) {
                getFragmentController().stackFragment(new MyPartsListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }
        };

        mAddToCartApi = new AddToCartApi();
//		mSeriesSearchApi = new SeriesSearchApi();
        mGetSpProductApi = new GetSpProductApi() {
            @Override
            protected void onSuccess(ResponseGetSpProduct response) {
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

            @Override
            protected String getScreenId() {
                return MyPartsListFragment.this.getScreenId();
            }
        };

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

/*
        // 現地法人判別用
        if (AppConst.LocalSubsidiary == AppConst.LOCAL_SUBSIDIARY_CHN) {
        }
*/

        //リクエストデータを最初に作成して、このデータクラスを画面編集と同期させる
        //アプリ内で変更するので都度
        if (savedInstanceState != null) {

//			mRequestSearch = (RequestSearchOrder)savedInstanceState.getSerializable("RequestSearch");
            mResponse = (ResponseGetMyComponents) savedInstanceState.getSerializable("ResponseGetMyComponents");

        } else {

//			if (mRequestSearch == null) {
//				mRequestSearch	= new RequestSearchOrder();
//			}

            if (mResponse == null) {
                mResponse = (ResponseGetMyComponents) getParameter();
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateLayout(inflater, R.layout.fragment_myparts_list, container, false);

        makeView(rootView);

        makeDataView(rootView);

        mView = rootView;
        return rootView;
    }


    protected void makeView(View rootView) {

        final LayoutInflater inflater = mParent.getLayoutInflater();

        //
        mListView = (ListView) rootView.findViewById(R.id.listView);

        //ヘッダー情報
        //Myフォルダ選択
        mFolderView = inflateLayout(inflater, R.layout.list_item_myparts_detail_header_folder, mListView, false);
        mListView.addHeaderView(mFolderView, null, false);


        //空の時
        mEmptyView = inflateLayout(inflater, R.layout.list_item_myparts_detail_empty, mListView, false);
        mListView.addFooterView(mEmptyView, null, false);
    }


/*
	private int getTextViewSize(TextView textView){

		float fontSize = textView.getTextSize();
		Paint paint = new Paint();
		paint.setTextSize(fontSize);

		AppLog.d("fontSize=" + fontSize);
		AppLog.d("text=" + textView.getText());

		//Paint.FontMetrics fm = paint.getFontMetrics();
		float textWidth = paint.measureText(textView.getText().toString());

		AppLog.d("textWidth=" + textWidth);
		return (int) textWidth;
	}
*/


    protected void makeDataView(final View rootView) {

//		final LayoutInflater inflater = mParent.getLayoutInflater();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int totalSize;
                View v = rootView.findViewById(R.id.layoutFolder);
                totalSize = v.getWidth();
                totalSize -= v.getPaddingLeft();
                totalSize -= v.getPaddingRight();

//		AppLog.e("totalSize=" + totalSize);

//				totalSize -= getTextViewSize((TextView) rootView.findViewById(R.id.textFolderNameSep));
//				totalSize -= getTextViewSize((TextView) rootView.findViewById(R.id.textListCount));
//				totalSize -= getTextViewSize((TextView) rootView.findViewById(R.id.textView));
                //AppLog.d("totalSize=" + totalSize);

                totalSize -= rootView.findViewById(R.id.layoutCount).getWidth();

//		AppLog.e("totalSize5=" + totalSize);

//				Display display = mParent.getWindowManager().getDefaultDisplay();
//				Point size = new Point();
//				display.getSize(size);

                int maxSize = totalSize;// - ( (15 * AppConfig.getInstance().dp)*4);
                //AppLog.d("maxSize=" + totalSize);

                TextView t = (TextView) rootView.findViewById(R.id.textFolderName);
                setTextEmptyGone(t, mResponse.folderName);
                t.setMaxWidth(maxSize);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        TextView tv;
        tv = (TextView) rootView.findViewById(R.id.textFolderName);
//		tv.setText(mResponse.folderName);
        setTextEmptyGone(tv, mResponse.folderName);
        tv.setText("");
        if (mResponse.folderName == null || mResponse.folderName.isEmpty()) {

            rootView.findViewById(R.id.textFolderNameSep).setVisibility(View.GONE);
        } else {

            rootView.findViewById(R.id.textFolderNameSep).setVisibility(View.VISIBLE);
        }


        List<ResponseGetMyComponents.Product> itemList = mResponse.mProductList;
        int itemCount = itemList.size();
        tv = (TextView) rootView.findViewById(R.id.textListCount);
        tv.setText("" + itemCount);

        if (itemCount == 0) {

            //空の時
            mEmptyView.findViewById(R.id.viewVisible).setVisibility(View.VISIBLE);
//			mFolderView.setVisibility(View.GONE);
            if (mResponse.mFolderList == null || mResponse.mFolderList.isEmpty()) {
//                rootView.findViewById(R.id.textFolderNameSep).setVisibility(View.GONE);
                rootView.findViewById(R.id.textFolderNameSep).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.textFolderNameSepLabel).setVisibility(View.VISIBLE);
            } else {
                rootView.findViewById(R.id.textFolderNameSep).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.textFolderNameSepLabel).setVisibility(View.GONE);
            }
        } else {

            //中身が有る時
            mEmptyView.findViewById(R.id.viewVisible).setVisibility(View.GONE);
        }

        //
        mListAdapter = new MyPartsDetailAdapter(getContext(), R.layout.list_item_myparts_detail_item, itemList, mOnItemClickListener);
        mListView.setAdapter(mListAdapter);


        // フォルダ一覧スライド
        LinearLayout slideTrigger = (LinearLayout) mFolderView.findViewById(R.id.layoutSlideTrigger);
        LinearLayout slideContents = (LinearLayout) mFolderView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = mFolderView.findViewById(R.id.imageSlideSwitch);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        if (mResponse.mFolderList.size() == 0) {

            slideTrigger.setVisibility(View.GONE);
            slideContents.setVisibility(View.GONE);
        } else {

            slideTrigger.setVisibility(View.VISIBLE);
            slideContents.setVisibility(View.VISIBLE);

            for (ResponseGetMyComponents.Folder info : mResponse.mFolderList) {

                View tableRow = inflateLayout(mParent.getLayoutInflater(), R.layout.include_item_text_folder, slideContents, false);

                TextView textView1 = (TextView) tableRow.findViewById(R.id.textView1);
                textView1.setText(info.folderName);

                slideContents.addView(tableRow);

                final ResponseGetMyComponents.Folder infoF = info;

                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sort = "0";
                        doMyParts(infoF.folderId, sort);
                    }
                });

            }

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);
        }
    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.MyParts;
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mMyPartsApi.close();
//		mMyPartsApiRefresh.close();
        mAddToCartApi.close();
//		mUpdateApi.close();
//		mDeleteApi.close();
//		mSeriesSearchApi.close();
        mGetSpProductApi.close();

        super.onPause();
    }


    //TODO:後にもっと簡略化する
    private void addExpandAnimator(View triggerView, final View viewSlideSwitch, View containerView) {

//        manager.put(key, createAnimator(v.findViewById(containerId)));

        final ExpandAnimator animator = createAnimator(containerView);

        triggerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                if (!animator.isAnimating()) {
                    if (animator.isExpand()) {

                        animator.unexpand();
                        viewSlideSwitch.setSelected(false);
                    } else {

                        animator.expand();
                        viewSlideSwitch.setSelected(true);
                    }
                }
            }
        });
    }


    private ExpandAnimator createAnimator(View containerView) {


        ExpandAnimator animator = new ExpandAnimator(containerView, new ExpandAnimator.OnAnimationListener() {
            @Override
            public void onExpanded(ExpandAnimator e) {
            }

            @Override
            public void onStartExpand(ExpandAnimator e) {
            }

            @Override
            public void onStartUnexpand(ExpandAnimator e) {
            }

            @Override
            public void onUnexpanded(ExpandAnimator e) {
            }
        });

        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setWrapContent(true);
        return animator;
    }


    // リストビューのアイテムがクリックされた時
    MyPartsDetailAdapter.OnItemClickListener mOnItemClickListener = new MyPartsDetailAdapter.OnItemClickListener() {

        public void onItemClick(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseGetMyComponents.Product itemInfo = (ResponseGetMyComponents.Product) parent.getItem(position);
            doSpProduct(itemInfo);
        }


        public void onItemAdd(ArrayAdapter<?> parent, View view, int position, long id) {

            ResponseGetMyComponents.Product itemInfo = (ResponseGetMyComponents.Product) parent.getItem(position);

            doAddToCart(itemInfo);

        }
    };


    private void doMyParts(String folderId, String sort) {

        hideKeyboard();

        mMyPartsApi.setParameter(folderId, sort);
        mMyPartsApi.connect(getContext());
    }


    /**
     * My部品表
     */
    private class MyPartsApi extends ApiAccessWrapper {

        String folderId = "0";
        String sort = "0";

        @Override
        protected String getScreenId() {
            return MyPartsListFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        public void setParameter(String folderId, String sort) {
            this.folderId = folderId;
            this.sort = sort;
        }

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetMyComponents(folderId, sort);
        }

        @Override
        public void onResult(int responseCode, String result) {
            ResponseGetMyComponents response = new ResponseGetMyComponents();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    onSuccess(response);

                    break;
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        protected void onSuccess(ResponseGetMyComponents response) {
        }
    }


    /**
     * カートに追加
     */
    private void doAddToCart(ResponseGetMyComponents.Product item) {

        mAddToCartApi.setParameter(item);
        mAddToCartApi.connect(getContext());
    }


    private class AddToCartApi extends ApiAccessWrapper {

        ResponseGetMyComponents.Product mItem;

        @Override
        protected String getScreenId() {
            return MyPartsListFragment.this.getScreenId();
        }

        public void setParameter(ResponseGetMyComponents.Product item) {
            mItem = item;
        }

        @Override
        public HashMap<String, String> getParameter() {

            return ApiBuilder.createAddToCartFromMyParts(mItem);
        }

        @Override
        public void onResult(int responseCode, String result) {

            AddToCart cart = new AddToCart();
            if (!cart.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //ヘッダのカート件数を更新する
                    //無条件に足し込む
                    int addCount = 1;

                    AppNotifier.getInstance().addCartCount(addCount);

                    new MessageDialog(getContext(), null)
                            .show(getResourceString(R.string.my_parts_dialog_added_cart_title),
                                    getResourceString(R.string.my_parts_dialog_added_cart), 0, R.string.dialog_button_close);

                    break;

                default:
                    showErrorMessage(cart.errorList);
                    break;
            }
        }
    }


    private void doSpProduct(ResponseGetMyComponents.Product itemInfo) {

        hideKeyboard();

        String completeType = "4";    //API仕様書より "4"固定
        String seriesCode = itemInfo.seriesCode;
        String innerCode = itemInfo.innerCode;
        String partNumber = itemInfo.partNumber;
        Integer quantity = itemInfo.quantity;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


    private void setTextEmptyGone(TextView tv, String str) {
        if (str == null || str.isEmpty()) {
            tv.setText("");
            tv.setVisibility(View.GONE);
            return;
        }

        tv.setText(str);
        tv.setVisibility(View.VISIBLE);
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.MyParts;
    }
}


