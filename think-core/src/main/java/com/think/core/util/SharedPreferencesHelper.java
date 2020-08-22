package com.think.core.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesHelper {

    /**
     * SharePreference 文件名字
     */
    private static final String SP_NAME = "hf_game_box";

    private SharedPreferences mSharedPreferences;

    private static SharedPreferencesHelper instance;

    /**
     * 上下文：单例模式下使用Application 上下文
     */
    private SharedPreferencesHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getInstance(Context context){
        synchronized (SharedPreferencesHelper.class){
            if(instance == null){
                synchronized (SharedPreferencesHelper.class){
                    instance = new SharedPreferencesHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean contains(String key){
        return mSharedPreferences.contains(key);
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key,value);
        edit.apply();
    }

    public String loadString(String key, String defaultValue){
        return mSharedPreferences.getString(key,defaultValue);
    }

    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(key,value);
        edit.apply();
    }

    public boolean loadBoolean(String key,boolean defaultValue){
        return mSharedPreferences.getBoolean(key,defaultValue);
    }
}
