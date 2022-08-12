package com.think.xposed;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;

import com.think.xposed.crypto.CryptoHook;
import com.think.xposed.http.HttpHook;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String TAG = AHook.class.getSimpleName();

    public static String PATH = null;

    public static final String PKS_KEY = "com.think.xposed";
    public static final String PKS = "com.think.xposed";

    private String hookPks = "com.kugou.android";

    private CryptoHook cryptoHook;

    private HttpHook httpHook;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
        log("handleLoadPackage ====> " + packageName);
//        System.out.println("AHook >> handleLoadPackage ====> " + packageName);

//        for (int i = 0; i < pks.length; i++) {
//            if (pks[i].equals(packageName)) {
////                hookMessageDigest(lpparam.classLoader);
////                hookSymmetric(lpparam.classLoader);
//                stringSymmetricHookMap.put(packageName,new SymmetricHook(50,lpparam.classLoader));
//            }
//        }
//        if(TextUtils.isEmpty(hookPks)){
//            XSharedPreferences sharedPreferences = new XSharedPreferences(PKS);
//            hookPks = sharedPreferences.getString(PKS_KEY,"");
//            log("hookPks ====> " + hookPks);
//        }
//
//        if(!packageName.equals(hookPks)){
//            log("equals = " + packageName.equals(hookPks));
//            return;
//        }
//        hookPks = packageName;
//
//        if(cryptoHook == null){
//            cryptoHook = new CryptoHook(1000, lpparam.classLoader,false);
//            cryptoHook.hook();
//        }
//
//        if(httpHook == null){
//            httpHook = new HttpHook(lpparam.classLoader,true);
//            httpHook.hook();
//        }
//
//        log("symmetricHook hook ====> " + hookPks + ", symmetricHook = " + cryptoHook);
//        log("httpHook hook ====> " + hookPks + ", httpHook = " + httpHook);
//        if(!"com.think.xposed".equals(packageName)){
//            hookApplication(lpparam);
//        }

//        Class<?> classIfExists = XposedHelpers.findClassIfExists("com.babybus.plugin.bdad.PluginBdAd", lpparam.classLoader);
//
//        if (classIfExists == null){
//            log("不能找到 com.babybus.plugin.bdad.PluginBdAd 类");
//            return;
//        }
//        XposedHelpers.findAndHookMethod(classIfExists, "initBdSdk", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//
//
//
//            }
//        });
//        Class<?> classIfExists = XposedHelpers.findClassIfExists("android.webkit.WebView", lpparam.classLoader);
//        if (classIfExists != null){
//            log("start hook WebView");
//            XposedHelpers.findAndHookConstructor(classIfExists, Context.class, AttributeSet.class, int.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    log("hook WebView 的构造方法 >>> stackTrace >> \n" + Log.getStackTraceString(new Throwable()));
//
//                    Class<?> classIfExists11 = XposedHelpers.findClassIfExists("com.sinyee.babybus.MainApplication", lpparam.classLoader);
//                    if (classIfExists11 != null){
//                        Object get = XposedHelpers.callStaticMethod(classIfExists11, "get");
//                        if (param.args[0] == null){
//                            param.args[0] = get;
//                            log("WebView 方法修复");
//                        }
//                    }
//
//                    log("WebView.方法参数1 >> " + param.args[0]);
//                    log("WebView.方法参数2 >> " + param.args[1]);
//                    log("WebView.方法参数3 >> " + param.args[2]);
//
//                    Class<?> classIfExists1 = XposedHelpers.findClassIfExists("com.baidu.mobads.container.adrequest.ProdAdRequestInfo", lpparam.classLoader);
//                    log("是否找到 com.baidu.mobads.container.adrequest.ProdAdRequestInfo 类信息 = " + classIfExists1);
//                    if (classIfExists1 != null){
//                        Class<?> classIfExists2 = XposedHelpers.findClassIfExists("com.baidu.mobads.container.adrequest.m", lpparam.classLoader);
//                        log("是否找到 com.baidu.mobads.container.adrequest.m 类信息 = " + classIfExists2);
//                        if (classIfExists2 != null){
//                            XposedHelpers.findAndHookConstructor(classIfExists2, Context.class, classIfExists1, new XC_MethodHook() {
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    super.beforeHookedMethod(param);
//                                    log("hook adrequest.m 的构造方法 >>> stackTrace >> \n" + Log.getStackTraceString(new Throwable()));
//                                    log("adrequest.m.方法参数1 >> " + param.args[0]);
//                                    log("adrequest.m.方法参数2 >> " + param.args[1]);
//                                }
//                            });
//                        }
//
//                    }
//                }
//            });
//        }


