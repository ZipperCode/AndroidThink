package com.think.core.util.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.think.core.R;
import com.think.core.mvp.sample.MainMvpActivity;

public class DownloadService extends Service {

    public static final String TAG = Service.class.getSimpleName();
    public static final String ACTION_CREATE_DOWNLOAD_TASK = "action.download.create";
    public static final String ACTION_START_DOWNLOAD_TASK = "action.download.start";
    public static final String ACTION_START_ALL_DOWNLOAD_TASK = "action.download.start.all";
    public static final String ACTION_PAUSE_DOWNLOAD_TASK = "action.download.pause";
    public static final String ACTION_PAUSE_ALL_DOWNLOAD_TASK = "action.download.pause.all";
    public static final String ACTION_CANCEL_DOWNLOAD_TASK = "action.download.cancel";
    public static final String ACTION_CANCEL_ALL_DOWNLOAD_TASK = "action.download.cancel.all";

    public static final int NOTIFICATION_ID = 10;
    public static final String NOTIFICATION_CHANNEL_ID = "10";

    public DownloadController mDownloadController;

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDownloadController = new DownloadController(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand");
        String action = null;
        if(intent == null){
            mNotificationManager.cancel(NOTIFICATION_ID);
        }else{
            action = intent.getAction();
        }
        if(action == null){
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action){
            case ACTION_CREATE_DOWNLOAD_TASK:

                break;
            case ACTION_START_DOWNLOAD_TASK:
                break;
            case ACTION_START_ALL_DOWNLOAD_TASK:
                break;
            case ACTION_PAUSE_DOWNLOAD_TASK:
                break;
            case ACTION_PAUSE_ALL_DOWNLOAD_TASK:
                break;
            case ACTION_CANCEL_DOWNLOAD_TASK:
                break;
            case ACTION_CANCEL_ALL_DOWNLOAD_TASK:
                break;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void updateNotification(){

    }

    private void getNotification(){

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_download_notification);

        remoteViews.setTextViewText(R.id.tv_title,"标题：下载中");
        remoteViews.setTextViewText(R.id.tv_content,"内容：1231231.apk ");
        remoteViews.setImageViewResource(R.id.iv_notify_logo,R.color._red);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = builder.setContentTitle("下载任务")
                .setContent(remoteViews)
                .setPriority(NotificationCompat.FLAG_FOREGROUND_SERVICE)
                .setAutoCancel(false)
                .setContentIntent(
                        PendingIntent.getActivity(this,
                                0,
                                new Intent(this, MainMvpActivity.class),
                                PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
//        mNotificationManager.notify(NOTIFICATION_ID,notification);
        startForeground(NOTIFICATION_ID,notification);
    }


}
