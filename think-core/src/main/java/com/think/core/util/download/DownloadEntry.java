package com.think.core.util.download;

import java.util.List;

public class DownloadEntry{
    /**
     * 下载id
     */
    public String did;
    /**
     * 文件总大小
     */
    public String totalSize;
    /**
     * 当前下载大小
     */
    public String currentSize;
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
    public boolean multiThread;
    /**
     * 线程数
     */
    public int threadNum;
    /**
     * 线程下载数
     */
    private List<SubDownloadEntry> subDownloadEntries;
}
