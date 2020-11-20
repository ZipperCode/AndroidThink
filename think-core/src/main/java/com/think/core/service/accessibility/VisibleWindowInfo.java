package com.think.core.service.accessibility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class VisibleWindowInfo {
    private final String mPackageName;
    /**
     * 包上下文，其他包的上下文对象用于获取资源
     */
    private Context mPackageContext;
    /**
     * 当前屏幕上所有的view信息，可能不包含顶层view：如decordView
     */
    private final List<ViewInfo> mAllViewInfoList;

    public VisibleWindowInfo(String mPackageName) {
        this.mPackageName = mPackageName;
        this.mAllViewInfoList = new ArrayList<>();
    }

    /**
     * 将view的信息进行保存，主要是id值娱屏幕中的位置信息
     * @param context 当前应用上下文，用于创建其他包的资源上下文
     * @param nodeInfo 节点信息
     */
    public void put(Context context, AccessibilityNodeInfo nodeInfo) {
        if (mPackageContext == null) {
            try {
                mPackageContext = context.createPackageContext(mPackageName, Context.CONTEXT_RESTRICTED);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        ViewInfo viewInfo = new ViewInfo();
        viewInfo.mClassName = String.valueOf(nodeInfo.getClassName());
        viewInfo.mViewInScreen = rect;
        viewInfo.clickable = nodeInfo.isClickable();
        viewInfo.viewIdName = nodeInfo.getViewIdResourceName();
        if (mPackageContext != null && !TextUtils.isEmpty(viewInfo.viewIdName)) {
            viewInfo.viewId = mPackageContext
                    .getResources()
                    .getIdentifier(viewInfo.viewIdName, "id", mPackageName);
        }
        mAllViewInfoList.add(viewInfo);
    }

    /**
     * 通过屏幕的位置信息获取所在的View信息
     * @param rect 屏幕位置
     * @return 所在位置的view信息
     */
    public ViewInfo getViewInfo(Rect rect) {
        for (ViewInfo viewInfo : mAllViewInfoList) {
            if (rect.left == viewInfo.mViewInScreen.left
                    && rect.right == viewInfo.mViewInScreen.right
                    && rect.top == viewInfo.mViewInScreen.top
                    && rect.bottom == viewInfo.mViewInScreen.bottom
            )
                return viewInfo;
        }
        return null;
    }
}
