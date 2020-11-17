package com.think.demo;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
