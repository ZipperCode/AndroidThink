package com.think.core.util.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 刘海屏处理辅助类
 */
public final class NotchScreenHelper {
    /**
     * 刘海屏的高度
     */
    private static int sCutoutHeight;
    /**
     * 是否包含刘海屏
     */
    private static boolean sHasCutout;

    private static boolean sHasInit;

    public static void initNotchSupport(@NonNull Window firstWindow) {
        if (sHasInit){
            return;
        }
        try {
            if(Build.VERSION.SDK_INT >= 28) {
                if (!sHasCutout) {
                    DisplayCutout displayCutout = firstWindow.getDecorView()
                            .getRootWindowInsets().getDisplayCutout();
                    sHasCutout = displayCutout != null;
                }
                if (sHasCutout){
                    sCutoutHeight = getCutoutHeight(firstWindow);
                }
            } else {
                String manufacturer = Build.BRAND.trim().toLowerCase(Locale.ROOT);
                ClassLoader classLoader = Activity.class.getClassLoader();
                assert classLoader != null;
                switch (manufacturer){
                    case "huawei":
                    case "honor":
                        Class<?> hwNotchSizeUtil = classLoader
                                .loadClass("com.huawei.android.util.HwNotchSizeUtil");
                        Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
                        sHasCutout = (Boolean) get.invoke(hwNotchSizeUtil);
                        if (sHasCutout){
                            Method getNotchSize = hwNotchSizeUtil.getMethod("getNotchSize");
                            int[] res = (int[]) getNotchSize.invoke(hwNotchSizeUtil);
                            if (res != null){
                                sCutoutHeight = res[1];
                            }
                        }
                        break;
                    case "vivo":
                        @SuppressLint("PrivateApi")
                        Class<?> ftFeatureCls = classLoader.loadClass("android.util.FtFeature");
                        Method isFeatureSupport = ftFeatureCls.getMethod("isFeatureSupport", int.class);
                        sHasCutout = (Boolean) isFeatureSupport.invoke(ftFeatureCls, 0x20);
                        if (sHasCutout){
                            sCutoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27,
                                    firstWindow.getContext().getResources().getDisplayMetrics());
                        }
                    case "oppo":
                        sHasCutout =  firstWindow.getContext().getPackageManager()
                                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
                        sCutoutHeight = 80;
                    case "xiaomi":
                    case "miui":
                        @SuppressLint("PrivateApi")
                        Class<?> clz = Class.forName("android.os.SystemProperties");
                        Method getInt = clz.getMethod("getInt", String.class, int.class);
                        sHasCutout = (int) getInt.invoke(clz, "ro.miui.notch", 0) == 1;
                        if (sHasCutout){
                            int res = firstWindow.getContext().getResources()
                                    .getIdentifier("notch_height", "dimen", "android");
                            if (res > 0) {
                                sCutoutHeight = firstWindow.getContext().getResources().getDimensionPixelSize(res);
                            }
                        }
                }
                sHasInit = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
            sHasInit = false;
        }
        sHasInit = true;
    }
    /**
     * 设置支持刘海屏
     */
    public static void openNotchSupport(@NonNull Window window){
        initNotchSupport(window);
        if(Build.VERSION.SDK_INT >= 28){
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static int getCutoutHeight(Window window){
        if (!sHasCutout){
            return 0;
        }

        if (sCutoutHeight > 0){
            return sCutoutHeight;
        }

        WindowInsets rootWindowInsets = window.getDecorView().getRootWindowInsets();
        if (rootWindowInsets == null) {
            return 0;
        }

        DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
        if (displayCutout == null){
            return 0;
        }
        int rotation = window.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0){
            return displayCutout.getSafeInsetTop();
        }else if(rotation == Surface.ROTATION_90){
            return displayCutout.getSafeInsetLeft();
        }else if(rotation == Surface.ROTATION_180){
            return displayCutout.getSafeInsetBottom();
        }else if(rotation == Surface.ROTATION_270){
            return displayCutout.getSafeInsetRight();
        }
        return 0;
    }
}
