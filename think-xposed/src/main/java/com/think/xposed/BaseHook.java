package com.think.xposed;

import com.think.xposed.web.WebViewHook;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class BaseHook {

    protected ClassLoader targetClassLoader;

    private boolean debug;

    public BaseHook(ClassLoader targetClassLoader,boolean debug) {
        this.targetClassLoader = targetClassLoader;
        this.debug = debug;
    }

    protected Class<?> findClassIfExists(String className){
        return XposedHelpers.findClassIfExists(className, targetClassLoader);
    }

    protected String getTag(){
        return "";
    }

    protected void catExp(RunFunction runFunction) {
        try {
            runFunction.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void log(String message) {
        if(debug){
            AHook.log("[" + getTag() + "]\t" + message);
        }
    }
}
