package com.think.xposed;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.think.xposed.crypto.CryptoHook;
import com.think.xposed.http.HttpHook;

import java.lang.reflect.Method;

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
        hookPks = packageName;

        if(cryptoHook == null){
            cryptoHook = new CryptoHook(1000, lpparam.classLoader,false);
            cryptoHook.hook();
        }

        if(httpHook == null){
            httpHook = new HttpHook(lpparam.classLoader,true);
            httpHook.hook();
        }

        log("symmetricHook hook ====> " + hookPks + ", symmetricHook = " + cryptoHook);
        log("httpHook hook ====> " + hookPks + ", httpHook = " + httpHook);
        if(!"com.think.xposed".equals(packageName)){
            hookApplication(lpparam);
        }
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
                            log("afterHookedMethod " + param.thisObject);
                            try {
                                Context context = (Context) param.thisObject;
                                Intent intent = new Intent(context, TargetService.class);
                                log("context = " + context + ", 开始绑定 TargetService");
                                context.bindService(intent, new ServiceConnection() {
                                    @Override
                                    public void onServiceConnected(ComponentName name, IBinder service) {
                                        log("ServiceConnection#onServiceConnected component = " + name);
                                        try{
                                            IStartInterface iStartInterface = (IStartInterface) service;
                                            iStartInterface.callback(new ILocalToServer.Stub() {
                                                @Override
                                                public void callback(Bundle bundle) throws RemoteException {
                                                    log("thinkXposed -> 目标进程: " + lpparam.processName);
                                                    int startCode = bundle.getInt("ACTIVITY",0);
                                                    if(startCode == 100){
                                                        try{
                                                            String packageName = bundle.getString("PKS","");
                                                            String componentName = bundle.getString("NAME","");
                                                            log("thinkXposed -> 目标进程: " + lpparam.processName + ", packageName = " +packageName + ", Component = " + componentName);
                                                            Intent intent1 = new Intent();
                                                            intent1.setComponent(new ComponentName(packageName, componentName));
                                                            context.startActivity(intent);
                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                            log("Error = " + e.getMessage());
                                                        }
                                                    }
                                                }
                                            });
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            log("Error = " + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onServiceDisconnected(ComponentName name) {
                                        log("ServiceConnection#onServiceDisconnected component = " + name);
                                    }
                                }, Context.BIND_AUTO_CREATE);
                            }catch (Throwable e){
                                log("Error = " + e.getMessage());
                            }

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
