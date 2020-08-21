package com.think.core.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment implements IFragment {

    private String mTagName;

    private WeakReference<Activity> mActivityRef;


    public BaseFragment(Activity activity){
        this.mActivityRef = new WeakReference<>(activity);
    }


    @Override
    public void setTagName(String tag) {
        this.mTagName = tag;
    }

    @Override
    public String getTagName() {
        return mTagName;
    }

    @Override
    public void show() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(this,mTagName);
        }
    }

    @Override
    public void hide() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(this);
        }
    }

    @Override
    public void dispose() {

    }
}
