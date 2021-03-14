package com.think.accessibility.activity

import android.app.Activity
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.think.accessibility.AccessibilityView
import com.think.accessibility.R
import com.think.accessibility.bean.ViewInfo

class TranslucentActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContentView(R.layout.activity_translucent)
        val contentView: ViewGroup = window.decorView.findViewById(android.R.id.content)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val list = intent.getParcelableArrayListExtra<ViewInfo>("ViewInfoList")
        list?.run {
            contentView.addView(AccessibilityView(this@TranslucentActivity,list ))
        }


    }


}