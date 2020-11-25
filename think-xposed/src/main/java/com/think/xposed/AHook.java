package com.think.xposed;

import java.security.PublicKey;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
        System.out.println("==================================================================================");
        XposedBridge.log("AHook >> handleLoadPackage ====> " + packageName);
        System.out.println("AHook >> handleLoadPackage ====> " + packageName);
        if(packageName.equals("com.think.demo")){
            Class<?> cls = XposedHelpers.findClass("java.security.MessageDigest",lpparam.classLoader);
            if(cls != null){
                XC_MethodHook.Unhook getInstance = XposedHelpers.findAndHookMethod(cls, "getInstance", String.class,new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("AHook >> beforeHookedMethod ====> 摘要的md5算法为 ： " + param.args[0]);
                        super.beforeHookedMethod(param);
                    }
                });
                XposedHelpers.findAndHookMethod(cls, "digest", Byte.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String string = new String((byte[]) param.args[0]);
                        XposedBridge.log("AHook >> beforeHookedMethod ====> 摘要的md5原文为 ： " + string);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        byte[] result = (byte[]) param.getResult();
                        StringBuilder stringBuffer = new StringBuilder();
                        for (byte datum : result) {
                            stringBuffer.append(String.format("%02X", datum & 0xFF));
                        }
                        XposedBridge.log("AHook >> beforeHookedMethod ====> 摘要的md5密文为 ： " + stringBuffer.toString());
                    }
                });
            }
        }
    }

    public static void test(){

    }

}
