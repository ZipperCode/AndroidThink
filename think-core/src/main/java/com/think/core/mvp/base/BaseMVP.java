package com.think.core.mvp.base;

public interface BaseMVP<
        M extends BaseContract.IModel,
        V extends BaseContract.IView,
        P extends BaseContract.IPresenter<M,V>> {
    V createView();
    P createPresenter();
}