package com.think.xposed.crypto;

import android.annotation.SuppressLint;
import android.util.Log;

import com.think.xposed.BaseHook;
import com.think.xposed.Utils;

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

public class CryptoHook extends BaseHook {

    public static final String TAG = "";

    private final SymmetricCallCache mSymmetricCallCache;

    private final Class<?> cipherCls;

    @Override
    protected String getTag() {
        return TAG;
    }

    public CryptoHook(int size, ClassLoader classLoader,boolean debug) {
        super(classLoader,debug);
        mSymmetricCallCache = new SymmetricCallCache(size);
        cipherCls = XposedHelpers.findClassIfExists("javax.crypto.Cipher", targetClassLoader);
    }

    public void hook() {
        hookGetInstance();
        hookInit();
        hookUpdate();
        hookDoFinal();
    }

    public void hookGetInstance() {
        if (cipherCls == null) return;
        XposedHelpers.findAndHookMethod(cipherCls, "getInstance", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        SymmetricBean symmetricBean = new SymmetricBean();
                        symmetricBean.mInstanceParam = (String) param.args[0];
                        mSymmetricCallCache.put((Cipher) param.getResult(), symmetricBean);
                        log(String.format("[%s][Cipher#getInstance(String)] ====> 对称加密算法为: %s",
                                param.getResult().toString(), symmetricBean.mInstanceParam));
                    }
                }
        );
    }

    private final XC_MethodHook mInitMethodCallBack = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            catExp(()->{
                SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
                if (symmetricBean == null) {
                    log("update ====> cache Object is null ");
                    return;
                }
                int decryptMode = (int) param.args[0];
                symmetricBean.mInitCryptMode = decryptMode;
                log(String.format("(%s)Cipher#init(..) ====> 对称算法Mode为: %s (1 -- 加密， 2 -- 解密)",
                        param.thisObject.toString(), decryptMode + ""));

                if (param.args[1] instanceof SecretKeySpec) {
                    symmetricBean.mInitSecretKey = (SecretKeySpec) param.args[1];
                    symmetricBean.mInitSecretKeyEncodeHex = Utils.byteHexToString(((SecretKeySpec) param.args[1]).getEncoded());
                    symmetricBean.mInitSecretKeySerial = Utils.serialToString(((SecretKeySpec) param.args[1]));
                    log(String.format("(%s)Cipher#init(int,SecretKeySpec,..) ====> 对称算法秘钥为(Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeyEncodeHex));
                    log(String.format("(%s)Cipher#init(int,SecretKeySpec,..) ====> 对称算法秘钥为(Serial Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeySerial));
                } else if (param.args[1] instanceof Certificate) {
                    symmetricBean.mInitCertificate = (Certificate) param.args[1];
                    symmetricBean.mInitCertificateSerial = Utils.serialToString((Certificate) param.args[1]);
                    log(String.format("(%s)Cipher#init(int,Certificate) ====> 对称算法Certificate序列化值为 (Serial Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitCertificateSerial));
                } else if (param.args[0] instanceof PublicKey) {
                    symmetricBean.mInitSecretKey = (PublicKey) param.args[1];
                    symmetricBean.mInitSecretKeyEncodeHex = Utils.byteHexToString(((PublicKey) param.args[1]).getEncoded());
                    symmetricBean.mInitSecretKeySerial = Utils.serialToString(((PublicKey) param.args[1]));
                    log(String.format("(%s)Cipher#init(int,PublicKey,..) ====> 非对称算法公钥为(Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeyEncodeHex));
                    log(String.format("(%s)Cipher#init(int,PublicKey,..) ====> 非对称算法公钥PublicKey序列化值为(Serial Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeySerial));
                } else if (param.args[0] instanceof PrivateKey) {
                    symmetricBean.mInitSecretKey = (PrivateKey) param.args[1];
                    symmetricBean.mInitSecretKeyEncodeHex = Utils.byteHexToString(((PrivateKey) param.args[1]).getEncoded());
                    symmetricBean.mInitSecretKeySerial = Utils.serialToString(((PrivateKey) param.args[1]));
                    log(String.format("(%s)Cipher#init(int,PrivateKey,..) ====> 非对称算法私钥为(Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeyEncodeHex));
                    log(String.format("(%s)Cipher#init(int,PrivateKey,..) ====> 非对称算法私钥PrivateKey序列化值为(Serial Hex): %s",
                            param.thisObject.toString(), symmetricBean.mInitSecretKeySerial));
                }
                if (param.args.length > 2) {
                    if (param.args[2] instanceof IvParameterSpec) {
                        symmetricBean.mInitIvParameterSpec = (IvParameterSpec) param.args[2];
                        IvParameterSpec iv = (IvParameterSpec) param.args[2];
                        String ivHex = Utils.byteHexToString(iv.getIV());
                        symmetricBean.mInitIvParameterSpecHex = ivHex;
                        log(String.format("(%s)Cipher#init(int,PrivateKey,IvParameterSpec) ====> 对称算法iv向量为(Hex): %s",
                                param.thisObject.toString(), ivHex));
                    }
                }
            });
        }
    };

    private void hookInit() {
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
            catExp(()->{
                SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
                if (symmetricBean == null) {
                    log("update ====> cache Object is null ");
                    return;
                }
                if (param.args.length == 1) {
                    byte[] data = (byte[]) param.args[0];
                    byte[] dst = new byte[data.length];
                    System.arraycopy(data,0,dst,0, dst.length);
                    symmetricBean.mUpdateData = dst;
                    String strUtf8 = new String(data, StandardCharsets.UTF_8);
                    String strHex = Utils.byteHexToString(data);
                    log(String.format("(%s)Cipher#update(byte[]) ====> 对称算法原文数据为(Hex): %s",
                            param.thisObject.toString(), strHex));
                    log(String.format("(%s)Cipher#update(byte[]) ====> 对称算法原文数据为(UTF8): %s",
                            param.thisObject.toString(), strUtf8));
                } else if (param.args.length == 2) {
                    if (param.args[0] instanceof ByteBuffer && param.args[1] instanceof ByteBuffer) {

                        ByteBuffer inputBuffer = (ByteBuffer) param.args[0];
                        ByteBuffer outputBuffer = (ByteBuffer) param.args[1];
                        symmetricBean.mUpdateInputBuffer = inputBuffer;
                        symmetricBean.mUpdateOutputBuffer = outputBuffer;

                        String inputHex = Utils.byteHexToString(inputBuffer.array());
                        String input = new String(inputBuffer.array(), StandardCharsets.UTF_8);

                        String outputHex = Utils.byteHexToString(outputBuffer.array());
                        String output = new String(outputBuffer.array(), StandardCharsets.UTF_8);

                        log(String.format("(%s)Cipher#update(ByteBuffer, ByteBuffer) ====> 对称算法原文数据为(Hex): %s",
                                param.thisObject.toString(), inputHex));

                        log(String.format("(%s)Cipher#update(ByteBuffer, ByteBuffer) ====> 对称算法原文数据为(UTF-8): %s",
                                param.thisObject.toString(), input));

                        log(String.format("(%s)Cipher#update(ByteBuffer, ByteBuffer) ====> 对称算法密文数据为(Hex): %s",
                                param.thisObject.toString(), outputHex));

                        log(String.format("(%s)Cipher#update(ByteBuffer, ByteBuffer) ====> 对称算法密文数据为(UTF-8): %s",
                                param.thisObject.toString(), output));
                    }
                } else if (param.args.length == 3) {
                    byte[] data = (byte[]) param.args[0];
                    int offset = (int) param.args[1];
                    int length = (int) param.args[1];
                    symmetricBean.mUpdateData = data;
                    symmetricBean.mUpdateOffset = offset;
                    symmetricBean.mUpdateLength = length;


                    log(String.format("(%s)Cipher#update(byte[],int,int) ====> 对称算法原文数据(Hex): %s, offset = %d, len = %d" ,
                            param.thisObject.toString(), Utils.byteHexToString(data), offset, length));

                    String str = new String(data, offset, length, StandardCharsets.UTF_8);
                    log(String.format("(%s)Cipher#update(byte[],int,int) ====> 对称算法原文数据(UTF-8): %s, offset = %d, len = %d",
                            param.thisObject.toString(), str, offset, length));

                    byte[] resultData = (byte[]) param.getResult();

                    log(String.format("(%s)Cipher#update(byte[],int,int) ====> 对称算法密文数据为(Hex) ：%s",
                            param.thisObject, Utils.byteHexToString(resultData)));

                    String result = new String(resultData, StandardCharsets.UTF_8);
                    log(String.format("(%s)Cipher#update(byte[],int,int) ====> 对称算法密文数据[UTF-8]为 ：%s",
                            param.thisObject, result));
                }
            });
        }
    };

    public void hookUpdate() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", targetClassLoader);
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
        @SuppressLint("DefaultLocale")
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            catExp(()->{
                SymmetricBean symmetricBean = mSymmetricCallCache.get((Cipher) param.thisObject);
                if (symmetricBean == null) {
                    log("doFinal ====> cache Object is null ");
                    return;
                }
                if (param.args.length == 0) {
                    byte[] res = (byte[]) param.getResult();
                    symmetricBean.mDoFinalResult = res;
                    log(String.format("(%s)Cipher#doFinal() ===> 加密/解密结果为：%s", param.thisObject, Utils.byteHexToString(res)));
                } else if (param.args.length == 1 && param.args[0] instanceof byte[]) {
                    symmetricBean.mDoFinalData = (byte[]) param.args[0];
                    symmetricBean.mDoFinalResult = (byte[]) param.getResult();

                    log(String.format("(%s)Cipher#doFinal(byte[]) ===> 加密前参数为(Hex)：%s", param.thisObject,
                            Utils.byteHexToString(symmetricBean.mDoFinalData)));
                    log(String.format("(%s)Cipher#doFinal(byte[]) ===> 加密/解密结果为(Hex)：%s", param.thisObject,
                            Utils.byteHexToString(symmetricBean.mDoFinalResult)));

                } else if (param.args.length == 3) {
                    symmetricBean.mDoFinalResult = (byte[]) param.getResult();
                    symmetricBean.mDoFinalData = (byte[]) param.args[0];
                    symmetricBean.mDoFinalOffset = (int) param.args[1];
                    symmetricBean.mDoFinalLength = (int) param.args[2];

                    int offset = symmetricBean.mDoFinalOffset;
                    int length = symmetricBean.mDoFinalLength;

                    log(String.format("(%s)Cipher#doFinal(byte[],int,int) ====> 对称算法原文数据(Hex): %s, offset = %d, len = %d" ,
                            param.thisObject.toString(), Utils.byteHexToString(symmetricBean.mDoFinalData), offset, length));

                    String str = new String(symmetricBean.mDoFinalData, offset, length, StandardCharsets.UTF_8);
                    log(String.format("(%s)Cipher#doFinal(byte[],int,int) ====> 对称算法原文数据(UTF-8): %s, offset = %d, len = %d",
                            param.thisObject.toString(), str, offset, length));

                    log(String.format("(%s)Cipher#doFinal(byte[],int,int) ===> 加密/解密结果为(Hex)：%s",
                            param.thisObject, Utils.byteHexToString(symmetricBean.mDoFinalResult)));
                }

                log(String.format("AHook >> [%s] ==> %s", param.thisObject, symmetricBean));
                log(Log.getStackTraceString(new Throwable()));
            });

        }
    };

    public void hookDoFinal() {
        Class<?> cipherCls = XposedHelpers.findClass("javax.crypto.Cipher", targetClassLoader);
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
