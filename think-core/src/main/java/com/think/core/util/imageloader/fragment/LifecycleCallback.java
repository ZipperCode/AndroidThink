package com.think.core.util.imageloader.fragment;

public interface LifecycleCallback {
    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
