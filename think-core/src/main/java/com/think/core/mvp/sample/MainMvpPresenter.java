package com.think.core.mvp.sample;

import com.think.core.mvp.BasePresenter;
import com.think.core.mvp.IView;

public class MainMvpPresenter extends BasePresenter<MainMvpModel,MainMvpView> {
    @Override
    protected MainMvpModel createModel() {
        return new MainMvpModel();
    }

    public void executeTask(){
        if(mModel != null){
            mModel.runTask();
        }
    }
}
