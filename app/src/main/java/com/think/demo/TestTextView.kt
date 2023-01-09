package com.think.demo

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.zipper.think.apt.anno.InflateUnReflectView

@InflateUnReflectView
class TestTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {


    init {
        Log.e("BAAA","trace = ${Log.getStackTraceString(Throwable())}")
    }
}