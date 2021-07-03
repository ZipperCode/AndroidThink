package com.think.core.dialogfragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseCommonDialog<T : BaseCommonDialog.Setting> : DialogFragment(), View.OnSystemUiVisibilityChangeListener, DialogInterface.OnKeyListener {

    abstract fun requireSetting(): T

    @LayoutRes
    abstract fun layoutId(): Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(requireSetting().cancelable)
            setCanceledOnTouchOutside(requireSetting().canceledOnTouchOutside)
            setOnKeyListener(this@BaseCommonDialog)
        }

        dialog.window?.apply {
            val layoutParams = attributes

            addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            // 背景透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 是否可展示键盘
            if(requireSetting().keyboardEnable){
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }else{
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
            // 设置全屏标记
            if(requireSetting().isFullScreen){
                setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                hideSystemUi()
                decorView.setPadding(0, 0, 0, 0)
            }
            // 透明度
            layoutParams.alpha = requireSetting().alpha
            // 背景透明度
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            layoutParams.dimAmount = requireSetting().dimAmount

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

            attributes = layoutParams
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnSystemUiVisibilityChangeListener(this)
    }

    override fun onDestroyView() {
        view?.setOnSystemUiVisibilityChangeListener(null)
        super.onDestroyView()
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK && !requireSetting().cancelable
    }

    override fun onSystemUiVisibilityChange(visibility: Int) {
        adjustSystemUi()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 清除屏幕焦点并添加键盘显示
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
        adjustSystemUi()
    }

    private fun adjustSystemUi(){
        when(requireSetting().systemUiMode){
            SystemUiMode.HIDE_SYSTEM_UI -> hideSystemUi()
            SystemUiMode.SHOW_SYSTEM_UI -> showSystemUi()
            SystemUiMode.IMMERSE -> immerseMode()
            SystemUiMode.CUSTOM -> customSystemUi()
        }
    }

    protected fun customSystemUi(){}

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
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        }
    }

    open class Setting{
        /**
         * 是否全屏
         */
        var isFullScreen: Boolean = false

        var dimAmount: Float = 0.5f

        var alpha: Float = 1.0f

        var cancelable: Boolean = false

        var canceledOnTouchOutside: Boolean = false

        var systemUiMode: SystemUiMode = SystemUiMode.IMMERSE

        var keyboardEnable: Boolean = false
    }

    abstract class Builder<T: Setting> {

        fun isFullScreen(isFullScreen: Boolean): Builder<T> {
            requireSetting().isFullScreen = isFullScreen
            return this
        }

        fun dimAmount(dimAmount: Float): Builder<T> {
            requireSetting().dimAmount = dimAmount
            return this
        }

        fun alpha(alpha: Float): Builder<T> {
            requireSetting().alpha = alpha
            return this
        }

        fun cancelable(cancelable: Boolean): Builder<T> {
            requireSetting().cancelable = cancelable
            return this
        }

        fun canceledOnTouchOutside(canceledOnTouchOutside: Boolean): Builder<T> {
            requireSetting().canceledOnTouchOutside = canceledOnTouchOutside
            return this
        }

        fun systemUiMode(systemUiMode: SystemUiMode): Builder<T> {
            requireSetting().systemUiMode = systemUiMode
            return this
        }

        fun keyboardEnable(keyboardEnable: Boolean): Builder<T> {
            requireSetting().keyboardEnable = keyboardEnable
            return this
        }

        abstract fun requireSetting(): T

        abstract fun build(): BaseCommonDialog<*>
    }

    enum class SystemUiMode{
        HIDE_SYSTEM_UI,
        SHOW_SYSTEM_UI,
        IMMERSE,
        CUSTOM,
    }

}