package com.think.core.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.think.core.R;
import com.think.core.fragment.IFragment;
import com.think.core.util.ScreenUtils;
import com.think.core.util.ToastHelper;

import java.lang.ref.WeakReference;


/**
 * @author zzp
 */
public abstract class BaseDialogFragment extends DialogFragment implements
        View.OnClickListener,
        DialogInterface.OnKeyListener,
        View.OnSystemUiVisibilityChangeListener,
        IDialog{
    /**
     * 标签名
     */
    protected String mTagName;

    private boolean mIsUseCustomWidthAndHeight = false;

    private WeakReference<Activity> mActivityRef;

    public BaseDialogFragment(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        mTagName = getClass().getSimpleName();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * 创建View 交给子类去完成，子类必须要实现
     *
     * @param inflater           布局加载器
     * @param container          外层容器
     * @param savedInstanceState 状态
     * @return 视图
     */
    @Nullable
    @Override
    public  View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View layoutView = inflateView();
        initView();
        return layoutView;
    }

    protected abstract View inflateView();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnSystemUiVisibilityChangeListener(this);
    }

    @Override
    public void initView() {

    }

    protected void initData() {
        // TODO subClass implement
    }

    protected void initListener() {
        // TODO subClass implement
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            // 判断是否是横屏，如果是横屏加载横屏的宽高

            Window window = dialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                // 状态栏隐藏
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(
                        View.INVISIBLE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                // 配置窗口动画
                window.setWindowAnimations(R.style.Dialog_Scale_Anim_Style);
                WindowManager.LayoutParams attributes = window.getAttributes();
                // 背景透明
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;
                // 是否处于横屏
                boolean isLandScope = ScreenUtils.getCurrentOrientation(getActivity());
                int dialogHeight = getDialogHeight(isLandScope);
                int dialogWidth = getDialogWidth(isLandScope);
                float heightScaleStand = isLandScope ? 0.6f : 0.5f;
                float widthScaleStand = isLandScope ? 0.6f : 0.8f;

                // 针对超出屏幕的宽高做适配
                if (!mIsUseCustomWidthAndHeight) {
                    // 设置宽高
                    attributes.height = dialogHeight > (screenHeight * heightScaleStand) ?
                            (int) (screenHeight * heightScaleStand) : dialogHeight;
                    attributes.width = dialogWidth > (screenWidth * widthScaleStand) ?
                            (int) (screenWidth * widthScaleStand) : dialogWidth;
                }
                attributes.alpha = 1f;
                attributes.dimAmount = 0f;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 设置使用自定义的宽高
     */
    protected void useCustomDialogWightHeight() {
        this.mIsUseCustomWidthAndHeight = true;
    }
    /**
     * 设置使用自定义的宽高
     */
    protected void cancelCustomDialogWightHeight() {
        this.mIsUseCustomWidthAndHeight = false;
    }

    /**
     * 获取默认实现的Dialog的宽度，返回像素值
     *
     * @param isLandScope 是否处于横屏
     * @return 像素值
     */
    protected int getDialogWidth(boolean isLandScope) {
        return getResources()
                .getDimensionPixelSize(isLandScope ?
                        R.dimen.base_dialog_fragment_land_width :
                        R.dimen.base_dialog_fragment_width
                );
    }

    /**
     * 获取默认实现的Dialog的高度，返回像素值
     *
     * @param isLandScope 是否处于横屏
     * @return 像素值
     */
    protected int getDialogHeight(boolean isLandScope) {
        return getResources()
                .getDimensionPixelSize(isLandScope ?
                        R.dimen.base_dialog_fragment_land_height :
                        R.dimen.base_dialog_fragment_height
                );
    }

    @Override
    public String getTagName() {
        return mTagName;
    }

    @Override
    public void setTagName(String tagName) {
        this.mTagName = tagName;
    }

    /**
     * 隐藏状态栏UI，全屏模式下，Activity的flag状态栏丢失，Dialog会被状态栏遮挡
     */
    protected void hideSystemUI() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            View decorView = getDialog().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.INVISIBLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void toast(String stringResName) {
        if (TextUtils.isEmpty(stringResName)) {
            return;
        }
        ToastHelper.getInstance(getActivity().getApplication()).toast(stringResName);
    }

    @Override
    public void showing() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(this,mTagName);
        }
    }

    @Override
    public void hiding() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(this);
        }
    }

    @Override
    public void dismissing() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(this);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK;
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        hideSystemUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(getClass().getSimpleName() + "onDestroy 方法调用");
    }
}
