package com.think.business.login;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.think.core.util.ToastHelper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @author zzp
 * @date 2020-1-15
 * 处理QQ第三方登录工具类
 */
public class QQLoginHelper implements IUiListener {

    public static final String QQ_INFO_CODE = "0";
    public static final String QQ_INFO_ERROR_CODE = "-1";
    private static final String QQ_APP_ID = "";

    private static final String QQ_SCOPE = "get_simple_userinfo";
    /**
     * QQ授权上下文必须要Activity
     */
    private WeakReference<Activity> mWeakReference;
    /**
     * QQ 接入接口
     */
    private Tencent tencent;

    public QQLoginHelper(@NonNull Activity context) {
        mWeakReference = new WeakReference<>(context);
        tencent = Tencent.createInstance(QQ_APP_ID, context);
    }

    /**
     * 授权登录
     */
    public void doLogin() {
        tencent.login(mWeakReference.get(), QQ_SCOPE, this);
    }

    /**
     * 获取QQ账号信息
     */
    public void getQQUserInfo() {
        if (tencent.isSessionValid()) {
            final QQToken qqToken = tencent.getQQToken();
            UserInfo userInfo = new UserInfo(mWeakReference.get(), qqToken);
            userInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    JSONObject jsonResponse = (JSONObject) o;
                    String nickname = jsonResponse.optString("nickname");  //用户昵称
                    String figureUrl = jsonResponse.optString("figureurl_qq_1"); //头像链接
                    String gender = jsonResponse.optString("gender");  //性别
                    // TODO
                }

                @Override
                public void onError(UiError uiError) {
                    Log.e(QQ_INFO_ERROR_CODE, "获取QQ信息出现错误");
                }

                @Override
                public void onCancel() {
                    Log.i(QQ_INFO_ERROR_CODE, "取消获取QQ信息");
                }
            });
        } else {
            Log.e(QQ_INFO_ERROR_CODE, "还未登录或当前会话已失效");
        }
        // 方法二：
        tencent.getAccessToken();
        tencent.getOpenId();
    }

    public boolean isInstalled() {
        if (tencent == null && mWeakReference.get() != null) {
            tencent = Tencent.createInstance(QQ_APP_ID, mWeakReference.get());
        }
        return tencent.isQQInstalled(mWeakReference.get());
    }

    public boolean checkSession() {
        if (tencent == null && mWeakReference.get() != null) {
            tencent = Tencent.createInstance(QQ_APP_ID, mWeakReference.get());
        }
        return tencent.checkSessionValid(QQ_APP_ID);
    }

    public String getAccessToken() {
        if (!checkSession()) {
            JSONObject jsonObject = tencent.loadSession(QQ_APP_ID);
            tencent.initSessionCache(jsonObject);
        }
        return tencent.getAccessToken();
    }

    public long getAccessTokenExpires() {
        if (!checkSession()) {
            JSONObject jsonObject = tencent.loadSession(QQ_APP_ID);
            tencent.initSessionCache(jsonObject);
        }
        return tencent.getExpiresIn();
    }

    public String getOpenId() {
        if (!checkSession()) {
            JSONObject jsonObject = tencent.loadSession(QQ_APP_ID);
            tencent.initSessionCache(jsonObject);
        }
        return tencent.getOpenId();
    }

    @Override
    public void onComplete(Object o) {
        if (mWeakReference.get() != null) {
            ToastHelper.getInstance(mWeakReference.get().getApplication()).toast("QQ授权成功");
        }
        JSONObject jsonResponse = (JSONObject) o;
        tencent.setOpenId(jsonResponse.optString("openid"));
        String expiresIn = jsonResponse.optString("expires_in");
        if (TextUtils.isEmpty(expiresIn)) {
            expiresIn = String.valueOf(jsonResponse.optLong("expires_in"));
        }
        tencent.setAccessToken(jsonResponse.optString("access_token"), expiresIn);
        tencent.setOpenId(jsonResponse.optString("openid"));
        getQQUserInfo();
    }

    @Override
    public void onError(UiError uiError) {
        if (mWeakReference.get() != null) {
            ToastHelper.getInstance(mWeakReference.get().getApplication()).toast("QQ授权出错  " + uiError.errorMessage);
        }
//        DialogManager.getInstance().showHomeDialog(mWeakReference.get());
    }

    @Override
    public void onCancel() {
        if (mWeakReference.get() != null) {
            ToastHelper.getInstance(mWeakReference.get().getApplication()).toast("用户取消QQ授权");
        }
    }

}
