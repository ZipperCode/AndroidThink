package com.think.accessibility.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.think.accessibility.AccessibilityConfig
import com.think.accessibility.Const
import com.think.accessibility.DumpManager
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.RunMode
import com.think.accessibility.bean.AppInfo
import com.think.accessibility.utils.AppUtils
import com.think.accessibility.utils.ThreadManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MyAccessibilityService : AccessibilityService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (AccessibilityUtil.mAccessibilityService == null) {
            AccessibilityUtil.mAccessibilityService = this
        }
        val packageName = event?.packageName?.toString() ?: ""
        val className = event?.className?.toString() ?: ""
        when (AccessibilityConfig.mode) {
            RunMode.DUMP_SPLASH -> {
                when (event?.eventType) {
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                        rootInActiveWindow?.run {
                            if(DumpManager.canDump(packageName)){
                                ThreadManager.getInstance().runOnSub(Runnable {
                                    dumpSplash(this, packageName, className)
                                })
                            }
                        }
                    }
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {

                        if (!TextUtils.isEmpty(packageName) and !TextUtils.isEmpty(className)) {
                            DumpManager.put(packageName, className)
                        }
//                        Log.d(TAG,"pkName = ${event.packageName} className = $className")
//                        if(Const.KU_GOU_SPLASH_ACTIVITY == className){
//                            val webview = AccessibilityUtil.findWebViewNode(rootInActiveWindow)
//                            val node = AccessibilityUtil.findWebViewContent(webview,"跳过")
//                            Log.d(TAG,"webview = $webview node = $node")
//                        }
                    }
                }
            }
            RunMode.CLOSE -> {
                Toast.makeText(this, "服务关闭，不运行跳过", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun dumpSplash(
            rootNodeInfo: AccessibilityNodeInfo,
            pks: String,
            className: String
    ) {

        // 能查找到[跳过]的组件
        var clicked = AccessibilityUtil
                .findNodeByText(rootNodeInfo, Const.DUMP_AD_TEXT_1)
                ?.let {
                    return@let if (it.isClickable) {
                        AccessibilityUtil.click(it)
                    } else {
                        AccessibilityUtil.deepClick(it)
                    }
                } ?: false


        if (!clicked) {
            Log.d(TAG, "未找到[跳过]的组件, 继续查找[跳过广告]的组件")
            clicked = AccessibilityUtil
                    .findNodeByText(rootNodeInfo, Const.DUMP_AD_TEXT_2)
                    ?.let {
                        return@let if (it.isClickable) {
                            AccessibilityUtil.click(it)
                        } else {
                            AccessibilityUtil.deepClick(it)
                        }
                    } ?: false
        }

//            if (!clicked) {
//                Log.d(TAG, "以上都没有跳过，可能组件是webview的，继续查找webview的组件")
//                val webViewNode = AccessibilityUtil.findWebViewNode(rootNodeInfo)
//                Log.d(TAG, "webViewNode = $webViewNode")
//                if (webViewNode != null) {
//                    clicked = AccessibilityUtil
//                            .findWebViewContent(webViewNode, Const.DUMP_AD_TEXT_1)
//                            ?.let {
//                                return@let if (it.isClickable) {
//                                    AccessibilityUtil.click(it)
//                                } else {
//                                    AccessibilityUtil.deepClick(it)
//                                }
//                            } ?: false
//                    if (clicked) {
//                        DumpManager.dumped(pks)
//                    }
//                }
//            } else {
//                DumpManager.dumped(pks)
//            }
    }

    override fun onInterrupt() {
        Log.d(TAG, "中断")
        Toast.makeText(this, "无障碍服务中断", Toast.LENGTH_LONG).show();
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "无障碍服务已打开")
        Toast.makeText(this, "无障碍服务已打开", Toast.LENGTH_LONG).show();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(Intent(this,GuardService::class.java));
//        } else{
//
//        }
        AccessibilityUtil.mAccessibilityService = this

        ThreadManager.getInstance().runOnSub(Runnable {
            AppUtils.getLaunch(this, DumpManager.mLaunchActivity)
        })

//        startService(Intent(this, GuardService::class.java))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "无障碍服务关闭，请重新打开")
        Toast.makeText(this, "无障碍服务关闭，请重新打开", Toast.LENGTH_LONG).show();
        stopService(Intent(this, GuardService::class.java));
        return super.onUnbind(intent)
    }

    companion object {
        private val TAG = MyAccessibilityService::class.java.simpleName
    }

}