package com.think.xposed.dex;

import android.annotation.TargetApi;

import com.think.xposed.AHook;
import com.think.xposed.JniHelper;
import com.think.xposed.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@TargetApi(28)
public class Dex9 {


    public static void hook(ClassLoader classLoader, String packageName) {
        AHook.log("Dex9 hook classLoader = " + classLoader);

        Set<Object> dexCookie = new HashSet<>();
        if (classLoader instanceof BaseDexClassLoader) {
            dexCookie.addAll(getElementObject((BaseDexClassLoader) classLoader));
        }

        Utils.i("查找到的DexFile Cookie 数量为：" + dexCookie);
        if (dexCookie.size() > 0) {
            JniHelper.dexFileByCookie(packageName, dexCookie.toArray());
        }
    }

    public static Set<Object> getElementObject(BaseDexClassLoader classLoader) {
        Utils.i("getElementObject");
        Set<Object> dexCookie = new HashSet<>();
        try {
            Field pathListField = XposedHelpers.findField(BaseDexClassLoader.class, "pathList");
            Object pathList = pathListField.get(classLoader);
            Field dexElementField = XposedHelpers.findField(pathList.getClass(), "dexElements");
            Object[] dexElements = (Object[]) dexElementField.get(pathList);
            Utils.i("getElementObject => dexElements = " + Arrays.toString(dexElements));
            for (Object dexElement : dexElements) {
                Field dexFileField = XposedHelpers.findField(dexElement.getClass(), "dexFile");
                DexFile dexFile = (DexFile) dexFileField.get(dexElement);
                if (dexFile != null) {
                    Field cookieField = XposedHelpers.findField(DexFile.class, "mInternalCookie");
                    Object cookie = cookieField.get(dexFile);
                    if (cookie != null) {
                        dexCookie.add(cookie);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.i("getElementObject e = " + e.toString());
        }

        Utils.i("Hook DexCookie = " + dexCookie);
        return dexCookie;
    }
}
