package com.think.ui.lean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.think.ui.R;

public class GradientLayout extends View {

    private Paint mPaint;

    private Bitmap mBitmap;


    public GradientLayout(Context context) {
        super(context);
    }

    public GradientLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GradientLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init() {
        mPaint = new Paint();
        // 设置画笔颜色
        mPaint.setColor(Color.GREEN);
        // 设置画笔描边宽度
        mPaint.setStrokeWidth(10f);
        // 设置文本颜色
        mPaint.setTextSize(12f);
        // 设置透明度
        mPaint.setAlpha(10);
        // 设置rgb颜色和透明度
        mPaint.setARGB(10, 0, 0, 0);
        // 设置颜色过滤器
        mPaint.setColorFilter(new ColorFilter());
        // 绘制文本
        mPaint.breakText("哈哈哈哈", true, 100, null);
        // 返回测量的文本的宽度 float
        mPaint.measureText("哈哈哈哈");
        // 重置画笔
        mPaint.reset();
        if (Build.VERSION.SDK_INT >= 21) {
            mPaint.setLetterSpacing(10f);
        }
        // 设置着色器
        mPaint.setShader(new Shader());
        // 设置画笔风格，填充、描边等
        mPaint.setStyle(Paint.Style.FILL);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_card_giftcard_black_24dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         *  线性渐变
         *  x0:起始横坐标
         *  y0:起始纵坐标
         *  x1:结束横坐标
         *  y1:结束纵坐标
         *  colors:渐变颜色
         *  positions:渐变偏移
         *  TileMode:模式，拉伸，平铺，镜像
         */
        LinearGradient linearGradient = new LinearGradient(0, 0, 100, 100,
                new int[]{Color.RED, Color.BLUE}, new float[]{0f, 1.0f}, TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        canvas.drawCircle(100,100,100,mPaint);
        canvas.save();
        /**
         *  放射
         *  x0:圆心横坐标
         *  y0:圆心纵坐标
         *  radius:半径
         *  colors:颜色数组
         *  positions:相对偏移
         *  TileMode:模式，同上
         */
        RadialGradient radialGradient = new RadialGradient(100,100, 100,
                new int[]{Color.RED, Color.TRANSPARENT}, new float[]{0.5f,1.0f}, TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        /**
         * 扫描渐变,雷达渐变圆弧等
         *  x0:圆心横坐标
         *  y0:圆心纵坐标
         *  colors:颜色数组
         *  positions:相对偏移
         */
        SweepGradient sweepGradient = new SweepGradient(100, 100,new int[]{Color.RED, Color.TRANSPARENT} , null);
        mPaint.setShader(sweepGradient);


        /**
         * 位图
         */
        BitmapShader bitmapShader = new BitmapShader(mBitmap, TileMode.CLAMP, TileMode.CLAMP);
        mPaint.setShader(bitmapShader);

        // 混合
        ComposeShader composeShader = new ComposeShader(bitmapShader, linearGradient, PorterDuff.Mode.ADD);
        mPaint.setShader(composeShader);

    }
}
