package com.think.core.util.imageloader.cache;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.think.core.util.imageloader.Key;
import com.think.core.util.imageloader.Value;

/**
 * 内存缓存（二级缓存）
 */
public class MemoryCache extends LruCache<Key, Value> {

    private MemoryCacheCallback memoryCacheCallback;

    private boolean useCustomRemove;

    public MemoryCacheCallback getMemoryCacheCallback() {
        return memoryCacheCallback;
    }

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize, MemoryCacheCallback memoryCacheCallback) {
        super(maxSize);
        this.memoryCacheCallback = memoryCacheCallback;
    }

    @Override
    protected int sizeOf(@NonNull Key key, @NonNull Value value) {
        Bitmap bitmap = value.getmBitmap();
        // 最低版本为19 所以直接用此方法不用getByteCount方法
        int size = bitmap.getAllocationByteCount();
        return size;
    }

    /**
     * 元素被移除的监听
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull Key key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (memoryCacheCallback != null && !useCustomRemove) {
            memoryCacheCallback.entryRemove(key, oldValue);
        }
    }

    public Value customRemove(Key key) {
        useCustomRemove = true;
        Value value = remove(key);
        useCustomRemove = false;
        return value;
    }

    public Value customRemove(String key) {
        useCustomRemove = true;
        Value value = remove(new Key(key));
        useCustomRemove = false;
        return value;
    }
}
