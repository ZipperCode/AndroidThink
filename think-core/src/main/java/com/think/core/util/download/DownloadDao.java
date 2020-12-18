package com.think.core.util.download;

import java.util.List;

public interface DownloadDao {
    void insert(DownloadEntry downloadEntry);

    int insertRetPrimary(DownloadEntry downloadEntry);

    void update(DownloadEntry downloadEntry);

    void update(int did, int status);


    void delete(DownloadEntry downloadEntry);

    void delete(int did);

    DownloadEntry queryDownloadTask(DownloadEntry downloadEntry);

    List<DownloadEntry> queryDownloadTask();

    DownloadEntry queryDownloadUnCompleteTask(int did);

}
