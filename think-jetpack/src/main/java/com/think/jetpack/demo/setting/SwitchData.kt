package com.think.jetpack.demo.setting

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import com.think.jetpack.demo.setting.BaseData

class SwitchData(
        @DrawableRes
        icon: Int,
        @DimenRes
        layoutHeight: Int = 0,
        @DrawableRes
        arrow: Int = 0,
        title: String = "",
        visibleCategorySeparator: Boolean = false,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = true,
        actionType: ActionType = ActionType.NONE_ACTION,
        var summary: String = "",
        var visibleSummary: Boolean = true,
        var switchValue: ObservableField<Boolean>
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