package com.zipper.think.rx.cache;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CacheUtils {

    public static <T> Observable<CacheResult<T>> loadRemoteWithCache(
            String cacheKey,
            ILocalCacheSource localCacheSource,
            Observable<T> networkSource
    ) {
        return networkSource
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<T, ObservableSource<? extends CacheResult<T>>>() {
                    @Override
                    public ObservableSource<? extends CacheResult<T>> apply(T t) throws Throwable {
                        return localCacheSource.save(cacheKey, t)
                                .map(aBoolean -> new CacheResult<>(false, t))
                                .onErrorReturn(throwable -> new CacheResult<>(false, t));
                    }
                });

    }
}
