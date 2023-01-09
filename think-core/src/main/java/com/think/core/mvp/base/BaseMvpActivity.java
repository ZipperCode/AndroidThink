package com.think.core.mvp.base;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMvpActivity<P extends BasePresenter<IView>> extends AppCompatActivity implements IView{

    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();
        presenter.attachView(this);
    }

    protected abstract P getPresenter();

}
