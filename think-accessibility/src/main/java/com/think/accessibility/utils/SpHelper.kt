package com.think.accessibility.utils

import android.content.Context
import android.content.SharedPreferences

object SpHelper {

    private lateinit var mContext: Context

    const val SP_NAME : String = "sp_dump"

    private lateinit var mSharedPreferences: SharedPreferences
    @JvmStatic
    fun init(context: Context){
        mContext = context.applicationContext
        mSharedPreferences = mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
    }

}