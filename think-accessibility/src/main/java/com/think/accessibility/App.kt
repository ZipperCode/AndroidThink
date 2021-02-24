package com.think.accessibility

import android.app.Application
import android.util.Log

class App : Application() {

    companion object {
        val launchActivityMap: HashMap<String, String> = HashMap()
    }

    override fun onCreate() {
        super.onCreate()
//        FloatWindow.getInstance(this)
        AppUtils.LaunchActivity(this, launchActivityMap)
    }
}