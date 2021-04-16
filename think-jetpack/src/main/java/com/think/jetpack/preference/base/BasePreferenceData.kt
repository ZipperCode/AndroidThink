package com.think.jetpack.preference.base

import android.graphics.drawable.Drawable

open class BasePreferenceData(
        val icon: Drawable,
        val title: String,
        var arrow: Drawable? = null,
        var visibleIcon: Boolean = false,
        var visibleDivider: Boolean = false,
        var showArrow: Boolean = false
)