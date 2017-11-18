//--ADD NT-LWL 17/05/19 Share FR -
package jp.co.misumi.misumiecapp.data;

/**
 * 分享详情实体
 */

public class ShareDetail {
    // 商品code
    private String scode;
    // 商品名称
    private String seriesName;
    // 品牌名称
    private String brandName;
    // 品牌code
    private String brandCode;
    // 型番
    private String pcode;
    // 型番确定标识
    private String ctype;
    // 数量
    private String number;
    // 总价
    private String totalPrice;
    // 含税总价
    private String totalPriceIncludingTax;
    // 发货日
    private String daysToShip;
    // 单价
    private String unitPrice;
    // 图片
    private String imageUrl;

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getDaysToShip() {
        return daysToShip;
    }

    public void setDaysToShip(String daysToShip) {
        this.daysToShip = daysToShip;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }
}
//--ADD NT-LWL 17/05/19 Share TO -