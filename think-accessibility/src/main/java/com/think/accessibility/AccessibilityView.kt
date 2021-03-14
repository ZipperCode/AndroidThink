package com.think.accessibility

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Toast
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.room.DBHelper

@SuppressLint("ViewConstructor")
class AccessibilityView(context:Context, viewRectList:List<ViewInfo>) :View(context) {

    private val mViewRectList:List<ViewInfo> = viewRectList

    private var mClickViewViewInfo: ViewInfo? = null

    private val mPaint: Paint = Paint()
    private val mClickPaint: Paint = Paint()

    private var mLongClick = false

    private var mLastClickTime = 0L

    private var mUp = false

    private var mScaledDoubleTapSlop = 0

    private val mLongClickRunnable = Runnable {
        if(!mUp){
            mLongClick = true
        }
    }

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.GREEN

        mClickPaint.style = Paint.Style.STROKE
        mClickPaint.color = Color.GREEN
        mClickPaint.strokeWidth = 5F
        mScaledDoubleTapSlop = ViewConfiguration.get(context).scaledDoubleTapSlop
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(mViewRectList.isNotEmpty()){
            for (viewInfo in mViewRectList) {
                canvas?.drawRect(viewInfo.screenRect, mPaint)
            }
        }

        mClickViewViewInfo?.run {
            canvas?.drawRect(screenRect, mClickPaint)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(mScaledDoubleTapSlop == 0){
            mScaledDoubleTapSlop = ViewConfiguration.get(context).scaledDoubleTapSlop
        }
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                Log.d(TAG,"ACTION_DOWN")
                mUp = false
                val rect = matchRect(Point(event.x.toInt(),event.y.toInt()))
                rect?.run {
                    mClickViewViewInfo = this
                    postInvalidate()
                }
                val time = System.currentTimeMillis() - mLastClickTime
                if(time < mScaledDoubleTapSlop){
                    Toast.makeText(context,"选中当前视图id为 = ${mClickViewViewInfo?.viewId}",Toast.LENGTH_LONG).show()
                    mClickViewViewInfo?.let { DBHelper.getViewInfoDao().insert(it) }
                }
                return true
            }
            MotionEvent.ACTION_UP ->{
                Log.d(TAG,"ACTION_UP")
                mUp = true
                mLastClickTime = System.currentTimeMillis()
                return true
            }
        }
        return true
    }


    private fun matchRect(point: Point):ViewInfo? {
        val pointRect = Rect(point.x,point.y,point.x,point.y)
        var result: ViewInfo? = null
        for (viewInfo in mViewRectList) {
            val rect = viewInfo.screenRect
            if(rect.contains(pointRect)){
                if(result == null){
                    result = viewInfo
                }else{
                    if(!rect.contains(result.screenRect)){
                        result = viewInfo
                    }
                }
            }
        }
        return result
    }

    companion object{
        private val TAG: String = AccessibilityView::class.java.simpleName
    }

}