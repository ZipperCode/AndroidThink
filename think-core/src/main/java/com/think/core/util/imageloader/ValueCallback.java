package com.think.core.util.imageloader;

/**
 * value使用的回调监听
 */
public interface ValueCallback {

    /**
     * 当value不再使用的时候回调此方法
     * @param key   key值
     * @param value value
     */
    void unUseListener(Key key, Value value);
}
