package com.think.business.login;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.think.core.util.LogUtils;

import java.lang.ref.WeakReference;

import static com.think.business.login.EntryActivity.WX_STATE;


/**
 * @author zzp
 * @date 2020-1-15
 * 处理微信登录工具类
 */
public class WXLoginHelper {

    /***
     * 微信APP_ID
     */
    public static final String WX_APP_ID = "";
    /**
     * 应用秘钥，微信开放平台中获取
     */
    private static final String WX_SECRET = "";
    /**
     * 微信授权类型
     */
    private static final String WX_GRANT_TYPE = "authorization_code";
    /**
     * 应用授权作用域，如需要获取个人信息则填写
     */
    private static final String WX_SCOPE = "snsapi_userinfo";

    /**
     * 微信获取openId url
     */
    public static final String WX_OPEN_ID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 微信获取 nickName
     */
    public static final String WX_NICKNAME_URL = "https://api.weixin.qq.com/sns/userinfo";

    private WeakReference<Activity> mWeakReference;
    /**
     * 微信登录api
     */
    private IWXAPI iwxapi;


    // 接受微信消息广播
    private LocalBroadcastManager mLocalBroadcastManager;

    private static boolean isReceive = false;

    private BroadcastReceiver mWeiXinLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int what = intent.getIntExtra("what", 0);
            if (what != 0 && isReceive) {
                return;
            }
            isReceive = true;
            switch (what) {
                case EntryActivity.SEND_CODE:
                    String code = intent.getStringExtra("code");
//                        mLocalBroadcastManager.unregisterReceiver(mWeiXinLoginReceiver);
//                    ToastUtil.showToast("接受code = "+ accessTokenRequest.getCode(),mWeakReference.get());
                    break;
                case EntryActivity.AUTH_DENIED:
                    LogUtils.debug("用户拒绝授权");
                    break;
                case EntryActivity.USER_CANCEL:
                    LogUtils.debug("用户取消了授权");
                    break;
                case EntryActivity.OTHER:
                default:
                    LogUtils.debug("出现其他错误");

            }

        }
    };

    public WXLoginHelper(@NonNull Activity context) {
        this.mWeakReference = new WeakReference<>(context);
        iwxapi = WXAPIFactory.createWXAPI(context, WX_APP_ID, true);
        initCallBack();
    }


    public void initCallBack() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mWeakReference.get());
        mLocalBroadcastManager.registerReceiver(mWeiXinLoginReceiver, new IntentFilter("com.xjsdk.weixin_receive"));

    }

    /**
     * 微信登录
     */
    public void doLogin() {
        isReceive = false;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = WX_SCOPE; // 标识获取用户信息
        req.state = WX_STATE; // 请求标志
        iwxapi.sendReq(req);
    }

    public void openWXApp() {
        iwxapi.openWXApp();
    }

    public boolean isInstalledApp() {
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(mWeakReference.get(), WX_APP_ID);
        }
        return iwxapi.isWXAppInstalled();
    }


    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(mWeakReference.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mWeakReference.get(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


}
