package com.think.jetpack.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.think.jetpack.R

class RowDialogPreference : DialogPreference {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RowPreference);
        typedArray.recycle()

    }

    constructor(context: Context) : super(context) {

    }
}