package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.data.ResponseSearchInfo;
import jp.co.misumi.misumiecapp.data.ResponseSearchItem;
import jp.co.misumi.misumiecapp.data.ResponseSearchOrder;
import jp.co.misumi.misumiecapp.util.Format;
import jp.co.misumi.misumiecapp.util.SpannableUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * 注文履歴一覧
 */
public class OrderSearchAdapter extends CommonAdapter<ResponseSearchInfo> {


    /**
     * LayoutInflator.
     */
    private final LayoutInflater mInflater;

    /**
     * 行レイアウトリソースID.
     */
    private final int mResource;

    private final boolean mIsIncludeTax;

    private final OnItemClickListener mOnItemClickListener;

    //-- UPD NT-LWL 16/11/11 AliPay Payment FR -
/*    private String mPriorityStatus[] = {
            "a1",   // 承認待ち
            "a2",   // 否認
            "a3",   // 差戻し
            "z",    // ミスミ確認中
            "1",    // 処理中
            "3",    // 注文済
            "4",    // 出荷済
            "x",    // キャンセル済
            "w",    // 入金待ち
            "f",    // 失敗
    };*/

    private String[] mPriorityStatus;
    //-- UPD NT-LWL 16/11/11 AliPay Payment TO -

    public interface OnItemClickListener {
        void onItemClick(ArrayAdapter<?> adapter, View view, int position, long id);

        //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
        void onGotoPayClick(ArrayAdapter<?> adapter, View view, int position);
        //-- ADD NT-LWL 16/11/11 AliPay Payment TO -
    }


    /**
     * コンストラクタ.
     *
     * @param context  コンテキスト
     * @param resource 行レイアウトリソースID
     * @param objects  一覧データ
     */
    public OrderSearchAdapter(Context context, int resource, List<ResponseSearchInfo> objects, OnItemClickListener onItemClickListener) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;

        mOnItemClickListener = onItemClickListener;

