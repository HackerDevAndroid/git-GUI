package jp.co.misumi.misumiecapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.ExpandAnimator;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.GoogleAnalytics;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.SessionRequiredDialog;
import jp.co.misumi.misumiecapp.api.ApiAccessWrapper;
import jp.co.misumi.misumiecapp.api.ApiBuilder;
import jp.co.misumi.misumiecapp.api.ShareSaveApi;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.ErrorList;
import jp.co.misumi.misumiecapp.data.ExpressInfo;
import jp.co.misumi.misumiecapp.data.GetCart;
import jp.co.misumi.misumiecapp.data.PriceInfo;
import jp.co.misumi.misumiecapp.data.ResponseGetMyComponents;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.ShareDetail;
import jp.co.misumi.misumiecapp.data.VolumeDiscount;
import jp.co.misumi.misumiecapp.header.HeaderView;
import jp.co.misumi.misumiecapp.header.MainHeader;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.ClipBoard;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.MsmFormat;
import jp.co.misumi.misumiecapp.util.PicassoUtil;
import jp.co.misumi.misumiecapp.util.ShareUtil;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewBgUtil;
import jp.co.misumi.misumiecapp.widget.IndicatorView;


//import android.widget.LinearLayout.LayoutParams;

/**
 * アイテム詳細画面
 */
public class ItemDetailFragment extends BaseGetSpProductApi {

    //画面表示用
    private ResponseGetSpProduct mResponse;

    // The items to be displayed in the ViewPager
    private List<String> mItems = new ArrayList<>();

    //API
    private AddToCartApi mAddToCartApi;
    private AddMyPartApi mAddMyPartApi;
    private GetSpProductApi mGetSpProductApi;
    private RefreshCartApi mRefreshCartApi;
    private RefreshMyPartApi mRefreshMyPartApi;

    //Ｚストークの文字列 メモ："0Z"が正しい。（API設計書の記載はバグ）
    private static final String EXPRESS_TYPE_Z = "0Z";
    private static final int MAX_PICTURE = 10;

    //ダイアログ
    private MessageDialog mMessageDialogGoCart;
    private MessageDialog mMessageDialogGoMyPart;

