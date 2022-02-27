package com.think.xposed.dex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.think.xposed.AHook;
import com.think.xposed.BuildConfig;
import com.think.xposed.JniHelper;
import com.think.xposed.Utils;
import com.think.xposed.crypto.CryptoHook;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DexHook {

    String[] pks = {"com.bet007.mobile.score"};

    private final Map<String, CryptoHook> stringSymmetricHookMap = new HashMap<>();

    public static String PKS = "com.a10155.wochanglian";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam){
        if (lpparam.packageName.equals(PKS)) {
            AHook.log("packageName = " + lpparam.packageName);
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
                                AHook.log("cat " + file.getAbsolutePath());
                                Process process = Runtime.getRuntime().exec("cat " + file.getAbsolutePath());
                                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                nativeLibDir = bufferedReader.readLine();
                            } catch (Exception e) {
                                e.printStackTrace();
                                AHook.log("packageName = " + e.toString());
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
                            nativeLibDir = AHook.PATH;
                        }
                        AHook.log("nativeLibDir = " + nativeLibDir);
                        if (!TextUtils.isEmpty(nativeLibDir)) {
                            Collection<String> libPaths = Collections.singletonList(nativeLibDir);
                            Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
                            Object pathListSelf = pathListField.get(selfClassLoader);
                            XposedHelpers.callMethod(pathListSelf, "addNativePath", libPaths);
//                            XposedHelpers.callMethod(Runtime.getRuntime(), "loadLibrary0", classLoader, "dex_helper");
                            JniHelper.init();
                            if (dexCookie.size() > 0) {
                                JniHelper.dexFileByCookie(lpparam.packageName, dexCookie.toArray());
                            }
                        }

                    }

                }
            });

        }
    }

    private static void hookNativeLibrary(Context application) {
        try {
            @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> list = application.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : list) {
                if (info.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    Log.i("TAG", info.nativeLibraryDir);
                    ClassLoader classLoader = AHook.class.getClassLoader();
                    Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
                    Object pathList = pathListField.get(classLoader);
                    Collection<String> libPaths = Collections.singletonList(new File(info.nativeLibraryDir).getAbsolutePath());
                    XposedHelpers.callMethod(pathList, "addNativePath", libPaths);
                }
            }
        } catch (Throwable throwable) {
            Log.e("TAG", "JNILoadHelper load library error:", throwable);
        }
    }
}
