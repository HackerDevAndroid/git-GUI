package jp.co.misumi.misumiecapp.autocomplete;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.SearchSuggest;
import jp.co.misumi.misumiecapp.util.SpannableUtil;


/**
 * AutoCompleteProcess
 */
public class AutoCompleteProcess {

    private Context context;
    private AutoCompleteTextView textView;
    private Adapter adapter;
    private SuggestManager suggestManager;
    private String keywordTitle;
    private String partsNumTitle;
    private KeyEnter callback;

    private String beforeText;

    public interface KeyEnter{
        void codeEnter();
    }

    /**
     * FilterType
     */
    public enum FilterType{
        keyword_title,
        keyword_data,
        divider_line,
        partsnum_title,
        partsnum_data,
    }


    /**
     * AutoCompleteProcess
     * @param context
     * @param textView
     */
    public AutoCompleteProcess(Context context, AutoCompleteTextView textView, KeyEnter callback){
        this.context = context;
        this.textView = textView;
        this.callback = callback;
        this.textView.addTextChangedListener(textWatcher);
        adapter = new Adapter(new ArrayList<FilterObject>());
        textView.setAdapter(adapter);
        textView.setThreshold(1);

        suggestManager = new SuggestManager(context);
        suggestManager.setListener(new SuggestManager.SuggestManagerListener() {
            @Override
            public void onResult(String keyword, SearchSuggest searchSuggest) {
                onGetSearchSuggest(keyword, searchSuggest);
            }
        });

        beforeText = "";
        keywordTitle = context.getResources().getString(R.string.autocomplete_keyword_title);
        partsNumTitle = context.getResources().getString(R.string.autocomplete_category_title);


        // 文字決定時のリスナーコール
        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && i == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) AutoCompleteProcess.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    AutoCompleteProcess.this.callback.codeEnter();
                    return true;
                }
                return false;
            }
        });

    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.length() == 0){
                beforeText = "";
                suggestManager.requestClearSuggest();
                return;
            }
            if (!text.equals(beforeText)) {
                AppLog.d("afterTextChanged=" + text);
                beforeText = text;
                suggestManager.requestSuggest(text, AppConst.SUGGEST_MAX_COUNT);
            }
        }
    };

    /**
     * onGetSearchSuggest
     * @param keyword
     * @param searchSuggest
     */
    private void onGetSearchSuggest(String keyword, SearchSuggest searchSuggest){
        AppLog.d("onGetSearchSuggest keyword=" + keyword);

		boolean hasKeywordList = false;
		boolean hasPartNumberList = false;

        if (searchSuggest.keywordList != null) {
			if (searchSuggest.keywordList.size() > 0) {
				hasKeywordList = true;
			}
		}

        if (searchSuggest.partNumberList != null) {
			if (searchSuggest.partNumberList.size() > 0) {
				hasPartNumberList = true;
			}
		}


        List<FilterObject> list = new ArrayList<>();

        if (hasKeywordList) {
            list.add(new FilterObject(FilterType.keyword_title, keywordTitle, null));
            for (String str : searchSuggest.keywordList) {
                list.add(new FilterObject(FilterType.keyword_data, str, null));
            }

	        if (hasPartNumberList) {
		        list.add(new FilterObject(FilterType.divider_line, null, null));
			}
        }

        if (hasPartNumberList) {
            list.add(new FilterObject(FilterType.partsnum_title, partsNumTitle, null));
            for (SearchSuggest.PartNumber part : searchSuggest.partNumberList) {

				String str = part.partNumber;
				if (android.text.TextUtils.isEmpty(str)) {
					str = "";
				}

				if (android.text.TextUtils.isEmpty(part.brandName)) {
					part.brandName = "";
				}
				str += " ["+ part.brandName +"]";
                list.add(new FilterObject(FilterType.partsnum_data, str, part));
            }
        }

        adapter = (Adapter) textView.getAdapter();
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * FilterObject
     */
    public class FilterObject{
        public FilterType type;
        public String suggestString;	// 画面表示用
        public SearchSuggest.PartNumber partNumber;

        public FilterObject(FilterType type, String suggestString, SearchSuggest.PartNumber partNumber){
            this.type = type;
            this.suggestString = suggestString;
            this.partNumber = partNumber;
        }

        @Override
        public String toString() {
			if (partNumber == null) {
	            return suggestString;
			}

            return partNumber.partNumber;
        }
    }


    private class Adapter extends AutoCompleteAdapter{

        LayoutInflater layoutInflater;

        public Adapter(List filterList) {
            super(filterList);

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List filterList){
            clear();
            addAll(filterList);
        }

        @Override
        protected View createView(int position) {

			int resId = R.layout.search_suggest_row;
            View view = layoutInflater.inflate(resId, null);

			setSpannableString(position, view);

            return view;
        }

        @Override
        protected void refreshView(int position, View view) {

			setSpannableString(position, view);
        }


		//
		private void setSpannableString(int position, View view) {

            FilterObject filterObject = (FilterObject) getItem(position);

            TextView textView = (TextView) view.findViewById(R.id.textViewFilter);
            View viewDivider = view.findViewById(R.id.viewDivider);
            if (filterObject.type == FilterType.divider_line) {

				//区切り線
				textView.setVisibility(View.GONE);
				viewDivider.setVisibility(View.VISIBLE);
				return;
			}

			textView.setVisibility(View.VISIBLE);
			viewDivider.setVisibility(View.GONE);

	        String str = filterObject.suggestString;

	        SpannableStringBuilder ssb = new SpannableStringBuilder();

            if (filterObject.type == FilterType.partsnum_title ||
                    filterObject.type == FilterType.keyword_title) {

				//ヘッダ
		        SpannableString ss = SpannableUtil.newSpannableString(str, 0, false, true);
	            ssb.append(ss);

            } else if (filterObject.type == FilterType.partsnum_data) {

				//カテゴリ
		        SpannableString ss = SpannableUtil.newSpannableString(str, 0,
                        AutoCompleteProcess.this.context.getResources().getColor(R.color.color_suggest_category), false);

	            ssb.append(ss);

            } else {

				//その他
	            ssb.append(str);
			}

            textView.setText(ssb);
		}


        @Override
        protected boolean isShowData(CharSequence input, Object obj) {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            FilterObject filterObject = (FilterObject) getItem(position);
            if (filterObject == null) {
                return true;
            }

            return !(filterObject.type == FilterType.partsnum_title ||
                    filterObject.type == FilterType.divider_line ||
                    filterObject.type == FilterType.keyword_title);
        }
    }
}

