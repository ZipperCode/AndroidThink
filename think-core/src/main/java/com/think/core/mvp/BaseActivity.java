package com.think.core.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

public abstract class BaseActivity<M extends IModel,V extends IView,P extends BasePresenter<M,V>> extends Activity implements IView{

    protected P presenter;

    protected abstract P createPresenter();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        presenter = createPresenter();
        presenter.attach((V)this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
