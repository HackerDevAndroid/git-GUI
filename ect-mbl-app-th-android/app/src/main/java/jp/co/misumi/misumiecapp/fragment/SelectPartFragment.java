package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.ExpandAnimator;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.util.SpannableUtil;


/**
 * 型番検索画面
 */
public class SelectPartFragment extends BaseGetSpProductApi {

    //画面表示用
    private ResponseGetSpProduct mResponse;

    private LinearLayout mLayoutPartList;
    private TextView mTextPartList;

    //条件選択で使用する
    //スペック項目リスト
    public List<SpecItem> mSpecItemList;
    public List<View> mConditionViewList;

    //API
    private GetSpProductApi mGetSpProductApi;

    public Integer itemCount = 0;

    public boolean selectedPartList = true;
    public boolean selectedCondition = false;

    //型番選択ダイアログ用
    private MessageDialog mMessageDialog;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public SelectPartFragment() {

        mGetSpProductApi = new GetSpProductApi() {

            @Override
            protected String getScreenId() {
                //画面IDを返す
                return SelectPartFragment.this.getScreenId();
            }

            protected void onSuccess(ResponseGetSpProduct response) {

                //ダイアログ閉じる
                if (mMessageDialog != null) {
                    mMessageDialog.hide();
                    mMessageDialog = null;
                }

                //画面遷移する
                getFragmentController().stackFragment(new ItemDetailFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
            }

        };

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if (mResponse == null) {
            mResponse = (ResponseGetSpProduct) getParameter();
        }

        if (mSpecItemList == null) {
            //絞り込み条件の生成
            mSpecItemList = new ArrayList<>();

            //layoutCondition
            List<ResponseGetSpProduct.SpecInfo> specNameList = mResponse.mPartNumber.mSpecList;

            List<ResponseGetSpProduct.PartNumberInfo> partList = mResponse.mPartNumber.mPartNumberList;

            for (int specIndex = 0; specIndex < specNameList.size(); ++specIndex) {

                String specName = specNameList.get(specIndex).specName;
                String specUnit = specNameList.get(specIndex).specUnit;

                SpecItem specItem = new SpecItem();
                specItem.specIndex = specIndex;
                specItem.specName = specName;
                specItem.specUnit = specUnit;

                //型番候補を舐めて選択条件をリストアップする
                for (ResponseGetSpProduct.PartNumberInfo info : partList) {

                    //条件リストの件数が不一致なので無効扱い
                    if (info.specValueList.size() != specNameList.size()) {
                        continue;
                    }

                    String specValue = info.specValueList.get(specIndex);
                    specItem.addSpecValue(specValue);
                }

                mSpecItemList.add(specItem);

//				//スペック項目リスト最大件数を20件
//				if (mSpecItemList.size() >= AppConst.PART_NUMBER_SPEC_LIST_COUNT) {
//					break;
//				}
            }
        }


    }


    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflateLayout(inflater, R.layout.fragment_select_part, container, false);

        final LinearLayout layoutCondition = (LinearLayout) rootView.findViewById(R.id.layoutCondition);
        final LinearLayout layoutPartList = (LinearLayout) rootView.findViewById(R.id.layoutPartList);
        layoutPartList.setVisibility(View.GONE);
        layoutCondition.setVisibility(View.VISIBLE);

        final View textCondition = rootView.findViewById(R.id.textCondition);
        final View textPartList = rootView.findViewById(R.id.textPartList);

        final TextView tabCondition = (TextView) rootView.findViewById(R.id.textCondition);
        final TextView tabPartList = (TextView) rootView.findViewById(R.id.textPartList);

        final TextView seriesName = (TextView) rootView.findViewById(R.id.textSeriesName);

        if (android.text.TextUtils.isEmpty(mResponse.seriesName)) {
            seriesName.setVisibility(View.GONE);
        } else {
            seriesName.setVisibility(View.VISIBLE);
            seriesName.setText(mResponse.seriesName);
        }

        setBackground(tabCondition, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_w));
        setBackground(tabPartList, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_g));

        tabCondition.setTextColor(getResources().getColor(R.color.color_text_default));
        tabPartList.setTextColor(getResources().getColor(R.color.color_text_white));

        final View leftLine = rootView.findViewById(R.id.tabLineLeft);
        final View rightLine = rootView.findViewById(R.id.tabLineRight);
        leftLine.setVisibility(View.VISIBLE);
        rightLine.setVisibility(View.INVISIBLE);


        textCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutPartList.setVisibility(View.GONE);
                layoutCondition.setVisibility(View.VISIBLE);

                setBackground(tabCondition, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_w));
                setBackground(tabPartList, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_g));

                tabCondition.setTextColor(getResources().getColor(R.color.color_text_default));
                tabPartList.setTextColor(getResources().getColor(R.color.color_text_white));

                leftLine.setVisibility(View.VISIBLE);
                rightLine.setVisibility(View.INVISIBLE);

                selectedPartList = false;
                selectedCondition = true;

                setTotalCountColor(itemCount, false);

            }
        });

        textPartList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //タブから下
                layoutCondition.setVisibility(View.GONE);
                layoutPartList.setVisibility(View.VISIBLE);

                //タブ背景
                setBackground(tabCondition, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_g));
                setBackground(tabPartList, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_w));

                //見出しテキストカラー
                tabCondition.setTextColor(getResources().getColor(R.color.color_text_white));
                tabPartList.setTextColor(getResources().getColor(R.color.color_text_default));

                //タブ下線
                leftLine.setVisibility(View.INVISIBLE);
                rightLine.setVisibility(View.VISIBLE);

                //ステータス
                selectedPartList = true;
                selectedCondition = false;
