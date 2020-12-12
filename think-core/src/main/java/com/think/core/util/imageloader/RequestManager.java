package com.think.core.util.imageloader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.think.core.util.imageloader.fragment.ActivityFragmentManager;
import com.think.core.util.imageloader.fragment.FragmentActivityFragmentManager;

public class RequestManager {

    static final String ACTIVITY = "ACTIVITY";
    static final String FRAGMENT_ACTIVITY = "FRAGMENT_ACTIVITY";

    private Context context;

    private RequestEngine requestEngine = new RequestEngine();


    public RequestManager(Context context) {
        this.context = context;
    }
    public RequestManager(Activity activity) {
        this.context = activity;
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(ACTIVITY);
        if(fragmentByTag == null){
            fragmentByTag = new ActivityFragmentManager(requestEngine);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragmentByTag,ACTIVITY);
            fragmentTransaction.commit();
        }

    }
    public RequestManager(FragmentActivity activity) {
        this.context = activity;
        androidx.fragment.app.FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        androidx.fragment.app.Fragment fragmentByTag = supportFragmentManager.findFragmentByTag(ACTIVITY);
        if(fragmentByTag == null){
            fragmentByTag = new FragmentActivityFragmentManager(requestEngine);
            androidx.fragment.app.FragmentTransaction fragmentTransaction =
                    supportFragmentManager.beginTransaction();
            fragmentTransaction.add(fragmentByTag,FRAGMENT_ACTIVITY);
            fragmentTransaction.commit();
        }
    }

    public RequestEngine load(String url){
        requestEngine.load(url,context);
        return requestEngine;
    }
}
