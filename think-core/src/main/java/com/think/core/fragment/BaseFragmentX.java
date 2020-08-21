package com.think.core.fragment;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;

public abstract class BaseFragmentX extends Fragment implements IFragment {

    private String mTagName;

    private WeakReference<FragmentActivity> mActivityRef;


    public BaseFragmentX(FragmentActivity activity){
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
            FragmentManager fm = mActivityRef.get().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(this,mTagName);
        }
    }

    @Override
    public void hide() {
        if(mActivityRef.get() != null){
            FragmentManager fm = mActivityRef.get().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(this);
        }
    }

    @Override
    public void dispose() {

    }
}
