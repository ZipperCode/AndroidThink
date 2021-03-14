package com.think.accessibility.convert

import android.graphics.Rect
import androidx.room.TypeConverter

class RectConverters {

    @TypeConverter
    fun fromString(rectString: String?): Rect?{
        return rectString?.let{
            val split = it.split(",")
            Rect(split[0].toInt(),split[1].toInt(),split[2].toInt(),split[3].toInt())
        }
    }

    @TypeConverter
    fun rectToString(rect: Rect): String{
        return "${rect.left},${rect.top},${rect.right},${rect.bottom}"
    }
}