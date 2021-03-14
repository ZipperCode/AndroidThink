package com.think.accessibility

import android.util.Log
import com.think.accessibility.bean.DumpAppInfo

@Deprecated("")
object DumpManager {

    private val mSplashMap:MutableMap<String, DumpAppInfo> = HashMap()
    private val mSplashStringMap:MutableMap<String, String> = HashMap()

    val mLaunchActivity:MutableMap<String, String> = HashMap()

    /**
     * 收到AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED事件时将启动窗口类名传入
     */
    fun put(pks: String, activityName: String){
//        if(mSplashMap.containsKey(pks)){
//            val dumpAppInfo = mSplashMap[pks]!!
//            Log.d("DumpManager","判断是否是闪屏页 pks equals = ${mLaunchActivity.contains(pks)} className equals = ${mLaunchActivity[pks] == activityName}")
//            // 判断是否是闪屏页，如果是闪屏页则将跳过标志设置为true
//            if(mLaunchActivity.contains(pks)){
//                if(mLaunchActivity[pks] == activityName){
//                    dumpAppInfo.isDump = true
//                }
//            }
//        }else{
//            mSplashMap[pks] = DumpAppInfo(pks, activityName, true)
//        }
        Log.d("DumpManager","put ==> pks = $pks = className = $activityName")
        mSplashStringMap[pks] = activityName;
    }

    /**
     * 检查管理器中是否存在启动窗口
     * 如果存在且跳过标识为 false，表示进行跳转
     */
    fun canDump(pks: String): Boolean{
//        Log.d("DumpManager","canDump = ${mSplashMap.containsKey(pks) and !(mSplashMap[pks]?.isDump ?: true)}")
//        return mSplashMap.containsKey(pks) and !(mSplashMap[pks]?.isDump ?: true)
        if(mSplashStringMap.containsKey(pks) and (mLaunchActivity.containsKey(pks))){
            Log.d("DumpManager","${mSplashStringMap[pks] == mLaunchActivity[pks]}")
            return mSplashStringMap[pks] == mLaunchActivity[pks]
        }
        return  false
    }

    fun dumped(pks: String){
        Log.d("DumpManager","splash = ${mSplashMap}")
        if(mSplashMap.containsKey(pks)){
            mSplashMap[pks]!!.isDump = false
        }
    }

}