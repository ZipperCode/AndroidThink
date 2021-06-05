package com.think.jetpack.databind

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.think.jetpack.R
import com.think.jetpack.databinding.ActivityDataBindingBinding

class DataBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databind: ActivityDataBindingBinding = DataBindingUtil.setContentView(this as Activity,R.layout.activity_data_binding)
    }
}