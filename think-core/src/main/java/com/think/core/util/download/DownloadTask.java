package com.think.core.util.download;

import com.think.core.util.IoUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable {

    private static final int DOWNLOAD_BUFFER_SIZE = 4096;

    private final DownloadEntry mDownloadEntry;
    private final OkHttpClient mHttpClient;
    private final DownloadListener mDownloadListener;
    private final DownloadDaoImpl mDownloadDao;
    private final CompleteCallback mCompleteCallback;

    private boolean isCancel;

    private boolean isPause;

    public DownloadTask(OkHttpClient httpClient,
                        DownloadEntry entry,
                        DownloadDaoImpl downloadDao,
                        DownloadListener downloadListener,
                        CompleteCallback completeCallback) {
        this.mDownloadEntry = entry;
        this.mHttpClient = httpClient;
        this.mDownloadDao = downloadDao;
        this.mDownloadListener = downloadListener;
        this.mCompleteCallback = completeCallback;
    }

    public void cancel() {
        isCancel = true;
    }


    public void pause() {
        isPause = true;
    }

    @Override
    public void run() {
        // 如果本地没有这个任务那么就添加一个新的任务
        if (!checkTaskExists()) {
            addDownloadInfo();
        }

        // 判断任务的状态
        if (!checkDownloadTaskStatus()) {
            mCompleteCallback.onComplete(mDownloadEntry);
            return;
        }

        // 走到这表明是一个新任务或者一个进行中的任务
        Request request = new Request.Builder()
                .url(mDownloadEntry.url)
                .addHeader("Range", "bytes=" + mDownloadEntry.currentSize + "-")
                .addHeader("Referer", mDownloadEntry.url)
                // 不缓存下载数据
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        boolean isUnPause = mDownloadEntry.status != DownloadStatus.DOWNLOAD_PAUSE;
        BufferedInputStream bufferedInputStream = null;
        RandomAccessFile randomAccessFile = null;
        onStart();
        try {
            Response response = mHttpClient.newCall(request).execute();
            File file = new File(mDownloadEntry.path, mDownloadEntry.fileName);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            String fileDesc = response.headers().get("Content-Disposition");
            if (!response.isSuccessful()) {
                updateStatus(DownloadStatus.DOWNLOAD_ERROR);
                if (mDownloadListener != null) {
                    mDownloadListener.onError(mDownloadEntry, new Exception("response unsuccessful code = " + response.code()));
                }
                return;
            }
            // 获取文件名，默认为unknown
            if (mDownloadEntry.fileName == null) {
                mDownloadEntry.fileName = splitFileName(fileDesc);
            }
            // 获取数据大小
            long responseLength = -1;
            try {
                // 可能包含的是-1或者根本没有这个响应头
                responseLength = Long.parseLong(response.headers().get("Content-Length"));
            } catch (Exception e) { /*TODO IGNORE*/}
            // 如果没有这个响应头，则判断transferEncoding
            if (responseLength <= 0) {
                String transferEncoding = response.headers().get("Transfer-Encoding");
                if (transferEncoding == null) {
                    responseLength = response.body().contentLength();
                }
            }
            // 文件存在大小说明有下载过了
            if (file.length() == responseLength) {
                mCompleteCallback.onComplete(mDownloadEntry);
                if (mDownloadListener != null) {
                    mDownloadEntry.status = DownloadStatus.DOWNLOAD_COMPLETED;
                    mDownloadEntry.currentSize = responseLength;
                    updateDownloadStore();
                    mDownloadListener.onComplete(mDownloadEntry);
                }
                return;
            }

            // 文件指针移动到指定位置，新的下载currentSize = 0
            randomAccessFile.seek(mDownloadEntry.currentSize);
            // 判断是否处于暂停,非暂停的状态都从新下载
            if (isUnPause) {
                mDownloadEntry.totalSize = responseLength;
                mDownloadEntry.currentSize = 0;
            }
            // 继续下载
            mDownloadEntry.status = DownloadStatus.DOWNLOAD_STARTING;
            // 插入数据
            updateDownloadStore();
            // 打开流下载
            InputStream inputStream = response.body().byteStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1 && !(isPause || isCancel)) {
                randomAccessFile.write(buffer, 0, len);
                mDownloadEntry.currentSize += len;
                updateDownloadStore();
                if (mDownloadListener != null) {
                    mDownloadListener.onUpdate(mDownloadEntry);
                }
            }
            // 任务是否被取消
            if (isCancel) {
                updateStatus(DownloadStatus.DOWNLOAD_CANCEL);
                mCompleteCallback.onComplete(mDownloadEntry);
                if (mDownloadListener != null) {
                    mDownloadListener.onStop(mDownloadEntry);
                }
                return;
            }
            // 任务是否暂停
            if (isPause) {
                updateStatus(DownloadStatus.DOWNLOAD_PAUSE);
                mCompleteCallback.onComplete(mDownloadEntry);
                if (mDownloadListener != null) {
                    mDownloadListener.onPause(mDownloadEntry);
                }
                return;
            }

            updateStatus(DownloadStatus.DOWNLOAD_COMPLETED);
            mCompleteCallback.onComplete(mDownloadEntry);
            if (mDownloadListener != null) {
                mDownloadListener.onComplete(mDownloadEntry);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (mDownloadListener != null) {
                mDownloadEntry.status = DownloadStatus.DOWNLOAD_ERROR;
                // 通知调用者
                mDownloadListener.onError(mDownloadEntry, e);
            }
        } finally {
            IoUtils.close(bufferedInputStream, randomAccessFile);
            // 调用存储
            updateDownloadStore();
        }
    }

    private void onStart() {
        if (mDownloadListener != null) {
            updateStatus(DownloadStatus.DOWNLOAD_READY);
            mDownloadListener.onStart(mDownloadEntry);
        }
    }

    /**
     * 任务是否存在
     *
     * @return true 存在，false 不存在
     */
    private boolean checkTaskExists() {
        if (mDownloadEntry.did == 0) {
            return false;
        }
        DownloadEntry entry = mDownloadDao.queryDownloadUnCompleteTask(mDownloadEntry.did);
        return entry != null;
    }

    private void addDownloadInfo() {
        mDownloadEntry.did = mDownloadDao.insertRetPrimary(mDownloadEntry);
    }

    private boolean checkDownloadTaskStatus() {
        // 如果没有任务那么addDownloadInfo后会获得did，如果存在任务did必定不为0
        DownloadEntry entry = mDownloadDao.queryDownloadUnCompleteTask(mDownloadEntry.did);
        // 将存储的任务数据和内存任务数据同步，以sqlite数据为准
        syncEntry(entry);
        boolean result = mDownloadEntry.status == DownloadStatus.DOWNLOAD_COMPLETED
                || mDownloadEntry.status != DownloadStatus.DOWNLOAD_CANCEL;

        if (mDownloadListener != null) {
            switch (mDownloadEntry.status) {
                case DownloadStatus.DOWNLOAD_CANCEL:
                    mDownloadListener.onStop(mDownloadEntry);
                    break;
                case DownloadStatus.DOWNLOAD_COMPLETED:
                    mDownloadListener.onComplete(mDownloadEntry);
                    break;
            }
        }
        return result;
    }

    private void syncEntry(DownloadEntry entry) {
        if (mDownloadEntry.did == entry.did) {
            mDownloadEntry.currentSize = entry.currentSize;
            mDownloadEntry.totalSize = entry.totalSize;
            mDownloadEntry.status = entry.status;
            mDownloadEntry.threadNum = entry.threadNum;
            mDownloadEntry.url = entry.url;
            mDownloadEntry.path = entry.path;
            mDownloadEntry.fileName = entry.fileName;
        }
    }

    private void updateStatus(int status) {
        mDownloadEntry.status = status;
        mDownloadDao.update(mDownloadEntry.did, status);
    }

    private void updateDownloadStore() {
        mDownloadDao.update(mDownloadEntry);
    }

    static String splitFileName(String contentDisposition) {
        String[] split = contentDisposition.split(";");
        for (String s : split) {
            if (s.startsWith("filename=")) {
                return s.split("=")[1];
            }
        }
        return "unknown";
    }
}