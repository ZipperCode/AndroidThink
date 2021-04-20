package com.think.jetpack.demo.setting.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.think.jetpack.R
import kotlin.math.sqrt

class HeadImageView : AppCompatImageView {

    private val mRadius: Float

    private val mPaint: Paint = Paint()

    private val mRadiusRect: Rect = Rect()

    private val mRadiusPath: Path = Path()

    private var mSize: Int = 0

    constructor(context: Context): this(context, null)

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(context, attr, defStyle)  {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.HeadImageView)
        mRadius = typeArray.getDimension(R.styleable.HeadImageView_circleRadius, 0f)
        typeArray.recycle()
        mPaint.isAntiAlias = true
        mPaint.flags = Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = measuredHeight.coerceAtMost(measuredWidth)
        mSize = MeasureSpec.getSize(result)
        mRadiusRect.set(0, 0, mSize, mSize)
        mRadiusPath.addCircle(mSize / 2.0f, mSize / 2.0f, sqrt(mSize.toDouble()).toFloat(), Path.Direction.CCW)
        super.onMeasure(mSize, mSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            canvas.clipPath(mRadiusPath)
        }
    }
    private fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        //根据传递的scaleType获取matrix对象，设置给bitmap
        val matrix = imageMatrix
        if (matrix != null) {
            canvas.concat(matrix)
        }
        drawable.draw(canvas)
        return bitmap
    }
}