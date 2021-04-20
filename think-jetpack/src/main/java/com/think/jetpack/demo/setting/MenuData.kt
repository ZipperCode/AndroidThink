package com.think.jetpack.demo.setting

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.think.jetpack.demo.setting.BaseData

class MenuData(
        @DrawableRes
        icon: Int,
        @DimenRes
        layoutHeight: Int,
        @DrawableRes
        arrow: Int = 0,
        title: String,
        visibleCategorySeparator: Boolean = false,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = false,
        actionType: ActionType = ActionType.NONE_ACTION
) : BaseData(
        icon = icon,
        layoutHeight = layoutHeight,
        arrow = arrow,
        title = title,
        visibleCategorySeparator = visibleCategorySeparator,
        visibleIcon = visibleIcon,
        visibleDivider = visibleDivider,
        visibleArrow = visibleArrow,
        actionType = actionType
)
