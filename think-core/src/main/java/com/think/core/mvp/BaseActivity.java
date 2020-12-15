package com.think.core.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

public abstract class BaseActivity<
        M extends IModel,
        P extends BasePresenter<M,IView>
        > extends Activity implements IView{

    private P presenter;

    protected abstract P createPresenter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attach(this);
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
