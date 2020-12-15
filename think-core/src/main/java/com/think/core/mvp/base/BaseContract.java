package com.think.core.mvp.base;

public interface BaseContract {
    interface IView{
        
    }

    interface IModel{
    }

    interface IPresenter<M extends IModel,V extends IView>{
        void registerModel(M model);
        void registerView(V view);
        void onDestroy();
    }
}
