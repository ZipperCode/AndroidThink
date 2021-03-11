package com.think.accessibility.utils

import android.content.Context
import android.content.SharedPreferences
import com.think.accessibility.BuildConfig

object SpHelper {

    private lateinit var mContext: Context

    const val SP_NAME : String = "sp_dump"

    private lateinit var mSharedPreferences: SharedPreferences

    private var isInit: Boolean = false

    @JvmStatic
    fun init(context: Context){
        mContext = context.applicationContext
        mSharedPreferences = mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
        isInit = true
    }

    fun saveStringArray(key: String, values: Set<String>) {
        if (!isInit) {
            error("SpHelper must be call init method")
        }
        val edit = mSharedPreferences.edit()
        edit.putStringSet(key, values).apply()
    }


    fun loadStringArray(key: String): Set<String> {
        if (!isInit) {
            error("SpHelper must be call init method")
        }
        return mSharedPreferences.getStringSet(key, HashSet())!!
    }
}