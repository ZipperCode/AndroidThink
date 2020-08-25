package com.think.business.login.yd;

import android.content.Context;
import android.util.Log;

import com.cmic.sso.wy.auth.AuthnHelper;
import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.json.JSONObject;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 易盾快速登录
 *
 * @author xj-134
 * @date 2020-6-5
 */
public class YDQuickLogin implements UIClickCallback {

    private static final String TAG = YDQuickLogin.class.getSimpleName();
    /**
     * 易盾BusinessId
     */
    private static final String YD_BUSINESS_ID = "";
    /**
     * 1分半的时间
     */
    private static final long FETCH_TIME = 90;

    private QuickLogin mQuickLogin;

    private Context mContext;

    /**
     * 提供任务线程任务取消机制
     */
    private Future<?> cancelFuture;
    /**
     * 取号任务
     */
    private Runnable runPrefetchNumber = new Runnable() {
        @Override
        public void run() {
            prefetchMobileNumber();
        }
    };
    /**
     * 定时器运行标志 true为运行，false为停止运行
     */
    private AtomicBoolean timerIsRunning = new AtomicBoolean(false);

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    /**
     * 是否已经预取号
     */
    private final AtomicBoolean isPrefetchNumber = new AtomicBoolean(false);

    private static YDQuickLogin ydQuickLogin = null;

    private YDQuickLogin(Context context) {
        this.mContext = context;
        mQuickLogin = QuickLogin.getInstance(mContext.getApplicationContext(), YD_BUSINESS_ID);
        mQuickLogin.setUnifyUiConfig(QuickLoginUiConfig
                .getDialogUiConfig(context, this, true));
        mQuickLogin.setDebugMode(true);
        startTask();
    }

    public synchronized static YDQuickLogin getInstance(Context context) {
        if (ydQuickLogin == null) {
            ydQuickLogin = new YDQuickLogin(context);
        }
        return ydQuickLogin;
    }

    public synchronized void startTask() {
        if (timerIsRunning.get()) {
            stopTask();
        }
        if (!timerIsRunning.get()) {
            Log.d(TAG, "开始定时刷新YDToken");
            timerIsRunning.compareAndSet(false, true);
            cancelFuture = scheduledExecutorService.scheduleAtFixedRate(runPrefetchNumber,
                    0, FETCH_TIME, TimeUnit.SECONDS);
        }
    }

    public synchronized void stopTask() {
        timerIsRunning.compareAndSet(true, false);
        if (cancelFuture != null && !cancelFuture.isCancelled()) {
            cancelFuture.cancel(true);
        }
        Log.d(TAG, "停止定时刷新YDToken");
    }

    private void prefetchMobileNumber() {

        mQuickLogin.prefetchMobileNumber(new com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                // 取号成功，标记状态为成功
                isPrefetchNumber.compareAndSet(false, true);
                Log.d(TAG, "[预取号成功：] mobileNumber is:" + mobileNumber + ", YDToken = " + YDToken);
            }

            @Override
            public void onGetMobileNumberError(String s, String s1) {
                isPrefetchNumber.compareAndSet(true, false);
            }
        });
    }

    /**
     * 实行一键登录
     */
    public void doOnePass() {
        if (isOnePassLogin() && isPrefetchNumberStatus()) {
            if (timerIsRunning.get()) {
                stopTask();
            }
            timerIsRunning.compareAndSet(true, false);
        }
        doPrefetchAndLogin();
    }

    /**
     * 取号并且登录
     */
    private void doPrefetchAndLogin() {
        mQuickLogin.prefetchMobileNumber(new com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                // 取号成功，标记状态为成功
                isPrefetchNumber.compareAndSet(false, true);
                onePass();
            }

            @Override
            public void onGetMobileNumberError(String s, String s1) {
                isPrefetchNumber.compareAndSet(true, false);
            }
        });
    }

    private void onePass() {
        mQuickLogin.onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                Log.d(TAG, String.format("[一键登录] yd token is:%s accessCode is:%s", YDToken, accessCode));
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                Log.d(TAG, "获取运营商token失败:" + msg);
//                ToastUtil.showToast("获取运营商token失败,请尝试其他登录方式", mContext);
                startTask();
            }

            @Override
            public void onCancelGetToken() {
                Log.d(TAG, "用户取消登录,重新取号");
            }
        });
    }

    /**
     * 本机校验，通过手机号去后台校验用户
     *
     * @param mobileNumber 手机掩码
     */
    public void mobileNumberVerify(final String mobileNumber) {
        QuickLogin.getInstance(mContext, YD_BUSINESS_ID)
                .getToken(mobileNumber, new QuickLoginTokenListener() {
                    @Override
                    public boolean onExtendMsg(JSONObject extendMsg) {
                        Log.d(TAG, "获取的扩展字段内容为:" + extendMsg.toString());
                        return super.onExtendMsg(extendMsg);
                    }

                    @Override
                    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                        Log.d(TAG, "获取Token成功,yd toke is:" + YDToken + " 运营商token is:" + accessCode);
                    }

                    @Override
                    public void onGetTokenError(final String YDToken, final String msg) {
                        Log.e(TAG, "获取Token失败,yd toke is:" + YDToken + " msg is:" + msg);
                    }
                });
    }

    /**
     * 是否取号成功
     *
     * @return
     */
    public boolean isPrefetchNumberStatus() {
        return isPrefetchNumber.get();
    }

    /**
     * 是否支持一键登录 1为移动网络，3为移动网络+wifi
     *
     * @return true表示支持，false表示不支持
     */
    public boolean isOnePassLogin() {
        JSONObject jsonObject = AuthnHelper.getInstance(mContext).getNetworkType(mContext);
        String networkType = jsonObject.optString("networkType", "0");
        return "1".equals(networkType) || "3".equals(networkType);
    }

    @Override
    public void onOther() {
        mQuickLogin.quitActivity();
    }

    public void destroy() {
        if (cancelFuture != null && !cancelFuture.isCancelled()) {
            cancelFuture.cancel(true);
        }
        ydQuickLogin = null;
    }
}
