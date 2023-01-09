package com.think.core.util.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.think.core.util.BarUtils;

public class ViewScreenHelper extends BroadcastReceiver {

    public static final String FULL_SCREEN_ACTION = "com.think.core.action.screen.full";
    public static final String LAND_SCREEN_ACTION = "com.think.core.action.screen.land";
    public static final String PORT_SCREEN_ACTION = "com.think.core.action.screen.port";
    /**
     * ui设计屏幕的宽高
     */
    private static final float UI_SCREEN_WIDTH = 1080f;
    private static final float UI_SCREEN_HEIGHT = 1920f;

    /**
     * 手机屏幕的宽高
     */
    private float mAppScreenWidth;
    private float mAppScreenHeight;

    /**
     * 是否处于横屏状态
     */
    private boolean mIsLandScope;
    /**
     * 是否全屏
     */
    private boolean mIsFullScreen;

    private float mSystemBarHeight;

    private float mSystemNavHeight;

    private static ViewScreenHelper INSTANCE;

    private ViewScreenHelper(Context context) {
        this.mIsLandScope = ScreenUtils.getCurrentOrientation(context);
        int[] appScreen = ScreenUtils.getAppScreenWH(context);
        this.mAppScreenWidth = appScreen[0];
        this.mAppScreenHeight = appScreen[1];
        this.mSystemNavHeight = BarUtils.getNavigationBarHeight(context);
        this.mSystemBarHeight = BarUtils.getStatusBarHeight(context);
    }

    public static ViewScreenHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ViewScreenHelper(context);
        }
        return INSTANCE;
    }

    public static ViewScreenHelper getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("必须先调用带Context的getInstance方法");
        }
        return INSTANCE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("收到广播： " + intent);
        if (FULL_SCREEN_ACTION.equals(action)) {
            mIsFullScreen = true;
        } else if (LAND_SCREEN_ACTION.equals(action)) {
            mIsLandScope = true;
        } else {
            mIsFullScreen = false;
            mIsLandScope = false;
        }
    }

    public float getHorizontalScale() {
        return mAppScreenWidth / UI_SCREEN_WIDTH;
    }

    public float getVerticalScale() {
        return mAppScreenHeight / UI_SCREEN_HEIGHT;
    }

    public void adjustRelativeLayoutViewSize(View view, int wPx, int hPx, int mTop, int mRight, int mBottom, int mLeft) {
        float scaleX = getHorizontalScale();
        float scaleY = getVerticalScale();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams((int) (wPx * scaleX), (int) (hPx * scaleY));
        } else {
            layoutParams.width = (int) (wPx * scaleX);
            layoutParams.height = (int) (hPx * scaleY);
        }

        layoutParams.topMargin = (int) (mTop * scaleY);
        layoutParams.rightMargin = (int) (mRight * scaleX);
        layoutParams.bottomMargin = (int) (mBottom * scaleY);
        layoutParams.leftMargin = (int) (mLeft * scaleX);
        view.setLayoutParams(layoutParams);
    }


    @Override
    public String toString() {
        return "ViewScreenHelper{" +
                "mAppScreenWidth=" + mAppScreenWidth +
                ", mAppScreenHeight=" + mAppScreenHeight +
                ", mIsLandScope=" + mIsLandScope +
                ", mIsFullScreen=" + mIsFullScreen +
                ", mSystemBarHeight=" + mSystemBarHeight +
                ", mSystemNavHeight=" + mSystemNavHeight +
                '}';
    }
}
