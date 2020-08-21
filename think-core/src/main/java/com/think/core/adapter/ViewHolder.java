package com.think.core.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {

    Context mContext;

    SparseArray<View> mViews;

    View mConvertView;

    int position;

    ViewHolder(Context context, int layoutId, int position,ViewGroup parent){
        mContext = context;
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
        this.position = position;
        mViews = new SparseArray<>();
    }


    public View getContentView(){
        return mConvertView;
    }

    public <T extends View> T getView(int resId){
        T view = (T) mViews.get(resId);
        if(view == null){
            view = mConvertView.findViewById(resId);
            mViews.put(resId,view);
        }
        return view;
    }


    public static ViewHolder bind(Context context, int layoutResId,int position, ViewGroup parent,View convertView){
        return  convertView == null ?new ViewHolder(context,layoutResId,position,parent) :(ViewHolder) convertView.getTag();
    }
}