//					textCondition.setSelected(false);
//					textPartList.setSelected(true);

                setTotalCountColor(itemCount, true);

            }
        });


        //条件選択エリア
        //条件リセット
        rootView.findViewById(R.id.viewReset).findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO:条件リセット
//                showToast("TODO:条件リセット: " + mSpecItemList.size());

                for (SpecItem specItem : mSpecItemList) {
                    specItem.resetSelectIndex();
                }

                for (View view : mConditionViewList) {
                    ((TextView) view).setText(R.string.part_number_select_label);
                }
                doRefreshPartList();
            }
        });

        //layoutCondition
        List<ResponseGetSpProduct.PartNumberInfo> partList = mResponse.mPartNumber.mPartNumberList;
        mConditionViewList = new ArrayList<>();

        //スペック項目リスト最大件数を20件
        for (int specIndex = 0; (specIndex < mSpecItemList.size()) && (specIndex < AppConst.PART_NUMBER_SPEC_LIST_COUNT); ++specIndex) {
            final SpecItem specItem = mSpecItemList.get(specIndex);
            String selectValue = specItem.getSelectValue();
            if (selectValue == null) {
                selectValue = (String) getText(R.string.part_number_select_label);
            }

            //
            View subView = inflateLayout(inflater, R.layout.include_item_spec_select, layoutCondition, false);

            TextView tv = (TextView) subView.findViewById(R.id.textView1);
            String str = specItem.specName;

            if (!android.text.TextUtils.isEmpty(specItem.specUnit)) {
                str += specItem.specUnit;
            }
            tv.setText(str);

            //
            TextView buttonSelect = (TextView) subView.findViewById(R.id.textView2);
            buttonSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyboard();

                    doSelectCondition(v, specItem);
                }
            });

            mConditionViewList.add(buttonSelect);
            buttonSelect.setText(selectValue);
            layoutCondition.addView(subView);
        }


        //
        mLayoutPartList = (LinearLayout) rootView.findViewById(R.id.layoutPartList);
        mTextPartList = (TextView) rootView.findViewById(R.id.textPartList);


        //型番候補　条件で動的に追加
        doRefreshPartList();

        {
            //タブから下
            layoutCondition.setVisibility(View.GONE);
            layoutPartList.setVisibility(View.VISIBLE);

            //タブ背景
            setBackground(tabCondition, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_g));
            setBackground(tabPartList, getResources().getDrawable(R.drawable.text_frame_black_1dp_tab_w));

            //見出しテキストカラー
            tabCondition.setTextColor(getResources().getColor(R.color.color_text_white));
            tabPartList.setTextColor(getResources().getColor(R.color.color_text_default));

            //タブ下線
            leftLine.setVisibility(View.INVISIBLE);
            rightLine.setVisibility(View.VISIBLE);

            //ステータス
            selectedPartList = true;
            selectedCondition = false;
            textCondition.setSelected(false);
            textPartList.setSelected(true);

            setTotalCountColor(itemCount, true);
        }
        return rootView;
    }


    //TODO:後にもっと簡略化する
    private void addExpandAnimator(View triggerView, final View viewSlideSwitch, View containerView, TextView tv, View rootView, String str1, String str2) {

        final ExpandAnimator animator = createAnimator(containerView, tv, str1, str2);

        final TextView btn = (TextView) rootView.findViewById(R.id.buttonMoreLabel);

        triggerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                if (!animator.isAnimating()) {
                    if (animator.isExpand()) {

                        animator.unexpand();
                        viewSlideSwitch.setSelected(false);
                        btn.setText(R.string.part_number_select_more);
                    } else {

                        animator.expand();
                        viewSlideSwitch.setSelected(true);
                        btn.setText(R.string.part_number_select_minus);
                    }
                }
            }
        });
    }


    private ExpandAnimator createAnimator(View containerView, final TextView tv, final String str1, final String str2) {


        ExpandAnimator animator = new ExpandAnimator(containerView, new ExpandAnimator.OnAnimationListener() {
            @Override
            public void onExpanded(ExpandAnimator e) {
            }

            @Override
            public void onStartExpand(ExpandAnimator e) {

                tv.setSingleLine(false);
                tv.setText(str1);
            }

            @Override
            public void onStartUnexpand(ExpandAnimator e) {
            }

            @Override
            public void onUnexpanded(ExpandAnimator e) {

                tv.setSingleLine(true);
                tv.setMaxLines(1);
                tv.setText(str2);
            }
        });

        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setWrapContent(true);
        return animator;
    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.SelectPart;
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mGetSpProductApi.close();

        super.onPause();
    }


    //条件選択で使用する
    //スペック項目リスト
    private static class SpecItem extends DataContainer {

        //選択した条件の項目番号
        public int selectIndex;

        //自分の配列番号
        public int specIndex;
        //スペック項目名
        public String specName;
        //スペック項目単位
        public String specUnit;


        //スペック項目値リスト
        public List<String> specValueList;

        public SpecItem() {

            specValueList = new ArrayList<>();
            selectIndex = -1;    //未選択
        }

        public void setSelectIndex(int selectIndex) {

            this.selectIndex = selectIndex;
        }

        public void resetSelectIndex() {

            this.selectIndex = -1;
        }

        public String getSelectValue() {

            if (selectIndex < 0) {
                return null;
            }
            return specValueList.get(selectIndex);
        }

        public void addSpecValue(String value) {

            if (value == null || value.isEmpty()) {
                return;
            }

            if (specValueList.contains(value)) {
                return;
            }

            //追加
            specValueList.add(value);

            //昇順でソート
            //2015/11/09 ソート処理をしない
//			Collections.sort(specValueList);
        }
    }


    //条件選択ダイアログを表示
    private void doSelectCondition(final View clickedView, final SpecItem specItem) {

        final MessageDialog messageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
            @Override
            public void onDialogResult(Dialog dlg, View view, int which) {
            }
        });


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_condition_layout, null, false);

        //項目を動的に追加する
        LinearLayout itemListLayout = (LinearLayout) view.findViewById(R.id.itemListLayout);
        itemListLayout.removeAllViews();

        View childView;

        int index;

        for (index = 0; index < specItem.specValueList.size() + 1; index++) {

            if (index == 0) {
                TextView tv;
                childView = inflateLayout(inflater, R.layout.list_item_choice_item, itemListLayout, false);
                tv = (TextView) childView.findViewById(R.id.textView1);
                tv.setText(getText(R.string.part_number_not_select_label));

                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        messageDialog.hide();

                        ((TextView) clickedView).setText(getText(R.string.part_number_select_label));

                        specItem.resetSelectIndex();
                        doRefreshPartList();
                    }
                });

                itemListLayout.addView(childView);

            } else {

                final int indexF = index;

                TextView tv;
                childView = inflateLayout(inflater, R.layout.list_item_choice_item, itemListLayout, false);
                tv = (TextView) childView.findViewById(R.id.textView1);
                tv.setText(specItem.specValueList.get(index - 1));

                //選択ボタン
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        messageDialog.hide();

                        specItem.setSelectIndex(indexF - 1);

                        ((TextView) clickedView).setText(specItem.getSelectValue());

                        doRefreshPartList();
                    }
                });

                itemListLayout.addView(childView);
            }
        }

        messageDialog.show(view, 0, R.string.dialog_button_cancel);


    }

    private void doRefreshPartList() {

        itemCount = 0;

        List<ResponseGetSpProduct.PartNumberInfo> partList = mResponse.mPartNumber.mPartNumberList;

        final LinearLayout layoutPartList = mLayoutPartList;

        final LayoutInflater inflater = getActivity().getLayoutInflater();


        //型番候補　条件で動的に追加
        layoutPartList.removeAllViews();

        for (final ResponseGetSpProduct.PartNumberInfo info : partList) {

            if (info.specValueList.size() != mSpecItemList.size()) {
                continue;
            }

            boolean isMatch = true;
            for (int specIndex = 0; specIndex < mSpecItemList.size(); ++specIndex) {

                SpecItem specItem = mSpecItemList.get(specIndex);
                String selectValue = specItem.getSelectValue();
                if (selectValue == null) {
                    continue;
                }

                String specValue = info.specValueList.get(specIndex);
                if (selectValue.equals(specValue)) {
                    continue;
                }

                isMatch = false;
                break;
            }

            if (!isMatch) {
                continue;
            }

            ++itemCount;

            final View subView = inflateLayout(inflater, R.layout.include_item_part, layoutPartList, false);


            View slideTrigger = subView.findViewById(R.id.buttonMore);
            LinearLayout slideContents = (LinearLayout) subView.findViewById(R.id.layoutSlideContents);
            View slideSwitch = subView.findViewById(R.id.buttonMore);

            slideSwitch.setSelected(false);

            TextView tv;

            tv = (TextView) subView.findViewById(R.id.textView1);
            tv.setText(info.partNumber);


            int slideItemCount = 0;
            String str1 = "";
            String str2 = "";
            String str3 = "";

            if (info.specValueList.size() == mSpecItemList.size()) {

                for (int idx = 0; idx < mSpecItemList.size(); ++idx) {

                    String str = mSpecItemList.get(idx).specName;
                    if (android.text.TextUtils.isEmpty(str)) {
                        continue;
                    }

                    String value = info.specValueList.get(idx);
                    if (android.text.TextUtils.isEmpty(value)) {
                        continue;
                    }

                    if (!android.text.TextUtils.isEmpty(mSpecItemList.get(idx).specUnit)) {
                        str += mSpecItemList.get(idx).specUnit;
                    }

                    if (slideItemCount < 1) {
                        str1 += str + "：" + value;
                    }

                    str2 += str + "：" + value + " \n";
                    ++slideItemCount;

                    if (slideItemCount > 1) {
                        str3 += str + "：" + value + "\n";
                    }
                }
            }

            tv = (TextView) subView.findViewById(R.id.textView3);
            tv.setText(str3);

            //textView2これを表示切替にする
            tv = (TextView) subView.findViewById(R.id.textView2);
            tv.setText(str2);

            //表示無し
            if (slideItemCount < 1) {

                slideSwitch.setVisibility(View.INVISIBLE);

            } else {

                addExpandAnimator(slideTrigger, slideSwitch, slideContents, tv, subView, str1, str2);
            }


            View buttonSelect = subView.findViewById(R.id.buttonSelect);
            buttonSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyboard();

                    //型番選択
                    mMessageDialog = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                doSpProduct(info);
                                //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
//                                JSONObject object=new JSONObject();
//                                try {
//                                    object.put("partNumber",info.partNumber);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_SELECT_MODLE, info.partNumber);
                                //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
                            } else {
                                //閉じる
                                mMessageDialog.hide();
                            }
                        }
                    }).setAutoClose(false);

                    //型番確定確認ダイアログ→型番は太字
                    String str;
                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    SpannableString ss;
                    str = (String) getText(R.string.part_number_select_dialog_msg_confirm);
                    ss = SpannableUtil.newSpannableString(str, 17, false, false);
                    ssb.append(ss);

                    if (!android.text.TextUtils.isEmpty(info.partNumber)) {
                        str = info.partNumber;
                        ss = SpannableUtil.newSpannableString(str, 17, false, true);
                        ssb.append(ss);
                    }
                    ssb.append("\n");
                    ssb.append(getText(R.string.part_number_select_dialog_msg));

                    mMessageDialog.show(ssb, R.string.part_number_select_dialog_confirm, R.string.part_number_select_dialog_cancel);
                }
            });

            layoutPartList.addView(subView);
        }


        //0件UI
        if (itemCount == 0) {

            View subView = inflateLayout(inflater, R.layout.include_item_text_notfound, layoutPartList, false);

            TextView tv;
            tv = (TextView) subView.findViewById(R.id.textView1);
            tv.setText(getText(R.string.part_number_select_notfount));

            layoutPartList.addView(subView);
        }

        //候補件数
