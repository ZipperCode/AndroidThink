package com.think.accessibility.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

object ThreadManager {
    /**
     * UI 线程handler
     */
    private val mMainHandler: Handler = Handler(Looper.getMainLooper())


    /**
     * 子线程Handler
     */
    private val mSubHandler: Handler

    /**
     * 子线程
     */
    private val mSubThread: HandlerThread = HandlerThread("Sub-Thread")

    init {
        mSubThread.start()
        mSubHandler = Handler(mSubThread.looper)
    }

    fun runOnMain(task: Runnable) {
        mMainHandler.post(task)
    }
    fun runOnMain(task: Runnable, delayMillis: Long) {
        mMainHandler.postDelayed(task, delayMillis)
    }

    fun runOnSub(task: ()-> Unit) {
        mSubHandler.post(task)
    }

    fun runOnSub(task: Runnable, delayMillis: Long) {
        mSubHandler.postDelayed(task, delayMillis)
    }

    fun removeOnSubCallback(task: Runnable){
        mSubHandler.removeCallbacks(task)
    }

}