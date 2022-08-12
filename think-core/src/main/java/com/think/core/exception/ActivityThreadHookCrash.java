package com.think.core.exception;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.think.core.util.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * TODO 未测试
 * @author zhangzhipeng
 * @date 2022/8/12
 */
public class ActivityThreadHookCrash implements Handler.Callback {

    private final Handler mH;

    private final Handler.Callback mOriginCallback;

    private final Set<IgnoreExceptionBean> mIgnoreExceptionBeans;

    private boolean inHandle = false;

    public ActivityThreadHookCrash(@NonNull IgnoreExceptionBean[] ignore){
        final Set<IgnoreExceptionBean> packages = new HashSet<>(Arrays.asList(ignore));
        this.mIgnoreExceptionBeans = Collections.unmodifiableSet(packages);
        this.mH = getHandler(getActivityThread());
        this.mOriginCallback = getHandlerCallback(mH);
    }

    public void hook(){
        if (mH == null){
            return;
        }
       try {
           Field mCallbackField = ReflectUtils.loadHideField(ReflectUtils.loadHideForName("android.app.ActivityThread$H"), "mCallback");
           mCallbackField.set(mH, this);
       }catch(Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (inHandle){
            return false;
        }
        try{
            boolean result = false;
            if (mOriginCallback != null){
                result = mOriginCallback.handleMessage(msg);
            }
            if (result){
                return true;
            }

            if (mH != null){
                inHandle = true;
                mH.handleMessage(msg);
                inHandle = false;
                return true;
            }

        }catch (Exception e){
            if (containException(e)){
                return true;
            }
            throw e;
        }

        return false;
    }

    private boolean containException(Exception e){
        String name = e.getClass().getName();
        String msg = e.getMessage();
        for (IgnoreExceptionBean bean : this.mIgnoreExceptionBeans) {
            if (bean.exceptionName.equals(name) && (!bean.ignoreMsg && bean.exceptionName.equals(msg))){
                return true;
            }
        }
        return false;
    }

    private static Object getActivityThread() {
        try {
            Class<?> atCls = ReflectUtils.loadHideForName("android.app.ActivityThread");
            Field currentActivityThreadField = ReflectUtils.loadHideField(atCls, "sCurrentActivityThread");
            return currentActivityThreadField.get(atCls);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Handler getHandler(Object activityThread){
        if (activityThread == null){
            return null;
        }
        try{
            Field mhField = ReflectUtils.loadHideField(activityThread.getClass(), "mH");
            return (Handler) mhField.get(activityThread);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Handler.Callback getHandlerCallback(Handler handler){
        if (handler == null){
            return null;
        }
        try {
            Field mCallbackField = ReflectUtils.loadHideField(ReflectUtils.loadHideForName("android.app.ActivityThread$H"), "mCallback");
            return (Handler.Callback) mCallbackField.get(handler);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
