package com.think.jetpack.preference

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.preference.PreferenceViewHolder
import com.think.jetpack.R
import com.think.jetpack.databinding.LayoutMenuPreferenceBinding
import com.think.jetpack.preference.base.BasePreference

class MenuPreference : BasePreference<LayoutMenuPreferenceBinding, MenuPreferenceData> {

    /**
     * 右边箭头资源id
     */
    private var mArrowResId: Int = 0

    /**
     * 是否显示指示图标
     */
    private var mShowArrow: Boolean = false

    /**
     * 是否显示下划线
     */
    private var mShowUnderDivider: Boolean = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun initAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuPreference);
        mArrowResId = typedArray.getResourceId(R.styleable.MenuPreference_arrow, R.drawable.button_arrow_right_yellow)
        mShowArrow = typedArray.getBoolean(R.styleable.MenuPreference_showArrow, false)
        mShowUnderDivider = typedArray.getBoolean(R.styleable.MenuPreference_showUnderDivider, false)
        typedArray.recycle()
    }

    override fun bindView(holder: PreferenceViewHolder?): LayoutMenuPreferenceBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_menu_preference,
                holder?.itemView as ViewGroup,
                false)
    }

    override fun bindData(): MenuPreferenceData {
        return MenuPreferenceData(
                icon = icon ?: AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background)!!,
                title = title?.toString() ?: "",
                arrow = AppCompatResources.getDrawable(context, mArrowResId)!!,
                visibleIcon = isIconSpaceReserved,
                visibleDivider = mShowUnderDivider,
                showArrow = mShowArrow
        )
    }

    override fun layout(): Int = R.layout.layout_menu_preference


}