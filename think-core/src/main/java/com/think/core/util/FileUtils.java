package com.think.core.util;

import android.os.Environment;

import java.io.File;

/**
 *
 * 文件操作工具类
 * Environment.getDataDirectory() = /data
 * Environment.getDownloadCacheDirectory() = /cache
 * Environment.getExternalStorageDirectory() = /mnt/sdcard
 * Environment.getExternalStoragePublicDirectory(“test”) = /mnt/sdcard/test
 * Environment.getRootDirectory() = /system
 * getPackageCodePath() = /data/app/com.my.app-1.apk
 * getPackageResourcePath() = /data/app/com.my.app-1.apk
 * getCacheDir() = /data/data/com.my.app/cache
 * getDatabasePath(“test”) = /data/data/com.my.app/databases/test
 * getDir(“test”, Context.MODE_PRIVATE) = /data/data/com.my.app/app_test
 * getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
 * getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
 * getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files
 * getFilesDir() = /data/data/com.my.app/files
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 存储卡根目录 /mnt/sdcard
     */
    public static final String SD_ROOT_PATH = Environment.getExternalStorageDirectory()
            + File.separator;
    /**
     * /data 根目录
     */
    public static final String DATA_PATH = Environment.getDataDirectory().getAbsolutePath();

    /**
     * /cache 缓存目录
     */
    public static final String CACHE_ROOT_PATH = Environment.getDownloadCacheDirectory().getAbsolutePath();

    /**
     * 判断外部存储是否可以写
     * @return true表示可写，false表示不可写
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 判断外部存储是否可读
     * @return true表示可读，false表示不可读
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
