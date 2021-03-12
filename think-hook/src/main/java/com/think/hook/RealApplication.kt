package com.think.hook

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log

class RealApplication : Application() {


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.d(TAG,"attachBaseContext base = $base")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG,"onConfigurationChanged")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG,"onTerminate")
    }


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG,"onTrimMemory")
    }

    companion object{
        private val TAG: String = RealApplication::class.java.simpleName
    }
}