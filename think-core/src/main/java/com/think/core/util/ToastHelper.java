package com.think.core.util;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Toast 封装类
 * @author zzp
 */
public final class ToastHelper {

    private static final String TAG = ToastHelper.class.getSimpleName();

    private volatile static ToastHelper mToastHelper = null;

    private Context mContext;

    private Toast mToast;

    public static ToastHelper getInstance(Application context){
        synchronized (ToastHelper.class){
            if(mToastHelper == null){
                synchronized (ToastHelper.class){
                    mToastHelper = new ToastHelper(context);
                }
            }
        }
        return mToastHelper;
    }

    private ToastHelper(Context context){
        mContext = context;
        mToast = new Toast(mContext);
        mToast.setGravity(Gravity.BOTTOM, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mToast.setDuration(Toast.LENGTH_SHORT);
    }

    /**
     * 显示提示消息
     * @param msg 字符串
     */
    public void toast(final String msg){
        ThreadManager.getInstance().execOnMainThread(new Runnable() {
            @Override
            public void run() {
                mToast.cancel();
                mToast.setText(msg);
                mToast.show();
            }
        });
    }

    /**
     * 显示提示消息
     * @param stringResId 字符串资源id
     */
    public void toast(int stringResId){
        String msg = mContext.getResources().getString(stringResId);
        toast(msg);
    }

    public void setDuration(int duration){
        this.mToast.setDuration(duration);
    }
}
