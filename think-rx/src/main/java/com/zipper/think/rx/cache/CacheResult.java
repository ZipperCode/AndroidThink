package com.zipper.think.rx.cache;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class CacheResult<T> implements Serializable {

    public final boolean isCache;

    public final T data;

    public CacheResult(boolean isCache, T data) {
        this.isCache = isCache;
        this.data = data;
    }
}
