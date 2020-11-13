package com.example.myapplication;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

public class FloatWindow extends RelativeLayout {

    private static final String TAG = FloatWindow.class.getSimpleName();
    /**
     * 默认view的宽高
     */
    private static final int DEFAULT_VIEW_W = 50;
    private static final int DEFAULT_VIEW_H = 50;

    private static final int[] SHADOWS_COLOR = new int[]{0xFFFFFFFF, 0x00FFFFFF, 0x00FFFFFF};

    /**
     * 屏幕的宽高
     */
    private int screenWPixel;
    private int screenHPixel;
    /**
     * 实际view的宽高 单位：dp1
     */
    private int viewW;
    private int viewH;

//    private int viewWPixel;
//    private int viewHPixel;

    private int inViewX;
    private int inViewY;

    private int inScreenX;
    private int inScreenY;

    private int downScreenX;
    private int downScreenY;
    /**
     * 最小的移动间隔，小于此间隔及响应事件
     */
    private int touchSlop;

    private int mStatusBarHeight = 0;

    private final WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private boolean mMoved;

    private int mRadius;

    private final Runnable mDelayAlphaAnim = new Runnable() {
        @Override
        public void run() {
            processAlpha(true);
        }
    };

    public FloatWindow(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        init(context);
    }

    public FloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        init(context);
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.layout_float_icon, this);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWPixel = displayMetrics.widthPixels;
        screenHPixel = displayMetrics.heightPixels;
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        Log.d(TAG, "init >>> screenW = " + screenWPixel + ",screenH = " + screenHPixel + ",touchSlop = " + touchSlop);
        viewW = DEFAULT_VIEW_W;
        viewH = DEFAULT_VIEW_H;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId != 0) {
            mStatusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        processScale(false);
        resetTouch(0, screenHPixel / 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG,"onLayout >>> change = " + changed + ", l = " + l + ",t = " + t + ",r = " + r + ",b = " + b);
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewW), MeasureSpec.AT_MOST);
        int height = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewH), MeasureSpec.AT_MOST);
        int result = Math.min(width,height);
        mRadius = result / 2;
        super.onMeasure(result, result);
        Log.d(TAG, "onMeasure >>> with = " + MeasureSpec.getSize(width) + ",height = " + MeasureSpec.getSize(height));
    }

    @SuppressLint("ClickableViewAccessibility")
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

    @Override
    public boolean performClick() {
        Log.d(TAG, "performClick");
        return super.performClick();
    }

    private boolean doDown(MotionEvent event) {
        inViewX = (int) event.getX();
        inViewY = (int) event.getY();
        downScreenX = (int) event.getRawX();
        downScreenY = (int) event.getRawY();
        inScreenX = (int) event.getRawX();
        inScreenY = (int) event.getRawY();
        setPressed(true);
        processScale(true);
        removeCallbacks(mDelayAlphaAnim);
        processAlpha(false);
        return true;
    }

    private boolean doMove(MotionEvent event) {
        inScreenX = (int) event.getRawX();
        inScreenY = (int) event.getRawY();
        mMoved = Math.abs(inScreenX - downScreenX) >= touchSlop || Math.abs(inScreenY - downScreenY) >= touchSlop;
        Log.d(TAG, "inScreenX = " + inScreenX + ",inScreenY = " + inScreenY + ", downScreenX = " + downScreenX + ",downScreenY = " + downScreenY);
        update();
        return true;
    }

    private boolean doUp(MotionEvent event) {
        Log.d(TAG, "pl = " + getPaddingLeft() + ",pr = " + getPaddingRight() + ",pt = " + getPaddingTop() + ",pb = " + getPaddingBottom());
        if (!mMoved) {
            performClick();
        }
        processTranslate(inScreenX < screenWPixel / 2);
        setPressed(false);
        processScale(false);
        postDelayed(mDelayAlphaAnim,3000);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG,"onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Log.e(TAG,"drawChild  child = " + child);
        return super.drawChild(canvas, child, drawingTime);
    }

    private boolean checkLayoutParam() {
        if (mLayoutParams == null) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams instanceof WindowManager.LayoutParams) {
                mLayoutParams = (WindowManager.LayoutParams) layoutParams;
            }
        }
        return mLayoutParams == null;
    }

    private void update() {
        if (checkLayoutParam()) {
            return;
        }
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.x = inScreenX - inViewX;
        mLayoutParams.y = inScreenY - inViewY - mStatusBarHeight;
        if (getParent() != null) {
            mWindowManager.updateViewLayout(this, mLayoutParams);
        }
    }

    private void resetTouch(int x, int y) {
        inScreenX = x;
        inScreenY = y;
        update();
    }

    private void processScale(boolean isDown) {
        float value = isDown ? 0.9f : 1.0f;
        animate().scaleX(value).scaleY(value).setDuration(100).start();
    }

    private void processAlpha(boolean process){
        float value = process ? 0.5f:1f;
        animate().alpha(value).setDuration(500).start();
    }

    private void processTranslate(boolean isLeft) {
        ValueAnimator mStartAnim = ValueAnimator.ofInt(inScreenX, isLeft ? 0 : screenWPixel);
        mStartAnim.setInterpolator(new AccelerateInterpolator());
//        mStartAnim.setInterpolator(new OvershootInterpolator());
        mStartAnim.setDuration(500);
//        mStartAnim.setRepeatMode(ValueAnimator.REVERSE);
        mStartAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                inScreenX = (int) valueAnimator.getAnimatedValue();
                update();
            }
        });
        mStartAnim.start();
    }

    public static boolean checkPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= 23 && activity.getApplicationInfo().targetSdkVersion >= 23) {
            try {
                if (!Settings.canDrawOverlays(activity)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, 123);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static FloatWindow getInstance(Activity context) {
        FloatWindow floatWindow = new FloatWindow(context.getApplicationContext());
        if(!checkPermission(context)){
            return floatWindow;
        }
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);
        mWindowManager.addView(floatWindow, layoutParams);
        return floatWindow;
    }
}
