package com.think.xposed.crypto;

import com.think.xposed.Utils;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SymmetricHook {

    private static final XC_MethodHook mInitMethodCallBack = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            int decryptMode = (int) param.args[0];
            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法Mode为 ： " + decryptMode);
            if (param.args[1] instanceof SecretKeySpec) {
                SecretKeySpec secretKeySpec = (SecretKeySpec) param.args[1];
                XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥为 ： " + Utils.byteHexToString(secretKeySpec.getEncoded()));
//                        XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥对象为 ： " + serializable);
            }

            if (param.args.length >= 2) {
                if (param.args[2] instanceof IvParameterSpec) {
                    IvParameterSpec iv = (IvParameterSpec) param.args[2];
                    String ivString = new String(iv.getIV());
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法iv向量为 ： " + ivString);
                }
            }

        }
    };

    public static void hookGetInstance(ClassLoader classLoader) {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(cipherCls, "getInstance", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("AHook >> [Cipher]  beforeHookedMethod ====> 对称加密算法为 ： " + param.args[0]);
            }
        });
    }

    private static void hookInit(ClassLoader classLoader) {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                SecretKeySpec.class,
                mInitMethodCallBack);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                SecretKeySpec.class,
                SecureRandom.class,
                mInitMethodCallBack);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                SecretKeySpec.class,
                AlgorithmParameterSpec.class,
                mInitMethodCallBack);
    }
}
