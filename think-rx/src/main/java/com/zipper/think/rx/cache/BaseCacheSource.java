package com.zipper.think.rx.cache;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseCacheSource implements ILocalCacheSource{
    @NonNull
    @Override
    public <T> Observable<CacheResult<T>> load(String cacheKey, Type type) {
        return Observable.create(new ObservableOnSubscribe<CacheResult<T>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<CacheResult<T>> emitter) throws Throwable {
                // TODO 加载数据
            }
        });
    }

    @Override
    public <T> Observable<Boolean> save(String cacheKey, T data) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    // TODO 保存数据
                    try {
                        emitter.onNext(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
}
