package com.think.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.util.Log
import com.think.accessibility.exception.UnOperationException
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.AppUtils
import com.think.accessibility.utils.TimeHelper
import java.lang.Exception

object AppHelper {

    private val TAG: String = AppHelper::class.java.simpleName

    fun dingDing(context: Context, accessibilityService: AccessibilityService?) {
        if (accessibilityService == null) {
            Log.d(TAG, "[dingDing] -> accessibilityService == null")
            return
        }

        try {
            // 回到桌面
//            AccessibilityUtil.goHome()
//            TimeHelper.waitTime(2 * 1000L)
            openDingDing(context)
            // 休眠5s
            TimeHelper.waitTime(5 * 1000L)
            // 执行打开工作台
            if (openDingDingWorkStation(accessibilityService)) {
                TimeHelper.waitTime(5 * 1000L)
                openAttendance(accessibilityService)
            }

        } catch (e: UnOperationException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openDingDing(context: Context) {
        if (AppUtils.checkAppInstall(context, Const.DING_DING_PACKAGE)) {
            val intent = context.packageManager.getLaunchIntentForPackage(Const.DING_DING_PACKAGE)
            Log.d(TAG, "intent = $intent")
            intent?.run {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        } else {
            Log.d(TAG, "未安装钉钉软件，无法启动")
        }
    }

    fun checkDingDingMain(accessibilityService: AccessibilityService): Boolean {
        if (accessibilityService.rootInActiveWindow == null) {
            return false
        }
        val nav = AccessibilityUtil.findNodeById(accessibilityService.rootInActiveWindow, Const.DING_DING_MAIN_NAV_BAR_ID)
        return nav != null
    }

    fun checkDingDingWork(accessibilityService: AccessibilityService): Boolean {
        if (accessibilityService.rootInActiveWindow == null) {
            return false
        }
        if (!checkDingDingMain(accessibilityService)) {
            return false
        }
        // 包含考勤打卡
//        AccessibilityUtil.findNodeByText(accessibilityService.rootInActiveWindow,Const.DING_DING_ATTENDANCE)
//                ?: throw UnOperationException("工作台中不包含考勤打卡")
        // 包含公司
        val company = AccessibilityUtil.findNodeById(accessibilityService.rootInActiveWindow, Const.DING_DING_CURRENT_COMPANY_ID)
        return company != null
    }

    fun openDingDingWorkStation(accessibilityService: AccessibilityService): Boolean {
        if (accessibilityService.rootInActiveWindow == null) {
            return false
        }
        val workstation = AccessibilityUtil.findNode(accessibilityService.rootInActiveWindow, Const.DING_DING_HOME_APP_ID, Const.DING_DING_WORKSTATION)
        Log.d(TAG, "workstation = $workstation")
        val work = AccessibilityUtil.findNodeByText(accessibilityService.rootInActiveWindow, Const.DING_DING_WORKSTATION)

        return work?.let {
            var result = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AccessibilityUtil.mAccessibilityService?.apply {
                    val rect = Rect()
                    it.getBoundsInScreen(rect)
                    // 无法使用performAction查找点击，只能模拟手势点击
                    AccessibilityUtil.gestureClick(this, AccessibilityUtil.getRandomPath(rect))
                    result = true
                }
            }
            result
        } ?: false
    }

    fun openAttendance(accessibilityService: AccessibilityService) {
        if (accessibilityService.rootInActiveWindow == null) {
            Log.e(TAG, "[openAttendance] rootInActiveWindow  == null")
            return
        }

        if (!checkDingDingWork(accessibilityService)) {
            Log.e(TAG, "[openAttendance] 当前界面不在工作台")
            return
        }
        val attendanceWebView = AccessibilityUtil.findWebViewNode(accessibilityService.rootInActiveWindow, Const.DING_DING_WEB_VIEW_CLASS_NAME)
        attendanceWebView?.apply {
            val attendanceNode = AccessibilityUtil.findWebViewContent(this, Const.DING_DING_ATTENDANCE)
            Log.d(TAG, "attendanceNode = $attendanceNode")
            attendanceNode?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    AccessibilityUtil.mAccessibilityService?.apply {
                        val rect = Rect()
                        attendanceNode.getBoundsInScreen(rect)
                        // 无法使用performAction查找点击，只能模拟手势点击
                        AccessibilityUtil.gestureClick(this, AccessibilityUtil.getRandomPath(rect))
                        Log.d(TAG,"5s 后进行打卡")
                        TimeHelper.waitTime(5 * 1000L)
                        clockIn(accessibilityService)
                    }
                }
            }
        }
    }

    fun attendance(accessibilityService: AccessibilityService) {
        if (accessibilityService.rootInActiveWindow == null) {
            Log.e(TAG, "[attendance] rootInActiveWindow  == null")
            return
        }

    }

    fun checkAttendanceArea(accessibilityService: AccessibilityService): Boolean {
        if (accessibilityService.rootInActiveWindow == null) {
            Log.e(TAG, "[checkAttendanceArea] rootInActiveWindow  == null")
            return false
        }
        val attendanceWebView = AccessibilityUtil.findWebViewNode(accessibilityService.rootInActiveWindow, Const.DING_DING_WEB_VIEW_CLASS_NAME)
        return attendanceWebView?.let {
            val areaNode = AccessibilityUtil.findWebViewContent(it, Const.DING_DING_ATTENDANCE_AREA)
            Log.d(TAG,"areaNode = $areaNode")
            areaNode != null
        }?:false
    }

    fun checkClockIn(accessibilityService: AccessibilityService): Boolean {
        if (accessibilityService.rootInActiveWindow == null) {
            Log.e(TAG, "[checkClockIn] rootInActiveWindow  == null")
            return false
        }

        val attendanceWebView = AccessibilityUtil.findWebViewNode(accessibilityService.rootInActiveWindow, Const.DING_DING_WEB_VIEW_CLASS_NAME)
        return attendanceWebView?.let {
            val clockIn = AccessibilityUtil.findWebViewContent(it, Const.DING_DING_ATTENDANCE_CLOCKED)
            Log.d(TAG, "clockIn = $clockIn")
            clockIn == null
        }?: false
    }

    fun clockIn(accessibilityService: AccessibilityService):Boolean{
        if (accessibilityService.rootInActiveWindow == null) {
            Log.e(TAG, "[checkAttendanceArea] rootInActiveWindow  == null")
            return false
        }
        val attendanceWebView = AccessibilityUtil.findWebViewNode(accessibilityService.rootInActiveWindow, Const.DING_DING_WEB_VIEW_CLASS_NAME)
        return attendanceWebView?.let {
            if(checkAttendanceArea(accessibilityService)){
                Log.d(TAG, "已进入考勤范围")
                val clockIn = AccessibilityUtil.findWebViewContent(it, Const.DING_DING_ATTENDANCE_CLOCK_OUT)
                Log.d(TAG,"clockIn = $clockIn")
                AccessibilityUtil.gestureClick(AccessibilityUtil.mAccessibilityService, clockIn)
                true
            }
            false
        }?: false
    }
}