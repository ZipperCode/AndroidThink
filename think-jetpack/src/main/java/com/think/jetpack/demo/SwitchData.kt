package com.think.jetpack.demo

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean

class SwitchData(
        icon: Drawable,
        title: String,
        arrow: Drawable? = null,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = true,
        var summary: String = "",
        var visibleSummary: Boolean = true,
        var switchValue: ObservableBoolean
) : BaseData(icon,
        title,
        arrow,
        visibleIcon,
        visibleDivider,
        visibleArrow)
