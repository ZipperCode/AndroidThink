package com.think.core.util.imageloader;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * bitmap的封装
 */
public class Value {

    static final String TAG = Value.class.getSimpleName();

    private Bitmap mBitmap;
    /**
     * 计数器，使用一次+1
     */
    private int count;
    /**
     * Key
     */
    private Key key;

    private ValueCallback mValueCallback;

    private volatile static Value instace;

    private Value(){}

    public static Value getInstance(){
        synchronized (Value.class){
            if(instace == null){
                synchronized (Value.class){
                    instace = new Value();
                }
            }
        }
        return instace;
    }

    public void useAction(){
        if(mBitmap == null || mBitmap.isRecycled()){
            count = 0;
            return;
        }

        count++;
    }

    public void unUseAction(){
        if(count-- <= 0 && mValueCallback != null){
            // 回调给外界，不再使用了
            mValueCallback.unUseListener(key,this);
        }
    }

    /**
     * 回收bitmap
     */
    public void recycleBitmap(){
        if(count > 0){
            Log.e(TAG,"bitmap计数值大于0，正在使用无法被回收");
            return;
        }
        if(mBitmap.isRecycled()){
            Log.e(TAG,"bitmap已经被回收了");
            return;
        }

        mBitmap.recycle();
        instace = null;
        System.gc();
    }


    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public ValueCallback getmValueCallback() {
        return mValueCallback;
    }

    public void setmValueCallback(ValueCallback mValueCallback) {
        this.mValueCallback = mValueCallback;
    }
}
