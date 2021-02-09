package com.think.ui.draw;

import android.content.Context;
import android.graphics.Canvas;

import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import java.util.List;

public class DrawView extends View {

    private List<SvgBean> svgShapeBeans;

    private int width = 400;

    private int height = 100;

    public DrawView(Context context, List<SvgBean> svgShapeBeans) {
        super(context);
        this.svgShapeBeans = svgShapeBeans;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("","");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        canvas.scale(0.5f,0.5f);
        for (SvgBean svgShapeBean : svgShapeBeans) {
            svgShapeBean.draw(canvas);
        }
        canvas.restore();
    }
}
