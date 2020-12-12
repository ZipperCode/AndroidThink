package com.think.jetpack.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import androidx.work.WorkManager

class LoggerInitializer : Initializer<LoggerInit> {
    override fun create(context: Context): LoggerInit {
        Log.i("LoggerInitializer","dependencies LoggerInitializer")
        return LoggerInit()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}