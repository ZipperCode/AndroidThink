package com.think.core.net.myokhttp.interceptor;

import com.think.core.net.myokhttp.Call;
import com.think.core.net.myokhttp.HttpConnection;
import com.think.core.net.myokhttp.body.Request;
import com.think.core.net.myokhttp.body.Response;

import java.io.IOException;

public interface Interceptor {

    Response intercept(Chain chain) throws Exception;


    public interface Chain {
        Request request();

        Response proceed(Request request) throws Exception;

        HttpConnection connection();

        Call call();

        int connectTimeoutMillis();

        int readTimeoutMillis();
    }
}
