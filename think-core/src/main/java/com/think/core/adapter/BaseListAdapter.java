package com.think.core.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected final Context mContext;

    protected final int mLayoutResId;

    protected final List<T> mDatas;

    public BaseListAdapter(Context context, int layoutResId, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutResId = layoutResId;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addData(List<T> newDatas){
        if(newDatas != null && newDatas.size() > 0){
            mDatas.clear();
            mDatas.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        if(mDatas != null && mDatas.size() > 0){
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        return bind(position,convertView,parent).getContentView();
    }

    protected abstract ViewHolder bind(int position, View convertView, ViewGroup parent);
}