//        Class<?> contextImplCls = XposedHelpers.findClassIfExists("android.app.ContextImpl", lpparam.classLoader);
//        if (contextImplCls == null){
//            log("未找到ContextImpl类");
//            return;
//        }
//        try {
//            Field sSharedPrefsCache = XposedHelpers.findFieldIfExists(contextImplCls, "sSharedPrefsCache");
//            if (sSharedPrefsCache == null){
//                log("未找到ContextImpl#sSharedPrefsCache属性");
//                return;
//            }
//            sSharedPrefsCache.setAccessible(true);
//            Object sSharedPrefsCache1 = XposedHelpers.getStaticObjectField(contextImplCls, "sSharedPrefsCache");
//            ArrayMap<String, ArrayMap<File, Object>> obj = (ArrayMap<String, ArrayMap<File, Object>>) sSharedPrefsCache.get(null);
//            log("sSharedPrefsCache1 >> " + sSharedPrefsCache1);
//            log("sSharedPrefsCache2 >> " + sSharedPrefsCache1);
//            log("sSharedPrefsCache >> " + obj);
//
//            if (obj == null){
//                log("未找到ContextImpl#sSharedPrefsCache属性对象");
//                return;
//            }
//            ArrayMap<File, Object> result = null;
//            for (Map.Entry<String, ArrayMap<File, Object>> stringArrayMapEntry : obj.entrySet()) {
//                String key = stringArrayMapEntry.getKey();
//                ArrayMap<File, Object> value = stringArrayMapEntry.getValue();
//                log("获取到Sp文件名为：" + key);
//                if ("setting_base".equals(key)){
//                    result = value;
//                    break;
//                }
//            }
//            if (result == null){
//                log("未找到对于的ArrayMap");
//                return;
//            }
//
//            for (Map.Entry<File, Object> fileObjectEntry : result.entrySet()) {
//                File file = fileObjectEntry.getKey();
//                SharedPreferences object = (SharedPreferences) fileObjectEntry.getValue();
//                log("fileObjectEntry >> file = " + file + ", SharedPreferences = " + object);
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }


    }

    private void hookApplication(XC_LoadPackage.LoadPackageParam lpparam){
        log("hookApplication package = " + lpparam.packageName);
        Class<?> cls = XposedHelpers.findClassIfExists(Application.class.getName(),lpparam.classLoader);
        if(cls == null){
            log("未找到" + Application.class.getName() + "类");
            return;
        }

        try{
            Method onCreate = XposedHelpers.findMethodExactIfExists(cls, "onCreate");
            log("findAndHookMethod Application#onCreate = " + onCreate);
            XposedHelpers.findAndHookMethod(
                    cls,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            log("afterHookedMethod >>"+ param.thisObject + ", onCreate");
                            Class<?> cls = XposedHelpers.findClassIfExists("com.common.busi.CustomView", lpparam.classLoader);
                            if (cls == null){
                                log("未找到com.common.busi.CustomView类");
                                return;
                            }
                            log("找到了com.common.busi.CustomView类");
                            XposedHelpers.findAndHookMethod(cls, "fixit", Object.class, String.class, String.class, int.class, String.class, String.class,
                                    String.class, int.class, int.class, new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                            super.beforeHookedMethod(param);
                                            log(">> 打印CustomView的堆栈 = " + Log.getStackTraceString(new Throwable()));
                                        }
                                    });
                        }
                    }
            );
        }catch (Throwable e){
            log("Error = " + e.getMessage());
        }
    }


    /**
     * hookDialog 解决窗口不能被关闭
     *
     * @param classLoader 类加载器
     */
    private static void hookDialog(ClassLoader classLoader) {
        Class<?> cls = XposedHelpers.findClass("android.app.Dialog", classLoader);
        XposedHelpers.findAndHookMethod(cls, "onBackPressed", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setBooleanField(param.thisObject, "mCancelable", true);
                super.beforeHookedMethod(param);
            }
        });

    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        log(startupParam.modulePath + ", " + startupParam.startsSystemServer + "");
        PATH = startupParam.modulePath.replace("/base.apk", "/lib/arm");
    }

    public static void log(String msg) {
        XposedBridge.log(">>[AHook]<< --- " + msg);
    }


}
