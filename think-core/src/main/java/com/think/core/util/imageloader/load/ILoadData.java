package com.think.core.util.imageloader.load;

import android.content.Context;

import com.think.core.util.imageloader.Value;

public interface ILoadData {

    Value loadResources(String path, ResponseListener responseListener, Context context);
}
