package jp.co.misumi.misumiecapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.autocomplete.AutoCompleteProcess;
import jp.co.misumi.misumiecapp.data.RequestKeywordSearch;
import jp.co.misumi.misumiecapp.data.SearchSuggest;


/**
 * SearchBar
 */
public class SearchBar {

    private LinearLayout mSearchBar;
    private int mHeight;
    private boolean show;
    private AutoCompleteProcess autoCompleteProcess;
    private AutoCompleteTextView mEditPartNumber;
    private SearchKeywordProcess searchKeywordProcess;
    private GetSpProductProcess getSpProductProcess;
    private Activity activity;
    private Button buttonKeywordClear;
    private Button buttonSearch;

    private final int BAR_OPEN = 1;
    private final int BAR_CLOSE = 2;

    private Handler mHandler;

    public SearchBar(Activity activity){
        this.activity = activity;
        mSearchBar = (LinearLayout) activity.findViewById(R.id.searchBarLayout);
        show = true;
        mEditPartNumber = (AutoCompleteTextView) activity.findViewById(R.id.editKeyword);
        autoCompleteProcess = new AutoCompleteProcess(activity, mEditPartNumber, new AutoCompleteProcess.KeyEnter() {
            @Override
            public void codeEnter() {
                if (!mEditPartNumber.getText().toString().isEmpty()) {
                    //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                    //设置 点击来源为点击 搜索按钮
                    searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                    //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                    onSearchRequest();
                }
            }
        });
        activity.findViewById(R.id.buttonKeywordClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPartNumber.setText("");
            }
        });

        searchKeywordProcess = new SearchKeywordProcess();
        getSpProductProcess = new GetSpProductProcess();
        buttonKeywordClear = (Button) activity.findViewById(R.id.buttonKeywordClear);
        buttonKeywordClear.setVisibility(View.INVISIBLE);
        buttonSearch = (Button) activity.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                //设置 点击来源为点击 搜索按钮
                searchKeywordProcess.setClickSource(GoogleAnalytics.searchBtn);
                //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
                onSearchRequest();
            }
        });
        buttonSearch.setEnabled(false);
        mEditPartNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    buttonSearch.setEnabled(false);
                    buttonKeywordClear.setVisibility(View.INVISIBLE);
                } else {
                    buttonSearch.setEnabled(true);
                    buttonKeywordClear.setVisibility(View.VISIBLE);
                }
            }
        });
		//サジェストを選択した時のリスナー
		mEditPartNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

				AutoCompleteProcess.FilterObject filterObject = (AutoCompleteProcess.FilterObject)parent.getItemAtPosition(position);

				doSuggest(filterObject);
			}

		});


        mHandler = new Handler(Looper.getMainLooper(),mCallback);
    }

    private void onSearchRequest(){

//		ここでは閉じない
//        closeBar();

        InputMethodManager imm = (InputMethodManager) SearchBar.this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditPartNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String str = mEditPartNumber.getText().toString();

        RequestKeywordSearch request = new RequestKeywordSearch();
        request.keyword = str;
//        searchKeywordProcess.runDefault(SearchBar.this.activity, ((MainActivity) SearchBar.this.activity).getFragmentController(), request, ScreenId.SearchKeyword);
// 2015/09/28 field=@defaultだと検索結果が 0件になるから下記に変更
        searchKeywordProcess.run(SearchBar.this.activity, ((MainActivity) SearchBar.this.activity).getFragmentController(), request, ScreenId.SearchKeyword, this);
    }

    public void hideBar(){
        if (mSearchBar != null){

            ViewGroup.LayoutParams params = mSearchBar.getLayoutParams();

            mHeight = params.height;
            params.height = 0;
            mSearchBar.setLayoutParams(params);
            show = false;
        }
    }

    public void closeBar(){
        Message msg = mHandler.obtainMessage();
        msg.what = BAR_CLOSE;
        msg.sendToTarget();
    }
    public void openBar(){
        Message msg = mHandler.obtainMessage();
        msg.what = BAR_OPEN;
        msg.sendToTarget();
    }

    public boolean isOpened(){
        return show;
    }


    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case BAR_OPEN:
                    AppLog.d("BAR_OPEN " + (mSearchBar != null));
                    if (mSearchBar != null){
                        mSearchBar.clearAnimation();
                        ExpandAnimation anm = new ExpandAnimation(mSearchBar, 0, 48 * AppConfig.getInstance().dp);
                        anm.setDuration(300);
                        mSearchBar.startAnimation(anm);
                        mEditPartNumber.setText("");
                        mEditPartNumber.setEnabled(true);
                        show = true;
                    }
                    break;
                case BAR_CLOSE:
                    AppLog.d("BAR_CLOSE " + (mSearchBar != null));
                    if (mSearchBar != null){
                        mSearchBar.clearAnimation();
                        ExpandAnimation anm = new ExpandAnimation(mSearchBar, 48 * AppConfig.getInstance().dp, 0);
                        anm.setDuration(300);
                        mSearchBar.startAnimation(anm);
                        show = false;
                        mEditPartNumber.setText("");
                        mEditPartNumber.setEnabled(false);
                    }
                    break;
            }

            return false;
        }
    };


    private class ExpandAnimation extends Animation {

        int targetHeight;
        int startHeight;

        View view;

        public ExpandAnimation(View view,int startHeight, int targetHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (int)(startHeight + (targetHeight - startHeight)*interpolatedTime);
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, ((View)view.getParent()).getWidth(), parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


	private void  doSuggest(AutoCompleteProcess.FilterObject filterObject) {

        closeBar();

//		showToast("CCC: "+ position);
//		showToast("CCC: "+ filterObject.suggestString);
//		doSuggest(filterObject);
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        //设置 点击来源为点击 suggest列表
        searchKeywordProcess.setClickSource(GoogleAnalytics.suggest);
        //-- ADD NT-LWL 17/03/23 AliPay Payment FR -
        SearchSuggest.PartNumber partNumber = filterObject.partNumber;

		//キーワード検索？
		if (partNumber == null) {
			//キーワード検索
			onSearchRequest();

			return;
		}

		//型番検索
		//BaseGetSpProductApi
//        hideKeyboard();
        closeBar();

        InputMethodManager imm = (InputMethodManager) SearchBar.this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditPartNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        String completeType = partNumber.completeType;
        String seriesCode = partNumber.seriesCode;
        String innerCode = partNumber.innerCode;
        String partNumberStr = partNumber.partNumber;
		Integer quantity = 1;
/*
        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumberStr, quantity);
        mGetSpProductApi.connect(getContext());
*/
        getSpProductProcess.run(SearchBar.this.activity, ((MainActivity) SearchBar.this.activity).getFragmentController(), completeType, seriesCode, innerCode, partNumberStr, quantity, ScreenId.SearchKeyword);

    }


}
