package com.think.accessibility

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.FingerprintGestureController.*
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEventSource
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi

class MyAccessibilityService : AccessibilityService() {

    private var mWindowIsChange: Boolean = false

    private var mWindowPackageName: String = ""

    private var mWindowClassName: String = ""

    private var mFindLaunchActivity: Boolean = false;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d(TAG,"onAccessibilityEvent ${event.toString()}")
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                mWindowIsChange = true
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                mWindowPackageName = if (event.packageName == null) "" else "${event.packageName}"
                mWindowClassName = if (event.className == null) "" else "${event.className}"
                Log.d(TAG, "mWindowPackageName = $mWindowPackageName mWindowClassName = $mWindowClassName rootInActiveWindow = $rootInActiveWindow windows = $windows self $this")
                windows?.forEach {
                    it.root?.takeIf { rootIt -> findDumpNode(rootIt) }?.apply {
                        handleDump(this)
                    }
                }

                handleDump(rootInActiveWindow)
//                    if(App.launchActivityMap.containsValue(mWindowClassName)){
//                        // 当前窗口为启动窗口,标记找到窗口
//                        mFindLaunchActivity = true
//
                    }

                else ->  Log.d(TAG,"onAccessibilityEvent ${event.toString()} \r\n source = ${event?.source} \n window = $windows root = $rootInActiveWindow")
            }
        }

        private fun findDumpNode(rootNode: AccessibilityNodeInfo?): Boolean {
            if (rootNode == null) return false
            return rootNode.findAccessibilityNodeInfosByText("跳过").size > 0
        }

        private fun handleDump(eventSource: AccessibilityNodeInfo?) {
            if (eventSource == null) return
            val nodeInfos = eventSource.findAccessibilityNodeInfosByText("跳过")
            nodeInfos?.forEach {
                Log.d(TAG, "node =========> $it")
            }
            Log.d(TAG, "$nodeInfos")
        }


        private fun isHandlePackage(pks: String): Boolean {
            // TODO
            return true
        }

        override fun onInterrupt() {
            Log.d(TAG, "中断")
        }

        override fun onServiceConnected() {
            super.onServiceConnected()
//            serviceInfo = AccessibilityConfig.config(serviceInfo)
            serviceInfo = serviceInfo.apply {
                flags =  AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
                feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
                eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            }
        }

        override fun onUnbind(intent: Intent?): Boolean {
            Toast.makeText(this, "无障碍服务关闭，请重新打开", Toast.LENGTH_LONG).show();
            return super.onUnbind(intent)
        }

        companion object {
            private val TAG = MyAccessibilityService::class.java.simpleName
        }

    }