//		mTextPartList.setText(String.format(getText(R.string.part_number_select_tab_partlist).toString(), itemCount));
//        mTextPartList.setText("型番の候補1000000000000000件");

        setTotalCountColor(itemCount, false);
    }


    private void doSpProduct(ResponseGetSpProduct.PartNumberInfo itemInfo) {

        hideKeyboard();

        String completeType = "4";
        String seriesCode = mResponse.seriesCode;
        String innerCode = itemInfo.innerCode;
        String partNumber = itemInfo.partNumber;
        Integer quantity = 1;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }

    public void setBackground(View v, Drawable d) {
        int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //API LEVEL 16以下の時。
            v.setBackgroundDrawable(d);
        } else {
            //API LEVEL 16以上の時。
            v.setBackground(d);
        }
    }

    private void setTotalCountColor(Integer itemCount, boolean status) {

        if (status) {
            String str;
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString ss;

            ssb.append(getText(R.string.part_number_select_tab_partlist_front)).append("");
            str = itemCount.toString();
            ss = SpannableUtil.newSpannableString(str, 15, true, true);
            ssb.append(ss);
            ssb.append(getText(R.string.part_number_select_tab_partlist_period)).append("）");
            mTextPartList.setText(ssb);
        } else {
/*
            mTextPartList.setText(String.format(getText(R.string.part_number_select_tab_partlist).toString(), itemCount));
*/

            String str;
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString ss;

            ssb.append(getText(R.string.part_number_select_tab_partlist_front)).append("");
            str = itemCount.toString();
            ss = SpannableUtil.newSpannableString(str, 12, 0xFFFFFFFF, false);
            ssb.append(ss);
            ssb.append(getText(R.string.part_number_select_tab_partlist_period)).append("）");
            mTextPartList.setText(ssb);
        }

    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.SelectPart;
    }
}
