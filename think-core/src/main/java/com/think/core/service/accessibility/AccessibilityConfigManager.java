package com.think.core.service.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public final class AccessibilityConfigManager {

    @SuppressLint("StaticFieldLeak")
    private static AccessibilityConfigManager Instance = null;

    private final List<String> useAccessibilityServicePackages;

    private AccessibilityServiceInfo mAccessibilityServiceInfo;

    private int notificationTimeout = 50;

    private int flag = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

    private int eventTypes = AccessibilityEvent.TYPES_ALL_MASK;

//    private Map<Integer,List<>>
    /** 当前窗口的包名 */
    private volatile String mCurrentWindowPackageName;
    /**
     * 包名和窗口的映射关系，使用LRU缓存
     */
    private LinkedHashMap<String,VisibleWindowInfo> mWindowCache = new LinkedHashMap<>(10,0.75f,true);

    private AccessibilityConfigManager(){
        useAccessibilityServicePackages = new ArrayList<>();
    }

    public void putPackage(String pk){
        this.useAccessibilityServicePackages.add(pk);
    }

    public AccessibilityConfigManager putPackages(String...pks){
        this.useAccessibilityServicePackages.addAll(Arrays.asList(pks));
        return this;
    }

    public AccessibilityConfigManager putPackages(List<PackageInfo> pkInfoList){
        for (PackageInfo appInfo : pkInfoList) {
            this.useAccessibilityServicePackages.add(appInfo.packageName);
        }
        return this;
    }

    public AccessibilityConfigManager flag(int flag){
        this.flag = flag;
        return this;
    }

    public AccessibilityConfigManager eventTypes(int eventTypes){
        this.eventTypes = eventTypes;
        return this;
    }

    public AccessibilityConfigManager notificationTimeout(int notificationTimeout){
        this.notificationTimeout = notificationTimeout;
        return this;
    }

    public void setAccessibilityServiceInfo(AccessibilityServiceInfo accessibilityServiceInfo){
        this.mAccessibilityServiceInfo = accessibilityServiceInfo;
    }

    public AccessibilityServiceInfo configAccessibilityServiceInfo(AccessibilityServiceInfo accessibilityServiceInfo){
        this.mAccessibilityServiceInfo = accessibilityServiceInfo;
        if(mAccessibilityServiceInfo == null){
            mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        }
        mAccessibilityServiceInfo.notificationTimeout = notificationTimeout;
        mAccessibilityServiceInfo.flags |= flag;
        mAccessibilityServiceInfo.eventTypes |= eventTypes;
        mAccessibilityServiceInfo.packageNames = this.useAccessibilityServicePackages.toArray(new String[0]);
        return this.mAccessibilityServiceInfo;
    }

    public void setCurrentWindowPackageName(String currentWindowPackageName){
        this.mCurrentWindowPackageName = currentWindowPackageName;
    }

    public String getCurrentWindowPackageName(){
        return mCurrentWindowPackageName;
    }

    public AccessibilityServiceInfo getAccessibilityServiceInfo(){
        return mAccessibilityServiceInfo;
    }

    public void put(String packageName, VisibleWindowInfo visibleWindowInfo){
        mWindowCache.put(packageName,visibleWindowInfo);
    }

    public VisibleWindowInfo get(String packageName){
        return mWindowCache.get(packageName);
    }

    public void clear(){
        mWindowCache.clear();
    }

    public static AccessibilityConfigManager getInstance() {
        synchronized (AccessibilityConfigManager.class) {
            if (Instance == null) {
                synchronized (AccessibilityConfigManager.class) {
                    Instance = new AccessibilityConfigManager();
                }
            }
        }
        return Instance;
    }

}
