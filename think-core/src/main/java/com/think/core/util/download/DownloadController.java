package com.think.core.util.download;

import android.content.Context;

import com.think.core.util.ThreadManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadController {

    private static final String TAG = DownloadController.class.getSimpleName();

    private final ExecutorService mDownloadExecutorService;

    private final OkHttpClient mHttpClient;

    private final Context mContext;

    public DownloadController(Context mContext) {
        this.mContext = mContext;
        this.mHttpClient = new OkHttpClient.Builder().build();
        this.mDownloadExecutorService = ThreadManager.getInstance().getScheduled();
    }


    public static class DownloadTask implements Runnable{

        private final DownloadEntry mDownloadEntry;
        private final OkHttpClient mHttpClient;
        private final DownloadListener mDownloadListener;

        public DownloadTask(OkHttpClient httpClient, DownloadEntry entry, DownloadListener downloadListener){
            this.mDownloadEntry = entry;
            this.mHttpClient = httpClient;
            this.mDownloadListener = downloadListener;
        }

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url(mDownloadEntry.url)
                    .addHeader("Range","bytes=" + mDownloadEntry.currentSize + "-")
                    .addHeader("Referer",mDownloadEntry.url)
                    .build();
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                Response response = mHttpClient.newCall(request).execute();
                File file = new File(mDownloadEntry.path , mDownloadEntry.fileName);
                if(!file.exists()){
                    File parentFile = file.getParentFile();
                    if(!parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file,true);
                String fileDesc = response.headers().get("Content-Disposition");
                int responseLength = Integer.parseInt(response.headers().get("Content-Length"));
                if(file.length() == responseLength){
                    if(mDownloadListener != null){
                        mDownloadListener.
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
