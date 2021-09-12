package com.think.xposed.http;

import com.safframework.http.interceptor.AndroidLoggingInterceptor;
import com.think.xposed.BaseHook;

import cn.netdiscovery.http.interceptor.LoggingInterceptor;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.OkHttpClient;

public class HttpHook extends BaseHook {

    public static final String TAG = "HttpHook";

    public HttpHook(ClassLoader targetClassLoader,boolean debug) {
        super(targetClassLoader, debug);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void hook() {

        Class<?> okHttpBuilderCls = XposedHelpers.findClassIfExists(OkHttpClient.Builder.class.getName(), targetClassLoader);
        if (okHttpBuilderCls == null) {
            log("未找到 okhttp3.OkHttpClient.Builder , 可能被混淆了");
            return;
        }

        XposedHelpers.findAndHookMethod(
                okHttpBuilderCls,
                "build",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        catExp(() -> {
                            log("注入日志拦截器 LoggingInterceptor");
                            CustomLogInterceptor loggingInterceptor = new CustomLogInterceptor();
                            OkHttpClient.Builder builder = (OkHttpClient.Builder) param.thisObject;
                            builder.addInterceptor(loggingInterceptor);
                            log("注入日志拦截器 成功");
                        });
                    }
                }
        );
    }
}
