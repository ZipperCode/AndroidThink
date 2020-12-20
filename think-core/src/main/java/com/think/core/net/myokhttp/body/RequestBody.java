package com.think.core.net.myokhttp.body;



import com.think.core.net.myokhttp.ContentType;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RequestBody {

    public abstract ContentType contentType();

    public long contentLength(){
        return -1L;
    }

    public abstract void writeTo(OutputStream outputStream) throws IOException;
}
