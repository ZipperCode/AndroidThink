package com.zipper.think.rx.cache;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;

/**
 * 缓存源接口
 * @author zhangzhipeng
 * @date 2022/12/22
 */
public interface ILocalCacheSource {

    @NonNull
    <T> Observable<CacheResult<T>> load(String cacheKey, Type type);

    <T> Observable<Boolean> save(String cacheKey, T data);
}
