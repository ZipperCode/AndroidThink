package com.zipper.think.rx.local;

import androidx.annotation.NonNull;

import com.zipper.think.rx.cache.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;

public interface IDataSource {

    @NonNull
    <T> Observable<CacheResult<T>> load(String cacheKey, Type type);

    <T> Observable<Boolean> save(String cacheKey, T data);
}
