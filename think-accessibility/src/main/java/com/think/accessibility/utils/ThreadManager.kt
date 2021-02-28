package com.think.accessibility.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class ThreadManager {

    /**
     * UI 线程handler
     */
    private val mMainHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * 线程池
     */
    private var mExecutorService: ExecutorService

    /**
     * 子线程Handler
     */
    private val mSubHandler: Handler

    /**
     * 子线程
     */
    private val mSubThread: HandlerThread = HandlerThread("Sub-Thread")

    private val mThreadFactory: ThreadFactory = object : ThreadFactory {
        private val threadGroup = ThreadGroup("WorkPool-")
        private val mAtomicInteger = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return Thread(threadGroup, r, "#" + mAtomicInteger.getAndIncrement())
        }
    }

    init {
        mSubThread.start()
        mSubHandler = Handler(mSubThread.looper)
        mExecutorService = ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                LinkedBlockingDeque<Runnable>(256),
                mThreadFactory
        )
    }

    fun runOnMain(task: Runnable) {
        mMainHandler.post(task)
    }

    fun runOnSub(task: Runnable) {
        mSubHandler.post(task)
    }

    fun runOnSub(task: Runnable, delayMillis: Long) {
        mSubHandler.postDelayed(task, delayMillis)
    }

    fun removeOnSubCallback(task: Runnable){
        mSubHandler.removeCallbacks(task)
    }

    fun exePool(task: Runnable) {
        mExecutorService.execute(task)
    }

    companion object {
        private var instance: ThreadManager? = null

        /**
         * 获取CPU核心数
         */
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

        /**
         * 默认核心线程数
         */
        private val CORE_POOL_SIZE = CPU_COUNT

        /**
         * 最大线程数
         */
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

        /**
         * 线程活跃时间
         */
        private const val KEEP_ALIVE_SECONDS = 60L

        @JvmStatic
        fun getInstance(): ThreadManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ThreadManager()
                    }
                }
            }
            return instance!!
        }
    }

}