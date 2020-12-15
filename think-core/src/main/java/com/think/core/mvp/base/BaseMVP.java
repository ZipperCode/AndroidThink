package com.think.core.mvp.base;

public interface BaseMVP<
        M extends BaseContract.IModel,
        V extends BaseContract.IView> {
    M createModel();
    V createView();
}