package com.think.core.util.imageloader.cache;

import android.text.TextUtils;

import com.think.core.util.imageloader.Key;
import com.think.core.util.imageloader.Value;
import com.think.core.util.imageloader.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存，直接引用的缓存
 */
public class ActiveCache {

    private final Map<Key, ValueWeakReference> mapList = new HashMap<>();

    private ReferenceQueue<Value> referenceQueue;

    private boolean isClosedThread;

    private Thread thread;

    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback){
        this.valueCallback = valueCallback;
    }

    public void put(Key key, Value value){
        if(key == null){
            throw new IllegalArgumentException("key 不能为空");
        }
        value.setmValueCallback(valueCallback);
        value.useAction();
        mapList.put(key, new ValueWeakReference(value, getQueue(), key));
    }

    public Value getValue(Key key){
        if(key == null){
            return null;
        }
        ValueWeakReference valueWeakReference = mapList.get(key);
        if(valueWeakReference != null){
            Value value = valueWeakReference.get();
            return value;
        }
        return null;
    }

    /**
     * 手动移除方法
     * @param key key
     * @return 移除的元素
     */
    public Value remove(String key){
        ValueWeakReference valueWeakReference = mapList.remove(new Key(key));
        if(valueWeakReference != null){
            Value value = valueWeakReference.get();
            return value;
        }
        return null;
    }

    public Value remove(Key key){
        ValueWeakReference valueWeakReference = mapList.remove(key);
        if(valueWeakReference != null){
            Value value = valueWeakReference.get();
            value.unUseAction();
            return value;
        }
        return null;
    }

    public void stopThread(){
        isClosedThread = true;
        if(thread != null){
            thread.interrupt();
            try {
                thread.join(TimeUnit.SECONDS.toMillis(5));
                if(thread.isAlive()){
                    throw new IllegalStateException("线程无法停止");
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public ReferenceQueue<Value> getQueue(){
        if(referenceQueue == null){
            referenceQueue = new ReferenceQueue<>();

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isClosedThread){
                        try {
                            // 当有元素类移除的时候会返回移除的元素，否则阻塞
                            ValueWeakReference remove = (ValueWeakReference) referenceQueue.remove();
                            // 判断容器存储的对象，并移除
                            if(!mapList.isEmpty() && mapList.containsKey(remove.key)){
                                mapList.remove(remove.key);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            thread.start();
        }
        return referenceQueue;
    }

    public static class ValueWeakReference extends WeakReference<Value>{
        private Key key;

        public ValueWeakReference(Value referent, ReferenceQueue<? super Value> q,Key key) {
            super(referent, q);
            this.key = key;
        }
    }



}
