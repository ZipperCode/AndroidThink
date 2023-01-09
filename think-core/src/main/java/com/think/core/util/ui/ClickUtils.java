package com.think.core.util.ui;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

/**
 * 点击处理工具栏
 */
public final class ClickUtils {

    /**
     * 默认点击时间间期（毫秒）
     */
    private final static long DEFAULT_INTERVAL_MILLIS = 1000;

    /**
     * 最近一次点击的时间（全局）
     */
    private static long sGlobalLastClickTime;

    /**
     * 是否快速双击了
     * @return true，快速双击
     */
    public static boolean isFastDoubleClick(){
        return isFastDoubleClick(DEFAULT_INTERVAL_MILLIS);
    }

    public static boolean isFastDoubleClick(long interval){
        long time = SystemClock.uptimeMillis();
        long diff = time - sGlobalLastClickTime;
        if (diff <= interval){
            return true;
        }
        sGlobalLastClickTime = time;
        return false;
    }


    public static boolean isFastDoubleClick(View view) {
        return isFastDoubleClick(view, DEFAULT_INTERVAL_MILLIS);
    }

    /**
     * 指定view的双击判断
     * @param view view
     * @param interval 间隔时间
     * @return
     */
    public static boolean isFastDoubleClick(View view, long interval) {
        if (view == null){
            return isFastDoubleClick();
        }
        int viewId = view.getId();
        Object lastClickTime = view.getTag(viewId);
        if (lastClickTime == null){
            long time = SystemClock.uptimeMillis();
            view.setTag(viewId, time);
            return false;
        }
        if (lastClickTime instanceof Long){
            long time = SystemClock.uptimeMillis();
            long diff = time - (long) lastClickTime;
            if (diff <= interval){
                return true;
            }
            view.setTag(viewId, time);
            return false;
        }
        return isFastDoubleClick();
    }

    private static long sGlobalDoubleClickTime;


    /**
     * 是否指定时间内双击了，
     * @param interval 间隔时间
     * @return true：指定时间内双击了，false：指定时间内未双击
     */
    public static boolean isIntervalDoubleClick(long interval) {
        long time = SystemClock.uptimeMillis();
        long diff = time - sGlobalDoubleClickTime;
        if (diff >= interval){
            sGlobalDoubleClickTime = time;
            return false;
        }
        return true;
    }

    /**
     * 给view添加点击缩放动画
     * @param view view
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void addScaleAnim(View view){
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                v.animate().cancel();
                v.animate().scaleXBy(1f).scaleYBy(1f)
                        .scaleX(.9f).scaleY(.9f).setDuration(50).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                view.animate().scaleXBy(.9f).scaleYBy(.9f)
                        .scaleX(1f).scaleY(1f).setDuration(50).start();
            }
            return v.onTouchEvent(event);
        });
    }
}
