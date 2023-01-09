package com.think.core.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.think.core.util.ui.NotchScreenHelper;
import com.think.core.util.ui.ScreenUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener {

    private static Field sStyleableWindowCache;
    private static Method sIsTranslucentOrFloatingMethodCache;
    private static Field sMActivityInfoFieldCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            // 8.0设备透明Activity奔溃
            fixOrientation();
        }
        super.onCreate(savedInstanceState);
        if (needHideSystemUi()) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
            setSystemUiInVisibilityHide();
        }
        NotchScreenHelper.openNotchSupport(getWindow());
    }

    /**
     * 是否隐藏状态栏
     */
    protected boolean needHideSystemUi() {
        return false;
    }

    /**
     * 导航栏设置透明
     */
    protected void translucentNavigation() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    /**
     * 实现点击键盘外空白位置关闭软键盘显示
     *
     * @param ev 事件
     * @return true
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (ScreenUtils.isHideKeyBoard(view, ev)) {
                ScreenUtils.closeKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected void setSystemUiInVisibilityHide() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if (visibility == View.VISIBLE) {
            setSystemUiInVisibilityHide();
        }
    }

    @SuppressLint("PrivateApi")
    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            if (sStyleableWindowCache == null){
                sStyleableWindowCache = Class.forName("com.android.internal.R$styleable").getField("Window");
            }
            int [] styleableRes = (int[]) sStyleableWindowCache.get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            if (sIsTranslucentOrFloatingMethodCache == null){
                sIsTranslucentOrFloatingMethodCache = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            }
            Method m = sIsTranslucentOrFloatingMethodCache;
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    @SuppressLint("DiscouragedPrivateApi")
    private void fixOrientation(){
        try {
            if (sMActivityInfoFieldCache == null){
                sMActivityInfoFieldCache = Activity.class.getDeclaredField("mActivityInfo");
            }
            Field field = sMActivityInfoFieldCache;
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
