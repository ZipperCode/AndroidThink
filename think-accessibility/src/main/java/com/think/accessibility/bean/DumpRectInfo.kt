package com.think.accessibility.bean

import android.graphics.Rect
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_dump_Rect_info")
data class DumpRectInfo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int,
        @ColumnInfo(name = "package_name")
        val packageName: String,
        @ColumnInfo(name = "activity_name")
        val activityName: String,
        @ColumnInfo(name = "in_screen_rect")
        val inScreenRect: Rect
)
