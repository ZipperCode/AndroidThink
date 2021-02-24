package com.think.accessibility

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.RelativeLayout
import com.think.accessibility.ScreenUtils.dp2px

class FloatWindow : RelativeLayout {
    /**
     * 屏幕的宽高
     */
    private var screenWPixel = 0
    private var screenHPixel = 0

    /**
     * 实际view的宽高 单位：dp1
     */
    private var viewW = 0
    private var viewH = 0

    /**
     * 当前view在父容器中的坐标
     */
    private var inViewX = 0
    private var inViewY = 0

    /**
     * 当前view在屏幕中的坐标
     */
    private var inScreenX = 0
    private var inScreenY = 0

    /**
     * 手指按下时当前view在屏幕中的坐标
     */
    private var downScreenX = 0
    private var downScreenY = 0

    /**
     * 最小的移动间隔，小于此间隔及响应事件
     */
    private var touchSlop = 0

    /**
     * 状态栏的高度
     */
    private var mStatusBarHeight = 0

    /**
     * 窗口管理器
     */
    private val mWindowManager: WindowManager

    /**
     * 布局参数，当前view在窗口中的位置
     */
    private var mLayoutParams: WindowManager.LayoutParams? = null

    /**
     * 是否处于移动的状态
     */
    private var mMoved = false

    /**
     * 延迟进行透明度变化
     */
    private val mDelayAlphaAnim = Runnable { processAlpha(true) }

    /**
     * 平移属性动画
     */
    private var mTranslateAnim: ValueAnimator? = null
    private val mRectF = RectF()
    private val mPath = Path()
    private val mPaint = Paint()

    constructor(context: Context) : super(context) {
        mWindowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mWindowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        init(context)
    }

