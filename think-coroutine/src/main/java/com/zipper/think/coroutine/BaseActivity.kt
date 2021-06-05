package com.zipper.think.coroutine

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var noCompatScaledDensity: Float = 0f
    private var noCompatDensity: Float = 0f

    private var screenOrientation: Boolean = false

    val imm get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noCompatScaledDensity = resources.displayMetrics.scaledDensity
        noCompatDensity = resources.displayMetrics.density
        screenOrientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        autoSize()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenOrientation = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fontScale = newConfig.fontScale
        if (fontScale > 0) {
            noCompatScaledDensity = resources.displayMetrics.scaledDensity
            autoSize()
        }
        Log.d("BaseActivity", "onConfigurationChanged $newConfig")
        Log.d("BaseActivity", "是否横屏： $screenOrientation")
        Log.d("BaseActivity", "densityDpi: ${newConfig.densityDpi}")
        Log.d("BaseActivity", "fontScale: ${newConfig.fontScale}")
        Log.d("BaseActivity", "硬键盘是否被隐藏: ${newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES}")
        Log.d("BaseActivity", "软键盘是否被隐藏: ${newConfig.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES}")
        Log.d("BaseActivity", "keyboard: ${newConfig.keyboard}")
        Log.d("BaseActivity", "isNightModeActive: ${newConfig.isNightModeActive}")
        Log.d("BaseActivity", "isScreenHdr: ${newConfig.isScreenHdr}")
        Log.d("BaseActivity", "colorMode: ${newConfig.colorMode}")
        Log.d("BaseActivity", "isScreenRound: ${newConfig.isScreenRound}")
        Log.d("BaseActivity", "isScreenWideColorGamut: ${newConfig.isScreenWideColorGamut}")
        Log.d("BaseActivity", "布局方向 左->右 右->左: ${newConfig.layoutDirection}")
        Log.d("BaseActivity", "navigation: ${newConfig.navigation}")
        Log.d("BaseActivity", "导航是否隐藏: ${newConfig.navigationHidden == Configuration.NAVIGATIONHIDDEN_YES}")
        Log.d("BaseActivity", "screenHeightDp: ${newConfig.screenHeightDp}")
        Log.d("BaseActivity", "screenLayout: ${newConfig.screenLayout}")
        Log.d("BaseActivity", "touchscreen: ${newConfig.touchscreen}")
        Log.d("BaseActivity", "uiMode: ${newConfig.uiMode}")

    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    protected fun autoSize() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidthPixels = displayMetrics.widthPixels
        // px = density * dp, density = dpi / 160 <==> dpi = density * 160
        val targetDensity = (screenWidthPixels * 1.0f) / 360
        val targetScaledDensity = targetDensity * (noCompatScaledDensity / noCompatDensity)
        val targetDensityDpi = (targetDensity * 160).toInt()
        displayMetrics.apply {
            density = targetDensity
            densityDpi = targetDensityDpi
        }
    }

    protected fun showKeyboard() {
        if (currentFocus != null) {
            imm.showSoftInput(currentFocus, 0)
        } else {
            window?.decorView?.apply {
                requestFocus()
                requestFocusFromTouch()
                imm.showSoftInput(this, 0)
            }
        }
    }

    protected fun showKeyboard(focusView: View?) {
        focusView?.apply {
            if (!this.isFocused) {
                requestFocus()
                requestFocusFromTouch()
            }
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun hideKeyboard() {
        window?.decorView?.apply {
            val focusView = findFocus()
            if (focusView == null) {
                imm.hideSoftInputFromWindow(this.windowToken, 0)
            } else {
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            }
        }
    }

    protected fun showToast(msg: String, isLong: Boolean = false) {
        Toast.makeText(this, msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}