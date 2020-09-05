package com.think.core.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author : zzp
 * @date : 2020/8/31
 **/
public class IoUtils {

    public static void close(Closeable...closeables){
        if(closeables != null && closeables.length > 0){
            for (Closeable closeable: closeables){
                try {
                    if(closeable != null){
                        closeable.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
