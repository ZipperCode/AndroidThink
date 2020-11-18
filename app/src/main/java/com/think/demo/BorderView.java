package com.think.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.List;

public class BorderView extends RelativeLayout {

    private static final String TAG = BorderView.class.getSimpleName();

    private List<Rect> mChildViewRect;

    private int rootViewW;
    private int rootViewH;

    Paint mPaint = new Paint();

    public BorderView(Context context, int rootViewW, int rootViewH, List<Rect> childViewRect) {
        super(context);
        this.rootViewW = rootViewW;
        this.rootViewH = rootViewH;
        this.mChildViewRect = childViewRect;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG,"onMeasure");
        super.onMeasure(MeasureSpec.makeMeasureSpec(rootViewW,MeasureSpec.getMode(widthMeasureSpec))
                , MeasureSpec.makeMeasureSpec(rootViewH,MeasureSpec.getMode(heightMeasureSpec)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG,"onDraw");
        super.onDraw(canvas);
        canvas.drawRect(0,0,rootViewW,rootViewH,mPaint);
        for (Rect rect : mChildViewRect) {
            canvas.drawRect(rect,mPaint);
        }
    }
}
