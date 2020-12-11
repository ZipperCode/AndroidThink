package com.think.jetpack.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.think.jetpack.databind.User

/**
 * 定义接口的生成类
 */
@Database(entities = [User::class], version = 1)
abstract class UserDataBase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
}