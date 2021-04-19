package com.think.jetpack.demo.data

import android.content.SharedPreferences
import android.util.Log
import com.think.jetpack.demo.MapTable
import com.think.jetpack.preference.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SettingSource {

    fun switchValue(key: String): Flow<Boolean> {
        return flow<Boolean> {
            Log.d(TAG,"switchValue emitter")
            emit(DataStore.instance().getBoolean(key,true))
        }.flowOn(Dispatchers.IO)
    }

    fun switchValue(key: String, value: Boolean){
        DataStore.instance().putBoolean(key, value)
    }

    fun selectValue(key: String): Flow<String>{
        return flow<String> {
            Log.d(TAG,"switchValue emitter")
            val time = DataStore.instance().getInt(key, 0)
            emit(MapTable.getTimeString(time))
        }.flowOn(Dispatchers.IO)
    }

    fun selectValue(key: String, value: Int){
        DataStore.instance().putInt(key,value)
    }

    companion object{
        val TAG:String = SettingSource::class.java.simpleName
    }
}