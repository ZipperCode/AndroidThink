package com.think.accessibility.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.think.accessibility.AccessibilityConfig
import com.think.accessibility.Const
import com.think.accessibility.RunMode
import com.think.accessibility.activity.MainActivity
import com.think.accessibility.activity.TranslucentActivity
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.utils.*
import kotlinx.coroutines.*

class MyAccessibilityService : AccessibilityService() {

    private val mCoroutineScopeIO = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
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
                            mCoroutineScopeIO.launch {
                                // 判断当前包名的app是否处理跳过
                                if (AccessibilityUtil.pksContains(packageName)) {
                                    dumpSplash(this@run, packageName)
                                }
                            }
                        }
                    }
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                        rootInActiveWindow?.run {

//                            Log.d(TAG, """
//                            [窗口状态改变]
//                            ----> event.packageName = ${event.packageName},
//                            ----> event.className = ${event.className},
//                            ----> event.beforeText = ${event.beforeText},
//                            ----> event.text = ${event.text},
//                            ----> event.contentDescription = ${event.contentDescription},
//                            ----> event.source = ${event.source},
//                            ----> event.windowId = ${event.windowId},
//                            ----> event.windowChanges = ${if (Build.VERSION.SDK_INT > 28) singleWindowChangeTypeToString(event.windowChanges) else "none"},
//                            ----> rootInActiveWindow.packageName = ${rootInActiveWindow.packageName},
//                            ----> rootInActiveWindow.className = ${rootInActiveWindow.className},
//                            ----> rootInActiveWindow.text = ${rootInActiveWindow.text},
//                            ----> rootInActiveWindow.contentDescription = ${rootInActiveWindow.contentDescription},
//                            ----> rootInActiveWindow.error = ${rootInActiveWindow.error},
//                            ----> rootInActiveWindow.tooltipText = ${if (Build.VERSION.SDK_INT > 28) rootInActiveWindow.tooltipText else "none"},
//                            ----> rootInActiveWindow.hintText = ${if (Build.VERSION.SDK_INT > 28) rootInActiveWindow.hintText else "none"},
//                            ----> rootInActiveWindow.labelFor = ${rootInActiveWindow.labelFor},
//                            ----> rootInActiveWindow.window = ${rootInActiveWindow.window},
//                            ----> rootInActiveWindow.isContentInvalid = ${rootInActiveWindow.isContentInvalid},
//                            ----> rootInActiveWindow.isVisibleToUser = ${rootInActiveWindow.isVisibleToUser},
//                            ----> rootInActiveWindow.extras = ${rootInActiveWindow.extras},
//                            ----> rootInActiveWindow.availableExtraData = ${if (Build.VERSION.SDK_INT > 26) rootInActiveWindow.availableExtraData?.toString() else "none"},
//                        """)
                        }
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
            pks: String
    ) {
        Log.d(TAG, "当前pks = $packageName 需要进行跳过处理")
        var clicked = false
        // 判断是否有自定义设定的viewId需要跳过
        val dumpViewIds = AccessibilityUtil.viewInfoListIds(pks)
        Log.d(TAG, "查找到的 dumpViewIds = $dumpViewIds")
        dumpViewIds.forEach {
            // 查找所有拥有当前id的view，处理跳过
            clicked = clicked or (AccessibilityUtil.findNodeById(rootNodeInfo, it)?.let { node ->
                if (node.isClickable) {
                    AccessibilityUtil.click(node)
                } else {
                    AccessibilityUtil.deepClick(node)
                }
            } ?: false)
        }


        if (!clicked) {
            val dumpName = SpHelper.loadString(pks)
            // 能查找到包含[跳过]文本的组件
            val dumpNode = AccessibilityUtil.findNodeByText(rootNodeInfo, if(TextUtils.isEmpty(dumpName)) Const.DUMP_AD_TEXT_1 else dumpName)
            dumpNode?.let {
                return@let if (it.isClickable) {
                    AccessibilityUtil.click(it)
                } else {
                    AccessibilityUtil.deepClick(it)
                }
            }
        }

    }

    override fun onInterrupt() {
        Log.d(TAG, "中断")
        Toast.makeText(this, "无障碍服务中断", Toast.LENGTH_LONG).show();
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "无障碍服务已打开")
        Toast.makeText(this, "无障碍服务已打开", Toast.LENGTH_LONG).show()

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }

        // 开启前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, GuardService::class.java));
        } else {
            startService(Intent(this, GuardService::class.java))
        }
        AccessibilityUtil.mAccessibilityService = this

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                AccessibilityUtil.init(this@MyAccessibilityService)
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "无障碍服务关闭，请重新打开")
        Toast.makeText(this, "无障碍服务关闭，请重新打开", Toast.LENGTH_LONG).show();
        stopService(Intent(this, GuardService::class.java));
        AccessibilityUtil.mAccessibilityService = null
        return super.onUnbind(intent)
    }

    companion object {
        private val TAG = MyAccessibilityService::class.java.simpleName

        private fun singleWindowChangeTypeToString(type: Int): String {
            return when (type) {
                AccessibilityEvent.WINDOWS_CHANGE_ADDED -> "WINDOWS_CHANGE_ADDED"
                AccessibilityEvent.WINDOWS_CHANGE_REMOVED -> "WINDOWS_CHANGE_REMOVED"
                AccessibilityEvent.WINDOWS_CHANGE_TITLE -> "WINDOWS_CHANGE_TITLE"
                AccessibilityEvent.WINDOWS_CHANGE_BOUNDS -> "WINDOWS_CHANGE_BOUNDS"
                AccessibilityEvent.WINDOWS_CHANGE_LAYER -> "WINDOWS_CHANGE_LAYER"
                AccessibilityEvent.WINDOWS_CHANGE_ACTIVE -> "WINDOWS_CHANGE_ACTIVE"
                AccessibilityEvent.WINDOWS_CHANGE_FOCUSED -> "WINDOWS_CHANGE_FOCUSED"
                AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED -> "WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED"
                AccessibilityEvent.WINDOWS_CHANGE_PARENT -> "WINDOWS_CHANGE_PARENT"
                AccessibilityEvent.WINDOWS_CHANGE_CHILDREN -> "WINDOWS_CHANGE_CHILDREN"
                AccessibilityEvent.WINDOWS_CHANGE_PIP -> "WINDOWS_CHANGE_PIP"
                else -> Integer.toHexString(type)
            }
        }
    }

}