    private fun init(context: Context) {
        inflate(getContext(), R.layout.layout_float_icon, this)
        val displayMetrics = context.resources.displayMetrics
        screenWPixel = displayMetrics.widthPixels
        screenHPixel = displayMetrics.heightPixels
        touchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
        viewW = DEFAULT_VIEW_W
        viewH = DEFAULT_VIEW_H
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId != 0) {
            mStatusBarHeight = resources.getDimensionPixelSize(resId)
        }
        processScale(false)
        resetTouch(0, screenHPixel / 2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.makeMeasureSpec(dp2px(context, viewW.toFloat()), MeasureSpec.AT_MOST)
        val height = MeasureSpec.makeMeasureSpec(dp2px(context, viewH.toFloat()), MeasureSpec.AT_MOST)
        val result = Math.min(width, height)
        super.onMeasure(result, result)
        Log.d(TAG, "onMeasure >>> with = " + MeasureSpec.getSize(width) + ",height = " + MeasureSpec.getSize(height))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF[0f, 0f, w.toFloat()] = h.toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (touchSlop != 0) {
            touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> //                Log.e(TAG, "down");
                return doDown(event)
            MotionEvent.ACTION_MOVE -> //                Log.e(TAG, "move");
                return doMove(event)
            MotionEvent.ACTION_UP -> //                Log.e(TAG, "up");
                return doUp(event)
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        Log.d(TAG, "performClick")
        return super.performClick()
    }

    private fun doDown(event: MotionEvent): Boolean {
        inViewX = event.x.toInt()
        inViewY = event.y.toInt()
        downScreenX = event.rawX.toInt()
        downScreenY = event.rawY.toInt()
        inScreenX = event.rawX.toInt()
        inScreenY = event.rawY.toInt()
        isPressed = true
        processScale(true)
        removeCallbacks(mDelayAlphaAnim)
        processAlpha(false)
        cancelTranslate()
        return true
    }

    private fun doMove(event: MotionEvent): Boolean {
        inScreenX = event.rawX.toInt()
        inScreenY = event.rawY.toInt()
        mMoved = Math.abs(inScreenX - downScreenX) >= touchSlop || Math.abs(inScreenY - downScreenY) >= touchSlop
        //        Log.d(TAG, "inScreenX = " + inScreenX + ",inScreenY = " + inScreenY + ", downScreenX = " + downScreenX + ",downScreenY = " + downScreenY);
        update()
        return true
    }

    private fun doUp(event: MotionEvent): Boolean {
        Log.d(TAG, "pl = $paddingLeft,pr = $paddingRight,pt = $paddingTop,pb = $paddingBottom")
        if (!mMoved) {
            performClick()
        }
        processTranslate(inScreenX < screenWPixel / 2)
        isPressed = false
        processScale(false)
        postDelayed(mDelayAlphaAnim, 3000)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        Log.e(TAG, "onDraw")
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas) {
        Log.e(TAG, "dispatchDraw")
        canvas.save()
        mPath.reset()
        mPath.addRoundRect(mRectF, measuredWidth / 2f, measuredHeight / 2f, Path.Direction.CW)
        canvas.drawPath(mPath, mPaint)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        Log.e(TAG, "drawChild  child = $child")
        return super.drawChild(canvas, child, drawingTime)
    }

    private fun checkLayoutParam(): Boolean {
        if (mLayoutParams == null) {
            val layoutParams = layoutParams
            if (layoutParams is WindowManager.LayoutParams) {
                mLayoutParams = layoutParams
            }
        }
        return mLayoutParams == null
    }

    private fun update() {
        if (checkLayoutParam()) {
            return
        }
        mLayoutParams!!.gravity = Gravity.START or Gravity.TOP
        mLayoutParams!!.x = inScreenX - inViewX
        mLayoutParams!!.y = inScreenY - inViewY - mStatusBarHeight
        if (parent != null) {
            mWindowManager.updateViewLayout(this, mLayoutParams)
        }
    }

    private fun resetTouch(x: Int, y: Int) {
        inScreenX = x
        inScreenY = y
        update()
    }

    private fun processScale(isDown: Boolean) {
        val value = if (isDown) 0.9f else 1.0f
        animate().scaleX(value).scaleY(value).setDuration(100).start()
    }

    private fun processAlpha(process: Boolean) {
        val value = if (process) 0.5f else 1f
        animate().alpha(value).setDuration(500).start()
    }

    private fun processTranslate(isLeft: Boolean) {
        mTranslateAnim = ValueAnimator.ofInt(inScreenX, if (isLeft) 0 else screenWPixel)
        mTranslateAnim?.setInterpolator(AccelerateInterpolator())
        //        mStartAnim.setInterpolator(new OvershootInterpolator());
        mTranslateAnim?.setDuration(500)
        //        mStartAnim.setRepeatMode(ValueAnimator.REVERSE);
        mTranslateAnim?.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            inScreenX = valueAnimator.animatedValue as Int
            update()
        })
        mTranslateAnim?.start()
    }

    private fun cancelTranslate() {
        if (mTranslateAnim != null && mTranslateAnim!!.isRunning) {
            mTranslateAnim!!.cancel()
        }
    }

    companion object {
        private val TAG = FloatWindow::class.java.simpleName

        /**
         * 默认view的宽高
         */
        private const val DEFAULT_VIEW_W = 50
        private const val DEFAULT_VIEW_H = 50

        @JvmStatic
        private fun checkPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= 23 && context.applicationInfo.targetSdkVersion >= 23) {
                try {
                    if (!Settings.canDrawOverlays(context)) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.packageName))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        return false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return true
        }

        @JvmStatic
        fun getInstance(context: Activity): FloatWindow {
            val floatWindow = FloatWindow(context.applicationContext)
            if (!checkPermission(context)) {
                return floatWindow
            }
            val mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGBA_8888)
            mWindowManager.addView(floatWindow, layoutParams)
            return floatWindow
        }

        @JvmStatic
        fun getInstance(context: Context): FloatWindow {
            val floatWindow = FloatWindow(context.applicationContext)
            if (!checkPermission(context)) {
                return floatWindow
            }
            val mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGBA_8888)
            mWindowManager.addView(floatWindow, layoutParams)
            return floatWindow
        }
    }
}