package com.think.xposed;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AHook implements IXposedHookLoadPackage , IXposedHookZygoteInit {


    private static final List<String> mCanCloseDialogPackageName = new ArrayList<>();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
        System.out.println("==================================================================================");
        XposedBridge.log("AHook >> handleLoadPackage ====> " + packageName);
        System.out.println("AHook >> handleLoadPackage ====> " + packageName);
        hookMessageDigest(lpparam.classLoader);
        hookSymmetric(lpparam.classLoader);
    }


    private static void hookMessageDigest(ClassLoader classLoader) {
        Class<?> cls = XposedHelpers.findClass("java.security.MessageDigest", classLoader);

        if (cls != null) {
            XposedHelpers.findAndHookMethod(cls, "getInstance", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "] beforeHookedMethod ====> 摘要的md5算法为 ： " + param.args[0]);
                }
            });

            XposedHelpers.findAndHookMethod(cls, "update", Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(cls, "digest", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] result = (byte[]) param.getResult();
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5密文为 ： " + Utils.byteHexToString(result));
                }
            });


            XposedHelpers.findAndHookMethod(cls, "digest", Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] result = (byte[]) param.getResult();
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5密文为 ： " + Utils.byteHexToString(result));
                }
            });


        }
    }

    private static void hookHmac(ClassLoader classLoader) {
        Class<?> macCls = XposedHelpers.findClass("javax.crypto.Mac", classLoader);
        if (macCls != null) {
            XposedHelpers.findAndHookMethod(macCls, "getInstance", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5算法为 ： " + param.args[0]);
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "init", Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5 key 为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "update", Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "doFinal", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] result = (byte[]) param.getResult();
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5密文为 ： " + Utils.byteHexToString(result));
                }
            });
        }
    }

    private static void hookSymmetric(ClassLoader classLoader) {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls != null) {

            XposedHelpers.findAndHookMethod(cipherCls, "getInstance", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 对称加密算法为 ： " + param.args[0]);
                }
            });

            XposedHelpers.findAndHookMethod(cipherCls, "init", int.class, Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int decryptMode = (int) param.args[0];
                    Serializable serializable = (Serializable) param.args[1];

                    if(serializable instanceof SecretKeySpec){
                        SecretKeySpec secretKeySpec = (SecretKeySpec) serializable;
                        XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥为 ： " + Utils.byteHexToString(secretKeySpec.getEncoded()));
                    }
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法Mode为 ： " + decryptMode);
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥对象为 ： " + serializable);
                }
            });


            XposedHelpers.findAndHookMethod(cipherCls, "doFinal", Byte.TYPE, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] textBytes = (byte[]) param.args[0];
                    byte[] result = (byte[]) param.getResult();
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法原文为 ： " + Utils.byteHexToString(textBytes));
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法密文为 ： " + Utils.byteHexToString(result));
                }
            });
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
        XposedBridge.log("AHook >>> "+ startupParam.modulePath + ", " + startupParam.startsSystemServer);
    }
}
