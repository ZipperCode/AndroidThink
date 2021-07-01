package com.think.core.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.think.core.util.AppUtils;

import java.lang.ref.WeakReference;

/**
 * @author : zzp
 * @date : 2020/9/3
 **/
public abstract class BaseDialog extends Dialog implements IDialog{

    private String mTagName;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View layoutView = inflateView();
        setContentView(layoutView);
        initView();
    }

    @Override
    public void showing() {
        show();
    }

    @Override
    public void hiding() {
        hide();
    }

    @Override
    public void dismissing() {
        dismiss();
    }

    @Override
    public String getTagName() {
        return mTagName;
    }

    @Override
    public void setTagName(String tagName) {
        this.mTagName = tagName;
    }


    protected abstract View inflateView();

}
