package com.think.xposed;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
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
//        XposedHelpers.findAndHookMethod("android.app.ContextImpl", lpparam.classLoader, "getSharedPreferences", String.class, int.class, new XC_MethodHook(){
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                String name = (String) param.args[0];
//                log("ContextImpl >> getSharedPreferences >> name = " + name +", sp = " + param.getResult());
//                SharedPreferences sharedPreferences = (SharedPreferences) param.getResult();
//                XposedHelpers.findAndHookMethod(sharedPreferences.getClass(),
//                        "getString", String.class, String.class, new XC_MethodHook(){
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        String key = (String) param.args[0];
//                        log("SharedPreferences >> name = " + key + ", value = " + param.getResult());
//                        if (key.equals("UBIMiAeUt")){
//                            log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                            log("key = UBIMiAeUt, value = " + param.getResult());
//                            log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                        }
//                    }
//                });
//            }
//        });

//        Class<?> classIfExists = XposedHelpers.findClassIfExists("com.UCMobile.model.a.i", lpparam.classLoader);
//        if (classIfExists == null){
//            log("com.UCMobile.model.a == null");
//            return;
//        }
//        log("com.UCMobile.model.a >> Class >> " + classIfExists);
//
//        XposedHelpers.findAndHookMethod(classIfExists, "i", String.class, String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                String key = (String) param.args[0];
//                if (key.equals("UBIMiAeUt")){
//                    log("com.UCMobile.model.a$i.f3222a >> name = " + key + ", value = " + param.getResult());
//                }
//            }
//        });

        Class<?> classIfExists2 = XposedHelpers.findClassIfExists("com.uc.base.secure.j", lpparam.classLoader);
        if (classIfExists2 == null){
            log("com.uc.base.secure.j == null");
            return;
        }
        log("com.uc.base.secure.j = " + classIfExists2);
        XposedHelpers.findAndHookMethod(classIfExists2, "a", int.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log("com.uc.base.secure.j >> a >> " + Log.getStackTraceString(new Exception()));
                int i = (int) param.args[0];
                String str = (String) param.args[1];
                String str2 = (String) param.args[2];
                if (i == 1){
                    log("str = " + str +", str2 = " + str2);
                    Class<?> anonymousClass1 = XposedHelpers.findClassIfExists("com.uc.base.secure.j$AnonymousClass1", param.thisObject.getClass().getClassLoader());
                    log("anonymousClass1 >> " + anonymousClass1);
                    if (anonymousClass1 != null){
                        int[] f34514a = (int[]) XposedHelpers.getStaticObjectField(anonymousClass1, "f34514a");
                        log("anonymousClass1 >> f34514a = " + Arrays.toString(f34514a));
                    }
                };
            }
        });

        Class<?> classIfExists3 = XposedHelpers.findClassIfExists("com.uc.base.secure.a", lpparam.classLoader);
        if (classIfExists3 == null){
            log("com.uc.base.secure.a == null");
            return;
        }
        log("com.uc.base.secure.a = " + classIfExists3);
        XposedHelpers.findAndHookMethod(classIfExists3, "c", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                log("com.uc.base.secure.a >> c >> "+Log.getStackTraceString(new Exception()));
                String param1 = (String) param.args[0];
                String param2 = (String) param.args[1];
                String result = (String) param.getResult();
                log("com.uc.base.secure.a >> c >> param1 = "+ param1 + ",param2 = " + param2 +", result = "+result);
//                Class<?> anonymousClass1 = XposedHelpers.findClassIfExists("com.uc.base.secure.j$AnonymousClass1", param.thisObject.getClass().getClassLoader());
//                log("anonymousClass1 >> " + anonymousClass1);
//                if (anonymousClass1 != null){
//                    int[] f34514a = (int[]) XposedHelpers.getStaticObjectField(anonymousClass1, "f34514a");
//                    log("anonymousClass1 >> f34514a = " + Arrays.toString(f34514a));
//                }
                // 134cecc1a-d5b0-b1ff-b534-4852c36b45aesy5th908xb9bmgiz2ssy0cykzezkq1jf
                Class<?> securityGuardParamContextCls = XposedHelpers.findClassIfExists("com.alibaba.wireless.security.open.SecurityGuardParamContext", lpparam.classLoader);
                log("securityGuardParamContextCls = " + securityGuardParamContextCls);
                XposedHelpers.callStaticMethod()
                if (securityGuardParamContextCls != null){
                    XposedHelpers.findAndHookMethod("com.alibaba.wireless.security.open.securesignature.ISecureSignatureComponent",
                            lpparam.classLoader, "signRequest", securityGuardParamContextCls, String.class, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    Object thisObject = param.thisObject;
                                    String str = (String) param.args[1];
                                    log("com.alibaba.wireless.security.open.securesignature.ISecureSignatureComponent >> " + thisObject +", str = " + str);
                                }
                            });
                }

            }
        });

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
            log("findAndHookMethod onCreate = " + onCreate);
            XposedHelpers.findAndHookMethod(
                    cls,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

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
