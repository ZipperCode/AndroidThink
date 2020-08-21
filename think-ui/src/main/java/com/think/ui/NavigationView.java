package com.think.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.think.core.util.LogUtils;
import com.think.core.util.ScreentUtils;

/**
 * 组合导航栏View
 * @date 2020-7-26
 * @author zzp
 */
public class NavigationView extends LinearLayout implements  NavigationItemView.OnSelectChangeListener {

    private static final int DEFAULT_TAB_SIZE = 3;

    private static final int DEFAULT_TAB_HEIGHT = 50;

    private static final int MAX_TAB_HEIGHT = 60;

    private NavigationItemView[] mNavigationItemViews;

    public NavigationView(Context context) {
        this(context,null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.border_navigation_line);
        LayoutParams viewLayoutParams = (LayoutParams) getLayoutParams();
        if(viewLayoutParams == null){
            viewLayoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    ScreentUtils.dp2px(getContext(),DEFAULT_TAB_HEIGHT));
        }
        int height = viewLayoutParams.height;
        LogUtils.debug("viewLayoutParams.height = " + viewLayoutParams.height);
        if(height > ScreentUtils.dp2px(getContext(),MAX_TAB_HEIGHT)){
            viewLayoutParams.height = ScreentUtils.dp2px(getContext(),DEFAULT_TAB_HEIGHT);
        }
        LogUtils.debug("viewLayoutParams.height = " + viewLayoutParams.height);
        setLayoutParams(viewLayoutParams);

        mNavigationItemViews = new NavigationItemView[DEFAULT_TAB_SIZE];
        for (int i = 0; i < DEFAULT_TAB_SIZE; i++) {
            mNavigationItemViews[i] = new NavigationItemView(getContext());
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            mNavigationItemViews[i].setLayoutParams(layoutParams);
            mNavigationItemViews[i].setIcon(R.drawable.selector_navigation_select);
            mNavigationItemViews[i].setText("发");
            mNavigationItemViews[i].setTextColorStateList(
                    ContextCompat.getColorStateList(getContext(),R.color.selector_check_color));
            mNavigationItemViews[i].setOnSelectChangeListener(this);
            addView(mNavigationItemViews[i]);
        }
        setDefaultSelected(1);
    }

    @Override
    public void onSelect(NavigationItemView navigationItemView) {
        for (NavigationItemView mNavigationItemView : mNavigationItemViews) {
            if (!mNavigationItemView.equals(navigationItemView)) {
                mNavigationItemView.clearSelect();
            }
        }
    }


    public void setDefaultSelected(int tabPosition){
        if(tabPosition <= 0 || tabPosition > mNavigationItemViews.length){
            mNavigationItemViews[0].setSelected(true);
            return;
        }
        mNavigationItemViews[tabPosition - 1].setSelected(true);
    }
}
