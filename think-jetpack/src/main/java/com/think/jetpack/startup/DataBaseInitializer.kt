package com.think.jetpack.startup

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.startup.Initializer
import com.think.jetpack.room.UserDataBase

class DataBaseInitializer : Initializer<UserDataBase> {
    override fun create(context: Context): UserDataBase {
        Log.i("DataBaseInitializer","create DatabaseInitializer context = ${context}")
        val db = Room    // 创建数据库创建器
                .databaseBuilder(context, UserDataBase::class.java, "user")
                // 数据库版本迁移
                .addMigrations()
                .build()

        return db
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        Log.i("DataBaseInitializer","dependencies LoggerInitializer")
        return arrayListOf(LoggerInitializer::class.java)
    }
}