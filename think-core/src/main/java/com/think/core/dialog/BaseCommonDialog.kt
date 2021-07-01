package com.think.core.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseCommonDialog<T : BaseCommonDialog.Builder> : DialogFragment, View.OnSystemUiVisibilityChangeListener {

    private val isFullscreen: Boolean

    constructor() : super(){
        isFullscreen = false
    }

    constructor(builder: T) : super(){
        isFullscreen = builder.isFullScreen
    }

    @LayoutRes
    abstract fun layoutId(): Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.apply {
            // 设置全屏标记
            if(isFullscreen){
                setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                decorView.setPadding(0,0,0,0)
                val layoutParams = attributes
                layoutParams.alpha

            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onSystemUiVisibilityChange(visibility: Int) {

    }

    /**
     * 隐藏状态栏
     *
     * [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 将应用的内容设置为显示在状态栏的后面 SDK > 4.1
     * [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] 将应用的内容设置为显示在导航栏的后面 SDK > 4.1
     * [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 保持稳定布局
     * [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 标记隐藏导航栏
     * [View.SYSTEM_UI_FLAG_FULLSCREEN] 标记隐藏状态栏
     * [View.SYSTEM_UI_FLAG_IMMERSIVE] 设置为沉浸式
     * [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] 设置为粘性沉浸式，划出状态栏后几秒后消失，点击状态栏时，事件也会传递到底下的视图，且无法收到回调信息
     *
     * 注意：android:fitsSystemWindows 会留出状态栏或导航栏的边距，即使设置了全屏
     */
    protected fun hideSystemUi() {
        dialog?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

    }

    /**
     * 显示状态栏
     */
    protected fun showSystemUi(){
        dialog?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    /**
     * 设置沉浸式模式
     */
    protected fun immerseMode(){
        dialog?.window?.apply {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    abstract class Builder {
        /**
         * 是否全屏
         */
        var isFullScreen: Boolean = false

        var alpha: Float = 1.0f

        abstract fun build(): BaseCommonDialog<*>
    }
}