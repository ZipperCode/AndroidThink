package com.think.core.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static final String DATA_PATH = Environment.getDataDirectory().getAbsolutePath()
            + File.separator;

    /**
     * /cache 缓存目录
     */
    public static final String CACHE_ROOT_PATH = Environment.getDownloadCacheDirectory()
            .getAbsolutePath() + File.separator;

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

    /**
     * 将BitMap图片放在根目录
     * @param bitmap bitmap图片
     * @param fileName 文件名称
     * @return 保存的路径
     */
    public String saveBitmap(Bitmap bitmap, String fileName){
        String imgPath = SD_ROOT_PATH +fileName;//直接放根目录
        File file = new File(imgPath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgPath;
    }

    /**
     * 从文件中获取Bitmap图片信息
     * @param path 文件路径
     * @return bitMap图片
     */
    public static Bitmap getImageForPath(String path){
        Bitmap bitmap = null;
        if(path == null || "".equals(path))
            return null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String downloadRename(String srcFileName){
        String parentPath = new File(srcFileName).getParent();
        System.out.println("parentPath = " + parentPath);
        return appendFileName(srcFileName,0);
    }

    public static String appendFileName(String srcFileName, int index){
        File file = new File(srcFileName);
        if(!file.exists()){
            return srcFileName;
        }
        String fileName = file.getName();
        String parentPath = file.getParent();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = fileName.substring(0,fileName.lastIndexOf("."));
        if(fileName.matches("(.+)\\(\\d*\\)")){
            fileName = fileName.replaceAll("\\(\\d*\\)","(" + (++index) + ")");
        }else{
            fileName = fileName + "(" +(++index)+ ")";
        }
        return appendFileName(parentPath + File.separator + fileName +"."+ ext,index);
    }
}
