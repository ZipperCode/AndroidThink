package com.example.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FloatView extends RelativeLayout {

    private static final String TAG = FloatView.class.getSimpleName();
    /**
     * 当前view父容器的宽高
     */
    private int parentViewW;
    private int parentViewH;

    /**
     * 屏幕的宽高
     */
    private int screenWPixel;
    private int screenHPixel;

    private int viewWPixel;
    private int viewHPixel;
    /**
     * 当前view的宽高和半径
     */
    private int viewW;
    private int viewH;
    private int viewRadius;
    /**
     * 当前view手指按下点的坐标
     */
    private int downX;
    private int downY;
    /**
     * 当前view手指按下移动后的最后坐标
     */
    private int lastX;
    private int lastY;
    /**
     * 是否处于移动状态
     */
    private boolean mMoved;

    private int touchSlop;

    private int mStatusBarHeight = 0;

    private final Runnable mDelayAlphaAnim = new Runnable() {
        @Override
        public void run() {
            processAlpha(true);
        }
    };

    private ValueAnimator mTranslateAnim;


    public FloatView(Context context) {
        super(context);
        init();
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatView(Context context, int viewW,int viewH){
        this(context);
        this.viewW = viewW;
        this.viewH = viewH;
    }

    private void init() {
        inflate(getContext(), R.layout.layout_float_icon, this);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWPixel = displayMetrics.widthPixels;
        screenHPixel = displayMetrics.heightPixels;
        if(viewW == 0){
            viewW = 50;
        }
        if(viewH == 0){
            viewH = 50;
        }
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId != 0) {
            mStatusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        setY(mStatusBarHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout >>> change = " + changed + ", l = " + l + ",t = " + t + ",r = " + r + ",b = " + b);
        super.onLayout(changed, l, t, viewWPixel, viewHPixel);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG,"onSizeChanged >>> w = " + w + ",h = " + "oldw = " + oldw + ",oldh = " + oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewW), MeasureSpec.AT_MOST);
        int height = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewH), MeasureSpec.AT_MOST);
        int result = Math.min(width, height);
        viewRadius = result / 2;
        super.onMeasure(result, result);
        viewWPixel = getMeasuredWidth();
        viewHPixel = getMeasuredHeight();
        Log.d(TAG, "onMeasure >>> with = " + MeasureSpec.getSize(width) + ",height = " + MeasureSpec.getSize(height));
        Log.d(TAG, "onMeasure >>> viewWPixel = " + viewWPixel + ",viewHPixel = " + viewHPixel);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchSlop != 0) {
            touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "down");
                return doDown(event);
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "move");
                return doMove(event);
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "up");
                return doUp(event);
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean doDown(MotionEvent event) {
        downX = (int) event.getRawX();
        downY = (int) event.getRawY();
        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
        setPressed(true);
        processScale(true);
        removeCallbacks(mDelayAlphaAnim);
        processAlpha(false);
        cancelTranslate();
        return true;
    }

    private boolean doMove(MotionEvent event) {
        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
        mMoved = Math.abs(lastX - downX) >= touchSlop || Math.abs(lastY - downY) >= touchSlop;
        setX(Math.min(Math.max(0, (lastX - viewWPixel / 2)), screenWPixel - viewWPixel));
        setY(Math.min(Math.max(mStatusBarHeight, (lastY - viewHPixel / 2)), screenHPixel - viewHPixel));
        return true;
    }

    private boolean doUp(MotionEvent event) {
        setPressed(false);
        processScale(false);
        postDelayed(mDelayAlphaAnim,3000);
        if (!mMoved) {
            performClick();
        }
        processTranslate(lastX < (screenWPixel / 2));
        return true;
    }

    @Override
    public boolean performClick() {
        Log.d(TAG,"performClick");
        return super.performClick();
    }

    private void processScale(boolean isDown) {
        float value = isDown ? 0.9f : 1.0f;
        animate().scaleX(value).scaleY(value).setDuration(100).start();
    }

    private void processAlpha(boolean process){
        float value = process ? 0.5f:1f;
        animate().alpha(value).start();
    }

    private void processTranslate(boolean isLeft) {
        Log.d(TAG,"processTranslate >>> isLeft = " + isLeft + ", lastX = " + lastX + ",viewWPixel = " + viewWPixel);
        mTranslateAnim = ValueAnimator.ofInt(lastX, isLeft ? 0 : screenWPixel);
        mTranslateAnim.setInterpolator(new AccelerateInterpolator());
        mTranslateAnim.setDuration(500);
//        mTranslateAnim.setInterpolator(new OvershootInterpolator());
//        mTranslateAnim.setRepeatMode(ValueAnimator.REVERSE);
        mTranslateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lastX = (int) valueAnimator.getAnimatedValue();
                setX(Math.min(Math.max(0, (lastX - viewWPixel / 2)), screenWPixel - viewWPixel));
            }
        });
        mTranslateAnim.start();
    }

    private void cancelTranslate(){
        if(mTranslateAnim != null && mTranslateAnim.isRunning()){
            mTranslateAnim.cancel();
        }
    }

    public static FloatView getInstance(Activity activity) {
        FloatView floatView = new FloatView(activity);
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(floatView);
        return floatView;
    }
}
