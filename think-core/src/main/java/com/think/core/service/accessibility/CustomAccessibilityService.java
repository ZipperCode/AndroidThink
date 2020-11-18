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
                if(viewInfo.clickable){
                    viewInfo.clickable = false;


                }


                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onClick(Rect rect){
        Path path = new Path();

        GestureDescription.Builder builder = new GestureDescription.Builder();

    }

    private Path getRandomPath(Rect rect){
        Region region = new Region(rect);
        Path path = new Path();
//        region.getBoundaryPath()
        return path;
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
