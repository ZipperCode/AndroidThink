package com.think.xposed.crypto;

import android.util.LruCache;

import javax.crypto.Cipher;

public class SymmetricCallCache extends LruCache<Cipher,SymmetricBean> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public SymmetricCallCache(int maxSize) {
        super(maxSize);
    }
}
