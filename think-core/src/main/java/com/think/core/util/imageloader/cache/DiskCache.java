package com.think.core.util.imageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.think.core.cache.disk.DiskLruCache;
import com.think.core.util.imageloader.Key;
import com.think.core.util.imageloader.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskCache {

    public static final String DEFAULT_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "Image_Disk";

    public final int APP_VERSION = 1;

    public final int VALUE_COUNT = 1;

    public final int MAX_VALUES = 1024 * 1024 * 100;

    private DiskLruCache diskLruCache;

    private String saveDir;
    // app版本，如果版本号被修改，则缓存被清空
    private int appVersion;
    // 1
    private int valueCount;
    // 缓存的最大值
    private int maxValues;

    public DiskCache(File saveDir) {
        appVersion = APP_VERSION;
        valueCount = VALUE_COUNT;
        maxValues = MAX_VALUES;

        try {
            diskLruCache = DiskLruCache.open(saveDir,appVersion,valueCount,maxValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将数据写入到磁盘缓存中去
     * @param key
     * @param value
     */
    public void put(String key, Value value){
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }finally {
            try {
                if(outputStream != null){
                    outputStream.close();
                }
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从磁盘缓存中获取数据
     * @param key key
     * @return 读取的数据，如果不存在返回null
     */
    public Value get(String key){
        InputStream inputStream = null;
        Value value = Value.getInstance();
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if(snapshot != null){
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                value.setKey(new Key(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
