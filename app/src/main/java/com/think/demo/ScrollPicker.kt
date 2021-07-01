package com.think.demo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class ScrollPicker : View, Runnable {

    private var mData: List<String> = arrayListOf(
            "11111111111111111",
            "2222222222222",
            "3333333333",
            "444444444",
            "5555555555555"
    )

    private lateinit var mSelectPaint: Paint
    private lateinit var mPaint: Paint
    private lateinit var mLinePaint: Paint

    private var mCurrentSelect: Int = 0

    private var mItemCount = DEF_ITEM_COUNT

    private var mItemH: Int = 0

    private var mViewH: Int = 0

    private var mViewW: Int = 0
    private var mTopLine: Float = 0f

    private var mButtonLine: Float = 0f
    private var mLastDownY: Float = 0f

    private var mMoveLen: Float = 0f
    private var mMaxScrollLen: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(attributeSet)
    }

    private fun init(attributeSet: AttributeSet) {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollPicker)
        mItemCount = typeArray.getInt(R.styleable.ScrollPicker_displayItemCount, DEF_ITEM_COUNT)
        mItemH = typeArray.getDimensionPixelSize(R.styleable.ScrollPicker_displayItemHeight, dp2px(context, DEF_VIEW_H.toFloat()))
        typeArray.recycle()

        mSelectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectPaint.apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            color = Color.RED
            textSize = 40f
        }

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.apply {
            color = Color.GREEN
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mTopLine = mItemH * ((mItemCount - 1) / 2f)
        mButtonLine = mItemH * (mItemCount / 2f)

        mMaxScrollLen = (mButtonLine - mTopLine) * mData.size * 1.0f
    }

    private fun initView() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        Log.d(TAG, "width = $width , height = $height")

        var measureSpecW = widthMeasureSpec
        var measureSpecH = heightMeasureSpec
        if (height == 0) {
            measureSpecH = MeasureSpec.makeMeasureSpec(mItemCount * mItemH, MeasureSpec.EXACTLY)
        }
        if (width == 0) {
            measureSpecW = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST)
        }
        setMeasuredDimension(measureSpecW, measureSpecH)

        mViewW = measuredWidth
        mViewH = measuredHeight

        Log.d(TAG, "mViewW = $mViewW , mViewH = $mViewH")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val textX = mViewW / 2.0f
        val textY = mButtonLine// + mMoveLen

        val fmi = mPaint.fontMetricsInt
        val baseline = textY // - (fmi.bottom / 2.0f + fmi.top / 2.0f)
        val baseline1 = textY - (mButtonLine - mTopLine) / 2 - (fmi.bottom / 2.0f + fmi.top / 2.0f)
        drawLine(canvas)

        canvas?.apply {
            // 绘制选中文本
            drawText(mData[mCurrentSelect], textX, baseline1, mSelectPaint)

            // 绘制上方
            for (i in 0 until mCurrentSelect) {
                drawOtherText(canvas, i + 1, -1)
            }

            for (i in mCurrentSelect  until  mData.size) {
                drawOtherText(canvas, i - mCurrentSelect + 1, 1)
            }


        }
    }

    private fun drawLine(canvas: Canvas?) {
        canvas?.apply {
            drawLine(0f, mTopLine, width.toFloat(), mTopLine, mLinePaint)
            drawLine(0f, mButtonLine, width.toFloat(), mButtonLine, mLinePaint)
        }
    }

    private fun drawOtherText(canvas: Canvas?, offset: Int, type: Int) {
        canvas?.apply {
            // 绘制选中文本
            val p = mCurrentSelect + type * offset
//            Log.e(TAG,"mCurrentSelect = $mCurrentSelect offset = $offset p = $p")
            if (p < mData.size) {
                val textY = mButtonLine  + type * (mButtonLine - mTopLine) * offset
                val fmi = mPaint.fontMetricsInt

                val baseline1 = textY - (mButtonLine - mTopLine) / 2 - (fmi.bottom / 2.0f + fmi.top / 2.0f)
                val baseline = textY - (fmi.bottom / 2.0f + fmi.top / 2.0f)
                drawText(mData[p], mViewW / 2.0f, baseline1, mPaint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                doDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                doMove(event)
            }
            MotionEvent.ACTION_UP -> {
                doUp(event)
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun doDown(event: MotionEvent): Boolean {
        mLastDownY = event.y
        return true
    }

    private fun doMove(event: MotionEvent): Boolean {
        Log.e(TAG,"是否向上 = ${event.y - mLastDownY < 0}")
        val originMoveLen = mMoveLen
        mMoveLen = (event.y - mLastDownY)

        Log.e(TAG,"mCurrentSelect= $mCurrentSelect originMoveLen = $originMoveLen mMoveLen = $mMoveLen event.y = ${event.y} mLastDownY = $mLastDownY")

        if(event.y - mLastDownY > 0){
            // 向下滑
            Log.e(TAG, "向下滑 mMoveLen = $mMoveLen")
            val index = (abs(mMoveLen) / (mButtonLine - mTopLine)).toInt()
            mCurrentSelect = ((mButtonLine - mTopLine) * mCurrentSelect / mMoveLen).toInt()
//            if(mCurrentSelect > 0){
//                Log.e(TAG,"originMoveLen - mMoveLen = ${originMoveLen - mMoveLen} area = ${mButtonLine - mTopLine}")
//                // 非顶部项
//                if(mMoveLen >= (mButtonLine - mTopLine)){
//                    // 选中项为 滑动长度 / 项大小
//                    mCurrentSelect = ((mButtonLine - mTopLine) * mCurrentSelect / mMoveLen).toInt()
//                    Log.e(TAG, "下滑到上个位置 mCurrentSelect = $mCurrentSelect mMoveLen = $mMoveLen")
//                }
//            }
        }else {
            // 向上滑 负值
            Log.e(TAG, "向上滑 mMoveLen = $mMoveLen")
            val index = (abs(mMoveLen) / (mButtonLine - mTopLine)).toInt()
            mCurrentSelect = if(index == 0) 0 else index - 1
//            if(mCurrentSelect < mData.size - 1){
//
//                if(abs(mMoveLen) >= (mButtonLine - mTopLine)){
//                    Log.e(TAG,"上滑到下个位置 mMoveLen = $mMoveLen mCurrentSelect = $mCurrentSelect")
//                }else if(abs(mMoveLen) >=  (mButtonLine - mTopLine) / 2){
//                    Log.e(TAG,"上滑到下一个位置 mMoveLen = $mMoveLen")
//                    if(mCurrentSelect < mData.size - 1){
//                        mCurrentSelect = (abs(mMoveLen) / (mButtonLine - mTopLine)).toInt() + 1
//                    }
//                }
//            }
        }
        invalidate()
//        Log.e(TAG,"doMove mMoveLen = $mMoveLen mLastDownY = $mLastDownY mMaxScrollLen = $mMaxScrollLen")
//        mLastDownY = event.y
        return true
    }


    private fun doUp(event: MotionEvent): Boolean {
//        Log.e(TAG, "doUp mMoveLen = $mMoveLen mMaxScrollLen = $mMaxScrollLen")

        postInvalidate()
//        if (mMoveLen > mMaxScrollLen) {
//            mMoveLen = mMaxScrollLen
//            invalidate()
//            return true
//        }
//        if(mMoveLen < 0){
//            mMoveLen = 0f
//
////            val textY = mButtonLine + mMoveLen
////            val fmi = mPaint.fontMetricsInt
////            val baseline1 = textY - (mButtonLine - mTopLine) / 2 - (fmi.bottom / 2.0f + fmi.top / 2.0f)
////            if(abs(mMoveLen) > (mButtonLine - mTopLine) ){
////
////            }
//        }

        return true
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    fun dp2px(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {

        val TAG: String = ScrollPicker::class.java.simpleName

        const val MAX_TEXT_SIZE = 80
        const val MIN_TEXT_SIZE = 40

        const val MAX_TEXT_ALPHA = 255
        const val MIN_TEXT_ALPHA = 120

        const val DEF_VIEW_H = 60

        const val DEF_ITEM_COUNT = 5
    }
}