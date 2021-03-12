package com.think.audioplayer

import android.content.Context
import android.util.Log

class AudioPlayerPlugin(context: Context) {

    var mAppContext: Context = context.applicationContext

    companion object{
        /**
         * debug模式打印日志
         */
        var debugMode: Boolean = true

        fun debug(tag: String, message: String){
            if(debugMode){
                Log.d(tag,message);
            }
        }
    }

}