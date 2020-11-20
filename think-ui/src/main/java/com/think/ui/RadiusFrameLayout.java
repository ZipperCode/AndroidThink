package com.think.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class RadiusFrameLayout extends FrameLayout {

    private static final String TAG = RadiusFrameLayout.class.getSimpleName();
    /**
     * 透明度
     */
    private float mTransparent;

    private RectF mRadiusRectF;

    private final float[] mRadii = new float[8];

    private final Path mRadiusPath = new Path();

    private final Paint mBackgroundPaint = new Paint();


    public RadiusFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.RadiusFrameLayout);
        /* 圆角 */
        int mRadius = typedArray.getDimensionPixelSize(R.styleable.RadiusFrameLayout_border_radius, 0);
        /* 左上角圆角*/
        int mLeftTopRadius = typedArray.getDimensionPixelSize(R.styleable.RadiusFrameLayout_left_top_radius, 0);
        /* 右上角圆角*/
        int mRightTopRadius = typedArray.getDimensionPixelSize(R.styleable.RadiusFrameLayout_right_top_radius, 0);
        /* 左下角圆角*/
        int mLeftBottomRadius = typedArray.getDimensionPixelSize(R.styleable.RadiusFrameLayout_left_bottom_radius, 0);
        /* 右下角圆角*/
        int mRightBottomRadius = typedArray.getDimensionPixelSize(R.styleable.RadiusFrameLayout_right_bottom_radius, 0);
        this.mTransparent = typedArray.getFloat(R.styleable.RadiusFrameLayout_transparent, 1f);
        typedArray.recycle();
        this.mRadiusRectF = new RectF();
        if (mRadius == 0) {
            if (mLeftTopRadius != 0) {
                mRadii[0] = mLeftTopRadius;
                mRadii[1] = mLeftTopRadius;
            }

            if (mRightTopRadius != 0) {
                mRadii[2] = mRightTopRadius;
                mRadii[3] = mRightTopRadius;
            }

            if (mRightBottomRadius != 0) {
                mRadii[4] = mRightBottomRadius;
                mRadii[5] = mRightBottomRadius;
            }

            if (mLeftBottomRadius != 0) {
                mRadii[6] = mLeftBottomRadius;
                mRadii[7] = mLeftBottomRadius;
            }
        } else {
            Arrays.fill(mRadii, mRadius);
        }
        mBackgroundPaint.setAntiAlias(true);
        setBackground(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mRadiusRectF.set(0, 0, w, h);
        this.mRadiusPath.addRoundRect(mRadiusRectF, mRadii, Path.Direction.CCW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.e(TAG, "dispatchDraw" );
        int save = canvas.save();
        canvas.clipPath(mRadiusPath);
        super.dispatchDraw(canvas);
        setAlpha(mTransparent);
        canvas.restoreToCount(save);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,"onDraw");
    }

}
