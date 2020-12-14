package com.think.core.mvp.sample;

import com.think.core.mvp.BasePresenter;

public class MainMvpPresenter extends BasePresenter<MainMvpActivity,MainMvpModel> {
    @Override
    protected MainMvpModel createModel() {
        return new MainMvpModel();
    }
}
