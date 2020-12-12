package com.think.core.util.imageloader;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.think.core.util.ThreadManager;
import com.think.core.util.imageloader.cache.ActiveCache;
import com.think.core.util.imageloader.cache.DiskCache;
import com.think.core.util.imageloader.cache.MemoryCache;
import com.think.core.util.imageloader.cache.MemoryCacheCallback;
import com.think.core.util.imageloader.fragment.LifecycleCallback;
import com.think.core.util.imageloader.load.LoadDataManager;
import com.think.core.util.imageloader.load.ResponseListener;

import java.io.File;

public class RequestEngine implements LifecycleCallback,
        ValueCallback,
        MemoryCacheCallback,
        ResponseListener {

    private ActiveCache activeCache;

    private MemoryCache memoryCache;

    private DiskCache diskCache;

    private String url;

    private Context context;

    private Key key;

    private Value value;

    private ImageView imageView;

    public RequestEngine() {
        activeCache = new ActiveCache(this);
        memoryCache = new MemoryCache(50, this);
        diskCache = new DiskCache(new File(DiskCache.DEFAULT_DIR));
    }

    public void load(String url, Context context) {
        this.url = url;
        this.context = context;
        this.key = new Key(url);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        activeCache.remove(key);
    }


    public void into(final ImageView imageView) {
        this.imageView = imageView;

        if (Looper.getMainLooper() != Looper.myLooper()) {
//            throw new IllegalThreadStateException("不是主线程");
            if (value != null) {
                ThreadManager.getInstance().execOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        final Value value = loadRes();
                        if(value != null){
                            imageView.setImageBitmap(value.getmBitmap());
                        }
                    }
                });
                return;
            }
        }
        final Value value = loadRes();
        if (value != null) {
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    private Value loadRes() {
        // 从一级缓存中查找
        Value value = activeCache.getValue(key);
        if (value != null) {
            value.useAction();
            return value;
        }
        // 从二级缓存中查找
        value = memoryCache.get(key);
        if (value != null) {
            // 资源被使用，移动到一级缓存中
            memoryCache.customRemove(key);
            activeCache.put(key, value);
            return value;
        }

        // 三级缓存
        value = diskCache.get(key.getKey());
        if (value != null) {
            activeCache.put(key, value);
            return value;
        }

        // 外部加载资源
        value = new LoadDataManager().loadResources(url, this, context);

        if (value != null) {
            return value;
        }
        return null;
    }


    @Override
    public void onSuccess(final Value value) {
        if (value != null) {
            ThreadManager.getInstance().execOnMainThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(value.getmBitmap());
                }
            });
            saveCache(value);
        }

    }

    public void saveCache(Value value) {
        activeCache.put(value.getKey(), value);
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void entryRemove(Key key, Value value) {
        diskCache.put(key.getKey(), value);
    }

    @Override
    public void unUseListener(Key key, Value value) {
        if (key != null && value != null) {
            activeCache.remove(key);
        }
    }
}
