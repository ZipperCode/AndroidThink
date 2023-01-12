package com.zipper.think.rx;

import com.zipper.think.rx.cache.BaseCacheSource;
import com.zipper.think.rx.cache.CacheStrategy;
import com.zipper.think.rx.cache.CacheUtils;
import com.zipper.think.rx.cache.ICacheStrategy;
import com.zipper.think.rx.cache.ILocalCacheSource;
import com.zipper.think.rx.cache.Transformer;
import com.zipper.think.rx.local.IDataSource;
import com.zipper.think.rx.remote.IRemoteSource;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BaseRepository<Remote extends IRemoteSource, Local extends ILocalCacheSource> {

    protected Remote remote;
    protected Local local;

    public BaseRepository(Remote remote, Local local) {
        this.remote = remote;
        this.local = local;
    }

    public <T> Observable<T> subscribe(Observable<T> network, ICacheStrategy cacheStrategy, String cacheKey, Type type) {
        return network
                .subscribeOn(Schedulers.io())
                .compose(Transformer.transformer(cacheStrategy, local, cacheKey, type));
    }
}
