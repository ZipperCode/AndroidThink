package com.think.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.think.core.util.ui.ScreenUtils;

/**
 * 圆形的ImageView
 */
public class RoundImageView extends AppCompatImageView {

    private static final String TAG = RoundImageView.class.getSimpleName();
    /**
     * 正常情况
     */
    private static final int MODE_NONE = 0;
    /**
     * 圆形情况
     */
    private static final int MODE_CIRCLE = 1;
    /**
     * 圆角情况
     */
    private static final int MODE_ROUND = 2;
    /**
     * 默认为正常
     */
    private int mCurrentMode = MODE_NONE;
    /**
     * 当前圆角大小
     */
    private int currentRound;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 矩阵
     */
    private Matrix matrix;

    public RoundImageView(Context context) {
        this(context,null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        this.paint.setAntiAlias(true);
        // 初始化变换矩阵
        this.matrix = new Matrix();
        this.currentRound = ScreenUtils.dp2px(context,10);
    }

    private void obtainStyledAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        this.mCurrentMode = a.hasValue(R.styleable.RoundImageView_type) ? a.getInt(R.styleable.RoundImageView_type, MODE_NONE) : MODE_NONE;
        currentRound = a.hasValue(R.styleable.RoundImageView_radius) ? a.getDimensionPixelSize(R.styleable.RoundImageView_radius, currentRound) : currentRound;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCurrentMode == MODE_CIRCLE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int result = Math.min(getMeasuredHeight(), getMeasuredWidth());
            setMeasuredDimension(result, result);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }
            canvas.translate(getPaddingLeft(), getPaddingTop());
            if (mCurrentMode == MODE_CIRCLE) {//当为圆形模式的时候
                Bitmap bitmap = drawable2Bitmap(mDrawable);
                paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);
            } else if (mCurrentMode == MODE_ROUND) {//当为圆角模式的时候
                Bitmap bitmap = drawable2Bitmap(mDrawable);
                paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()),
                        currentRound, currentRound, paint);
            } else {
                if (mDrawMatrix != null) {
                    canvas.concat(mDrawMatrix);
                }
                mDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        //根据传递的scaletype获取matrix对象，设置给bitmap
        Matrix matrix = getImageMatrix();
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        return bitmap;
    }

}
