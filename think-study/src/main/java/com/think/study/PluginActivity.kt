package com.think.study

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.think.study.context.ReflectHelper

class PluginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ReflectHelper.init(this)
        val view: View = ReflectHelper.getLayoutView("") as View
        Log.e("PluginActivity","view = $view")
        setContentView(view)
    }
}