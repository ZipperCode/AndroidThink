package com.think.jetpack.preference

object MapTable {

    fun switchValue(value: Boolean): String{
        return if (value) "选中" else "未选中";
    }


}