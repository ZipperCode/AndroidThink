package com.zipper.think.rx.cache;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.functions.Function;

public class Transformer {

    public static <T> ObservableTransformer<T, T> transformer(
            ICacheStrategy cacheStrategy,
            ILocalCacheSource cacheSource,
            String cacheKey, Type type) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return cacheStrategy.execute(cacheSource, cacheKey, upstream, type)
                        .map(new Function<CacheResult<T>, T>() {
                            @Override
                            public T apply(CacheResult<T> tCacheResult) throws Exception {
                                return tCacheResult.data;
                            }
                        });
            }
        };
    }
}
