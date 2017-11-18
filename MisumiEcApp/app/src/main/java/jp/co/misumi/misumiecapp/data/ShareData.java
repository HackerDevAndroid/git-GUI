//--ADD NT-LWL 17/05/19 Share FR -
package jp.co.misumi.misumiecapp.data;

/**
 * 分享实体类
 */
public class ShareData {
    // 图片url
    private String imageUrl;
    // 分享标题 限购物车分享
    private String title;
    // 分享内容
    private String content;
    // 点击后跳转地址
    private String targetUrl;

    public ShareData() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
//--ADD NT-LWL 17/05/19 Share TO -