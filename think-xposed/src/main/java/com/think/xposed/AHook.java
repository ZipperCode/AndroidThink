package com.think.xposed;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.think.xposed.crypto.SymmetricHook;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class AHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String TAG = AHook.class.getSimpleName();

    String[] pks = {"com.bet007.mobile.score"};

    private final Map<String, SymmetricHook> stringSymmetricHookMap = new HashMap<>();

    public static String PKS = "com.a10155.wochanglian";

    public static String PATH = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
//        XposedBridge.log("AHook >> handleLoadPackage ====> " + packageName);
//        System.out.println("AHook >> handleLoadPackage ====> " + packageName);

//        for (int i = 0; i < pks.length; i++) {
//            if (pks[i].equals(packageName)) {
////                hookMessageDigest(lpparam.classLoader);
////                hookSymmetric(lpparam.classLoader);
//                stringSymmetricHookMap.put(packageName,new SymmetricHook(50,lpparam.classLoader));
//            }
//        }
        if (lpparam.packageName.equals(PKS)) {
            log("packageName = " + lpparam.packageName);
            final Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
            final Class<?> pathListClass = XposedHelpers.findClass("dalvik.system.DexPathList", lpparam.classLoader);
            final Class<?> pathListElementClass = XposedHelpers.findClass("dalvik.system.DexPathList$Element", lpparam.classLoader);
            final Field dexElementField = XposedHelpers.findField(pathListClass, "dexElements");
            final Field dexFileField = XposedHelpers.findField(pathListElementClass, "dexFile");
            final Field cookieField = XposedHelpers.findField(DexFile.class, "mInternalCookie");
            final ClassLoader selfClassLoader = AHook.class.getClassLoader();
            XposedHelpers.findAndHookMethod(Class.class, "getClassLoader", new XC_MethodHook() {
                String nativeLibDir = null;
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
//                    log("afterHookedMethod params " + Arrays.toString(param.args) + "result = " + param.getResult());
                    Set<Object> dexCookie = new HashSet<>();
                    ClassLoader classLoader = (ClassLoader) param.getResult();
                    if (classLoader instanceof BaseDexClassLoader) {
                        AHook.log("hook classLoader = " + classLoader);
                        Object pathList = pathListField.get(classLoader);
                        AHook.log("hook pathList = " + pathList);
                        Object[] dexElements = (Object[]) dexElementField.get(pathList);
                        Utils.i("getElementObject => dexElements = " + Arrays.toString(dexElements));
                        for (Object dexElement : dexElements) {
                            Object dexFile = dexFileField.get(dexElement);
                            if (dexFile != null) {
                                Object cookie = cookieField.get(dexFile);
                                if (cookie != null) {
                                    dexCookie.add(cookie);
                                }
                            }
                        }
                        Utils.i("查找到的DexFile Cookie 数量为：" + dexCookie);

                        if(nativeLibDir == null){
                            BufferedReader bufferedReader = null;
                            try {
                                XposedHelpers.callStaticMethod(Environment.class, "setUserRequired", false);
                                File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "__share");
                                log("cat " + file.getAbsolutePath());
                                Process process = Runtime.getRuntime().exec("cat " + file.getAbsolutePath());
                                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                nativeLibDir = bufferedReader.readLine();
                            } catch (Exception e) {
                                e.printStackTrace();
                                log("packageName = " + e.toString());
                            } finally {
                                try {
                                    if (bufferedReader != null) {
                                        bufferedReader.close();
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        }

                        if(TextUtils.isEmpty(nativeLibDir)){
                            nativeLibDir = PATH;
                        }
                        log("nativeLibDir = " + nativeLibDir);
                        if (!TextUtils.isEmpty(nativeLibDir)) {
                            Collection<String> libPaths = Collections.singletonList(nativeLibDir);
                            Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
                            Object pathListSelf = pathListField.get(selfClassLoader);
                            XposedHelpers.callMethod(pathListSelf, "addNativePath", libPaths);
//                            XposedHelpers.callMethod(Runtime.getRuntime(), "loadLibrary0", classLoader, "dex_helper");
                            JniHelper.init();
                            if (dexCookie.size() > 0) {
                                JniHelper.dexFileByCookie(packageName, dexCookie.toArray());
                            }
                        }


                    }

                }
            });

        }

    }




    private static void hookNativeLibrary(Context application) {
        try {
            List<ApplicationInfo> list = application.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : list) {
                if (info.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    Log.i(TAG, info.nativeLibraryDir);
                    ClassLoader classLoader = AHook.class.getClassLoader();
                    Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
                    Object pathList = pathListField.get(classLoader);
                    Collection<String> libPaths = Collections.singletonList(new File(info.nativeLibraryDir).getAbsolutePath());
                    XposedHelpers.callMethod(pathList, "addNativePath", libPaths);
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "JNILoadHelper load library error:", throwable);
        }
    }


    private static void hookMessageDigest(ClassLoader classLoader) {
        Class<?> cls = XposedHelpers.findClass("java.security.MessageDigest", classLoader);

        if (cls != null) {
            XposedHelpers.findAndHookMethod(cls, "getInstance",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("AHook >> [MessageDigest] getInstance(String) beforeHookedMethod ====> 摘要的md5算法为 ： " + param.args[0]);
                        }
                    });

            XposedHelpers.findAndHookMethod(cls, "update",
                    byte[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (param.args[0] != null) {
                                String text = new String((byte[]) param.args[0]);
                                String textUtf8 = new String((byte[]) param.args[0], "UTF-8");
                                XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符为 ：%s ",
                                        param.thisObject, text));
                                XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符[UTF-8]为 ：%s ",
                                        param.thisObject, textUtf8));
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(cls, "update",
                    byte[].class, int.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            int offset = (int) param.args[1];
                            int len = (int) param.args[2];
                            String text = new String((byte[]) param.args[0], offset, len);
                            XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符为 ：%s , offset=%d, len=%d ",
                                    param.thisObject, text, offset, len));

                        }
                    });

            XposedHelpers.findAndHookMethod(cls, "digest", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] result = (byte[]) param.getResult();
                    String text = new String(result);
                    String textUtf8 = new String(result, "UTF-8");
                    XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符为 ：%s ",
                            param.thisObject, text));
                    XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符[UTF-8]为 ：%s ",
                            param.thisObject, textUtf8));
                    XposedBridge.log("AHook >> [" + param.thisObject + "][digest(void)]  beforeHookedMethod ====> 摘要的md5密文为 ： "
                            + Utils.byteHexToString(result));
                }
            });


            XposedHelpers.findAndHookMethod(cls, "digest", byte[].class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String text = new String((byte[]) param.args[0]);
                    String textUtf8 = new String((byte[]) param.args[0], "UTF-8");
                    XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符为 ：%s ",
                            param.thisObject, text));
                    XposedBridge.log(String.format("AHook >> [%s][update(byte[])]  beforeHookedMethod ====> 摘要的md5原文字符[UTF-8]为 ：%s ",
                            param.thisObject, textUtf8));
                    byte[] result = (byte[]) param.getResult();
                    XposedBridge.log("AHook >> [" + param.thisObject + "][digest(byte[])]  beforeHookedMethod ====> 摘要的md5密文为 ： "
                            + Utils.byteHexToString(result));
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
                    XposedBridge.log("AHook >> [Mac]  getInstance(String) beforeHookedMethod ====> 摘要的md5算法为 ： " + param.args[0]);
                }
            });


            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
                SecretKey key = keyGen.generateKey();
                // 打印随机生成的key:
                byte[] skey = key.getEncoded();
                Mac mac = Mac.getInstance("HmacMD5");
                mac.init(key);
                mac.update("HelloWorld".getBytes("UTF-8"));
                byte[] result = mac.doFinal();
            } catch (Exception e) {
                e.printStackTrace();
            }

            XposedHelpers.findAndHookMethod(macCls, "init", byte[].class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5 key 为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "update", byte[].class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String text = new String((byte[]) param.args[0], "UTF-8");
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + text);
//                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "update", byte[].class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String text = new String((byte[]) param.args[0], (int) param.args[1], (int) param.args[2]);
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + text);
//                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                }
            });

            XposedHelpers.findAndHookMethod(macCls, "update", ByteBuffer.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String text = new String(((ByteBuffer) param.args[0]).array(), "UTF-8");
                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + text);
