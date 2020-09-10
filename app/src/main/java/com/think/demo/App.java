package com.think.demo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.think.study.JNIUtil;

import java.lang.reflect.Method;

import me.weishu.reflection.Reflection;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Reflection.unseal(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Class<?> aClass = null;
        try {
            aClass = Class.forName("android.content.pm.ApplicationInfo");
            aClass.getDeclaredMethod("setHiddenApiEnforcementPolicy",int.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
//        try {
//
////            Class<?> activityClass = Class.forName("dalvik.system.VMRuntime");
////            Method field = activityClass.getDeclaredMethod("setHiddenApiExemptions", String[].class);
////            field.setAccessible(true);
//
//            Method forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
//            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod",String.class,Class[].class);
//
//            Log.e(TAG,"getClassLoader() = " + getClassLoader());
//            Class<?> hiddenClass = (Class<?>) forNameMethod.invoke(null,"android.content.pm.ApplicationInfo");
//
//            Method setHiddenApiMethod = (Method) getDeclaredMethod.invoke(hiddenClass,
//                    "" +
//                            "" +
//                            "",new Class[]{int.class});
//
//            Log.e(TAG,"forNameMethod = " + forNameMethod);
//            Log.e(TAG,"getDeclaredMethod = " + getDeclaredMethod);
//            Log.e(TAG,"hiddenClass = " + hiddenClass);
//            Log.e(TAG,"setHiddenApiMethod = " + setHiddenApiMethod);
////            Class<?> aClass = Class.forName("android.content.pm.ApplicationInfo");
////            aClass.getDeclaredMethod("setHiddenApiEnforcementPolicy",int.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println(JNIUtil.getString());
        JNIUtil.dynamicFunc();
    }
}
