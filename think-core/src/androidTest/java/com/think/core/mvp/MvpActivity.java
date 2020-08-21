package com.think.core.mvp;

import android.app.Activity;

import com.think.core.mvp.base.BaseContract;
import com.think.core.mvp.base.BaseMvpActivity;

public class MvpActivity extends BaseMvpActivity<MvpModel, MvpPresenter> {



    @Override
    public MvpModel createModel() {
        return new MvpModel();
    }

    @Override
    public BaseContract.IView createView() {
        return this;
    }

    @Override
    public MvpPresenter createPresenter() {
        return new MvpPresenter();
    }
}
