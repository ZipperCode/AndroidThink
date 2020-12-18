package com.think.core.util.download;

import android.content.Context;
import android.content.SharedPreferences;

public class DownloadSPHelper {
    private static final String SP_NAME = "download";

    public static final String DL_MAX_SIZE = "dl_max_size";

    private SharedPreferences mSharedPreferences;

    private static DownloadSPHelper instance;

    private DownloadSPHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static DownloadSPHelper getInstance(Context context) {
        synchronized (DownloadSPHelper.class) {
            if (instance == null) {
                synchronized (DownloadSPHelper.class) {
                    instance = new DownloadSPHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static DownloadSPHelper getInstance() {
        if(instance == null){
            throw new RuntimeException("必须先调用带getInstance(Context)");
        }
        return instance;
    }

    public void putString(String key,String value){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }
}
