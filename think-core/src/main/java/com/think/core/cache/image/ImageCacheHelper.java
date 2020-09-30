package com.think.core.cache.image;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.LruCache;

import com.think.core.cache.disk.DiskLruCache;
import com.think.core.util.security.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ImageCacheHelper {

    /* 默认内存缓存为内存大小的1/8 */
    public static final int DEFAULT_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
    /* 默认磁盘缓存的大小 */
    public static final int DEFAULT_DISK_CACHE_SIZE = 100 * 1024 * 1024;
    /* 默认复用内存的大小 */
    public static final int DEFAULT_BITMAP_MULTIPLEX_POOL_SIZE = 20;

    public static ImageCacheHelper mInstance = null;

    public static ImageCacheHelper getInstance() {
        synchronized (ImageCacheHelper.class) {
            if (mInstance == null) {
                synchronized (ImageCacheHelper.class) {
                    mInstance = new ImageCacheHelper();
                }
            }
        }
        return mInstance;
    }
    /* App 上下文 */
    private Context mContext;
    /* 内存Lru缓存 */
    private final LruCache<String, Bitmap> mCache;
    /* 磁盘LRU缓存 */
    private DiskLruCache mDiskLruCache;
    /* 复用内存 */
    private final Set<WeakReference<Bitmap>> mMultiplePool;
    /* 引用队列，用于回收bitmap */
    private final ReferenceQueue<Bitmap> mReferenceQueue;

    private boolean mIsShutdownCycle;

    private ImageCacheHelper() {
        this.mMultiplePool = Collections.synchronizedSet(
                new HashSet<WeakReference<Bitmap>>(DEFAULT_BITMAP_MULTIPLEX_POOL_SIZE));
        mReferenceQueue = new ReferenceQueue<>();
        mCache = new LruCache<String, Bitmap>(DEFAULT_CACHE_SIZE) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (evicted) {
                    // 表示释放空间被删除， 将key存储进磁盘中
                    saveDiskCache(key,oldValue);
                    oldValue.recycle();
                }else{
                    // 被移除或者新添加后被排出，将这一部分空间保存在复用池中
                    if(oldValue.isMutable()){
                        mMultiplePool.add(new WeakReference<Bitmap>(oldValue,mReferenceQueue));
                    }else{
                        oldValue.recycle();
                    }
                }
            }

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getAllocationByteCount() / 1024;
            }
        };
        // 开始引用队列轮询
        recycleReference();
    }

    /**
     * 初始化方法，初始化上下文，DiskLruCache
     * @param context 上下文
     */
    public void init(Context context) {
        assertInit();
        this.mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                throw new RuntimeException("can't grant storage permission so that DiskLruCache unused");
            }
        }

        File cacheDir = getCacheFile(context);
        try {
            this.mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1,
                    DEFAULT_DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(String key){
        return mCache.get(key) != null;
    }

    public void saveCache(String key,Bitmap bitmap){
        if(!contains(key)){
            mCache.put(key, bitmap);
        }
    }

    public Bitmap loadCache(String key){
        if(contains(key)){
            return mCache.get(key);
        }
        assertInit();
        Bitmap bitmap = loadDiskCache(key);
        if(bitmap != null){
            mCache.put(key,bitmap);
        }
        return bitmap;
    }

    public void shutdown(){
        mIsShutdownCycle = true;
        mCache.evictAll();
        try {
            mDiskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveDiskCache(String key, Bitmap bitmap) {
        if (mDiskLruCache != null) {
            try {
                DiskLruCache.Editor edit = mDiskLruCache.edit(key);
                OutputStream outputStream = edit.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                edit.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap loadDiskCache(String key){
        if(mDiskLruCache != null){
            try {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                if(snapshot != null){
                    InputStream inputStream = snapshot.getInputStream(0);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private File getCacheFile(Context context) {
        String cacheDir = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();
        }else{
            cacheDir = context.getCacheDir().getAbsolutePath();
        }
        File file = new File(cacheDir + File.separator + "images");
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    private void assertInit() {
        assert mContext != null : "ImageCache#init method context is null";
    }

    private void recycleReference(){
        if(mReferenceQueue != null){
            new Thread(){
                @Override
                public void run() {
                    while (!mIsShutdownCycle){
                        Reference<? extends Bitmap> poll = mReferenceQueue.poll();
                        if(poll.get() != null && !poll.get().isRecycled()){
                            try {
                                poll.get().recycle();
                                mReferenceQueue.remove();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 生成一个bitmap图片的key值
     *
     * @param bitmap bitmap图片
     * @return key值
     */
    public static String getMemCacheKey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        String config = bitmap.getConfig().name();
        int memSize = bitmap.getRowBytes();
        return MessageUtil.md5Crypt(width + "," + height + "," + config + "," + memSize);
    }

}
