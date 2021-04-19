package com.think.xposed;

import android.util.Log;

public class JniHelper {

    public static void init(){
        try {
            System.loadLibrary("dex_helper");
        }catch (Throwable e){
            Log.e("AHook", e.toString());
        }

    }

    public static native void dexFileByCookie(String packageName, Object[] cookies);

    public static native void test(String name);
}
