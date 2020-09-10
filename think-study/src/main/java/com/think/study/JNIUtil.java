package com.think.study;

public class JNIUtil {


    static {
        System.loadLibrary("reflect");
    }

    public static native String getString();

    public static native void dynamicFunc();
}
