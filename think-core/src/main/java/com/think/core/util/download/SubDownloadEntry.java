package com.think.core.util.download;

import java.util.Objects;

public class SubDownloadEntry {
    /**
     * 线程下载的切片id
     */
    public int sdId;
    /**
     * 下载任务的id
     */
    public int did;

    /**
     * 切片起始位置
     */
    public int sliceStartPosition;
    /**
     * 切片总大小
     */
    public int sliceEndPosition;

    /**
     * 切片下载的大小
     */
    public int sliceDownloadSize;

    /**
     * 下载状态
     */
    public int status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubDownloadEntry that = (SubDownloadEntry) o;
        return sdId == that.sdId &&
                did == that.did &&
                sliceStartPosition == that.sliceStartPosition &&
                sliceEndPosition == that.sliceEndPosition &&
                sliceDownloadSize == that.sliceDownloadSize &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sdId, did, sliceStartPosition, sliceEndPosition, sliceDownloadSize, status);
    }
}
