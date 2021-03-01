package com.think.accessibility

import android.util.Log
import com.think.accessibility.bean.DumpAppInfo

object DumpManager {

    private val mSplashMap:MutableMap<String, DumpAppInfo> = HashMap()

    val mLaunchActivity:MutableMap<String, String> = HashMap()

    /**
     * 收到AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED事件时将启动窗口类名传入
     */
    fun put(pks: String, activityName: String){
        if(mSplashMap.containsKey(pks)){
            val dumpAppInfo = mSplashMap[pks]!!
            // 判断是否是闪屏页
            if(mLaunchActivity.contains(pks)){
                if(mLaunchActivity[pks] == activityName){
                    dumpAppInfo.isDump = false
                }
            }
        }else{
            mSplashMap[pks] = DumpAppInfo(pks, activityName, false)
        }
    }

    /**
     * 检查管理器中是否存在启动窗口
     * 如果存在且跳过标识为 false，表示进行跳转
     */
    fun canDump(pks: String): Boolean{
        Log.d("DumpManager","canDump = ${mSplashMap.containsKey(pks) and !(mSplashMap[pks]?.isDump ?: true)}")
        return mSplashMap.containsKey(pks) and !(mSplashMap[pks]?.isDump ?: true)
    }

    fun dumped(pks: String){
        if(mSplashMap.containsKey(pks)){
            mSplashMap[pks]!!.isDump = true
        }
    }

}