package com.think.jetpack.demo

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes

open class BaseData(
        @DrawableRes
        val icon: Int = 0,
        @DimenRes
        val layoutHeight: Int = 0,
        @DrawableRes
        var arrow: Int = 0,
        val title: String = "",
        var visibleIcon: Boolean = false,
        var visibleDivider: Boolean = false,
        var showArrow: Boolean = false
)