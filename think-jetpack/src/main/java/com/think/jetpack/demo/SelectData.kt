package com.think.jetpack.demo

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.databinding.ObservableField

class SelectData(
        @DrawableRes
        icon: Int,
        @DimenRes
        layoutHeight: Int = 0,
        @DrawableRes
        arrow: Int = 0,
        title: String = "",
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = true,
        var summary: String = "",
        var visibleSummary: Boolean = true,
        var selectValue: ObservableField<String>
) : BaseData(
        icon,
        layoutHeight,
        arrow,
        title,
        visibleIcon,
        visibleDivider,
        visibleArrow)
