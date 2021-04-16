package com.think.jetpack.preference

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

class SettingRowData(
        val key: String = "",
        val icon: Drawable,
        val title: String = "",
        val summary: String = "",
        val arrow: Drawable? = null,
        val showSummary: Boolean = false,
        val visibleIcon: Boolean = false,
        val visibleDivider: Boolean = false,
        val showArrow: Boolean = false,
        var switchValue: ObservableBoolean,
        var selectValue: ObservableField<String>
){
    override fun toString(): String {
        return "SettingRowData(key='$key', icon=$icon, title='$title', summary='$summary', arrow=$arrow, showSummary=$showSummary, visibleIcon=$visibleIcon, visibleDivider=$visibleDivider, showArrow=$showArrow, switchValue=${switchValue.get()}, selectValue=${selectValue.get()})"
    }
}
