package com.think.core.exception;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.think.core.util.store.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseCrashHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = BaseCrashHandler.class.getSimpleName();

    private final static String ERROR_LOG_FILENAME_PREFIX = "error";
    private final static String ERROR_LOG_FILENAME_SUFFIX = ".log";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);

    @SuppressLint("StaticFieldLeak")
    private static BaseCrashHandler handler = null;
    //保存系统默认的handler
    private Thread.UncaughtExceptionHandler defaultHandler = null;

    private final Context context;
    /**
     * 当前线程名
     */
    private String currentThreadName = null;

    private BaseCrashHandler(Context context){
        this.context = context.getApplicationContext();
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public synchronized static BaseCrashHandler getInstance(Context context){
        return handler == null ? new BaseCrashHandler(context):handler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        currentThreadName = t.getName();
        if(!handleException(e) && defaultHandler != null){
            defaultHandler.uncaughtException(t,e);
        }else{
            // 关闭页面
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息，ex：主线程都崩溃了，这个会不会显示，待定
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context,"程序遇到异常，即将推出。",Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 输出日志
        ex.printStackTrace();
        // 保存日志文件
        saveCrashInfoToFile(ex);
        return true;
    }


    private void saveCrashInfoToFile(Throwable ex) {
        StringBuffer stringBuffer = new StringBuffer();
        String date = SIMPLE_DATE_FORMAT.format(new Date());
        stringBuffer.append(date).append(" ")
                .append(this.context.getApplicationInfo().uid).append("/")
                .append(this.context.getPackageName()).append(" ")
                .append(currentThreadName == null? "Main":currentThreadName).append("->")
                .append(TAG).append(" : ").append(ex.getMessage());


        FileUtils.writeString(context.getFilesDir() + FileUtils.LOG_DIR + File.separator
                        + ERROR_LOG_FILENAME_PREFIX + "-" + date + ERROR_LOG_FILENAME_SUFFIX,
                stringBuffer.toString(),true);
    }
}
