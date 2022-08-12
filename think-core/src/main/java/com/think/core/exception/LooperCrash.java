package com.think.core.exception;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangzhipeng
 * @date 2022/8/12
 */
public class LooperCrash {

    private static Set<IgnoreExceptionBean> sIgnoreExceptionBeans = new HashSet<>(0);

    public static boolean sRunning = true;

    public static void initialize(@NonNull IgnoreExceptionBean[] ignore){
        final Set<IgnoreExceptionBean> packages = new HashSet<>(Arrays.asList(ignore));
        sIgnoreExceptionBeans = Collections.unmodifiableSet(packages);
        new Handler(Looper.getMainLooper()).post(() -> {
            while(sRunning && !Thread.interrupted()){
                try{
                    Looper.loop();
                }catch (Exception e) {
                    if (!containException(e)){
                        throw e;
                    }
                }
            }
        });
    }

    private static boolean containException(Exception e){
        String name = e.getClass().getName();
        String msg = e.getMessage();
        for (IgnoreExceptionBean bean : sIgnoreExceptionBeans) {
            if (bean.exceptionName.equals(name) && (!bean.ignoreMsg && bean.exceptionName.equals(msg))){
                return true;
            }
        }
        return false;
    }

}
