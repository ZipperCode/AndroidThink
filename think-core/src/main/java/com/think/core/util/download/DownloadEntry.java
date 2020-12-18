package com.think.core.util.download;

import java.util.List;
import java.util.Objects;

public class DownloadEntry{
    /**
     * 下载id 使用uuid
     */
    public int did;
    /**
     * 文件总大小
     */
    public long totalSize;
    /**
     * 当前下载大小
     */
    public long currentSize;
    /**
     * 下载url
     */
    public String url;
    /**
     * 文件名
     */
    public String fileName;
    /**
     * 0：未下载
     * 1：下载中
     * 2：完成
     * 3：下载失败
     */
    public int status;
    /**
     * 存储路径
     */
    public String path;
    /**
     * 是否多线程下载
     */
    public int multiThread;
    /**
     * 线程数
     */
    public int threadNum;
    /**
     * 线程下载数
     */
    private List<SubDownloadEntry> subDownloadEntries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadEntry entry = (DownloadEntry) o;
        return did == entry.did &&
                totalSize == entry.totalSize &&
                currentSize == entry.currentSize &&
                status == entry.status &&
                multiThread == entry.multiThread &&
                threadNum == entry.threadNum &&
                url.equals(entry.url) &&
                Objects.equals(fileName, entry.fileName) &&
                Objects.equals(path, entry.path) &&
                Objects.equals(subDownloadEntries, entry.subDownloadEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(did, totalSize, currentSize, url, fileName, status, path, multiThread, threadNum, subDownloadEntries);
    }
}
