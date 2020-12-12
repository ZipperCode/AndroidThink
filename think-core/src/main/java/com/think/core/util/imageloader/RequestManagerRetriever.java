package com.think.core.util.imageloader;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class RequestManagerRetriever {
    public RequestManager get(Context context){
        return new RequestManager(context);
    }
    public RequestManager get(Activity activity){
        return new RequestManager(activity);
    }
    public RequestManager get(FragmentActivity activity){
        return new RequestManager(activity);
    }
}
