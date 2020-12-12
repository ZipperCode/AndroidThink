package com.think.core.util.imageloader.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class ActivityFragmentManager extends Fragment {

    private LifecycleCallback lifecycleCallback;

    public ActivityFragmentManager() {
    }

    @SuppressLint("ValidFragment")
    public ActivityFragmentManager(LifecycleCallback lifecycleCallback){
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(lifecycleCallback != null){
            lifecycleCallback.onCreate();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(lifecycleCallback != null){
            lifecycleCallback.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lifecycleCallback != null){
            lifecycleCallback.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(lifecycleCallback != null){
            lifecycleCallback.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lifecycleCallback != null){
            lifecycleCallback.onDestroy();
        }
    }
}
