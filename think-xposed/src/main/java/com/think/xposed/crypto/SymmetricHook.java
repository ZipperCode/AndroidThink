package com.think.xposed.crypto;

import android.annotation.SuppressLint;

import com.think.xposed.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SymmetricHook {

    private final SymmetricCallCache mSymmetricCallCache;

    private final ClassLoader classLoader;

    public SymmetricHook(int size,ClassLoader classLoader) {
        mSymmetricCallCache = new SymmetricCallCache(size);
        this.classLoader = classLoader;
        hook();
    }

    public void hook(){
        hookGetInstance();
        hookInit();
        hookUpdate();
        hookDoFinal();
    }

    public void hookGetInstance() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(cipherCls, "getInstance", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        SymmetricBean symmetricBean = new SymmetricBean();
                        symmetricBean.mInstanceParam = (String) param.args[0];
                        mSymmetricCallCache.put((Cipher) param.getResult(), symmetricBean);
                        XposedBridge.log(String.format("AHook >> [%s][getInstance(String)]  beforeHookedMethod ====> 对称加密算法为 ： %s",
                                param.getResult().toString(), symmetricBean.mInstanceParam));
                    }
                });
    }


    private final XC_MethodHook mInitMethodCallBack = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            if (param.thisObject != null) {
                SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
                if (symmetricBean != null) {
                    int decryptMode = (int) param.args[0];
                    symmetricBean.mInitCryptMode = decryptMode;
                    XposedBridge.log("AHook >> [" + param.thisObject + "][init(...)]  afterHookedMethod ====> 对称算法Mode为 ： " +
                            decryptMode +"\t\t" + "1 -- 加密， 2 -- 解密");
                    if (param.args[1] instanceof SecretKeySpec) {
                        symmetricBean.mInitSecretKey = (SecretKeySpec) param.args[1];
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,SecretKeySpec,..)]  afterHookedMethod ====> 对称算法秘钥为 ： "
                                + Utils.byteHexToString(((SecretKeySpec) param.args[1]).getEncoded()));
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,SecretKeySpec,..)]  afterHookedMethod ====> 对称算法秘钥SecretKeySpec序列化值为 ： "
                                + Utils.serialToString(((SecretKeySpec) param.args[1])));
                    } else if (param.args[1] instanceof Certificate) {
                        symmetricBean.mInitCertificate = (Certificate) param.args[1];
                        XposedBridge.log(String.format("AHook >> [%s][init(int,Certificate)]  afterHookedMethod ====> 对称算法Certificate序列化值为 ：%s",
                                param.thisObject,
                                Utils.serialToString((Certificate) param.args[1])));
                    }else if(param.args[0] instanceof PublicKey){
                        symmetricBean.mInitSecretKey = (PublicKey) param.args[1];
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,PublicKey,..)]  afterHookedMethod ====> 非对称算法公钥为 ： "
                                + Utils.byteHexToString(((PublicKey) param.args[1]).getEncoded()));
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,PublicKey,..)]  afterHookedMethod ====> 非对称算法公钥PublicKey序列化值为 ： "
                                + Utils.serialToString(((PublicKey) param.args[1])));
                    }else if(param.args[0] instanceof PrivateKey){
                        symmetricBean.mInitSecretKey = (PrivateKey) param.args[1];
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,PrivateKey,..)]  afterHookedMethod ====> 非对称算法私钥为 ： "
                                + Utils.byteHexToString(((PrivateKey) param.args[1]).getEncoded()));
                        XposedBridge.log("AHook >> [" + param.thisObject + "][init(int,PrivateKey,..)]  afterHookedMethod ====> 非对称算法私钥PrivateKey序列化值为 ： "
                                + Utils.serialToString(((PrivateKey) param.args[1])));
                    }
                    if (param.args.length > 2) {
                        if (param.args[2] instanceof IvParameterSpec) {
                            symmetricBean.mInitIvParameterSpec = (IvParameterSpec) param.args[2];
                            IvParameterSpec iv = (IvParameterSpec) param.args[2];
                            XposedBridge.log(String.format("AHook >> [%s][init(int,SecretKeySpec,IvParameterSpec)]  afterHookedMethod ====> 对称算法iv向量为 ： %s",
                                    param.thisObject, Utils.byteHexToString(iv.getIV())));
                        }
                    }
                }
            }
        }
    };


    private void hookInit() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                Key.class,
                mInitMethodCallBack);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                Key.class,
                SecureRandom.class,
                mInitMethodCallBack);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                Key.class,
                AlgorithmParameterSpec.class,
                mInitMethodCallBack);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "init",
                int.class,
                Certificate.class,
                mInitMethodCallBack);
    }

    private final XC_MethodHook mUpdateMethodCallback = new XC_MethodHook() {
        @SuppressLint("DefaultLocale")
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (param.thisObject != null) {
                SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
                if (symmetricBean != null) {
                    if (param.args.length == 1) {
                        XposedBridge.log("AHook >> [" + param.thisObject + "][update(byte)]  afterHookedMethod  ");
                        byte[] data = (byte[]) param.args[0];
                        symmetricBean.mUpdateData = data;
                        String str = new String(data);
                        String strUtf8 = new String(data, StandardCharsets.UTF_8);
                        XposedBridge.log("AHook >> [" + param.thisObject + "] ====> 对称算法原文数据为 ： " + Utils.byteHexToString(data));
                        XposedBridge.log("AHook >> [" + param.thisObject + "] ====> 对称算法原文字符为 ： " + str);
                        XposedBridge.log("AHook >> [" + param.thisObject + "] ====> 对称算法原文字符[UTF8]为 ： " + strUtf8);
                    } else if (param.args.length == 2) {
                        if (param.args[0] instanceof ByteBuffer && param.args[1] instanceof ByteBuffer) {
                            XposedBridge.log("AHook >> [" + param.thisObject + "][update(ByteBuffer,ByteBuffer)]  <<afterHookedMethod>> ");
                            ByteBuffer inputBuffer = (ByteBuffer) param.args[0];
                            ByteBuffer outputBuffer = (ByteBuffer) param.args[1];
                            symmetricBean.mUpdateInputBuffer = inputBuffer;
                            symmetricBean.mUpdateOutputBuffer = outputBuffer;
                            XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法原文数据为 ：%s",
                                    param.thisObject, Utils.byteToHexString(inputBuffer)));
                            XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法密文数据为 ：%s",
                                    param.thisObject, Utils.byteToHexString(outputBuffer)));
                        }
                    } else if (param.args.length == 3) {
                        XposedBridge.log("AHook >> [" + param.thisObject + "][update(byte[],int,int)]  afterHookedMethod ");
                        byte[] data = (byte[]) param.args[0];
                        int offset = (int) param.args[1];
                        int length = (int) param.args[1];
                        symmetricBean.mUpdateData = data;
                        symmetricBean.mUpdateOffset = offset;
                        symmetricBean.mUpdateLength = length;
                        String str = new String(data, offset, length);
                        XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法原文数据为 ： %s, offset=%d,len=%s",
                                param.thisObject.toString(), Utils.byteHexToString(data), offset, length));

                        XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法原文数据[Str]为 ： %s ", param.thisObject, str));

                        byte[] resultData = (byte[]) param.getResult();

                        XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法密文数据为 ：%s",
                                param.thisObject, Utils.byteHexToString(resultData)));

                        String result = new String(resultData, StandardCharsets.UTF_8);
                        XposedBridge.log(String.format("AHook >> [%s] ====> 对称算法密文数据[UTF-8]为 ：%s",
                                param.thisObject, result));
                    }
                }
            }
        }
    };

    public void hookUpdate() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(
                cipherCls,
                "update",
                byte[].class,
                mUpdateMethodCallback);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "update",
                byte[].class,/*data*/
                int.class,  /*offset*/
                int.class, /*length*/
                mUpdateMethodCallback);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "update",
                ByteBuffer.class,/*input*/
                ByteBuffer.class,  /*output*/
                mUpdateMethodCallback);

    }


    private final XC_MethodHook mDoFinalMethodCallback = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if(param.thisObject == null){
                XposedBridge.log("AHook >> doFinal afterHookedMethod ====> thisObject == null" );
                return;
            }

            SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
            if (symmetricBean == null) {
                XposedBridge.log("AHook >> doFinal afterHookedMethod ====> cache Object is null " );
                return;
            }

            if (param.args.length == 0) {
                XposedBridge.log(String.format("AHook >> [%s][doFinal()]  afterHookedMethod ", param.thisObject));
                symmetricBean.mDoFinalResult = (byte[]) param.getResult();
                XposedBridge.log(String.format("AHook >> [%s][doFinal()]  afterHookedMethod ===> 加密/解密结果为：%s",
                        param.thisObject,Utils.byteHexToString((byte[]) param.getResult())));

            } else if (param.args.length == 1) {
                symmetricBean.mDoFinalData = (byte[]) param.args[0];
                symmetricBean.mDoFinalResult = (byte[]) param.getResult();
                XposedBridge.log(String.format("AHook >> [%s][doFinal(byte[])]  afterHookedMethod ===> 加密/解密结果为：%s",
                        param.thisObject,Utils.byteHexToString((byte[]) param.getResult())));
            } else if (param.args.length == 3) {
                XposedBridge.log(String.format("AHook >> [%s][doFinal(byte[],int,int)]  afterHookedMethod ", param.thisObject));
                symmetricBean.mDoFinalResult = (byte[]) param.getResult();
                symmetricBean.mDoFinalData = (byte[]) param.args[0];
                symmetricBean.mDoFinalOffset = (int) param.args[1];
                symmetricBean.mDoFinalLength = (int) param.args[2];
                XposedBridge.log(String.format("AHook >> [%s][doFinal(byte[],int,int)]  afterHookedMethod ===> 加密/解密结果为：%s",
                        param.thisObject,Utils.byteHexToString((byte[]) param.getResult())));
            }

            XposedBridge.log(String.format("AHook >> [%s] ==> %s",param.thisObject,symmetricBean));
        }
    };

    public void hookDoFinal() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", classLoader);
        if (cipherCls == null) return;

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "doFinal",
                mDoFinalMethodCallback);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "doFinal",
                byte[].class,/*input*/
                mDoFinalMethodCallback);

        XposedHelpers.findAndHookMethod(
                cipherCls,
                "doFinal",
                byte[].class,/*data*/
                int.class,  /*offset*/
                int.class, /*length*/
                mDoFinalMethodCallback);
    }
}
