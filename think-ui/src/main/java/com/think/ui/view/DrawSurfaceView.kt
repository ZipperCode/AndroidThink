package com.think.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 *
 * @author  zhangzhipeng
 * @date    2022/10/10
 */
class DrawSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    init {
        setZOrderOnTop(true)
        holder?.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        Log.e("BAAAA", "DrawSurfaceView draw = $canvas")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.e("BAAAA", "DrawSurfaceView surfaceCreated")
        Thread {
            val rect = Rect(0, 0, width, height)
            val paint = Paint().apply {
                color = Color.rgb((Math.random() * 255).toInt(), (Math.random() * 255).toInt() , (Math.random() * 255).toInt())
            }
            val drawRect = Rect(0,0,width / 2, height/ 2)

            while (!Thread.interrupted()){
                holder?.run {
                    val lockCanvas = lockCanvas(rect)
                    lockCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    lockCanvas.drawRect(drawRect, paint)
                    unlockCanvasAndPost(lockCanvas)

                    Thread.sleep(100)
                    paint.color = Color.rgb((Math.random() * 255).toInt(), (Math.random() * 255).toInt() , (Math.random() * 255).toInt())
                }

            }
        }.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.e("BAAAA", "DrawSurfaceView surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.e("BAAAA", "DrawSurfaceView surfaceDestroyed")
    }


}
