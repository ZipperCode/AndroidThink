package com.think.jetpack.demo

import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes

class MenuData(
        @DrawableRes
        icon: Int,
        @DimenRes
        layoutHeight: Int,
        @DrawableRes
        arrow: Int = 0,
        title: String,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = false
) : BaseData(
        icon,
        layoutHeight,
        arrow,
        title,
        visibleIcon,
        visibleDivider,
        visibleArrow)
