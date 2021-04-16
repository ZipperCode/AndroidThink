package com.think.jetpack.demo

import android.graphics.drawable.Drawable

open class BaseData(
        val icon: Drawable,
        val title: String,
        var arrow: Drawable? = null,
        var visibleIcon: Boolean = false,
        var visibleDivider: Boolean = false,
        var showArrow: Boolean = false
)