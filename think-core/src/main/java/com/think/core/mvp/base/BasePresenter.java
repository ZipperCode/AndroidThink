package com.think.core.mvp.base;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M extends BaseContract.IModel, V extends BaseContract.IView>
        implements BaseContract.IPresenter<M,V> {
    private M model;
    private WeakReference<V> viewReference; //弱引用防止内存泄露

    @Override
    public void registerModel(M model) {
        this.model = model;
    }

    @Override
    public void registerView(V view) {
        viewReference = new WeakReference<V>(view);
    }

    public V getView(){
        return viewReference.get() == null? null:viewReference.get();
    }

    public M getModel(){
        return model;
    }
}