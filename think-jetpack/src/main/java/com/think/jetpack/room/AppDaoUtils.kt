package com.think.jetpack.room

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import java.security.AccessControlContext

object AppDaoUtils {
    public fun openDatabase(context: Context) : UserDataBase{
        val db = Room    // 创建数据库创建器
                .databaseBuilder(context, UserDataBase::class.java, "tb_user")
                // 数据库版本迁移
                .addMigrations()
                .build()

        return db
    }

}