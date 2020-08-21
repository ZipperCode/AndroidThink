package com.think.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ScreentUtils {

    public static int px2dp(Context context, float px) {
        return (int) (px / getDensity(context) + 0.5f);
    }

    public static int dp2px(Context context, float dp){
        return (int) (dp * getDensity(context) + 0.5f);
    }

    public static int px2sp(Context context,float px){
        return (int) (px / getScaledDensity(context) + 0.5f);
    }

    public static int sp2px(Context context, float sp){
        return (int)(sp * getScaledDensity(context) + sp);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 判断当前是否是横屏
      * @param context 上下文
     * @return true 是， false 否
     */
    public static boolean getCurrentOrientation(Context context) {
        // 获取设置的配置信息
        Configuration mConfiguration = context.getResources().getConfiguration();
        if(mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    /**
     * 关闭软键盘
     * @param activity act
     */
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            if(imm.isActive()){
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    /**
     * 是否已经隐藏软键盘
     * @param view view
     * @param motionEvent 事件
     * @return true 隐藏
     */
    public static boolean isHideKeyBoard(View view, MotionEvent motionEvent){
        if(view instanceof EditText){
            int[] location = new int[2];
            view.getLocationInWindow(location);
            int left = location[0],
                    top = location[1],
                    bottom = top + view.getHeight(),
                    right = left + view.getWidth();
            // 点击EditText的事件，忽略它。
            return  !(motionEvent.getX() > left && motionEvent.getX() < right
                    && motionEvent.getY() > top && motionEvent.getY() < bottom);
        }
        return false;
    }

}
