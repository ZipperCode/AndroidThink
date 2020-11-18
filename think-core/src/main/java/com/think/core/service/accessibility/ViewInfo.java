package com.think.core.service.accessibility;

import android.graphics.Path;
import android.graphics.Rect;

public class ViewInfo {

    public String mClassName;
    public String viewIdName;
    public Rect mViewInScreen;
    public int viewId;
    public boolean clickable;
    public String mText;
    public int mWidthPixel;
    public int mHeightPixel;

    public boolean isClickable;
    public boolean isLongClickable;
    public boolean isEnable;
    public boolean isChecked;
    public boolean isEditable;
    public boolean isSelected;
    public String mActivityName;

    private Path mPath;

    @Override
    public String toString() {
        return "ViewInfo{" +
                "mClassName='" + mClassName + '\'' +
                ", viewIdName='" + viewIdName + '\'' +
                ", mViewInScreen=" + mViewInScreen +
                ", viewId=" + viewId +
                ", clickable=" + clickable +
                ", mText='" + mText + '\'' +
                ", mWidthPixel=" + mWidthPixel +
                ", mHeightPixel=" + mHeightPixel +
                ", isClickable=" + isClickable +
                ", isLongClickable=" + isLongClickable +
                ", isEnable=" + isEnable +
                ", isChecked=" + isChecked +
                ", isEditable=" + isEditable +
                ", isSelected=" + isSelected +
                ", mActivityName='" + mActivityName + '\'' +
                '}';
    }
}
