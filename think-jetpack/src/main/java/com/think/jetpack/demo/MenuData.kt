package com.think.jetpack.demo

import android.graphics.drawable.Drawable

class MenuData(
        icon: Drawable,
        title: String,
        arrow: Drawable? = null,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        visibleArrow: Boolean = false
) : BaseData(icon,
        title,
        arrow,
        visibleIcon,
        visibleDivider,
        visibleArrow)
