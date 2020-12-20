package com.think.core.net.myokhttp;

import com.think.core.net.myokhttp.body.Response;

public interface Callback {
    void onSuccess(Call call, Response response);

    void onFailure(Call call, Exception e);
}
