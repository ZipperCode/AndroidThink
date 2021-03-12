package com.think.hook.proxy

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.think.hook.Hook
import java.lang.reflect.Type

class ProxyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.d(TAG, "attachBaseContext base = $base")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        val a = Class::class.java.getDeclaredMethod(
                "getDeclaredField",
                String::class.java
        )

        val m = Class::class.java.getDeclaredMethod(
                "getDeclaredMethod",
                String::class.java,
                arrayOf<Class<*>>()::class.java
        )

        Hook.ApplicationHook.replaceDelegateApplication(this, "com.think.hook.RealApplication")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "onTerminate")
    }


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory")
    }

    companion object {
        private val TAG: String = ProxyApplication::class.java.simpleName
    }
}

typealias AnyClass = Class<*>