    //--ADD NT-LWL 17/05/19 Share FR -
    // 分享保存数据api
    private ShareSaveApi mShareSaveApi = new ShareSaveApi() {
        @Override
        protected String getScreenId() {
            return getSaicataId();
        }
    };
    //--ADD NT-LWL 17/05/19 Share TO -

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ItemDetailFragment() {

        mAddToCartApi = new AddToCartApi();
        mAddMyPartApi = new AddMyPartApi();

        mRefreshCartApi = new RefreshCartApi();
        mRefreshMyPartApi = new RefreshMyPartApi();

        mGetSpProductApi = new GetSpProductApi() {

            @Override
            protected String getScreenId() {
                //画面IDを返す
                return ItemDetailFragment.this.getScreenId();
            }

            protected void onSuccess(ResponseGetSpProduct response) {

                mResponse = response;
                makeDataView(getView());
                //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
                //警告表示
                String errorCode = "success";
                ErrorList.ErrorInfo errorInfo = null;
                if (response.errorList != null) {
                    for (ErrorList.ErrorInfo info : response.errorList.ErrorInfoList) {

                        //エラータイプ3だけ有効
                        if (!("3".equals(info.errorType))) {
                            continue;
                        }

                        if (info.errorCode == null) {
                            continue;
                        }

                        //大小比較
                        if (errorCode.compareTo(info.errorCode) >= 0) {
                            continue;
                        }

                        String errorStr = info.getErrorMessage(getScreenId());
                        if (android.text.TextUtils.isEmpty(errorStr)) {
                            continue;
                        }

                        errorInfo = info;
                        if (!TextUtils.isEmpty(errorInfo.errorCode)) {
                            errorCode = errorInfo.errorCode;
                        }
                    }
                }
                // 型番+数量+errorCode
                JSONObject object = new JSONObject();
                try {
                    object.put("partNumber", mPartNumber);
                    object.put("quantity", mQuantity);
                    object.put("errorCode", errorCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = getStringBuilder(object);
//				String lable = object.toString();
                GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_CONFIRM_PRICE, sb.toString());
                //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
            }

            //-- ADD NT-LWL 17/03/22 AliPay Payment FR -
            @Override
            public void onResult(int responseCode, String result) {
                super.onResult(responseCode, result);
                if (responseCode != NetworkInterface.STATUS_OK) {
                    setSearchGA(responseCode);
                }
            }

            @Override
            protected void onLostSession(int responseCode, String result) {
                super.onLostSession(responseCode, result);
                setSearchGA(responseCode);
            }

            @Override
            protected void onNetworkError(int responseCode) {
                super.onNetworkError(responseCode);
                setSearchGA(responseCode);
            }

            private void setSearchGA(int responseCode) {
                // 型番+数量+errorCode
                String errorCode = responseCode + "";
                JSONObject object = new JSONObject();
                try {
                    object.put("partNumber", mPartNumber);
                    object.put("quantity", mQuantity);
                    object.put("errorCode", errorCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = getStringBuilder(object);
//				String lable = object.toString();
                GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_CONFIRM_PRICE, sb.toString());
            }
            //-- ADD NT-LWL 17/03/22 AliPay Payment TO -
        };
    }

    //-- ADD NT-LWL 17/03/31 AliPay Payment FR -
    @NonNull
    private StringBuilder getStringBuilder(JSONObject object) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("{\"partNumber\":");
            sb.append("\"");
            sb.append(object.get("partNumber"));
            sb.append("\",");

            sb.append("\"quantity\":");
            sb.append(object.get("quantity"));
            sb.append(",");

            sb.append("\"errorCode\":");
            sb.append("\"");
            sb.append(object.get("errorCode"));
            sb.append("\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb;
    }
    //-- ADD NT-LWL 17/03/31 AliPay Payment TO -

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if (mResponse == null) {

            //エラー避け
            if (getParameter() instanceof ResponseGetSpProduct) {
                mResponse = (ResponseGetSpProduct) getParameter();
            }
        }


        //エラー避け
        if (mResponse == null) {
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

        View rootView = inflateLayout(inflater, R.layout.fragment_item_detail, container, false);


        //デバグ用エラー避け
        if (mResponse == null) {
            return rootView;
        }


        makeView(rootView);

        makeDataView(rootView);

        return rootView;
    }


    protected void makeView(View rootView) {
    }


    protected void makeDataView(View rootView) {


        String completeType = mResponse.completeType;

        View vw;

        //SNS連携ボタン
        View imageShare = rootView.findViewById(R.id.imageShare);
        if (SubsidiaryCode.isJapan()) {
            imageShare.setVisibility(View.GONE);
        } else {
            //中国版は常時出す
            imageShare.setVisibility(View.VISIBLE);
            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //SNS連携
                    String seriesCode = mResponse.seriesCode;
                    String partNumber = mResponse.partNumber;

                    String url = ShareUtil.makeShareUrl(getContext(), "detail", null, seriesCode, partNumber);
                    ShareUtil.doShareUrl(getContext(), url);

                    AppLog.v("url = " + url);
                }
            });

            //--ADD NT-LWL 17/05/19 Share FR -
            // 此处为已选择型号的情况
            final View share = rootView.findViewById(R.id.share_has_pcode);
            ;
            // 显示选型后的分享按钮条件
            if ("4".equals(mResponse.completeType) || mResponse.hasPrice) {
                share.setVisibility(View.VISIBLE);
            } else {
                share.setVisibility(View.GONE);
            }
            View view = rootView.findViewById(R.id.viewShipDate);
            final EditText editQuantity = (EditText) rootView.findViewById(R.id.editQuantity);
            final TextView daysView = (TextView) view.findViewById(R.id.textView2);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ctype = mResponse.completeType;
                    if (AppConfig.getInstance().hasSessionId()) {
                        if (!TextUtils.isEmpty(ctype) && ctype.equals("4")) {
                            ctype = "5";// 选型已登录 需要特殊处理
                        }
                    }
                    // 传入分享数据
                    final ShareDetail shareDetail = new ShareDetail();
                    shareDetail.setCtype(ctype);
                    // 品牌名称
                    shareDetail.setBrandName(mResponse.brandName);
                    // 型号
                    shareDetail.setPcode(mResponse.partNumber);
                    // 商品code
                    shareDetail.setScode(mResponse.seriesCode);
                    // 商品名称
                    shareDetail.setSeriesName(mResponse.seriesName);
                    // 商品图片
                    if (!mResponse.productImageUrlList.isEmpty()) {
                        shareDetail.setImageUrl(mResponse.productImageUrlList.get(0));
                    }

                    // 判断是否已登录 且询价
                    if (AppConfig.getInstance().hasSessionId() && mResponse.mPrice.totalPrice != null) {
                        // 数量
                        shareDetail.setNumber(mResponse.mPrice.quantity + "");
                        // 总价
                        String totalPrice = Format.formatAmount(mResponse.mPrice.totalPrice);
                        shareDetail.setTotalPrice(totalPrice);
                        // 含税总价
                        String totalPriceIncludingTax = Format.formatAmount(mResponse.mPrice.totalPriceIncludingTax);
                        shareDetail.setTotalPriceIncludingTax(totalPriceIncludingTax);
                        // 单价
                        String unitPrice = Format.formatAmount(mResponse.mPrice.unitPrice);
                        shareDetail.setUnitPrice(unitPrice);
                        // 发货日
                        shareDetail.setDaysToShip(daysView.getText().toString());
                    } else {
                        shareDetail.setNumber("unconfirmed");
                        shareDetail.setTotalPrice("unconfirmed");
                        shareDetail.setTotalPriceIncludingTax("unconfirmed");
                        shareDetail.setUnitPrice("unconfirmed");
                        shareDetail.setDaysToShip("unconfirmed");
                    }
                    // 设置数据
                    mShareSaveApi.setShareDetail(shareDetail);

                    if (AppConfig.getInstance().hasSessionId() && mResponse.mPrice.totalPrice != null && !editQuantity.getText().toString().equals(shareDetail.getNumber())) {
                        // 弹出数量不一致提示

                        new MessageDialog(mParent, new MessageDialog.MessageDialogListener() {
                            @Override
                            public void onDialogResult(Dialog dlg, View view, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    // 选择分享平台
                                    ShareUtil.show(mParent, shareDetail, getSaicataId(), mShareSaveApi);
                                }
                            }
                        }).show(R.string.share_number_tip, R.string.share_continue, R.string.cancel);
                    } else {

                        // 选择分享平台
                        ShareUtil.show(mParent, shareDetail, getSaicataId(), mShareSaveApi);
                    }
                }
            });
            //--ADD NT-LWL 17/05/19 Share TO -
        }
        if (BuildConfig.hideWechatShare) {
            imageShare.setVisibility(View.GONE);
        }

        //
        TextView tv;
        tv = (TextView) rootView.findViewById(R.id.textSeriesName);
        setTextEmptyGone(tv, mResponse.seriesName);

        tv = (TextView) rootView.findViewById(R.id.textBrandName);
        setTextEmptyGone(tv, mResponse.brandName);

        tv = (TextView) rootView.findViewById(R.id.textCatchCopy);
        setTextEmptyGone(tv, mResponse.catchCopy);

		/*
        型番
		*/
        tv = (TextView) rootView.findViewById(R.id.textPartNumber);

        //型番確定
        if ("4".equals(completeType)) {
            String str;

            if (android.text.TextUtils.isEmpty(mResponse.partNumber)) {
                str = "";
            } else {
                str = mResponse.partNumber;
            }
            tv.setText(str);

        } else {

            //ＰＣで見てね表示制御
            boolean isUsePc = false;
            boolean isNoPart = false;

            //単純品判別
            if ("0".equals(mResponse.complexFlag)) {
                //レスポンスにPartNumberが存在して 51以上
                if (mResponse.hasPartNumber) {
                    if (mResponse.mPartNumber.totalCount == 0) {
                        isNoPart = true;
                    } else if (mResponse.mPartNumber.totalCount >= AppConst.PART_NUMBER_LIST_REQUEST_COUNT + 1) {
                        isUsePc = true;
                    }
                }
            } else {

                //単純品以外は無条件
                isUsePc = true;
            }

            if (isUsePc) {

                //ＰＣで見てね文言
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                String str = getResourceString(R.string.item_detail_part_pc);
                SpannableString ss = SpannableUtil.newSpannableString(str, 0, true, false);
                ssb.append(ss);
                tv.setText(ssb);
            } else {

                //型番選択してね文言
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                String str;
                if (isNoPart) {
                    str = getResourceString(R.string.item_detail_no_part);
                } else {
                    str = getResourceString(R.string.item_detail_part_select);
                }
                SpannableString ss = SpannableUtil.newSpannableString(str, 0, true, false);
                ssb.append(ss);
                tv.setText(ssb);
            }
        }


        //商品画像
        mItems.clear();
        if (mResponse.productImageUrlList != null) {
            for (String str : mResponse.productImageUrlList) {
                if (android.text.TextUtils.isEmpty(str)) {
                    continue;
                }

                mItems.add(str);

                if (mItems.size() >= MAX_PICTURE) {
                    break;
                }
            }
        }

        if (mItems.size() > 0) {

            // ViewPager を生成
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
            viewPager.setAdapter(mPagerAdapter);

            IndicatorView indicator = (IndicatorView) rootView.findViewById(R.id.indicator);
            indicator.setViewPager(viewPager);
            indicator.setViewPagerEvent(viewPager);
            indicator.setPosition(0);

            rootView.findViewById(R.id.layoutPicture).setVisibility(View.VISIBLE);


//		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
//		//viewPagerに高さをセット
//		LayoutParams lp = viewPager.getLayoutParams();
//		lp.width = (int)(width * 0.8f);
//		lp.height = (int)(width * 0.8f);

        } else {

            rootView.findViewById(R.id.layoutPicture).setVisibility(View.GONE);
        }


        //アイコン表示
        LinearLayout layoutIcon = (LinearLayout) rootView.findViewById(R.id.layoutIcon);
        layoutIcon.removeAllViews();

        int iconCount = 0;
        boolean hasRecommend = false;
        boolean hasSale = false;
        boolean hasEconomy = false;
        boolean hasCvalue = false;
        boolean hasSlideDisc = false;

        if ("1".equals(mResponse.recommendFlag)) {
            hasRecommend = true;
            ++iconCount;
        }

        String campainEndDateStr = MsmFormat.convertCampainEndDate(getContext(), mResponse.campainEndDate);
        if (!android.text.TextUtils.isEmpty(campainEndDateStr)) {
            hasSale = true;
            ++iconCount;
        }

        if ("1".equals(mResponse.gradeType)) {
            hasEconomy = true;
            ++iconCount;
        }

        if ("1".equals(mResponse.cValueFlag)) {
            hasCvalue = true;
            ++iconCount;
        }

        if ("1".equals(mResponse.volumeDiscountFlag)) {
            hasSlideDisc = true;
            ++iconCount;
        }

        if (mResponse.iconList.size() > 0) {
            iconCount += mResponse.iconList.size();
        }