        mIsIncludeTax = AppConfig.getInstance().isIncludeTax();
    }


    /* (非 Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            ViewUtil.setSplitMotionEventsToAll(convertView);
        }
        //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
        if (SubsidiaryCode.isJapan()) {
            //日本环境下先判w再判f
            mPriorityStatus = new String[]{"a1", "a2", "a3", "z", "1", "3", "4", "x", "w", "f"};
        } else {
            //中文环境下先判f
            mPriorityStatus = new String[]{"a1", "a2", "a3", "z", "1", "3", "4", "x", "f"};
        }
        //-- ADD NT-LWL 16/11/11 AliPay Payment FR -

        // 1行分のアイテムデータを取得
        final ResponseSearchOrder.ListInfo itemData = (ResponseSearchOrder.ListInfo) getItem(position);
        AppLog.d("订购单号=" + itemData.infoSlipNo + ",size = " + itemData.mItemList.size());
        for (int i = 0; i < itemData.mItemList.size(); i++) {
            AppLog.d("item = " + i + ",status=" + itemData.mItemList.get(i).status);
        }
        // ステータス表示追加　MISUMI_MOBILE_APP-647
        int status_res = R.string.common_status_unknown;
        status_check:
        {
            for (String pri : mPriorityStatus) {
                for (ResponseSearchItem item : itemData.mItemList) {
                    if (null != item.status && item.status.equals(pri)) {
                        switch (pri) {
                            case "1":
                                status_res = R.string.common_status_1;
                                break;
                            case "3":
                                status_res = R.string.common_status_3;
                                break;
                            case "4":
                                status_res = R.string.common_status_4;
                                break;
                            case "f":
                                status_res = R.string.common_status_f;
                                break;
                            case "z":
                                status_res = R.string.common_status_z;
                                break;
                            case "x":
                                status_res = R.string.common_status_x;
                                break;
                            case "w":
                                status_res = R.string.common_status_w;
                                break;
                            case "a1":
                                status_res = R.string.common_status_a1;
                                break;
                            case "a2":
                                status_res = R.string.common_status_a2;
                                break;
                            case "a3":
                                status_res = R.string.common_status_a3;
                                break;
                        }
                        break status_check;
                    }
                }
            }
        }
        if (status_res == R.string.common_status_unknown) {
            setIncludeItemText(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(status_res), false);
        } else {
            setIncludeItemTextRed(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(status_res), false);
        }


        //
        setIncludeItemText(convertView.findViewById(R.id.textSlipNo), getResourceString(R.string.order_hist_slip_no), itemData.infoSlipNo, false);
        setIncludeItemText(convertView.findViewById(R.id.textDateTime), getResourceString(R.string.order_hist_date), itemData.infoDateTime, false);

        String str;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        if (itemData.mItemList == null || itemData.mItemList.size() == 0) {
            ;
        } else {
            int itemCount = itemData.mItemList.size();

            str = Format.formatCount(itemCount);

            SpannableString ss = SpannableUtil.newSpannableString(str, 0, true, false);
            ssb.append(ss);

            ss = new SpannableString(getResourceString(R.string.quote_hist_count_unit));
            ssb.append(ss);

            //ミスミ確認中
            int misumiCheckCount = 0;
            for (int idx = 0; idx < itemCount; ++idx) {

                ResponseSearchOrder.ItemInfo itemInfo = (ResponseSearchOrder.ItemInfo) itemData.mItemList.get(idx);

                if ("z".equals(itemInfo.status)) {
                    ++misumiCheckCount;
                }
            }

            if (misumiCheckCount > 0) {

                ss = new SpannableString("（");
                ssb.append(ss);

                str = String.format(getResourceString(R.string.order_hist_misumi_check_str), Format.formatCount(misumiCheckCount));
                ss = SpannableUtil.newSpannableString(str, 0, true, false);
                ssb.append(ss);

                ss = new SpannableString("）");
                ssb.append(ss);
            }
        }

        setIncludeItemText(convertView.findViewById(R.id.textItemCount), getResourceString(R.string.order_hist_item_count), ssb, false);


        //
        ssb = new SpannableStringBuilder();


        if (!mIsIncludeTax) {

            //こっちは非表示
            View textTotalPriceWithTax = convertView.findViewById(R.id.textTotalPriceWithTax);
            textTotalPriceWithTax.setVisibility(View.GONE);

            //日本外税
            if (itemData.totalPrice == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(itemData.totalPrice, true, true, false, getResourceString(R.string.quote_hist_total_price_unit)));
            }

            setIncludeItemText(convertView.findViewById(R.id.textTotalPrice), getResourceString(R.string.quote_hist_total_price), ssb, false);
        }


        if (mIsIncludeTax) {

            //こっちは非表示
            View textTotalPrice = convertView.findViewById(R.id.textTotalPrice);
            textTotalPrice.setVisibility(View.GONE);

            //中国税込み
            ssb = new SpannableStringBuilder();
            if (itemData.totalPriceIncludingTax == null) {

                ssb.append(getResourceString(R.string.label_hyphen));   //ハイフン化
            } else {

                ssb.append(Format.formatAmountWithUnit(itemData.totalPriceIncludingTax, true, true, false, getResourceString(R.string.quote_hist_total_price_tax_unit)));
            }

            setIncludeItemText(convertView.findViewById(R.id.textTotalPriceWithTax), getResourceString(R.string.quote_hist_total_price_tax), ssb, false);
        }

        //-- ADD NT-LWL 16/11/11 AliPay Payment FR -
        //不是日文环境 支付方式为ADV显示支付方式,其他情况隐藏支付方式、支付期限支付按钮
        AppLog.d("settlementType = " + itemData.settlementType + ",paymentGroup = " + itemData.paymentGroup + ",paymentGroupName = " + itemData.paymentGroupName);
        if (!SubsidiaryCode.isJapan()) {
            boolean isAllW = true;//订单状态是否全是w(待支付)
            for (int i = 0; i < itemData.mItemList.size(); i++) {
                if (!"w".equals(itemData.mItemList.get(i).status)) {
                    isAllW = false;
                    break;
                }
            }
            if (itemData.settlementType != null && "ADV".equals(itemData.settlementType)) {
                if (itemData.paymentGroupName != null && !itemData.paymentGroupName.isEmpty()) {
                    setIncludeItemText(convertView.findViewById(R.id.textPaymentType), getResourceString(R.string.order_list_payment_type), itemData.paymentGroupName, false);
                } else {
                    setIncludeItemText(convertView.findViewById(R.id.textPaymentType), getResourceString(R.string.order_list_payment_type), getResourceString(R.string.label_hyphen), false);
                }
                if (isAllW) {

                    if (itemData.paymentDeadlineDateTime == null || itemData.paymentDeadlineDateTime.isEmpty()) {//支付期限为空显示“-”
                        AppLog.d("paymentDeadlineDateTime = " + itemData.paymentDeadlineDateTime);
                        setIncludeItemText(convertView.findViewById(R.id.textPaymentDeadlineDateTime), getResourceString(R.string.order_list_payment_deadline), getResourceString(R.string.label_hyphen), false);
                        setIncludeItemTextRed(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(R.string.common_status_c), false);
                        convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                    } else {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                        Date nowdate = new Date();
                        Date d;

                        //有没有超过有效期标记
                        boolean compareFlag = false;
                        try {
                            d = sDateFormat.parse(itemData.paymentDeadlineDateTime);
                            compareFlag = !d.before(nowdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //判断paymentGroup=1并且paymentDeadlineDateTime比当前时间晚，显示支付按钮
                        if (itemData.paymentGroup != null && "1".equals(itemData.paymentGroup) && compareFlag) {
                            //-- UDP NT-LWL 17/09/27 Category FR -
                            //convertView.findViewById(R.id.buttonPay).setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                            //-- UDP NT-LWL 17/09/27 Category TO -
                        } else {
                            convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                        }
                        if (!compareFlag) {//超过有效期显示已过有效期
                            convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                            setIncludeItemTextRed(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(R.string.common_status_c), false);
                        } else {
                            //全是w的情况并且未过有效期显示等待支付
                            setIncludeItemTextRed(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(R.string.common_status_w), false);
                        }
                        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String showDeadLine = getResourceString(R.string.label_hyphen);
                        showDeadLine = dFormat.format(new Date(itemData.paymentDeadlineDateTime));
                        AppLog.d("当前时间 = " + dFormat.format(new Date()));
                        AppLog.d("paymentDeadlineDateTime = " + showDeadLine);
//                        showDeadLine = getResourceString(R.string.label_hyphen);

                        setIncludeItemText(convertView.findViewById(R.id.textPaymentDeadlineDateTime), getResourceString(R.string.order_list_payment_deadline), showDeadLine, false);
                    }
                    if (!TextUtils.isEmpty(itemData.paymentGroup) && itemData.paymentGroup.equals("1")) {
                        convertView.findViewById(R.id.textPaymentDeadlineDateTime).setVisibility(View.VISIBLE);
                    } else {
                        convertView.findViewById(R.id.textPaymentDeadlineDateTime).setVisibility(View.GONE);
                    }
                } else {
                    convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                    convertView.findViewById(R.id.textPaymentDeadlineDateTime).setVisibility(View.GONE);
                }
            } else {
                if (isAllW) {
                    setIncludeItemTextRed(convertView.findViewById(R.id.textStatus), getResourceString(R.string.order_hist_status), getResourceString(R.string.common_status_w), false);
                }
                convertView.findViewById(R.id.buttonPay).setVisibility(View.GONE);
                convertView.findViewById(R.id.textPaymentType).setVisibility(View.GONE);
                convertView.findViewById(R.id.textPaymentDeadlineDateTime).setVisibility(View.GONE);
            }
        }


        convertView.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onGotoPayClick(OrderSearchAdapter.this, null, position);
            }
        });
        //-- ADD NT-LWL 16/11/11 AliPay Payment TO -

//        final View imageNext = convertView.findViewById(R.id.imageNext);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClickListener.onItemClick(OrderSearchAdapter.this, null, position, getItemId(position));
            }
        });

        return convertView;
    }

}
