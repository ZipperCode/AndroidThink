package com.think.demo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        Log.d(TAG,"wincow = " + rootInActiveWindow);
        int type=event.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String mWindowClassName = event.getClassName().toString();
                String mCurrentPackage = event.getPackageName()==null?"":event.getPackageName().toString();
                Log.e(TAG,"窗口类为 = " + mWindowClassName + ", 包名为 = " + mCurrentPackage);
//                AccessibilityNodeInfo source = event.getSource();
//
//                String viewIdResourceName = source.getViewIdResourceName();
//                print(source);
//                Log.e(TAG,"布局名称为 = " + viewIdResourceName);

                if(mCurrentPackage.equals(getPackageName())){
                    AccessibilityNodeInfo rootView = event.getSource();
                    final Rect rootRect = new Rect();
                    rootView.getBoundsInScreen(rootRect);
                    final ArrayList<Rect> list = new ArrayList<>();

                    parserRect(rootView,list);

                }

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                AccessibilityNodeInfo node = event.getSource();
//                Toast.makeText(this, ,Toast.LENGTH_LONG).show();
                Log.e(TAG,"所点击的组件 = " + node);
                Log.e(TAG,"所点击的组件的类是 = " + node.getClassName());
                Log.e(TAG,"所点击的组件的类的子类数量 = " + node.getChildCount());
                Log.e(TAG,"所点击的组件的id名称是 = " + node.getViewIdResourceName());
                AccessibilityNodeInfo parent = node.getParent();
                Log.e(TAG,"所点击的组件的类是 = " + parent.getClassName());
                Log.e(TAG,"所点击的组件的类的子类数量 = " + parent.getChildCount());
                Log.e(TAG,"所点击的组件的id名称是 = " + parent.getViewIdResourceName());

                print(parent);
                break;
        }
    }

    private void parserRect(AccessibilityNodeInfo rootNode, List<Rect> rectList){
        if(rootNode != null){
            int childCount = rootNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = rootNode.getChild(i);
                Rect rect = new Rect();
                child.getBoundsInParent(rect);
                rectList.add(rect);
                parserRect(child,rectList);
            }
        }
    }

    private void print(AccessibilityNodeInfo rootNode){
        Log.d(TAG,"Node = " + rootNode.getClassName() + ",id = " + rootNode.getViewIdResourceName());
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = rootNode.getChild(i);
            print(child);
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG,"onServiceConnected");
        AccessibilityServiceInfo accessibilityServiceInfo = getServiceInfo();
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
//        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        List<String> packages = new ArrayList<>(installedPackages.size());
        for (PackageInfo installedPackage : installedPackages) {
            packages.add(installedPackage.packageName);
        }
        accessibilityServiceInfo.packageNames = packages.toArray(new String[]{});
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.flags = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        accessibilityServiceInfo.notificationTimeout = 100;
        setServiceInfo(accessibilityServiceInfo);
    }
}
