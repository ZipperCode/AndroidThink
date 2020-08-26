package com.think.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.think.core.util.LogUtils;
import com.think.core.util.ScreentUtils;

public class NavigationItemView extends LinearLayout implements View.OnClickListener {

    /**
     * 默认字体颜色
     */
    private static final int DEFAULT_TEXT_COLOR = android.R.color.black;
    /**
     * 默认字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 14;
    /**
     * 最大字体大小
     */
    private static final int MAX_TEXT_SIZE = 18;
    /**
     * 默认icon大小
     */
    private static final int DEFAULT_ICON_SIZE = 25;
    /**
     * 最大icon大小
     */
    private static final int MAX_ICON_SIZE = 30;

    /**
     * 是否被选中
     */
    private boolean mSelected;
    /**
     * 显示的icon
     */
    private ImageView mNavIcon;
    /**
     * 显示的标题
     */
    private TextView mNavTitle;

    private OnSelectChangeListener mOnSelectChangeListener;


    public NavigationItemView(@NonNull Context context) {
        this(context, null);
    }

    public NavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        mNavIcon = new ImageView(context);
        mNavTitle = new TextView(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationItemView);
        Drawable icon = typedArray.getDrawable(R.styleable.NavigationItemView_icon);
        String title = TextUtils.isEmpty(typedArray.getString(R.styleable.NavigationItemView_title)) ?
                "" : typedArray.getString(R.styleable.NavigationItemView_title);

        int iconSize = typedArray.getDimensionPixelSize(R.styleable.NavigationItemView_icon_size,
                ScreentUtils.dp2px(context, DEFAULT_ICON_SIZE));
        float textSize = typedArray.getDimension(R.styleable.NavigationItemView_text_size,
                DEFAULT_TEXT_SIZE);

//        if(typedArray.hasValue(R.styleable.NavigationItemView_text_color)){
//            int textColor = typedArray.getColor(R.styleable.NavigationItemView_text_color,
//                    ContextCompat.getColor(context,DEFAULT_TEXT_COLOR));
//            mNavTitle.setTextColor(textColor);
//            LogUtils.debug("TextColor = Color");
//        }
        if (typedArray.hasValue(R.styleable.NavigationItemView_text_color_state)) {
            ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.NavigationItemView_text_color_state);
            mNavTitle.setTextColor(colorStateList);
        }
        typedArray.recycle();

        if (iconSize >= ScreentUtils.dp2px(context, MAX_ICON_SIZE)) {
            iconSize = ScreentUtils.dp2px(context, MAX_ICON_SIZE);
        }
        LayoutParams mNavIconLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams mNavTitleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mNavIconLayoutParams.gravity = Gravity.CENTER;
        mNavTitleLayoutParams.gravity = Gravity.CENTER;
        mNavIconLayoutParams.height = iconSize;
        mNavIconLayoutParams.width = iconSize;
        LogUtils.debug("TextSize = " + textSize + ",title = " + title + ", iconSize = " + iconSize);

        mNavIcon.setLayoutParams(mNavIconLayoutParams);
        mNavTitle.setLayoutParams(mNavTitleLayoutParams);
        setTextSize((int) textSize);
        setText(title);
        setIcon(icon);
        addView(mNavIcon);
        addView(mNavTitle);
        setClickable(true);
        setFocusable(true);
        setOnClickListener(null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
        mNavTitle.setSelected(selected);
        mNavIcon.setSelected(selected);
        if (mOnSelectChangeListener != null && selected) {
            mOnSelectChangeListener.onSelect(this);
        }
        super.setSelected(selected);
    }

    public void setText(String title) {
        mNavTitle.setText(title);
    }

    public void setTextColor(@ColorInt int textColor) {
        mNavTitle.setTextColor(textColor);
    }

    public void setTextColorStateList(ColorStateList colorStateList) {
        mNavTitle.setTextColor(colorStateList);
    }


    public void setTextSize(@Dimension int textSize) {
        if (textSize > MAX_TEXT_SIZE) {
            textSize = MAX_TEXT_SIZE;
        }
        mNavTitle.setTextSize(textSize);
    }

    public void setIcon(int iconDrawable) {
        if (iconDrawable != 0) {
            mNavIcon.setImageResource(iconDrawable);
        }
    }

    public void setIcon(Drawable iconDrawable) {
        if (iconDrawable != null) {
            mNavIcon.setImageDrawable(iconDrawable);
        }
    }

    public void setIconSize(@Dimension int iconSize) {
        LayoutParams layoutParams = (LayoutParams) mNavIcon.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
        }

        if (iconSize >= MAX_ICON_SIZE) {
            iconSize = MAX_ICON_SIZE;
        }
        layoutParams.weight = ScreentUtils.dp2px(getContext(), iconSize);
        layoutParams.height = ScreentUtils.dp2px(getContext(), iconSize);
        mNavIcon.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_button_click));
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(true);
                onClick(v);
                if (l != null) {
                    l.onClick(v);
                }
            }
        });
    }

    public void clearSelect() {
        setSelected(false);
    }

    public boolean getSelect() {
        return mSelected;
    }

    public void setOnSelectChangeListener(OnSelectChangeListener mOnSelectChangeListener) {
        this.mOnSelectChangeListener = mOnSelectChangeListener;
    }

    public interface OnSelectChangeListener {
        void onSelect(NavigationItemView navigationItemView);
    }

}
