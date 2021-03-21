package com.think.accessibility.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*

object ScreenUtils {
    /**
     * 应用的dpi
     */
    const val APP_DENSITY_DPI = 400f
    var mAppDensity = 0f
    var mAppScaleDensity = 0f

    /**
     * px 转化为 dp
     * @param context 上下文
     * @param px px 像素
     * @return dp值
     */
    fun px2dp(context: Context, px: Float): Int {
        return (px / getDensity(context) + 0.5f).toInt()
    }

    /**
     * dp 单位 转换为像素
     * @param context 上下文
     * @param dp dp值
     * @return px值
     */
    @JvmStatic
    fun dp2px(context: Context, dp: Float): Int {
        return (dp * getDensity(context) + 0.5f).toInt()
    }

    /**
     * px 转化为 sp
     * @param context 上下文
     * @param px px 像素
     * @return sp
     */
    fun px2sp(context: Context, px: Float): Int {
        return (px / getScaledDensity(context) + 0.5f).toInt()
    }

    /**
     * sp 转化 为像素
     * @param context 上下文
     * @param sp sp值
     * @return px值
     */
    fun sp2px(context: Context, sp: Float): Int {
        return (sp * getScaledDensity(context) + sp).toInt()
    }

    fun getDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    fun getScaledDensity(context: Context): Float {
        return context.resources.displayMetrics.scaledDensity
    }

    fun getScreenWh(context: Context): IntArray {
        val screenWh = IntArray(4)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (windowManager != null) {
            val defaultDisplay = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            // 应用宽高
            defaultDisplay.getMetrics(displayMetrics)
            val displayRealMetrics = DisplayMetrics()
            // 实际宽高
            defaultDisplay.getRealMetrics(displayRealMetrics)
            screenWh[0] = displayRealMetrics.widthPixels
            screenWh[1] = displayRealMetrics.heightPixels
            screenWh[2] = displayMetrics.widthPixels
            screenWh[3] = displayMetrics.heightPixels
        }
        return screenWh
    }

    /**
     * 获取系统真实的屏幕宽高
     * @param context 上下文
     * @return int数组 0：宽，1：高
     */
    fun getSysScreenWH(context: Context): IntArray {
        return Arrays.copyOf(getScreenWh(context), 2)
    }

}