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

    /**
     * 判断当前是否是横屏
     *
     * @param context 上下文
     * @return true 是， false 否
     */
    fun getCurrentOrientation(context: Context): Boolean {
        // 获取设置的配置信息
        val mConfiguration = context.resources.configuration
        return if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            true
        } else false
    }

    /**
     * 关闭软键盘
     *
     * @param activity act
     */
    fun closeKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
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
    fun isHideKeyBoard(view: View, motionEvent: MotionEvent): Boolean {
        if (view is EditText) {
            val location = IntArray(2)
            view.getLocationInWindow(location)
            val left = location[0]
            val top = location[1]
            val bottom = top + view.getHeight()
            val right = left + view.getWidth()
            // 点击EditText的事件，忽略它。
            return !(motionEvent.x > left && motionEvent.x < right && motionEvent.y > top && motionEvent.y < bottom)
        }
        return false
    }

    fun isFullScreen(context: Context): Boolean {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (windowManager != null) {
            val defaultDisplay = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            defaultDisplay.getMetrics(displayMetrics)
            // 应用宽高
            println("displayMetrics.heightPixels = " + displayMetrics.heightPixels)
            println("displayMetrics.widthPixels = " + displayMetrics.widthPixels)
            val displayRealMetrics = DisplayMetrics()
            // 实际宽高
            defaultDisplay.getRealMetrics(displayRealMetrics)
            println("displayMetrics1.heightPixels = " + displayRealMetrics.heightPixels)
            println("displayMetrics1.widthPixels = " + displayRealMetrics.widthPixels)


            // 真实的手机屏幕宽高
            val outSize = Point()
            defaultDisplay.getRealSize(outSize)
            println("outSize.x = " + outSize.x)
            println("outSize.y = " + outSize.y)
            // 应用宽高
            val outSize1 = Point()
            defaultDisplay.getSize(outSize1)
            println("outSize1.x = " + outSize1.x)
            println("outSize1.y = " + outSize1.y)
            // 实际宽高
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val mode = defaultDisplay.mode
                println("mode.getPhysicalWidth = " + mode.physicalWidth)
                println("mode.getPhysicalHeight = " + mode.physicalHeight)
            }
            return (displayMetrics.widthPixels == displayRealMetrics.widthPixels
                    && displayMetrics.heightPixels == displayRealMetrics.heightPixels)
        }
        return false
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

    /**
     * 获取当前应用显示的屏幕宽高
     * @param context 上下文
     * @return int数组 0：宽，1：高
     */
    fun getAppScreenWH(context: Context): IntArray {
        return Arrays.copyOfRange(getScreenWh(context), 1, 3)
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
    fun adjustDensity(application: Application, activity: Activity) {
        val displayMetrics = application.resources.displayMetrics
        if (mAppDensity == 0f) {
            println("App : density = " + displayMetrics.density)
            println("App : scaledDensity = " + displayMetrics.scaledDensity)
            println("App : densityDpi = " + displayMetrics.densityDpi)
            mAppDensity = displayMetrics.density
            mAppScaleDensity = displayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mAppScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {}
            })
        }
        val targetDensity = displayMetrics.widthPixels / APP_DENSITY_DPI
        val targetScaledDensity = targetDensity * (mAppScaleDensity / mAppDensity)
        val targetDpi = (targetDensity * 160).toInt()
        println("target : targetDensity = $targetDensity")
        println("target : targetScaledDensity = $targetScaledDensity")
        println("target : targetDpi = $targetDpi")

//        displayMetrics.density = targetDensity;
//        displayMetrics.scaledDensity = targetScaledDensity;
//        displayMetrics.densityDpi = targetDpi;
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDpi
    }
}