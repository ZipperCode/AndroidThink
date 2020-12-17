package com.think.core.util.download;

public interface DownloadListener {
    void onStart(String fileName,int fileSize);
    void onPause();
    void onStop();
}
