package com.think.core.mvp.sample;

import android.util.Log;

import com.think.core.mvp.IModel;

import java.util.concurrent.TimeUnit;

public class MainMvpModel implements IModel {

    private static final String TAG = MainMvpModel.class.getSimpleName();

    public void runTask(){
        try {
            TimeUnit.SECONDS.sleep(5);
            Log.i(TAG,"耗时任务执行结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