//                    XposedBridge.log("AHook >> [" + param.thisObject + "]  beforeHookedMethod ====> 摘要的md5原文为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
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
                    XposedBridge.log("AHook >> [Cipher]  beforeHookedMethod ====> 对称加密算法为 ： " + param.args[0]);
                }
            });

            XposedHelpers.findAndHookMethod(cipherCls, "init", int.class,
                    byte[].class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            int decryptMode = (int) param.args[0];
                            Serializable serializable = (Serializable) param.args[1];

                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法Mode为 ： " + decryptMode);
                            if (serializable instanceof SecretKeySpec) {
                                SecretKeySpec secretKeySpec = (SecretKeySpec) serializable;
                                XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥为 ： " + Utils.byteHexToString(secretKeySpec.getEncoded()));
                                XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥对象为 ： " + serializable);
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    cipherCls,
                    "init",
                    int.class,
                    byte[].class,
                    AlgorithmParameterSpec.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            int decryptMode = (int) param.args[0];
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法Mode为 ： " + decryptMode);
                            if (param.args[1] instanceof SecretKeySpec) {
                                SecretKeySpec secretKeySpec = (SecretKeySpec) param.args[1];
                                XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥为 ： " + Utils.byteHexToString(secretKeySpec.getEncoded()));
//                        XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法秘钥对象为 ： " + serializable);
                            }

                            if (param.args[2] instanceof IvParameterSpec) {
                                IvParameterSpec iv = (IvParameterSpec) param.args[2];
                                String ivString = new String(iv.getIV());
                                XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法iv向量为 ： " + ivString);
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    cipherCls,
                    "update",
                    byte[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            byte[] data = (byte[]) param.args[0];
                            String str = new String(data, "UTF-8");
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法原文数据为 ： " + Utils.byteHexToString(data));
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法原文字符为 ： " + str);
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    cipherCls,
                    "update",
                    byte[].class,
                    int.class,
                    int.class,
                    new XC_MethodHook() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            byte[] data = (byte[]) param.args[0];
                            int offset = (int) param.args[1];
                            int len = (int) param.args[2];
                            String str = new String(data, offset, len);
                            XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法原文数据为 ：%s , offset=%d, len=%d ",
                                    param.thisObject, Utils.byteHexToString(data, offset, len), offset, len));
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法原文字符为 ： " + str);
                        }
                    });
            XposedHelpers.findAndHookMethod(cipherCls, "doFinal", byte[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] textBytes = (byte[]) param.args[0];
                    byte[] result = (byte[]) param.getResult();
                    String str = new String(textBytes, "UTF-8");
                    XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法原文为 ：%s ",
                            param.thisObject, Utils.byteHexToString(textBytes)));
                    XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法原文字符为 ：%s ",
                            param.thisObject, str));
                    String resString = new String(result, "UTF-8");
                    XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法密文为 ：%s ",
                            param.thisObject, Utils.byteHexToString(result)));
                    XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法密文字符为 ：%s ",
                            param.thisObject, resString));
                }
            });

            XposedHelpers.findAndHookMethod(
                    cipherCls,
                    "doFinal",
                    byte[].class,
                    int.class,
                    int.class,
                    new XC_MethodHook() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            byte[] data = (byte[]) param.args[0];
                            int offset = (int) param.args[1];
                            int len = (int) param.args[2];
                            byte[] result = (byte[]) param.getResult();
                            XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法原文数据为 ：%s , offset=%d, len=%d ",
                                    param.thisObject, Utils.byteHexToString(data, offset, len), offset, len));
                            String str = new String(data, offset, len);
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法原文字符为 ： " + str);
                            XposedBridge.log("AHook >> [" + param.thisObject + "]  afterHookedMethod ====> 对称算法密文为 ： "
                                    + Utils.byteHexToString(result));

                            String resUtf8String = new String(result, "UTF-8");
                            String resString = new String(result);
                            XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法密文为 ：%s ",
                                    param.thisObject, Utils.byteHexToString(result)));
                            XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法密文字符为 ：%s ",
                                    param.thisObject, resString));
                            XposedBridge.log(String.format("AHook >> [%s]  afterHookedMethod ====> 对称算法密文字符[UTF8]为 ：%s ",
                                    param.thisObject, resUtf8String));

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
        XposedBridge.log("AHook >>> " + startupParam.modulePath + ", " + startupParam.startsSystemServer + "");
        PATH = startupParam.modulePath.replace("/base.apk", "/lib/arm");
    }

    public static void log(String msg) {
        XposedBridge.log("AHook >>> " + msg);
    }
}
