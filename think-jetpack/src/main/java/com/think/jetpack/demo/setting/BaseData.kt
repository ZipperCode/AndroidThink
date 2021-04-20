package com.think.jetpack.demo.setting

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
        val visibleCategorySeparator: Boolean = false,
        var visibleIcon: Boolean = false,
        var visibleDivider: Boolean = false,
        var visibleArrow: Boolean = false,
        var actionType: ActionType = ActionType.NONE_ACTION
)