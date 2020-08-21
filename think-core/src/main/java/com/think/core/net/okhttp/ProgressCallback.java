package com.think.core.net.okhttp;

@FunctionalInterface
public interface ProgressCallback{
    void uploadProgress(long progress);
}