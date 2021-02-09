package com.think.ui.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class SvgBean {

    Path drawPath;

    int fillColor;

    Paint drawPaint;

    public SvgBean() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
    }

    void draw(Canvas canvas){
        canvas.drawPath(drawPath,drawPaint);
    }
}
