package com.think.core.mvp.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

public class BasePresenter <V extends IView> implements LifecycleObserver {

    private WeakReference<V> mViewReference;

    public void attachView(V view) {
        this.mViewReference = new WeakReference<>(view);
        view.getLifecycle().addObserver(this);
    }

    public V getView() {
        return mViewReference == null ? null : mViewReference.get();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        V view = getView();
        if (view != null) {
            view.getLifecycle().removeObserver(this);
        }
        if (this.mViewReference != null){
            this.mViewReference.clear();
            this.mViewReference = null;
        }
    }
}
