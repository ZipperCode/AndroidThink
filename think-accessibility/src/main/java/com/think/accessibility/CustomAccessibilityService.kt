package com.think.accessibility

import android.accessibilityservice.*
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

class CustomAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val type = event.eventType
        val mWindowClassName = if (event.className == null) "" else event.className.toString()
        val mCurrentPackage = if (event.packageName == null) "" else event.packageName.toString()
        when (type) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.e(TAG, "窗口类为 = $mWindowClassName, 包名为 = $mCurrentPackage")
                val source = event.source
                val visibleWindowInfo = VisibleWindowInfo(mCurrentPackage)
                addChildRect(source, visibleWindowInfo)
                AccessibilityConfigManager.getInstance().put(mCurrentPackage, visibleWindowInfo)
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                // 控件被点击
                val clickedNode = event.source
                val cacheVisibleWindowInfo = AccessibilityConfigManager.getInstance()[mCurrentPackage]
                val rect = Rect()
                clickedNode.getBoundsInScreen(rect)
                val viewInfo = cacheVisibleWindowInfo.getViewInfo(rect)
                Log.e(TAG, "当前点击的View的信息为 = $viewInfo")
                if (Build.VERSION.SDK_INT > 24) {
                    onClick(viewInfo.mViewInScreen)
                }
                if (viewInfo.clickable) {
//                    viewInfo.clickable = false;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun onClick(rect: Rect) {
        val builder = GestureDescription.Builder()
        /*
         * StrokeDescription参数，path：参数路径，startTime：从开始到手势到画笔遍历的时间，duration：画笔遍历路径的持续时间
         */
        val gestureDescription = builder.addStroke(GestureDescription.StrokeDescription(getRandomPath(rect), 0, 500)).build()
        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                Log.e(TAG, "GestureDescription >>> onCompleted")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
                Log.e(TAG, "GestureDescription >>> onCancelled")
            }
        }, null)
    }

    private fun getRandomPath(rect: Rect): Path {
        val path = Path()
        val point = Point(getRandomPoint(rect.left, rect.right), getRandomPoint(rect.top, rect.bottom))
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        //        path.lineTo((float) point.x + 1, (float) point.y + 1);
        Log.d(TAG, rect.toString())
        Log.d(TAG, point.toString())
        return path
    }

    /**
     * 递归方式获取每一个view节点的信息
     * @param rootNode 根节点
     * @param visibleWindowInfo 保存屏幕信息的类
     */
    private fun addChildRect(rootNode: AccessibilityNodeInfo?, visibleWindowInfo: VisibleWindowInfo) {
        if (rootNode != null) {
            val childCount = rootNode.childCount
            for (i in 0 until childCount) {
                val child = rootNode.getChild(i)
                visibleWindowInfo.put(this, child)
                addChildRect(child, visibleWindowInfo)
                child.recycle()
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
        serviceInfo = AccessibilityConfigManager.getInstance().configAccessibilityServiceInfo(serviceInfo)
    }

    companion object {
        private val TAG = CustomAccessibilityService::class.java.simpleName
        private fun getRandomPoint(start: Int, end: Int): Int {
            return (Math.random() * (end - start) + start).toInt()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            //Rect(0, 183 - 480, 258)
            //Point(480, 163)
            for (i in 0..49) {
                println("x = " + getRandomPoint(0, -480) + ", y = " + getRandomPoint(183, 258))
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 无障碍按钮调用无障碍服务，用户按无障碍服务按钮后的操作
     */
    private var mAccessibilityButtonController: AccessibilityButtonController? = null
    private var accessibilityButtonCallback:
            AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var mIsAccessibilityButtonAvailable: Boolean = false

    /**
     * 无障碍按钮配置,需要sdk26以上
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessibilityButton(){
        mAccessibilityButtonController = accessibilityButtonController
        mIsAccessibilityButtonAvailable = accessibilityButtonController.isAccessibilityButtonAvailable
        if (!mIsAccessibilityButtonAvailable) return
        serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }
        accessibilityButtonCallback =
                object : AccessibilityButtonController.AccessibilityButtonCallback() {
                    override fun onClicked(controller: AccessibilityButtonController) {
                        Log.d("MY_APP_TAG", "Accessibility button pressed!")
                    }

                    override fun onAvailabilityChanged(
                            controller: AccessibilityButtonController,
                            available: Boolean
                    ) {
                        if (controller == mAccessibilityButtonController) {
                            mIsAccessibilityButtonAvailable = available
                        }
                    }
                }

        accessibilityButtonCallback?.also {
            mAccessibilityButtonController?.registerAccessibilityButtonCallback(it, Handler(Looper.getMainLooper()))
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////

    private var gestureController: FingerprintGestureController? = null
    private var fingerprintGestureCallback:
            FingerprintGestureController.FingerprintGestureCallback? = null
    private var mIsGestureDetectionAvailable: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onCreateCall(){
        gestureController = fingerprintGestureController
        mIsGestureDetectionAvailable = gestureController?.isGestureDetectionAvailable ?: false
    }

    /**
     * 无障碍手势滑动，需要sdk26以上
     * 1、需要在manifest重声明 USE_FINGERPRINT 权限和 CAPABILITY_CAN_REQUEST_FINGERPRINT_GESTURES
     * 2、在服务的xml文件中设置标记 FLAG_REQUEST_FINGERPRINT_GESTURES
     * 3、注册手势回调 registerFingerprintGestureCallback()
     *
     * <p>判断设备是否支持指纹传感器：isHardwareDetected()
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fingerprintGestures(){
        if (fingerprintGestureCallback != null || !mIsGestureDetectionAvailable) return

        fingerprintGestureCallback =
                object : FingerprintGestureController.FingerprintGestureCallback() {
                    override fun onGestureDetected(gesture: Int) {
                        when (gesture) {
                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN -> Log.e(TAG, "move down")
                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT -> Log.e(TAG, "move left")
                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT -> Log.e(TAG, "move right")
                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP -> Log.e(TAG, "move up")
                            else -> Log.e(TAG, "Error: Unknown gesture type detected!")
                        }
                    }

                    override fun onGestureDetectionAvailabilityChanged(available: Boolean) {
                        mIsGestureDetectionAvailable = available
                    }
                }

        fingerprintGestureCallback?.also {
            gestureController?.registerFingerprintGestureCallback(it, null)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

}