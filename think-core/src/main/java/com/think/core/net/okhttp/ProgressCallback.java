package com.think.core.net.okhttp;

@FunctionalInterface
public interface ProgressCallback{
    void progress(long currentSize, long maxSize);
}