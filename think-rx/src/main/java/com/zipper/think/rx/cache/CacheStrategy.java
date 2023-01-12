package com.zipper.think.rx.cache;

import androidx.annotation.Keep;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

@Keep
public enum CacheStrategy implements ICacheStrategy{

    /**
     * 先显示缓存, 后请求网络
     * 总共会回调两次，本地 -> 网络
     */
    CacheAndRemoteStrategy() {
        @Override
        public <T> Observable<CacheResult<T>> execute(ILocalCacheSource cacheSource, String cacheKey, Observable<T> networkSource, Type type) {
            Observable<CacheResult<T>> cache = cacheSource.load(cacheKey, type);
            Observable<CacheResult<T>> remote = CacheUtils.loadRemoteWithCache(cacheKey, cacheSource, networkSource);
            if (!cache.isEmpty().blockingGet()) {
                // 存在缓存的时候，如果网络请求出错了，则使用本地缓存
                remote = remote.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                    @Override
                    public ObservableSource<? extends CacheResult<T>> apply(Throwable throwable) throws Exception {
                        return Observable.empty();
                    }
                });
            }
            return Observable.merge(remote, cache.takeUntil(remote));
        }
    };
}
