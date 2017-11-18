//--ADD NT-LWL 17/05/20 Share FR -
package com.misumi_ec.cn.misumi_ec.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.R;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, AppConst.WEIXIN_APP_ID, false);
        // 将该app注册到微信
        api.registerApp(AppConst.WEIXIN_APP_ID);


// 第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，
// 则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        AppLog.e("errorCode=" + resp.errCode + ",errorStr=" + resp.errStr);
        AppLog.e("getType=" + resp.getType());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                AppLog.v("微信 分享成功！");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                AppLog.e("微信 分享取消！");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                AppLog.e("微信 分享失败！");
                new MessageDialog(this, null).show(R.string.share_fail);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                AppLog.e("微信 分享失败！");
                new MessageDialog(this, null).show(R.string.share_fail);
                break;
            default:
                break;
        }

        finish();
    }

}
//--ADD NT-LWL 17/05/20 Share TO -