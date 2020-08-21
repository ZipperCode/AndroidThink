package com.think.core.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMvpActivity<
        M extends BaseContract.IModel,
        P extends BaseContract.IPresenter>
        extends AppCompatActivity implements BaseMVP<M, BaseContract.IView, P>, BaseContract.IView {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(mPresenter != null){
            mPresenter.registerModel(createModel());
            mPresenter.registerView(createView());
        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null){
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }
}
