package com.think.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent

object AccessibilityConfig {
    var useAccessibilityServicePackages: List<String> = ArrayList()

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

    fun config(serviceInfo: AccessibilityServiceInfo) : AccessibilityServiceInfo{
        mAccessibilityServiceInfo = serviceInfo
        if (mAccessibilityServiceInfo == null) {
            mAccessibilityServiceInfo = AccessibilityServiceInfo()
        }
        mAccessibilityServiceInfo!!.notificationTimeout = notificationTimeout.toLong()
        mAccessibilityServiceInfo!!.flags = mAccessibilityServiceInfo!!.flags or flag
        mAccessibilityServiceInfo!!.eventTypes = mAccessibilityServiceInfo!!.eventTypes or eventTypes
        mAccessibilityServiceInfo!!.packageNames = useAccessibilityServicePackages.toTypedArray()
        return mAccessibilityServiceInfo!!
    }


}