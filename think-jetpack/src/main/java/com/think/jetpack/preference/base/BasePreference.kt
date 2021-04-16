package com.think.jetpack.preference.base

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.think.jetpack.R

abstract class BasePreference<T : ViewDataBinding, V: BasePreferenceData> : Preference {

    /**
     * Row布局的高度
     */
    private var mLayoutHeight: Int = 0

    lateinit var mBindData: T

    lateinit var mData: V

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePreference);
        mLayoutHeight = typedArray.getDimensionPixelSize(R.styleable.BasePreference_layoutHeight,
                context.resources.getDimensionPixelSize(R.dimen.setting_row_layout_height))
        typedArray.recycle()
        init(attrs)
    }

    constructor(context: Context) : this(context, null)

    private fun init(attrs: AttributeSet?) {
//        val layoutView = layout()
//        val field = Preference::class.java.getDeclaredField("mLayoutResId")
//        field.isAccessible = true
//        field.set(this, layoutView)
        initAttr(attrs)
        initData()
        initListener()
    }

    open fun initAttr(attrs: AttributeSet?) {}

    @CallSuper
    open fun initData() {
        mData = bindData()
    }

    open fun initListener() {}

    abstract fun bindData():V

    abstract fun bindView(holder: PreferenceViewHolder?): T

    abstract fun layout(): Int

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        mBindData = bindView(holder)
        holder?.itemView?.layoutParams?.height = mLayoutHeight
    }

    companion object {
        const val MENU: Int = 1
        const val SWITCH: Int = 2
    }
}