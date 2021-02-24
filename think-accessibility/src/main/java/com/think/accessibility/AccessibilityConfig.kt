package com.think.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent

object AccessibilityConfig {

    var mAccessibilityServiceInfo: AccessibilityServiceInfo? = null

    var notificationTimeout = 50

    private var flag = ( AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            or AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS)
        set(value) {
            field = field or value
        }
        get() = field

    private var eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        get() = field
        set(value){
            field = field or value
        }

    private var mFilterPackages: Set<String> = HashSet()

    fun putPackage(pks: String){
        mFilterPackages.plus(pks)
    }

    fun putPackage(pks: List<String>){
        mFilterPackages.plus(pks)
    }

    var mode: RunMode = RunMode.DUMP_SPLASH

    fun defaultConfig() : AccessibilityServiceInfo{
        mAccessibilityServiceInfo = AccessibilityServiceInfo()
        mAccessibilityServiceInfo!!.notificationTimeout = 50
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mAccessibilityServiceInfo!!.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        mAccessibilityServiceInfo!!.flags = mAccessibilityServiceInfo!!.flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        mAccessibilityServiceInfo!!.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        mAccessibilityServiceInfo!!.packageNames = emptyArray()
        return mAccessibilityServiceInfo!!
    }

    fun config(serviceInfo: AccessibilityServiceInfo) : AccessibilityServiceInfo{
        mAccessibilityServiceInfo = serviceInfo
        if (mAccessibilityServiceInfo == null) {
            mAccessibilityServiceInfo = AccessibilityServiceInfo()
        }
        mAccessibilityServiceInfo!!.notificationTimeout = notificationTimeout.toLong()
        mAccessibilityServiceInfo!!.flags = mAccessibilityServiceInfo!!.flags or flag
        mAccessibilityServiceInfo!!.eventTypes = mAccessibilityServiceInfo!!.eventTypes or eventTypes
        mAccessibilityServiceInfo!!.packageNames = mFilterPackages.toTypedArray()
        return mAccessibilityServiceInfo!!
    }




}