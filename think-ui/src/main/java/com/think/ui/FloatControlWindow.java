package com.think.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
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
import android.widget.RelativeLayout;

import com.think.core.util.ui.ScreenUtils;


public class FloatControlWindow extends RelativeLayout {

    private static final String TAG = FloatControlWindow.class.getSimpleName();
    /**
     * 默认view的宽高
     */
    private static final int DEFAULT_VIEW_W = 150;
    private static final int DEFAULT_VIEW_H = 150;

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
    /**
     * 当前view在父容器中的坐标
     */
    private int inViewX;
    private int inViewY;
    /**
     * 当前view在屏幕中的坐标
     */
    private int inScreenX;
    private int inScreenY;
    /**
     * 手指按下时当前view在屏幕中的坐标
     */
    private int downScreenX;
    private int downScreenY;
    /**
     * 最小的移动间隔，小于此间隔及响应事件
     */
    private int touchSlop;
    /**
     * 状态栏的高度
     */
    private int mStatusBarHeight = 0;
    /**
     * 窗口管理器
     */
    private final WindowManager mWindowManager;
    /**
     * 布局参数，当前view在窗口中的位置
     */
    private WindowManager.LayoutParams mLayoutParams;
    /**
     * 是否处于移动的状态
     */
    private boolean mMoved;
    /**
     * 延迟进行透明度变化
     */
    private final Runnable mDelayAlphaAnim = new Runnable() {
        @Override
        public void run() {
            processAlpha(true);
        }
    };

    private final RectF mRectF = new RectF();

    private final Path mPath = new Path();

    private final Paint mPaint = new Paint();


    public FloatControlWindow(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        init(context);
    }

    public FloatControlWindow(Context context, AttributeSet attrs) {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewW), MeasureSpec.AT_MOST);
        int height = MeasureSpec.makeMeasureSpec(ScreenUtils.dp2px(getContext(), viewH), MeasureSpec.AT_MOST);
        int result = Math.min(width, height);
        super.onMeasure(result, result);
        Log.d(TAG, "onMeasure >>> with = " + MeasureSpec.getSize(width) + ",height = " + MeasureSpec.getSize(height));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchSlop != 0) {
            touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(TAG, "down");
                return doDown(event);
            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG, "move");
                return doMove(event);
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "up");
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
//        Log.d(TAG, "inScreenX = " + inScreenX + ",inScreenY = " + inScreenY + ", downScreenX = " + downScreenX + ",downScreenY = " + downScreenY);
        update();
        return true;
    }

    private boolean doUp(MotionEvent event) {
        Log.d(TAG, "pl = " + getPaddingLeft() + ",pr = " + getPaddingRight() + ",pt = " + getPaddingTop() + ",pb = " + getPaddingBottom());
        if (!mMoved) {
            performClick();
        }
        setPressed(false);
        processScale(false);
        postDelayed(mDelayAlphaAnim, 3000);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.e(TAG, "dispatchDraw");
        canvas.save();
        mPath.reset();
        mPath.addRoundRect(mRectF,getMeasuredWidth() / 2f,getMeasuredHeight() / 2f, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Log.e(TAG, "drawChild  child = " + child);
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

    private void processAlpha(boolean process) {
        float value = process ? 0.5f : 1f;
        animate().alpha(value).setDuration(500).start();
    }

    public static boolean checkPermission(Activity activity) {
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
        if (!checkPermission(context)) {
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
