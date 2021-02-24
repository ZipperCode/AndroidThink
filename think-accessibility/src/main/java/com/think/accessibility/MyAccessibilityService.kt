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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when(AccessibilityConfig.mode){
            RunMode.DUMP_SPLASH -> {
                when (event?.eventType) {
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                        if (rootInActiveWindow != null) {
                            val findAccessibilityNodeInfosByText =
                                    rootInActiveWindow?.findAccessibilityNodeInfosByText("跳过")
                            Log.d(TAG, "找到跳过的元素个数：${findAccessibilityNodeInfosByText?.size ?: 0}")
                            findAccessibilityNodeInfosByText?.distinct()?.forEach {
                                if (it.isClickable) {
                                    performAction(it)
                                } else {
                                    performAction(findClickableView(it))
                                }
                            }
                        }
                    }
                }
            }
            RunMode.CLOSE ->{
                Toast.makeText(this, "服务关闭，不运行跳过", Toast.LENGTH_LONG).show();
            }
        }


    }

    private fun performAction(nodeInfo: AccessibilityNodeInfo){
        try{
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }finally {
            nodeInfo.recycle()
        }
    }

    private fun findClickableView(childNode: AccessibilityNodeInfo): AccessibilityNodeInfo {
        return if (!childNode.isClickable and (childNode.parent != null)) {
            findClickableView(childNode.parent)
        } else {
            childNode
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "中断")
        Toast.makeText(this, "无障碍服务中断", Toast.LENGTH_LONG).show();
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "无障碍服务已打开", Toast.LENGTH_LONG).show();
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Toast.makeText(this, "无障碍服务关闭，请重新打开", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent)
    }

    companion object {
        private val TAG = MyAccessibilityService::class.java.simpleName
    }

}