package com.think.core.util;

import android.text.TextUtils;
import android.util.Log;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.WARN;


/**
 * SDK日志输出工具类
 * @author zzp
 */
public class LogUtils {

    /**
     * TAG
     */
    public static final String TAG = "LogUtils";

    private static boolean isDebugMode = true;

    public static void info(String msg) {
        print(Log.INFO, msg);
    }

    public static void info(String tag, String msg) {
        print(Log.INFO, TAG,tag + ":=>"+ msg);
    }

    public static void infoM(String msg) {
        print(Log.INFO, TAG, ":=>[Method]" + msg);
    }

    public static void debug(String msg) {
        print(Log.DEBUG, msg);
    }

    public static void debug(String tag, String msg) {
        print(Log.DEBUG,TAG,tag + ":=>"+ msg);
    }

    public static void debugM(String msg) {
        print(Log.INFO, TAG, ":=>[Method]" + msg);
    }

    public static void debugM(String tag, String msg) {
        print(Log.INFO, TAG, tag + ":=>[Method]" + msg);
    }

    public static void warn(String msg) {
        print(Log.WARN, msg);
    }

    public static void warn(String tag, String msg) {
        print(Log.WARN, TAG,tag + ":=>"+ msg);
    }


    public static void error(String msg) {
        print(Log.ERROR, msg);
    }

    public static void error(String tag, String msg) {
        print(Log.ERROR, TAG,tag + ":=>"+ msg);
    }

    public static void setIsDebugMode(boolean isDebug) {
        isDebugMode = isDebug;
    }

    public static void print(int logType, String msg) {
        print(logType, TAG, msg);
    }

    public static void print(int logType, String Tag, String msg) {
        switch (logType) {
            case WARN:
                Log.w(Tag, TextUtils.isEmpty(msg) ? "" : msg);
                break;
            case DEBUG:
                if (isDebugMode) {
                    Log.d(Tag, TextUtils.isEmpty(msg) ? "" : msg);
                }
                break;
            case ERROR:
                Log.e(Tag, TextUtils.isEmpty(msg) ? "" : msg);
                break;
            case INFO:
            default:
                Log.i(Tag, TextUtils.isEmpty(msg) ? "" : msg);
        }

    }
}
