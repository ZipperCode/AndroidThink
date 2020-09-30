package com.think.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ParticleView extends View {
    /**
     * 粒子直径
     */
    private static final int DIAMETER = 2;

    private Paint mPaint;

    private Bitmap mBitmap;

    private ValueAnimator mValueAnimator;

    private int mDiameter = DIAMETER;

    private final List<Particle> mParticleList = new ArrayList<>();


    public ParticleView(Context context) {
        this(context,null);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        for (int i = 0; i < mBitmap.getWidth(); i++) {
            for (int j = 0; j < mBitmap.getHeight(); j++) {
                Particle particle = new Particle();
                particle.color = mBitmap.getPixel(i,j);
                particle.x = i * DIAMETER + DIAMETER / 2;
                particle.y = j * DIAMETER + DIAMETER / 2;
                particle.r = DIAMETER / 2;

                particle.vX = (float) (Math.pow(-1,Math.ceil(Math.random() * 1000)) * 20 * Math.random());
                particle.vY = rangInt(-15,35);
                particle.aX = 1f;
                particle.aY = 0.98f;

                mParticleList.add(particle);
            }
        }
        mValueAnimator = ValueAnimator.ofFloat(0,1.0f);
        mValueAnimator.setRepeatCount(-1);
        mValueAnimator.setDuration(1000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateParticle();
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wh = MeasureSpec.makeMeasureSpec(500,MeasureSpec.EXACTLY);
        super.onMeasure(wh, wh);
    }

    private void updateParticle(){
        for (Particle particle : mParticleList){
            particle.x += particle.vX * particle.aX;
            particle.y += particle.vY * particle.aY;
        }
    }

    private int rangInt(int x, int y){
        int max = Math.max(x,y);
        int min = Math.min(x,y) - 1;

        return (int)(min + Math.ceil(Math.random() * (max - min)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Particle particle : mParticleList){
            mPaint.setColor(particle.color);
            canvas.drawCircle(particle.x,particle.y,particle.r,mPaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mValueAnimator.start();
        }
        return super.onTouchEvent(event);
    }

    class Particle{
        /**
         * 粒子的颜色，可以通过bitmap获取某个像素点的颜色
         */
        int color;
        /**
         * 粒子坐标和半径
         */
        float x;
        float y;
        float r;
        /**
         * 粒子的速度
         */
        float vX;
        float vY;
        /**
         * 粒子的加速度
         */
        float aX;
        float aY;
    }
}
