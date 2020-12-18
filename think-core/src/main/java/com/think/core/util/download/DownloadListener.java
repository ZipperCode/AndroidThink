package com.think.core.util.download;

public interface DownloadListener {
    void onStart(DownloadEntry downloadEntry);
    void onUpdate(DownloadEntry downloadEntry);
    void onPause(DownloadEntry downloadEntry);
    void onStop(DownloadEntry downloadEntry);
    void onComplete(DownloadEntry downloadEntry);
    void onError(DownloadEntry downloadEntry,Exception e);
}
