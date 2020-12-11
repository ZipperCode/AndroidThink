package com.think.jetpack

import androidx.multidex.MultiDexApplication
import com.think.jetpack.room.AppDaoUtils

class App : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        val db = AppDaoUtils.openDatabase(this)

    }
}