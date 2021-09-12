package com.think.xposed.web;

import android.net.http.SslError;
import android.os.Build;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.think.xposed.BaseHook;
import com.think.xposed.ReflectUtils;
import com.think.xposed.RunFunction;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WebViewHook extends BaseHook {

    public static final String TAG = "WebViewHook";

    @Override
    protected String getTag() {
        return TAG;
    }

    public WebViewHook(ClassLoader classLoader,boolean debug) {
        super(classLoader,debug);
        catExp(()->{
            hookWebClient();
            hookWebViewChromeClient();
        });
    }

    private void hookWebClient() {
        Class<?> webViewClientCls = XposedHelpers.findClass("android.webkit.WebViewClient", targetClassLoader);
        XposedHelpers.findAndHookMethod(
                webViewClientCls,
                "shouldOverrideUrlLoading",
                WebView.class,
                String.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        catExp(() -> {
                            String url = (String) param.args[1];
                            log(" shouldOverrideUrlLoading url = " + url);
                        });
                    }
                });

        /**
         * 拦截super.onReceivedSslError
         */
        XposedHelpers.findAndHookMethod(
                webViewClientCls,
                "onReceivedSslError",
                WebView.class,
                SslErrorHandler.class,
                SslError.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        catExp(()->{
                            log("onReceivedSslError Hook SslErrorHandler 防止调用cancel");
                            SslErrorHandler instance = ReflectUtils.findInstance(SslErrorHandler.class);
                            SslErrorHandler originHandler = (SslErrorHandler) param.args[1];
                            // 替换handler 防止 调用cancel
                            param.args[1] = instance;
                            // 调用proceed 忽略ssl证书验证
                            originHandler.proceed();
                            SslError error = (SslError) param.args[2];
                            log("onReceivedSslError error = " + error);
                        });
                    }
                }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            XposedHelpers.findAndHookMethod(
                    webViewClientCls,
                    "onReceivedClientCertRequest",
                    WebView.class,
                    ClientCertRequest.class,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            catExp(()->{
                                log("onReceivedClientCertRequest Hook ClientCertRequest 防止调用cancel");
                                ClientCertRequest instance = ReflectUtils.findInstance(ClientCertRequest.class);
                                ClientCertRequest originHandler = (ClientCertRequest) param.args[1];
                                // 替换 request 防止 调用cancel
                                param.args[1] = instance;
                                // 调用 ignore 忽略ssl证书验证
                                originHandler.ignore();
                            });
                        }
                    }
            );
        }
    }

    public void hookWebViewChromeClient(){
        Class<?> wccCls = XposedHelpers.findClass("android.webkit.WebChromeClient", targetClassLoader);

        XposedHelpers.findAndHookMethod(
                wccCls,
                "onConsoleMessage",
                ConsoleMessage.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        catExp(()->{
                            ConsoleMessage consoleMessage = (ConsoleMessage) param.args[0];
                            log("onConsoleMessage line = " +consoleMessage.lineNumber() + ":" + consoleMessage.message());
                        });
                    }
                }
        );
    }

}
