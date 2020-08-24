package com.think.business.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;

/**
 * @author zzp
 * @date 2020-1-13
 * 微信SDK 必须窗口
 */
public abstract class EntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * 发送微信验证code
     */
    public static final int SEND_CODE = 10000;
    /**
     * 用户拒绝授权
     */
    public static final int AUTH_DENIED = 10001;
    /**
     * 用户取消
     */
    public static final int USER_CANCEL = 10002;
    /**
     * 其他
     */
    public static final int OTHER = 10003;
    /**
     * 回调状态标志
     */
    public static final String WX_STATE = "wechat";

    private IWXAPI iwxapi;
    private MyHandler handler;

    private static class MyHandler extends Handler {
        // 弱引用，防止WXEntryActivity生命周期引用
        private final WeakReference<EntryActivity> wxEntryActivityWeakReference;

        public MyHandler(EntryActivity wxEntryActivity) {
            wxEntryActivityWeakReference = new WeakReference(wxEntryActivity);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题
        iwxapi = WXAPIFactory.createWXAPI(this, WXLoginHelper.WX_APP_ID, false);
        handler = new MyHandler(this);
        try {
            Intent intent = getIntent();
            iwxapi.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        System.out.println(baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            String state = resp.state;
            Intent intent = new Intent("com.xjsdk.weixin_receive");
            if (WX_STATE.equals(state)) {
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                            final String code = resp.code;
                            intent.putExtra("what", SEND_CODE);
                            intent.putExtra("code", code);
                        }
                        break;
                    // 用户拒绝
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        intent.putExtra("what", AUTH_DENIED);
                        break;
                    // 取消
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        intent.putExtra("what", USER_CANCEL);
                        break;
                    default:
                        intent.putExtra("what", OTHER);
                        break;
                }
            } else {
                intent.putExtra("what", AUTH_DENIED);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        finish();
    }
}
