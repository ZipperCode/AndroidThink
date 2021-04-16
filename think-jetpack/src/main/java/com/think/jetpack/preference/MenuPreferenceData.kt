package com.think.jetpack.preference

import android.graphics.drawable.Drawable
import com.think.jetpack.preference.base.BasePreferenceData

class MenuPreferenceData(
        icon: Drawable,
        title: String,
        arrow: Drawable? = null,
        visibleIcon: Boolean = false,
        visibleDivider: Boolean = false,
        showArrow: Boolean = false
) : BasePreferenceData(icon,
        title,
        arrow,
        visibleIcon,
        visibleDivider,
        showArrow)
