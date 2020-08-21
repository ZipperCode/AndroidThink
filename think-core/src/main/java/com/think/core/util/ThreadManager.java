package com.think.core.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadManager {

    /**
     * 获取CPU核心数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 默认核心线程数 最小四核最大八核
     */
    private static final int CORE_POOL_SIZE = Math.max(4, Math.min(CPU_COUNT - 1, 8));
    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 线程活跃时间
     */
    private static final int KEEP_ALIVE_SECONDS = 60;
    /**
     * UI 线程handler
     */
    private final Handler mMainHandler;

    private final Handler mSubHandler;

    private final HandlerThread mSubThread;
    /**
     * 线程锁
     */
    private final Object mHandlerLock = new Object();
    /**
     * 线程池
     */
    private final ExecutorService mExecutorService;

    private final static class ThreadManagerHolder{
        private static final ThreadManager THREAD_MANAGER = new ThreadManager();
    }

    public static ThreadManager getInstance(){
        return ThreadManagerHolder.THREAD_MANAGER;
    }

    private ThreadManager(){
        mMainHandler = new Handler(Looper.getMainLooper());
        mSubThread = new HandlerThread("Sub-Thread");
        mSubThread.start();
        mSubHandler = new Handler(mSubThread.getLooper());
        mExecutorService = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(256),
                new ThreadFactory() {
                    ThreadGroup threadGroup = new ThreadGroup("WorkPool ");
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(threadGroup, r, "# " + atomicInteger.getAndIncrement());
                    }
                }
        );
    }

    /**
     * 获取主线程Handler
     * @return Handler
     */
    public Handler getMainHandler(){
        return mMainHandler;
    }

    /**
     * 获取线程池
     * @return 线程池
     */
    public Executor getExecutor(){
        return mExecutorService;
    }

    public void exeOnMainThread(Runnable runnable){
        mMainHandler.post(runnable);
    }

    public void exePool(Runnable runnable){
        mExecutorService.execute(runnable);
    }


}
