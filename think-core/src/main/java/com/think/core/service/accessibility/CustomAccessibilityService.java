package com.think.core.service.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.RequiresApi;

public class CustomAccessibilityService extends AccessibilityService {

    private static final String TAG = CustomAccessibilityService.class.getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        String mWindowClassName = event.getClassName() == null ? "" : event.getClassName().toString();
        String mCurrentPackage = event.getPackageName() == null ? "" : event.getPackageName().toString();
        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e(TAG, "窗口类为 = " + mWindowClassName + ", 包名为 = " + mCurrentPackage);
                AccessibilityNodeInfo source = event.getSource();
                VisibleWindowInfo visibleWindowInfo = new VisibleWindowInfo(mCurrentPackage);
                addChildRect(source, visibleWindowInfo);
                AccessibilityConfigManager.getInstance().put(mCurrentPackage, visibleWindowInfo);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                // 控件被点击
                AccessibilityNodeInfo clickedNode = event.getSource();
                VisibleWindowInfo cacheVisibleWindowInfo = AccessibilityConfigManager.getInstance().get(mCurrentPackage);
                Rect rect = new Rect();
                clickedNode.getBoundsInScreen(rect);
                ViewInfo viewInfo = cacheVisibleWindowInfo.getViewInfo(rect);
                Log.e(TAG, "当前点击的View的信息为 = " + viewInfo);
                if(Build.VERSION.SDK_INT > 24){
                    onClick(viewInfo.mViewInScreen);
                }
                if(viewInfo.clickable){
//                    viewInfo.clickable = false;

                }
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onClick(Rect rect){
        GestureDescription.Builder builder = new GestureDescription.Builder();
        /*
         * StrokeDescription参数，path：参数路径，startTime：从开始到手势到画笔遍历的时间，duration：画笔遍历路径的持续时间
         */
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.
                StrokeDescription(getRandomPath(rect), 0, 500)).build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG,"GestureDescription >>> onCompleted");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"GestureDescription >>> onCancelled");
            }
        },null);
    }

    private Path getRandomPath(Rect rect){
        Path path = new Path();

        Point point = new Point(getRandomPoint(rect.left,rect.right),getRandomPoint(rect.top, rect.bottom));
        path.moveTo(point.x, point.y);
//        path.lineTo((float) point.x + 1, (float) point.y + 1);
        Log.d(TAG,rect.toString());
        Log.d(TAG,point.toString());
        return path;
    }

    private static int getRandomPoint(int start, int end){
        return (int) ((Math.random() * (end - start)) + start);
    }


    public static void main(String[] args) {
        //Rect(0, 183 - 480, 258)
        //Point(480, 163)
        for (int i = 0; i < 50 ; i++) {
            System.out.println("x = " + getRandomPoint(0,-480) +", y = " + getRandomPoint(183,258));
        }

    }
    /**
     * 递归方式获取每一个view节点的信息
     * @param rootNode 根节点
     * @param visibleWindowInfo 保存屏幕信息的类
     */
    private void addChildRect(AccessibilityNodeInfo rootNode, VisibleWindowInfo visibleWindowInfo) {
        if (rootNode != null) {
            int childCount = rootNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = rootNode.getChild(i);
                visibleWindowInfo.put(this, child);
                addChildRect(child, visibleWindowInfo);
                child.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        setServiceInfo(AccessibilityConfigManager.getInstance().configAccessibilityServiceInfo(getServiceInfo()));
    }

}
