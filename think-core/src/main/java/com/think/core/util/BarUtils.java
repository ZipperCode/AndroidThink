package com.think.core.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.think.core.R;

import java.lang.reflect.Field;

public class BarUtils {

    public static void adjustStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initLollipopAndUpper(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initKitkatBar(activity);
        }
    }

    public static void adjustStatusBar(Activity activity, int backgroundColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initLollipopAndUpper(activity, backgroundColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initKitkatBar(activity, backgroundColor);
        }
    }

    public static void initKitkatBar(Activity activity) {
//        initKitkatBar(activity, R.color.colorPrimary);
        initKitkatBar(activity, android.R.color.transparent);
    }

    public static void initKitkatBar(Activity activity, int backgroundColor) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        /*虚拟键盘也透明*/
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        layoutParams.gravity = Gravity.TOP;
        // 自定义状态栏的View，设置FLAG_TRANSLUCENT_STATUS后状态栏的Padding会消失
        View barView = new View(activity);
        barView.setId(R.id.custom_status_bar_id);
        barView.setVisibility(View.VISIBLE);
        setStatusBarColor(activity, backgroundColor);
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        decorView.addView(barView, layoutParams);
        ViewGroup contentView = decorView.findViewById(android.R.id.content);
        ViewGroup childAt = (ViewGroup) contentView.getChildAt(0);
        if (childAt != null) {
            childAt.setFitsSystemWindows(true);
            childAt.setClipToPadding(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initLollipopAndUpper(Activity activity) {
//        initLollipopAndUpper(activity, R.color.colorPrimary);
        initLollipopAndUpper(activity, android.R.color.transparent);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initLollipopAndUpper(Activity activity, int backgroundColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, backgroundColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ViewGroup contentView = window.getDecorView().findViewById(android.R.id.content);
        View childAt = contentView.getChildAt(0);
        if (childAt != null) {
            childAt.setFitsSystemWindows(true);
            childAt.setClipToOutline(true);
        }
    }

    public static int getStatusBarHeight(Activity activity) {
        int x = 0, sbar = 38;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(String.valueOf(field.get(obj)));
            sbar = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("bar height = " + sbar);
        return sbar;
    }

    public static int getStatusBarHeight(Context context) {
        int x = 0, sbar = 38;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(String.valueOf(field.get(obj)));
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("bar height = " + sbar);
        return sbar;
    }

    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            View barView = decorView.findViewById(R.id.custom_status_bar_id);
            if (barView != null) {
                barView.setBackgroundColor(ContextCompat.getColor(activity, colorId));
            }
        }
    }

    @Deprecated
    public static boolean checkHasNavigation(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(realDisplayMetrics);

        return false;
    }

    /**
     * 获取手机导航栏的高度
     *
     * @param context 上下文
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int navigationHeight = resources.getDimensionPixelSize(resourceId);
        return navigationHeight;
    }
}
