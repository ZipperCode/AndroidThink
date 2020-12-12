package com.think.core.util.imageloader;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class Glide {

    RequestManagerRetriever retriever;

    private Glide(RequestManagerRetriever retriever){
        this.retriever = retriever;
    }

    public static RequestManager with(Context context){
        return getRetriever(context).get(context);
    }

    public static RequestManager with(Activity activity){
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(FragmentActivity activity){
        return getRetriever(activity).get(activity);
    }

    public static RequestManagerRetriever getRetriever(Context context){
        return Glide.get(context).retriever;
    }

    public static Glide get(Context context){
        return new GlideBuilder().build();
    }

    public static class GlideBuilder{
        public Glide build(){
            RequestManagerRetriever retriever = new RequestManagerRetriever();
            Glide glide = new Glide(retriever);
            return glide;
        }
    }
}


