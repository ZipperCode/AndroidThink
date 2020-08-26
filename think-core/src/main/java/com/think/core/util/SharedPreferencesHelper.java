package com.think.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {

    /**
     * SharePreference 文件名字
     */
    private static final String SP_NAME = "hf_game_box";

    private SharedPreferences mSharedPreferences;

    private static SharedPreferencesHelper instance;


    private SharedPreferencesHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
//
//    public static SharedPreferencesHelper getInstance() {
//        synchronized (SharedPreferencesHelper.class) {
//            if (instance == null) {
//                synchronized (SharedPreferencesHelper.class) {
//                    instance = new SharedPreferencesHelper(Global.mAppContext);
//                }
//            }
//        }
//        return instance;
//    }

    public static SharedPreferencesHelper getInstance(Context context) {
        synchronized (SharedPreferencesHelper.class) {
            if (instance == null) {
                synchronized (SharedPreferencesHelper.class) {
                    instance = new SharedPreferencesHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String loadString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void saveStringArray(String key, Set<String> values) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putStringSet(key, values);
        edit.apply();
    }

    public void saveStringArray(String key, String... values) {
        Set<String> stringSet = mSharedPreferences.getStringSet(key, new HashSet<String>());
        stringSet.addAll(Arrays.asList(values));
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putStringSet(key, stringSet);
        edit.apply();
    }

    public Set<String> loadStringArray(String key) {
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    /**
     * 获取一个boolean值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    public boolean loadBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }
}
