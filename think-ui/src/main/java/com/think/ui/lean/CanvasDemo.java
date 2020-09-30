package com.think.ui.lean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.think.ui.R;

public class CanvasDemo extends View {


    private Paint mPaint;

    private Bitmap mBitmap;


    public CanvasDemo(Context context) {
        this(context, null);
    }

    public CanvasDemo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasDemo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.RED);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_card_giftcard_black_24dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制蓝色
        canvas.drawColor(Color.BLUE);
        // 绘制RGB
        canvas.drawRGB(0xFF, 0xFF, 0xFF);
        // 绘制一个100像素的矩形
        canvas.drawRect(0, 0, 100, 100, mPaint);
        // 绘制一个带圆角的矩形
        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(50, 50, 150, 150, 10f, 10f, mPaint);
        }
        // 绘制一个圆形
        canvas.drawCircle(50, 50, 50, mPaint);
        // 绘制一个路径
        Path path = new Path();
        path.lineTo(100, 100);
        canvas.drawPath(path, mPaint);
        // 绘制一条线，参数：开始x坐标，开始y坐标，结束x坐标，结束y坐标，画笔
        canvas.drawLine(0, 0, 100, 100, mPaint);
        // 绘制一个圆弧,创建一个矩形
        RectF rect = new RectF(200,200,400,400);
        // 参数： 矩形，开始角度，结束角度，是否显示弧的两边，画笔
        canvas.drawArc(rect,0,180,false,mPaint);
        // 绘制一个椭圆,传入一个矩形
        canvas.drawOval(rect,mPaint);
        // 绘制一个点，参数，点坐标
        canvas.drawPoint(300,300,mPaint);
        // 绘制多个点
        canvas.drawPoints(new float[]{100f,200f,300f,400f},10,4,mPaint);
        // 绘制一个文本,参数：文本，起点坐标，画笔
        canvas.drawText("哈哈哈",400,400,mPaint);
        // 沿着一条路径绘制文本
        canvas.drawTextOnPath("h哈哈哈",path,10,10,mPaint);
        // 将bitmap绘制到view上，参数：bitmap，左边距离，顶部距离，画笔
        canvas.drawBitmap(mBitmap,500,500,mPaint);


    }
}
