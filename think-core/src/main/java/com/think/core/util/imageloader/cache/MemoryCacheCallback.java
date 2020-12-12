package com.think.core.util.imageloader.cache;

import com.think.core.util.imageloader.Key;
import com.think.core.util.imageloader.Value;

public interface MemoryCacheCallback {

    /**
     * 内存缓存中元素被移除的方法回调
     * @param key
     * @param value
     */
    void entryRemove(Key key , Value value);
}
