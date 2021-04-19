package com.think.jetpack.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceDataStore
import java.lang.IllegalStateException

class DataStore private constructor(context: Context, name: String) : PreferenceDataStore() {

    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = mSharedPreferences.edit()

    val sharedPreferences = mSharedPreferences

    override fun putString(key: String?, value: String?) {
        Log.d(TAG, "putString key = $key, value = $value")
        editor.putString(key, value)
        editor.apply()
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        Log.d(TAG, "putStringSet key = $key, values = $values")
        editor.putStringSet(key, values)
        editor.apply()
    }

    override fun putInt(key: String?, value: Int) {
        Log.d(TAG, "putInt key = $key, value = $value")
        editor.putInt(key, value)
        editor.apply()
    }

    override fun putLong(key: String?, value: Long) {
        Log.d(TAG, "putLong key = $key, value = $value")
        editor.putLong(key, value)
        editor.apply()
    }

    override fun putFloat(key: String?, value: Float) {
        Log.d(TAG, "putFloat key = $key, value = $value")
        editor.putFloat(key, value)
        editor.apply()
    }

    override fun putBoolean(key: String?, value: Boolean) {
        Log.d(TAG, "putBoolean key = $key, value = $value")
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun getString(key: String?, defValue: String?): String? {
        Log.d(TAG, "getString key = $key def = $defValue")
        return mSharedPreferences.getString(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        Log.d(TAG, "getStringSet key = $key def = $defValues")
        return mSharedPreferences.getStringSet(key, defValues ?: mutableSetOf())!!
    }

    override fun getInt(key: String?, defValue: Int): Int {
        Log.d(TAG, "getInt key = $key def = $defValue")
        return mSharedPreferences.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        Log.d(TAG, "getLong key = $key def = $defValue")
        return mSharedPreferences.getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        Log.d(TAG, "getFloat key = $key def = $defValue")
        return mSharedPreferences.getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        Log.d(TAG, "getBoolean key = $key def = $defValue")
        val value =  mSharedPreferences.getBoolean(key, defValue)
        Log.d(TAG, "getBoolean key = $key value = $value")
        return value
    }

    companion object {
        private const val TAG = "DataStore"

        private const val STORE_NAME = "sp_store"

        private var INSTANCE: DataStore? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                synchronized(TAG) {
                    if (INSTANCE == null) {
                        INSTANCE = DataStore(context.applicationContext, STORE_NAME)
                    }
                }
            }
        }

        fun instance(): DataStore {
            if (INSTANCE == null) {
                throw IllegalStateException("must call init method")
            }
            return INSTANCE!!
        }
    }
}