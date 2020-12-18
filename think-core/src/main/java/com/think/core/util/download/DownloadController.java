package com.think.core.util.download;

import android.content.Context;
import android.os.Environment;

import com.think.core.util.ThreadManager;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DownloadController implements CompleteCallback {

    private static final String TAG = DownloadController.class.getSimpleName();

    private final String mStorePath;

    private final ExecutorService mDownloadExecutorService;

    private final OkHttpClient mHttpClient;

    private final Context mContext;

    private final DownloadDaoImpl downloadDao;

    private final Map<DownloadEntry, DownloadTask> mDownloadTaskMap;

    private final List<DownloadEntry> mDownloadList;

    private int mMaxDownloadSize = 3;

    public DownloadController(Context mContext) {
        this.mContext = mContext;
        this.mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .callTimeout(5, TimeUnit.SECONDS)
                .sslSocketFactory(SSLHelper.getSSLSocketFactory(), new CustomX509TrustManager())
                .hostnameVerifier((s, session) -> true)
                .build();
        this.mStorePath = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        this.mDownloadExecutorService = ThreadManager.getInstance().getScheduled();
        downloadDao = new DownloadDaoImpl(DbHelper.getInstance(mContext));
        this.mDownloadTaskMap = new HashMap<>();
        this.mDownloadList = new ArrayList<>();
        this.mMaxDownloadSize = DownloadSPHelper.getInstance(mContext).getInt(DownloadSPHelper.DL_MAX_SIZE, 3);
        init();
    }

    private void init() {
        // 加载未完成的下载任务
        loadDownloadList();

    }

    /**
     * 加载所有下载任务
     */
    private void loadDownloadList() {
        mDownloadList.clear();
        List<DownloadEntry> downloadEntries = downloadDao.queryDownloadTask();
        mDownloadList.addAll(downloadEntries);
    }

    /**
     * 获取所有下载任务
     *
     * @return
     */
    public List<DownloadEntry> getDownloadList() {
        return mDownloadList;
    }

    /**
     * 设置最大下载数
     *
     * @param maxDownloadSize 最大下载数
     */
    public void setMaxDownloadSize(int maxDownloadSize) {
        this.mMaxDownloadSize = maxDownloadSize;
        DownloadSPHelper.getInstance().putInt(DownloadSPHelper.DL_MAX_SIZE, maxDownloadSize);
    }

    /**
     * 下载任务调度，新创建一个下载时使用，进行下载前，会将任务信息存储到sql中
     *
     * @param entry            下载实体类，必须包含url参数
     * @param downloadListener 下载监听回调，可以为空
     */
    public void newDownload(DownloadEntry entry, DownloadListener downloadListener) {
        if (entry.url == null || "".equals(entry.url)) {
            if (downloadListener != null) {
                downloadListener.onError(entry, new Exception("entry need a url param"));
            }
            return;
        }
        entry.path = mStorePath;
        entry.status = DownloadStatus.DOWNLOAD_READY;
        entry.multiThread = 0;  // 暂时未单线程，0-单线程，1-多线程
        entry.threadNum = 1;    // 线程数
        entry.did = downloadDao.insertRetPrimary(entry);
        this.mDownloadList.add(entry);
        DownloadTask downloadTask = new DownloadTask(mHttpClient, entry, downloadDao, downloadListener, this);
        mDownloadExecutorService.execute(downloadTask);
        // 将任务添加到管理队列
        mDownloadTaskMap.put(entry, downloadTask);
    }

    /**
     * 继续下载
     *
     * @param entry            下载实体类，必须包含did，path，fileName
     * @param downloadListener 下载监听器
     */
    public void continueDownload(DownloadEntry entry, DownloadListener downloadListener) {
        if (entry.url == null || "".equals(entry.url)
                || entry.did != 0 || entry.fileName != null
                || entry.path != null) {
            if (downloadListener != null) {
                downloadListener.onError(entry, new Exception("entry param error"));
            }
            return;
        }

        // 检查任务是否取消
        if (entry.status == DownloadStatus.DOWNLOAD_CANCEL) {
            downloadDao.delete(entry.did);
            if (downloadListener != null) {
                downloadListener.onError(entry, new Exception("task already canceled"));
            }
            return;
        }
        // 任务是否已经完成
        if (entry.status == DownloadStatus.DOWNLOAD_COMPLETED) {
            if (downloadListener != null) {
                downloadListener.onComplete(entry);
            }
            return;
        }
        this.mDownloadList.add(entry);
        DownloadTask downloadTask = new DownloadTask(mHttpClient, entry, downloadDao, downloadListener, this);
        mDownloadExecutorService.execute(downloadTask);
        // 将任务添加到管理队列
        mDownloadTaskMap.put(entry, downloadTask);
    }

    public void cancel(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTaskMap.remove(downloadEntry);
        if (downloadTask != null) {
            // 将任务中断
            downloadTask.cancel();
            // 删除这个任务
            downloadDao.delete(downloadEntry);
        }
    }

    public void pause(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTaskMap.remove(downloadEntry);
        if (downloadTask != null) {
            downloadTask.pause();
            downloadDao.update(downloadEntry.did, DownloadStatus.DOWNLOAD_PAUSE);
        }
    }

    @Override
    public synchronized void onComplete(DownloadEntry downloadEntry) {
        mDownloadTaskMap.remove(downloadEntry);
        mDownloadList.remove(downloadEntry);
    }

    @Deprecated
    public class DownloadDequeTask implements Runnable {

        private boolean isStop = false;

        private final int maxDownloadSize;

        private final Deque<DownloadEntry> runTasks;

        private int idle;

        public DownloadDequeTask(int maxDownloadSize, Deque<DownloadEntry> runTasks, int idle) {
            this.maxDownloadSize = maxDownloadSize;
            this.runTasks = runTasks;
            this.idle = idle;
        }

        public void setStop(boolean stop) {
            isStop = stop;
        }

        @Override
        public void run() {
            try {
                while (!isStop) {
                    while (runTasks.peek() != null) {
                        DownloadEntry pop = runTasks.pop();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
