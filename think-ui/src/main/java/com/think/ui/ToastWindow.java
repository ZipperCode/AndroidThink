package com.think.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

@SuppressLint("ViewConstructor")
public class ToastWindow extends FrameLayout implements Runnable {

    private static final String TAG = ToastWindow.class.getSimpleName();

    private final RectF mRect = new RectF();

    private final Rect mLayoutRect = new Rect();

    private final Paint mPaint = new Paint();

    private final int screenWPixel;
    private final int screenHPixel;

    private WeakReference<ViewGroup> mDecorView;

    private String mMessage = "";

    @ColorInt
    private int mBackgroundColor = Color.BLACK;

    @ColorInt
    private int mTextColor = Color.WHITE;
    /**
     * 圆角大小
     */
    private int mRadius = 10;

    /**
     * 显示位置
     */
    private Alignment mAlignment = Alignment.CENTER;

    /**
     * 文字大小
     */
    private int mTextSize = 12;

    private boolean isLandScope = false;

    public ToastWindow(@NonNull Context context, @NonNull Builder builder) {
        super(context);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWPixel = displayMetrics.widthPixels;
        screenHPixel = displayMetrics.heightPixels;

        mPaint.setStyle(Paint.Style.FILL);
        init(builder);
        mPaint.setColor(mBackgroundColor);
    }

    private void init(Builder builder) {

        if (builder.decorView != null) {
            this.mDecorView = new WeakReference<>(builder.decorView);
        }
        mAlignment = builder.alignment;
        if (builder.textSize != 0) {
            this.mTextSize = builder.textSize;
        }

        if (builder.textColor != 0) {
            this.mTextColor = builder.textColor;
        }

        if (builder.backgroundColor != 0) {
            this.mBackgroundColor = builder.backgroundColor;
        }

        if (builder.radius != 0) {
            this.mRadius = builder.radius;
        }

        if (!TextUtils.isEmpty(builder.message)) {
            this.mMessage = builder.message;
        }

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScope = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        Log.d(TAG, "onConfigurationChanged isLandScope = " + isLandScope);
    }

    private void show() {
        inflate(getContext(), R.layout.layout_toast, this);
        setPadding(20, 10, 20, 10);
        TextView tvText = findViewById(R.id.tv_toast_text);
        if (tvText != null) {
            tvText.setText(mMessage);
            tvText.setTextColor(mTextColor);
            tvText.setTextSize(mTextSize);
        }

        FrameLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
        }
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(layoutParams);

        int alignTop;
        if (mAlignment == Alignment.BOTTOM) {
            alignTop = (isLandScope ? screenWPixel : screenHPixel) / 4 * 3;
        } else if (mAlignment == Alignment.TOP) {
            alignTop = (isLandScope ? screenWPixel : screenHPixel) / 4;
        } else {
            alignTop = (isLandScope ? screenWPixel : screenHPixel) / 2;
        }

        setY(alignTop);
        animate().setDuration(200).scaleX(0).scaleXBy(1.0f).scaleY(0).scaleYBy(1.0f);
        getHandler().postDelayed(this, 2000);
    }

    public void cancel() {
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeCallbacks(this);
        }

        ViewGroup decorView = this.mDecorView.get();
        if (decorView != null) {
            animate().setDuration(200).scaleX(1.0f).scaleXBy(0).scaleY(1.0f).scaleYBy(0);
            decorView.removeView(this);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        this.mDecorView.clear();
        super.onDetachedFromWindow();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d(TAG, "dispatchDraw = " + mRect);
        canvas.clipRect(mRect);
        canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);
        super.dispatchDraw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout >>> change = " + changed + ", l = " + l + ", t = " + t + ", r = " + r + ", b = " + b);
        super.onLayout(changed, l, t, r, b);
        mLayoutRect.set(l, t, r, b);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged >>> w = " + w + " ,h = " + h + ", oldw = " + oldw + ", oldh = " + oldh);
        mRect.set(0, 0, w, h);
    }


    public static void show(Activity context, Builder builder) {
        ViewGroup decorView = (ViewGroup) context.getWindow().getDecorView();
        if (decorView == null) return;

        builder.decorView(decorView);
        ToastWindow toastWindow = new ToastWindow(context, builder);
        View view = decorView.findViewById(R.id.toast_view_id);
        if (view != null) {
            decorView.removeView(view);
        }
        toastWindow.setId(R.id.toast_view_id);
        decorView.addView(toastWindow);
        toastWindow.show();
    }

    public static void show(Activity context, String message) {
        show(context, new Builder()
                .alignment(Alignment.BOTTOM)
                .message(message)
        );
    }

    @Override
    public void run() {
        cancel();
    }

    public static class Builder {
        Alignment alignment = Alignment.CENTER;

        String message;

        ViewGroup decorView;

        int backgroundColor;

        int textColor;

        int textSize;

        int radius = 10;

        public Builder alignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder decorView(ViewGroup decorView) {
            this.decorView = decorView;
            return this;
        }


        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder textColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder radius(int radius) {
            this.radius = radius;
            return this;
        }
    }

    public enum Alignment {
        TOP,
        CENTER,
        BOTTOM
    }
}
