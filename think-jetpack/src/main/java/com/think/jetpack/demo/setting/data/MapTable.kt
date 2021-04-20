package com.think.jetpack.demo.setting.data

import androidx.collection.arrayMapOf

object MapTable {

    private val timeMap: Map<String, Int> = arrayMapOf(
            "无限制" to 0,
            "5分钟" to 5,
            "10分钟" to 10,
            "15分钟" to 15
    )

    fun getTime(timeString: String): Int{
        return timeMap[timeString] ?: 0
    }

    fun getTimeString(time: Int): String{
        var result = "无限制"
        for (map in timeMap){
            if(map.value == time){
                result = map.key
            }
        }
        return result
    }



}