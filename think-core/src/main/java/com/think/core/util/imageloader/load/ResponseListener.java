package com.think.core.util.imageloader.load;

import com.think.core.util.imageloader.Value;

public interface ResponseListener {
    void onSuccess(Value value);

    void onFailure(Exception e);
}
