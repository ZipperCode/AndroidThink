package com.think.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Arrays;

public class ScreenUtils {

    /**
     * 应用的dpi
     */
    public static final float APP_DENSITY_DPI = 400f;

    public static float mAppDensity = 0;

    public static float mAppScaleDensity = 0;

    public static int px2dp(Context context, float px) {
        return (int) (px / getDensity(context) + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        return (int) (dp * getDensity(context) + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        return (int) (px / getScaledDensity(context) + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        return (int) (sp * getScaledDensity(context) + sp);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 判断当前是否是横屏
     *
     * @param context 上下文
     * @return true 是， false 否
     */
    public static boolean getCurrentOrientation(Context context) {
        // 获取设置的配置信息
        Configuration mConfiguration = context.getResources().getConfiguration();
        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    /**
     * 关闭软键盘
     *
     * @param activity act
     */
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    /**
     * 是否已经隐藏软键盘
     *
     * @param view        view
     * @param motionEvent 事件
     * @return true 隐藏
     */
    public static boolean isHideKeyBoard(View view, MotionEvent motionEvent) {
        if (view instanceof EditText) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            int left = location[0],
                    top = location[1],
                    bottom = top + view.getHeight(),
                    right = left + view.getWidth();
            // 点击EditText的事件，忽略它。
            return !(motionEvent.getX() > left && motionEvent.getX() < right
                    && motionEvent.getY() > top && motionEvent.getY() < bottom);
        }
        return false;
    }


    public static boolean isFullScreen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            // 应用宽高
            System.out.println("displayMetrics.heightPixels = " + displayMetrics.heightPixels);
            System.out.println("displayMetrics.widthPixels = " + displayMetrics.widthPixels);
            DisplayMetrics displayRealMetrics = new DisplayMetrics();
            // 实际宽高
            defaultDisplay.getRealMetrics(displayRealMetrics);
            System.out.println("displayMetrics1.heightPixels = " + displayRealMetrics.heightPixels);
            System.out.println("displayMetrics1.widthPixels = " + displayRealMetrics.widthPixels);


            // 真实的手机屏幕宽高
            Point outSize = new Point();
            defaultDisplay.getRealSize(outSize);
            System.out.println("outSize.x = " + outSize.x);
            System.out.println("outSize.y = " + outSize.y);
            // 应用宽高
            Point outSize1 = new Point();
            defaultDisplay.getSize(outSize1);
            System.out.println("outSize1.x = " + outSize1.x);
            System.out.println("outSize1.y = " + outSize1.y);
            // 实际宽高
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Display.Mode mode = defaultDisplay.getMode();
                System.out.println("mode.getPhysicalWidth = " + mode.getPhysicalWidth());
                System.out.println("mode.getPhysicalHeight = " + mode.getPhysicalHeight());
            }

            return (displayMetrics.widthPixels == displayRealMetrics.widthPixels
                    && displayMetrics.heightPixels == displayRealMetrics.heightPixels);
        }
        return false;
    }

    public static int[] getScreenWh(Context context){
        int[] screenWh = new int[4];
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            // 应用宽高
            defaultDisplay.getMetrics(displayMetrics);
            DisplayMetrics displayRealMetrics = new DisplayMetrics();
            // 实际宽高
            defaultDisplay.getRealMetrics(displayRealMetrics);
            screenWh[0] = displayRealMetrics.widthPixels;
            screenWh[1] = displayRealMetrics.heightPixels;
            screenWh[2] = displayMetrics.widthPixels;
            screenWh[3] = displayMetrics.heightPixels;
        }
        return screenWh;
    }

    /**
     * 获取系统真实的屏幕宽高
     * @param context 上下文
     * @return int数组 0：宽，1：高
     */
    public static int[] getSysScreenWH(Context context) {
        return Arrays.copyOf(getScreenWh(context),2);
    }
    /**
     * 获取当前应用显示的屏幕宽高
     * @param context 上下文
     * @return int数组 0：宽，1：高
     */
    public static int[] getAppScreenWH(Context context){
        return Arrays.copyOfRange(getScreenWh(context),1,3);
    }

    /**
     * 屏幕的DPI 适配，根据系统的dpi与ui适配的dpi做计算，计算出当前系统适配的dpi
     * px = density * dp, density = dpi / 160 <==> dpi = density * 160
     * ==> px = dp * dpi / 160
     * ==> dpi = px * 160 / dp
     *
     * @param application 上下文
     * @param activity 要设置的窗口
     */
    public static void adjustDensity(final Application application,Activity activity){
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if(mAppDensity == 0){
            System.out.println("App : density = " +displayMetrics.density);
            System.out.println("App : scaledDensity = " +displayMetrics.scaledDensity);
            System.out.println("App : densityDpi = " +displayMetrics.densityDpi);
            mAppDensity = displayMetrics.density;
            mAppScaleDensity = displayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mAppScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }


        float targetDensity = displayMetrics.widthPixels / APP_DENSITY_DPI;
        float targetScaledDensity = targetDensity * (mAppScaleDensity / mAppDensity);
        int targetDpi = (int) (targetDensity * 160);

        System.out.println("target : targetDensity = " + targetDensity);
        System.out.println("target : targetScaledDensity = " + targetScaledDensity);
        System.out.println("target : targetDpi = " + targetDpi);

//        displayMetrics.density = targetDensity;
//        displayMetrics.scaledDensity = targetScaledDensity;
//        displayMetrics.densityDpi = targetDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDpi;
    }

}