/*
	public String recommendFlag;			//○おすすめフラグ
	public String campainEndDate;			//○キャンペーン終了日
	public String gradeType;				//　グレードタイプ
	public String cValueFlag;				//○C-Valuフラグ
	public String volumeDiscountFlag;		//○スライド値引きフラグ
	public List<String> iconList = new ArrayList<>();			//○アイコンリスト
*/

        if (iconCount == 0) {
            layoutIcon.setVisibility(View.GONE);
        } else {
            layoutIcon.setVisibility(View.VISIBLE);

            int iconAddCount = 0;
            View viewIcons = null;

            if (hasRecommend) {
                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 1);
                textIcon.setText(getResourceString(R.string.item_detail_icon_recommend));
            }
            if (hasSale) {
                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 2);
                textIcon.setText(campainEndDateStr);
            }
            if (hasEconomy) {
                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 1);
                textIcon.setText(getResourceString(R.string.item_detail_icon_grade));
            }
            if (hasCvalue) {
                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 1);
                textIcon.setText(getResourceString(R.string.item_detail_icon_cvalue));
            }
            if (hasSlideDisc) {
                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 3);
                textIcon.setText(getResourceString(R.string.item_detail_icon_slide));
            }

            //アイコン配列
            for (String iconStr : mResponse.iconList) {

                viewIcons = getIconLayoutView(iconAddCount, layoutIcon, viewIcons);
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                setIconDesign(textIcon, 3);
                textIcon.setText(iconStr);
            }

            //余りを不可視にする
            while ((iconAddCount % 3) != 0) {
                TextView textIcon = getIconTextView(iconAddCount++, viewIcons);
                textIcon.setVisibility(View.INVISIBLE);
            }
        }


        //価格情報の表示を判別
        boolean dispPrice = false;
        if ("4".equals(completeType) || mResponse.hasPrice) {
            //型番確定、もしくはレスポンスに存在する場合
            dispPrice = true;
        }

        if (!dispPrice) {

            //価格エリア非表示
            rootView.findViewById(R.id.layoutUnitPrice).setVisibility(View.GONE);

            //数量スライド表とか
            rootView.findViewById(R.id.viewSlide).setVisibility(View.GONE);
            rootView.findViewById(R.id.viewExpress).setVisibility(View.GONE);
            rootView.findViewById(R.id.viewStokeZ).setVisibility(View.GONE);

        } else {

            //価格エリア表示
            rootView.findViewById(R.id.layoutUnitPrice).setVisibility(View.VISIBLE);

            final PriceInfo priceInfo = mResponse.mPrice;

            //数量
            final EditText editQuantity = (EditText) rootView.findViewById(R.id.editQuantity);
            editQuantity.setText("" + priceInfo.quantity);


            //合計
            {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                SpannableString ss;
                String str = null;

                if (priceInfo.totalPrice == null) {

                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                    ssb.append(str);
                } else {

                    str = Format.formatAmount(priceInfo.totalPrice);

                    ss = SpannableUtil.newSpannableString(str, 20, true, true);
                    ssb.append(ss);

                    //米国通貨書式対応
                    String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                    if (MsmFormat.isUsa(getContext())) {

                        ssb.insert(0, currencyCode);
                    } else {

                        ssb.append(currencyCode);
                    }

                }

                setIncludeItemText(rootView.findViewById(R.id.viewTotalPrice), getResourceString(R.string.item_detail_total_price), ssb, "");

            }

            //日本中国
            TextView textTotalPriceTax = (TextView) rootView.findViewById(R.id.textTotalPriceTax);

            if (SubsidiaryCode.isJapan()) {

//		        rootView.findViewById(R.id.viewTotalPriceTax).setVisibility(View.GONE);
                textTotalPriceTax.setVisibility(View.GONE);
            } else {

//		        rootView.findViewById(R.id.viewTotalPriceTax).setVisibility(View.VISIBLE);
                textTotalPriceTax.setVisibility(View.VISIBLE);

                String str = null;

                if (priceInfo.totalPriceIncludingTax == null) {

                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {

                    str = Format.formatAmount(priceInfo.totalPriceIncludingTax);

                    //米国通貨書式対応
                    String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                    if (MsmFormat.isUsa(getContext())) {

                        str = currencyCode + str;
                    } else {

                        str += currencyCode;
                    }

                    str = getResourceString(R.string.item_detail_total_price_wtax) + str;
                }

                textTotalPriceTax.setText(str);

//				setIncludeItemText(rootView.findViewById(R.id.viewTotalPriceTax), null, str, "");
            }


            //出荷日
            {
                String daysToShipStr = "";
                CharSequence daysToShipUnitStr = "";
                String shipTypeStr = "";

                daysToShipStr = MsmFormat.convertDaysToShip(getContext(), priceInfo.daysToShip);
                daysToShipUnitStr = MsmFormat.convertDaysToShipUnitWithSpaned(getContext(), priceInfo.daysToShip);
                shipTypeStr = MsmFormat.convertShipType(getContext(), priceInfo.shipType);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                String str;

                if (android.text.TextUtils.isEmpty(daysToShipStr)
                        && android.text.TextUtils.isEmpty(shipTypeStr)) {

                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                    ssb.append(str);

                }

                if (!android.text.TextUtils.isEmpty(shipTypeStr)) {

                    ssb.append(shipTypeStr);
                }

                if (!android.text.TextUtils.isEmpty(daysToShipStr)) {

                    ssb.append(daysToShipUnitStr);

                }

                setIncludeItemText(rootView.findViewById(R.id.viewShipDate), getResourceString(R.string.item_detail_ship), ssb, "");
            }


            //単価
            {
                String str = null;

                if (priceInfo.unitPrice == null) {

                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {

                    str = Format.formatAmount(priceInfo.unitPrice);

                    //米国通貨書式対応
                    String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                    if (MsmFormat.isUsa(getContext())) {

                        str = currencyCode + str;
                    } else {

                        str += currencyCode;
                    }

                }

                //入り数
                if (priceInfo.piecesPerPackage != null && priceInfo.piecesPerPackage != 0) {
                    str += String.format(getResourceString(R.string.item_detail_pack_quantity), Format.formatCount(priceInfo.piecesPerPackage));
                }

                setIncludeItemText(rootView.findViewById(R.id.viewUnitPrice), getResourceString(R.string.item_detail_unit_price), str, "");
            }

            //数量
            setQuantityListener(rootView, priceInfo);

            //警告表示
            String errorCode = "";
            ErrorList.ErrorInfo errorInfo = null;
            if (priceInfo.errorList != null) {
                for (ErrorList.ErrorInfo info : priceInfo.errorList.ErrorInfoList) {

                    //エラータイプ3だけ有効
                    if (!("3".equals(info.errorType))) {
                        continue;
                    }

                    if (info.errorCode == null) {
                        continue;
                    }

                    //大小比較
                    if (errorCode.compareTo(info.errorCode) >= 0) {
                        continue;
                    }

                    String errorStr = info.getErrorMessage(getScreenId());
                    if (android.text.TextUtils.isEmpty(errorStr)) {
                        continue;
                    }

                    errorInfo = info;
                    errorCode = errorInfo.errorCode;
                }
            }

            tv = (TextView) rootView.findViewById(R.id.textWarning);

            if (errorInfo == null) {

                tv.setVisibility(View.GONE);
            } else {

                tv.setVisibility(View.VISIBLE);
                tv.setText(errorInfo.getErrorMessage(getScreenId()));
            }

            final String errorCodeF = errorCode;


            //型番と数量をコピー
            vw = rootView.findViewById(R.id.buttonCopy);
            if (vw != null) {
                vw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //型番と数量をコピー
                        AppConfig config = AppConfig.getInstance();

                        String customerCode = "";
                        String loginId = "";

                        //ログイン状態なら得意先コードとログインID
                        if (AppConfig.getInstance().hasSessionId()) {
                            customerCode = config.getCustomerCode();
                            if (android.text.TextUtils.isEmpty(customerCode)) {
                                customerCode = "";
                            }
                            loginId = config.getLoginId();
                        }

                        //その時の入力状態の数値を使う様に仕様変更
                        //※空欄や０の時は数量１とみなす
                        String str = priceInfo.editedQuantity;
                        int qty = getQuantity(str);

                        String info = ""
                                + mResponse.partNumber + ","
                                + qty + ","
                                + customerCode + ","
                                + loginId + ","
                                + errorCodeF + "!";    //最後に！

                        //クリップボードにコピー
                        ClipBoard.setData(getContext(), info);

                        MessageDialog messageDialog = new MessageDialog(getContext(), null);

                        messageDialog.show(R.string.item_detail_copy_message,
                                0, R.string.dialog_button_close);
                    }
                });
            }


            //
            vw = rootView.findViewById(R.id.buttonAddCart);
            if (vw != null) {
                vw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //カートへ追加
                        doAddToCart(mResponse);
                    }
                });
            }

            //
            vw = rootView.findViewById(R.id.buttonAddMy);
            if (vw != null) {
                vw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //My部品表に追加
                        doAddMyPart(mResponse);
                    }
                });
            }


            //TODO:日本、中国
            //数量スライド
            setSlideListView(rootView.findViewById(R.id.viewSlide), getResourceString(R.string.item_detail_slide_label), priceInfo.mVolumeDiscountList);

            //緊急出荷
            setExpressListView(rootView.findViewById(R.id.viewExpress), getResourceString(R.string.item_detail_express_label), priceInfo.mExpressList);

            //早割30/早割25
            setStokeView(rootView.findViewById(R.id.viewStokeZ), getResourceString(R.string.item_detail_stokez_label), priceInfo.mExpressList);

        }


        //指定中の寸法
        setSpecListView(rootView.findViewById(R.id.viewSpec), getResourceString(R.string.item_detail_selected_label), mResponse.mSelectedSpecList, true);


        //基本情報
        setSpecListView(rootView.findViewById(R.id.viewInfo), getResourceString(R.string.item_detail_spec_label), mResponse.mStandardSpecInfo, false);


        //型番選択
        //単純品判別
        boolean dispSelectParts = false;
        if ("0".equals(mResponse.complexFlag)) {
            //レスポンスにPartNumberが存在して、totalCountが 1～50の時に表示
            if (mResponse.hasPartNumber) {
                int totalCount = mResponse.mPartNumber.totalCount;
                if (totalCount >= 1 && totalCount <= AppConst.PART_NUMBER_LIST_REQUEST_COUNT) {
                    dispSelectParts = true;
                }
            }
        }

        View layoutSelectParts = rootView.findViewById(R.id.layoutSelectParts);
        if (!dispSelectParts) {

            layoutSelectParts.setVisibility(View.GONE);
            rootView.findViewById(R.id.viewSelectPartsDiv).setVisibility(View.GONE);

            layoutSelectParts.setOnClickListener(null);

        } else {

            layoutSelectParts.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.viewSelectPartsDiv).setVisibility(View.VISIBLE);

            layoutSelectParts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doSearchPartNumber();
                }
            });
        }


    }


    private void setTextEmptyGone(TextView tv, String str) {

        if (android.text.TextUtils.isEmpty(str)) {
            tv.setText(null);
            tv.setVisibility(View.GONE);
            return;
        }

        tv.setText(str);
        tv.setVisibility(View.VISIBLE);
    }


    private View getIconLayoutView(int iconAddCount, LinearLayout layoutIcon, View viewIcons) {

        if ((iconAddCount % 3) == 0) {
            viewIcons = inflateLayout(mParent.getLayoutInflater(), R.layout.include_item_detail_icons, layoutIcon, false);
            layoutIcon.addView(viewIcons);

            return viewIcons;
        }

        return viewIcons;
    }


    private TextView getIconTextView(int iconAddCount, View viewIcons) {

        int pos = (iconAddCount % 3);
        int resId = 0;

        switch (pos) {
            case 0:
                resId = R.id.textIcon1;
                break;
            case 1:
                resId = R.id.textIcon2;
                break;
            case 2:
                resId = R.id.textIcon3;
                break;
        }

        return (TextView) viewIcons.findViewById(resId);
    }

    private void setIconDesign(TextView textIcon, int type) {

        textIcon.setBackgroundDrawable(null);

        switch (type) {
            case 1:
                textIcon.setTextColor(0xFFDD0000);
                textIcon.setBackgroundResource(R.drawable.text_frame_red_1dp);
                break;

            case 2:
                textIcon.setTextColor(0xFFFFFFFF);
                textIcon.setBackgroundResource(R.drawable.text_frame_red_fill);
                break;

            case 3:
                textIcon.setTextColor(0xFFFFFFFF);
                textIcon.setBackgroundColor(0xFF749D9D);
                break;
        }

        textIcon.setPadding(0, 0, 0, 0);

    }


    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.ItemDetail;
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void onPause() {

        mGetSpProductApi.close();
        mRefreshCartApi.close();
        mRefreshMyPartApi.close();
        mAddToCartApi.close();
        mAddMyPartApi.close();
        //--ADD NT-LWL 17/05/19 Share FR -
        mShareSaveApi.close();
        //--ADD NT-LWL 17/05/19 Share TO -

        super.onPause();
    }


    private void setSpecListView(View subView, String title, ArrayList<ResponseGetSpProduct.StandardSpecInfo> listData, boolean hasWarning) {

        // 指定中の仕様、基本情報スライド
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout slideTrigger = (LinearLayout) subView.findViewById(R.id.layoutSlideTrigger);
        final LinearLayout slideContents = (LinearLayout) subView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = subView.findViewById(R.id.imageSlideSwitch);

        TextView tv = (TextView) subView.findViewById(R.id.textSlideSwitch);
        tv.setText(title);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        if (listData == null || listData.size() == 0) {

            subView.setVisibility(View.GONE);
        } else {

            slideContents.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

            subView.setVisibility(View.VISIBLE);

            if (hasWarning) {
                //警告
                View viewWarning = inflateLayout(inflater, R.layout.include_item_spec_warning, slideContents, false);
                slideContents.addView(viewWarning);
            }

            //頭の線
            View viewDivider = inflateLayout(inflater, R.layout.include_item_divider, slideContents, false);
            slideContents.addView(viewDivider);

            //動的
            for (ResponseGetSpProduct.StandardSpecInfo info : listData) {
                View tableRow = inflateLayout(inflater, R.layout.include_item_text_14_19_spec, slideContents, false);

                TextView textView1 = (TextView) tableRow.findViewById(R.id.textView1);
                String str = "";

                if (!android.text.TextUtils.isEmpty(info.specName)) {
                    str += info.specName;
                }

                if (!android.text.TextUtils.isEmpty(info.specUnit)) {
                    str += info.specUnit;
                }
                textView1.setText(str);

                TextView textView3 = (TextView) tableRow.findViewById(R.id.textView3);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                SpannableString ss;

                if (android.text.TextUtils.isEmpty(info.specValue)) {
                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                    ssb.append(str);
                } else {
                    str = info.specValue;
                    ss = SpannableUtil.newSpannableString(str, 15, false, true);
                    ssb.append(ss);
                }
                textView3.setText(ssb);

                //強制背景色設定
                //include_item_text_14_19
                ViewBgUtil.requestLayout(tableRow, R.id.viewDiv, R.id.viewLeft, R.id.textView3);

                slideContents.addView(tableRow);
            }

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);

            slideContents.post(new Runnable() {

                @Override
                public void run() {

                    slideContents.getLayoutParams().height = 0;
                    slideContents.requestLayout();
                }
            });
        }
    }


    //数量スライド表
    private void setSlideListView(View subView, String title, ArrayList<VolumeDiscount> listData) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout slideTrigger = (LinearLayout) subView.findViewById(R.id.layoutSlideTrigger);
        final LinearLayout slideContents = (LinearLayout) subView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = subView.findViewById(R.id.imageSlideSwitch);

        TextView tv = (TextView) subView.findViewById(R.id.textSlideSwitch);
        tv.setText(title);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        if (SubsidiaryCode.isJapan()) {
        } else {

            //日本、中国
            subView.setVisibility(View.GONE);
            return;
        }

        if (listData.size() == 0) {

            subView.setVisibility(View.GONE);
        } else {

            slideContents.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

            subView.setVisibility(View.VISIBLE);

            //頭の線
            View viewDivider = inflateLayout(mParent.getLayoutInflater(), R.layout.item_acc_quantity_slide_header, slideContents, false);
            slideContents.addView(viewDivider);

            //動的
            for (VolumeDiscount info : listData) {
                View tableRow = inflateLayout(inflater, R.layout.item_acc_quantity_slide_row, slideContents, false);

                TextView textView1 = (TextView) tableRow.findViewById(R.id.textQuantity);
                String str = "";
                if (info.minQuantity == null) {
                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {
                    str = "" + info.minQuantity;
                    if (!info.minQuantity.equals(info.maxQuantity)) {
                        str += "～";
                        if (info.maxQuantity != null) {
                            str += info.maxQuantity;
                        }
                    }
                }
                textView1.setText(str);

                TextView textView2 = (TextView) tableRow.findViewById(R.id.textNormalPrice);
                if (info.unitPrice == null) {
                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {
                    str = Format.formatAmount(info.unitPrice);

                    //米国通貨書式対応
                    String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                    if (MsmFormat.isUsa(getContext())) {

                        str = currencyCode + str;
                    } else {

                        str += currencyCode;
                    }

                }

                textView2.setText(str);

                //出荷日
                TextView textView3 = (TextView) tableRow.findViewById(R.id.textDaysToShip);

                String daysToShipStr = "";
                String daysToShipUnitStr = "";

                daysToShipStr = MsmFormat.convertDaysToShip(getContext(), info.daysToShip);
                daysToShipUnitStr = MsmFormat.convertDaysToShipUnit2(getContext(), info.daysToShip);

                if (android.text.TextUtils.isEmpty(daysToShipStr)) {
                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {
                    str = daysToShipUnitStr;
                }
                textView3.setText(str);

                slideContents.addView(tableRow);
            }

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);

            slideContents.getLayoutParams().height = 0;
            slideContents.requestLayout();
        }
    }


    //緊急出荷
    private void setExpressListView(View subView, String title, ArrayList<ExpressInfo> listData) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout slideTrigger = (LinearLayout) subView.findViewById(R.id.layoutSlideTrigger);
        final LinearLayout slideContents = (LinearLayout) subView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = subView.findViewById(R.id.imageSlideSwitch);

        TextView tv = (TextView) subView.findViewById(R.id.textSlideSwitch);
        tv.setText(title);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        if (listData.size() == 0) {

            subView.setVisibility(View.GONE);
        } else {

            slideContents.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

            subView.setVisibility(View.VISIBLE);

            //頭の線
            View viewDivider = inflateLayout(inflater, R.layout.item_acc_express_shipment_header, slideContents, false);
            slideContents.addView(viewDivider);


            //日本、中国
            TextView textChargeTitle = (TextView) viewDivider.findViewById(R.id.textChargeTitle);
            if (SubsidiaryCode.isJapan()) {

            } else {

                textChargeTitle.setVisibility(View.INVISIBLE);
            }


            //動的
            for (ExpressInfo info : listData) {

                //Ｚストークは別枠で表示
                if (EXPRESS_TYPE_Z.equals(info.expressType)) {
                    continue;
                }

                View tableRow = inflateLayout(inflater, R.layout.item_acc_express_shipment_row, slideContents, false);

                String str = "";

                //
                String expressType = "";
                expressType = MsmFormat.convertExpressTypeFull(getContext(), info.expressType);
                TextView textView1 = (TextView) tableRow.findViewById(R.id.textExpressType);
                textView1.setText(expressType);

                //
                TextView textView2 = (TextView) tableRow.findViewById(R.id.textExpressDeadline);
                if (android.text.TextUtils.isEmpty(info.expressDeadline)) {
                    str = getResourceString(R.string.label_hyphen);    //ハイフン化
                } else {
                    str = info.expressDeadline;
                    str += getResourceString(R.string.item_detail_express_deadline);
                }
                textView2.setText(str);


                //日本、中国
                TextView textView3 = (TextView) tableRow.findViewById(R.id.textCharge);
                if (SubsidiaryCode.isJapan()) {

                    //前￥
                    if (info.charge == null) {
                        str = getResourceString(R.string.label_hyphen);    //ハイフン化
                    } else {

                        //2015/12/17 後ろ円に仕様変更
                        str = Format.formatAmount(info.charge);

                        //米国通貨書式対応
                        String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                        if (MsmFormat.isUsa(getContext())) {

                            str = currencyCode + str;
                        } else {

                            str += currencyCode;
                        }

                    }
                    textView3.setText(str);

                } else {

                    textView3.setVisibility(View.INVISIBLE);
                }

                slideContents.addView(tableRow);
            }

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);

            slideContents.getLayoutParams().height = 0;
            slideContents.requestLayout();
        }
    }

    //早割30/早割25
    private void setStokeView(View subView, String title, ArrayList<ExpressInfo> listData) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout slideTrigger = (LinearLayout) subView.findViewById(R.id.layoutSlideTrigger);
        final LinearLayout slideContents = (LinearLayout) subView.findViewById(R.id.layoutSlideContents);
        View slideSwitch = subView.findViewById(R.id.imageSlideSwitch);

        TextView tv = (TextView) subView.findViewById(R.id.textSlideSwitch);
        tv.setText(title);

        slideContents.removeAllViews();
        slideSwitch.setSelected(false);

        //動的
        ExpressInfo infoZ = null;

        //日本、中国
        if (SubsidiaryCode.isJapan()) {

            for (ExpressInfo info : listData) {

                //Ｚストークの有無
                if (EXPRESS_TYPE_Z.equals(info.expressType)) {
                    infoZ = info;
                }
            }

        } else {

        }

        if (infoZ == null) {

            subView.setVisibility(View.GONE);
        } else {

            slideContents.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

            subView.setVisibility(View.VISIBLE);

            //頭の線
            View viewDivider = inflateLayout(inflater, R.layout.item_acc_express_z_header, slideContents, false);


            //日本、中国
            TextView textView3 = (TextView) viewDivider.findViewById(R.id.textCharge);
            //前￥
            String str = "";

            if (infoZ.charge == null) {
                str = getResourceString(R.string.label_hyphen);    //ハイフン化
            } else {

                //2015/12/17 後ろ円に仕様変更
                str = Format.formatAmount(infoZ.charge);

                //米国通貨書式対応
                String currencyCode = MsmFormat.getCurrencyCodeWithLogin(getContext());
                if (MsmFormat.isUsa(getContext())) {

                    str = currencyCode + str;
                } else {

                    str += currencyCode;
                }
            }
            textView3.setText(str);


            slideContents.addView(viewDivider);

            addExpandAnimator(slideTrigger, slideSwitch, slideContents);

            slideContents.getLayoutParams().height = 0;
            slideContents.requestLayout();
        }

    }


    //アコーディオン処理
    private void addExpandAnimator(View triggerView, final View viewSlideSwitch, View containerView) {

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


    private final PagerAdapter mPagerAdapter = new PagerAdapter() {
        LayoutInflater mInflater;

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Just remove the view from the ViewPager
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Ensure that the LayoutInflater is instantiated
            if (mInflater == null) {
                mInflater = LayoutInflater.from(getContext());
            }

            // Inflate item layout for images
            View vw = inflateLayout(mInflater, R.layout.viewpager_item_image, container, false);
            final ImageView iv = (ImageView) vw.findViewById(R.id.imageView);
            View pv = vw.findViewById(R.id.progressView);

            // Load the image from it's content URI
            final String imageUrl = mItems.get(position);
            PicassoUtil.PicassoLoad(iv, pv, imageUrl);


            vw.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //NoImage時はタップで拡大しない処理
                    //読み込み完了識別タグ
                    boolean flag = (boolean) (iv.getTag(PicassoUtil.PICASSO_LOAD_KEY));

                    if (flag) {
                        doScaleImageDialog(imageUrl);
                    }
                }
            });


            // Add the view to the ViewPager
            container.addView(vw);
            return vw;
        }
    };

    private void doScaleImageDialog(String imageUrl) {

        MessageDialog messageDialog = new MessageDialog(getContext(), null);

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflateLayout(inflater, R.layout.dialog_scale_image_layout, null, false);

        messageDialog.show(view, 0, R.string.dialog_button_close);

        //画面サイズより大きい場合に伸びきらない対応
//		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        LayoutParams lp = view.getLayoutParams();
//		lp.width = (int)(width * 0.8f);		//横は横幅
        lp.height = (int) (height * 0.7f);    //縦は明示的に計算

        //
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        View pv = view.findViewById(R.id.progressView);

        PicassoUtil.PicassoLoad(iv, pv, imageUrl);
    }


    /**
     * 型番検索検索
     */
    private void doSearchPartNumber() {
        hideKeyboard();

        getFragmentController().stackFragment(new SelectPartFragment(), FragmentController.ANIMATION_SLIDE_IN, mResponse);

    }


    protected void setIncludeItemText(View subView, CharSequence str1, CharSequence str2, CharSequence str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);
        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);
        ((TextView) subView.findViewById(R.id.textView3)).setText(str3);
    }


    protected EditText getIncludeItemEdit(View subView) {

        if (subView instanceof EditText) {

            return (EditText) subView;
        }

        return (EditText) subView.findViewById(R.id.editText1);
    }

    protected String getIncludeItemEditString(View subView) {
        return getIncludeItemEdit(subView).getText().toString();
    }


    private void setQuantityListener(View childView, final PriceInfo itemInfo) {

        //数量更新
        final EditText editQuantity = (EditText) childView.findViewById(R.id.editQuantity);

        final View buttonMinus = childView.findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = editQuantity.getText().toString();

                if (android.text.TextUtils.isEmpty(str)) {

					/*
					数量入力欄が空欄の時の＋－ボタンの挙動について
					動作仕様
					・両方とも押せる
					・押した場合は0が入る
					*/
                    editQuantity.setText("0");
                } else {

                    int qty = getQuantity(str);
                    --qty;
                    if (qty < 1) {
                        qty = 1;
                    }

                    editQuantity.setText("" + qty);
                }
            }
        });

        final View buttonPlus = childView.findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = editQuantity.getText().toString();

                if (android.text.TextUtils.isEmpty(str)) {

					/*
					数量入力欄が空欄の時の＋－ボタンの挙動について
					動作仕様
					・両方とも押せる
					・押した場合は0が入る
					*/
                    editQuantity.setText("0");
                } else {

                    int qty = getQuantity(str);
                    if (qty < 0) {
                        qty = 0;
                    }
                    ++qty;
                    if (qty > 99999) {
                        qty = 99999;
                    }

                    editQuantity.setText("" + qty);
                }

            }
        });


        final View imageReload = childView.findViewById(R.id.imageReload);
        imageReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //価格チェック通信
                String str = editQuantity.getText().toString();
                int qty = getQuantity(str);

                doSpProduct(qty);
            }
        });


        //途中で編集している場合
        String quantity = itemInfo.editedQuantity;

        //EditText監視
        editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = s.toString();

                if (str.isEmpty()) {
                    str = "1";
                }
                //編集途中を保持
                itemInfo.editedQuantity = str;

                int qty = getQuantity(str);

                if (qty < 1) {
                    imageReload.setEnabled(false);
                } else {
                    imageReload.setEnabled(true);
                }

                if (qty >= 99999) {
                    buttonPlus.setEnabled(false);
                } else if (qty <= 1 && !str.isEmpty()) {
                    buttonMinus.setEnabled(false);
                } else {
                    buttonPlus.setEnabled(true);
                    buttonMinus.setEnabled(true);
                }
            }
        });

        //更新ボタン制御も同時に行う
        editQuantity.setText(quantity);
    }


    private int getQuantity(String str) {

        int qty = 0;
        if (!android.text.TextUtils.isEmpty(str)) {
            try {
                qty = Integer.parseInt(str);
            } catch (Exception e) {
            }
        }

        return qty;
    }


    //
    private void doAddToCart(ResponseGetSpProduct itemInfo) {

        //ログイン必須
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {

            itemInfo.seriesCode = mResponse.seriesCode;

            mAddToCartApi.setParameter(itemInfo);
            mAddToCartApi.connect(getContext());

            //-- ADD NT-LWL 17/08/14 GA追加 FR -
            // GA追加 添加购物车 型番+数量
            StringBuilder sb = new StringBuilder();
            sb.append("{\"partNumber\":");
            sb.append("\"");
            sb.append(itemInfo.partNumber);
            sb.append("\",");

            sb.append("\"quantity\":");
            sb.append(itemInfo.mPrice.editedQuantity);
            sb.append("}");
            GoogleAnalytics.sendAction(mTracker, null, GoogleAnalytics.CATEGORY_ADD_CART, sb.toString());
            //-- ADD NT-LWL 17/08/14 GA追加 TO -
        }
    }

    private class AddToCartApi extends ApiAccessWrapper {

        private ResponseGetSpProduct mInfo;

        @Override
        protected String getScreenId() {
            return ItemDetailFragment.this.getScreenId();
        }

        public void setParameter(ResponseGetSpProduct info) {

            mInfo = info;
        }

        public HashMap<String, String> getParameter() {

            //その時の入力状態の数値を使う様に仕様変更
            //※空欄や０の時は数量１とみなす
            String str = mInfo.mPrice.editedQuantity;
            int qty = getQuantity(str);

            return ApiBuilder.createAddToCart(mInfo.brandCode, mInfo.seriesCode, mInfo.partNumber, qty);
        }

        public void onResult(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    mMessageDialogGoCart = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                doGoCart();
                            } else {
                                //閉じる
                                mMessageDialogGoCart.hide();
                            }
                        }
                    }).setAutoClose(false);

                    String string = "";
                    HeaderView h = getHeader();
                    if (h instanceof MainHeader) {
                        MainHeader mainHeader = (MainHeader) h;
                        Integer integer = mainHeader.addCartCount(1);
                        AppNotifier.getInstance().updateCartCount();
                        string = String.format(getResourceString(R.string.item_detail_dialog_added_cart), integer.toString());
                    } else {
                        AppNotifier.getInstance().addCartCount(1);
                    }
                    mMessageDialogGoCart.showCart(getResourceString(R.string.item_detail_dialog_added_cart_title), string,
                            R.string.dialog_button_go_cart, R.string.dialog_button_close);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    private void doGoCart() {

        mRefreshCartApi.connect(getContext(), mMessageDialogGoCart);
    }

    private void doGoMyPart() {

        hideKeyboard();
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {
            mRefreshMyPartApi.connect(getContext(), mMessageDialogGoMyPart);
        }
    }

    //
    private void doAddMyPart(ResponseGetSpProduct itemInfo) {

        //ログイン必須
        if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {

            itemInfo.seriesCode = mResponse.seriesCode;

            mAddMyPartApi.setParameter(itemInfo);
            mAddMyPartApi.connect(getContext());
        }
    }

    private class AddMyPartApi extends ApiAccessWrapper {

        private ResponseGetSpProduct mInfo;

        @Override
        protected String getScreenId() {
            return ItemDetailFragment.this.getScreenId();
        }

        public void setParameter(ResponseGetSpProduct info) {

            mInfo = info;
        }


        public HashMap<String, String> getParameter() {

            //その時の入力状態の数値を使う様に仕様変更
            //※空欄や０の時は数量１とみなす
            String str = mInfo.mPrice.editedQuantity;
            int qty = getQuantity(str);

            return ApiBuilder.createAddToMyComponents(mInfo.brandCode, mInfo.seriesCode, mInfo.partNumber, qty);
        }

        public void onResult(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (!response.setData(result)) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    mMessageDialogGoMyPart = new MessageDialog(getContext(), new MessageDialog.MessageDialogListener() {
                        @Override
                        public void onDialogResult(Dialog dlg, View view, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                doGoMyPart();
                            } else {
                                //閉じる
                                mMessageDialogGoMyPart.hide();
                            }
                        }
                    }).setAutoClose(false);
//-- UDP NT-LWL 17/03/22 AliPay Payment FR -
//                    mMessageDialogGoMyPart.show(null, getResourceString(R.string.item_detail_dialog_added_my_parts),
//                            R.string.dialog_button_go_my_part, R.string.dialog_button_close);
                    mMessageDialogGoMyPart.show(null, getResourceString(R.string.item_detail_dialog_added_my_parts),
                            R.string.dialog_button_go_my_part, R.string.dialog_button_cancel);
                    break;
//-- UDP NT-LWL 17/03/22 AliPay Payment FR -
                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }
    }


    //数量更新
    //価格チェック通信
    private void doSpProduct(int quantity) {

        hideKeyboard();

        if (SubsidiaryCode.isJapan()) {

            doSpProductSub(quantity);
        } else {

            //中国はログイン必須
            if (new SessionRequiredDialog().judgeLaunchRestriction(getContext())) {

                doSpProductSub(quantity);
            }
        }
    }

    private void doSpProductSub(int quantity) {

        String completeType = mResponse.completeType;
        String seriesCode = mResponse.seriesCode;
        String innerCode = mResponse.innerCode;
        String partNumber = mResponse.partNumber;

        mGetSpProductApi.setParameter(completeType, seriesCode, innerCode, partNumber, quantity);
        mGetSpProductApi.connect(getContext());
    }


    /**
     * 自分画面更新用
     */
    private class RefreshCartApi extends ApiAccessWrapper {

        private MessageDialog mMessageDialog;

        @Override
        public HashMap<String, String> getParameter() {
            return ApiBuilder.createGetCart();
        }


        public void connect(Context context, MessageDialog messageDialog) {
            mMessageDialog = messageDialog;
            super.connect(context);
        }


        @Override
        public void onResult(int responseCode, String result) {

            GetCart response = new GetCart();
            boolean pars = response.setData(result);
            if (!pars) {
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }

                    getFragmentController().stackFragment(new CartFragment(), FragmentController.ANIMATION_SLIDE_IN, response);

                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected String getScreenId() {
            return ItemDetailFragment.this.getScreenId();
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }

    private class RefreshMyPartApi extends ApiAccessWrapper {

        private MessageDialog mMessageDialog;

        @Override
        protected String getScreenId() {
            return ItemDetailFragment.this.getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            String folderId = "0";
            String sort = "0";
            return ApiBuilder.createGetMyComponents(folderId, sort);
        }

        public void connect(Context context, MessageDialog messageDialog) {
            mMessageDialog = messageDialog;
            super.connect(context);
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

                    //ダイアログ閉じる
                    if (mMessageDialog != null) {
                        mMessageDialog.hide();
                        mMessageDialog = null;
                    }

                    getFragmentController().stackFragment(new MyPartsListFragment(), FragmentController.ANIMATION_SLIDE_IN, response);
                    break;

                default:
                    showErrorMessage(response.errorList);
                    break;
            }
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }
    }


    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.ItemDetail;
    }
}
