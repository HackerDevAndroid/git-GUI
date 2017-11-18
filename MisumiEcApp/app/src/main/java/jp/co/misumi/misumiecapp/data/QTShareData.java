//--ADD NT-LWL 17/05/19 Share FR -
package jp.co.misumi.misumiecapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 报价详情分享数据
 */

public class QTShareData {
    // 报价商品列表
    private List<ShareDetail> qtList = new ArrayList<>();
    // 报价单号
    private String quotationSlipNo;
    // 报价日期
    private String quotationDateTime;
    // 报价有效期
    private String quotationExpireDateTime;
    // 订购人
    private String userName;
    // 订购商品件数
    private String itemCount;
    // 运费
    private String freight;
    // 运费折扣
    private String freightDiscount;
    // 订单总计金额（不含税）
    private String totalPrice;
    // 订单总计金额（含税）
    private String totalPriceIncludingTax;

    public List<ShareDetail> getQtList() {
        return qtList;
    }

    public void setQtList(List<ShareDetail> qtList) {
        this.qtList = qtList;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getFreightDiscount() {
        return freightDiscount;
    }

    public void setFreightDiscount(String freightDiscount) {
        this.freightDiscount = freightDiscount;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceIncludingTax() {
        return totalPriceIncludingTax;
    }

    public void setTotalPriceIncludingTax(String totalPriceIncludingTax) {
        this.totalPriceIncludingTax = totalPriceIncludingTax;
    }

    public String getQuotationSlipNo() {
        return quotationSlipNo;
    }

    public void setQuotationSlipNo(String quotationSlipNo) {
        this.quotationSlipNo = quotationSlipNo;
    }

    public String getQuotationDateTime() {
        return quotationDateTime;
    }

    public void setQuotationDateTime(String quotationDateTime) {
        this.quotationDateTime = quotationDateTime;
    }

    public String getQuotationExpireDateTime() {
        return quotationExpireDateTime;
    }

    public void setQuotationExpireDateTime(String quotationExpireDateTime) {
        this.quotationExpireDateTime = quotationExpireDateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
//--ADD NT-LWL 17/05/19 Share TO -