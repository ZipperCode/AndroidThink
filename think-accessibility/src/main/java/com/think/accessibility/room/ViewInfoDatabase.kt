package com.think.accessibility.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.convert.RectConverters

@Database(entities = [ViewInfo::class] ,version = 2)
@TypeConverters(RectConverters::class)
abstract class ViewInfoDatabase: RoomDatabase() {

    abstract fun getViewInfoDao(): ViewInfoDao
}