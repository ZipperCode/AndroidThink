package com.think.study.context

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

public class PhoneLayoutInflater : LayoutInflater {
    private val sClassPrefixList = arrayOf(
            "android.widget.",
            "android.webkit.",
            "android.app."
    )

    public constructor(context: Context): super(context)

    public constructor(original: LayoutInflater?, newContext: Context?)
            : super(original, newContext)


    @Throws(ClassNotFoundException::class)
    override fun onCreateView(name: String?, attrs: AttributeSet?): View? {
        for (prefix in sClassPrefixList) {
            try {
                val view = createView(name, prefix, attrs)
                if (view != null) {
                    return view
                }
            } catch (e: ClassNotFoundException) { // In this case we want to let the base class take a crack
// at it.
            }
        }
        return super.onCreateView(name, attrs)
    }

    override fun cloneInContext(newContext: Context?): LayoutInflater? {
        return PhoneLayoutInflater(this, newContext)
    }
}