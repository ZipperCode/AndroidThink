package com.zipper.think.rx.cache;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;

public interface ICacheStrategy {

    /**
     * 缓存策略抽象
     * 缓存源由外部提供，可自定义实现缓存的位置
     * @param cacheSource       缓存源
     * @param cacheKey          key
     * @param networkSource     源请求
     * @param type              类型
     */
    abstract <T> Observable<CacheResult<T>> execute(ILocalCacheSource cacheSource, String cacheKey, Observable<T> networkSource, Type type);

}
