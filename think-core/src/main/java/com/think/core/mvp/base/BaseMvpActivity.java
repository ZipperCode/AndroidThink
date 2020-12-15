package com.think.core.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMvpActivity<
        M extends BaseContract.IModel,
        V extends BaseContract.IView,
        P extends BaseContract.IPresenter<M,V>
        > extends AppCompatActivity
        implements BaseContract.IView {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(mPresenter != null){
            mPresenter.registerView(createView());
        }
    }

    protected abstract V createView();

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        if(mPresenter != null){
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }
}
