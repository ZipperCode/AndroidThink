package com.think.accessibility.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.think.accessibility.exception.UnOperationException
import java.lang.Exception
import java.util.*

object TimeHelper {

    private val TAG: String = TimeHelper::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun scheduleJob(context: Context, jobId: Int, jobServiceComponent: ComponentName) {
        val job = JobInfo.Builder(jobId, jobServiceComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // 任何网络下执行
                .setRequiresDeviceIdle(true) // 只有在用户有一段时间没有使用时才会执行
                .build()
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(job)

    }

    @Throws(UnOperationException::class)
    fun waitTime(time: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "[waitTime] 当前线程为主线程，不允许进行阻塞")
            throw UnOperationException("当前线程为主线程，不允许进行阻塞")
        }
        try {
            Thread.sleep(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}