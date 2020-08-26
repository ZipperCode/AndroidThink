package com.think.core.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : zzp
 * @date : 2020/8/5
 */


public class AppUtils {

    /**
     * 通过包名检查手机是否安装了某一款app
     *
     * @param context     当前上下文
     * @param packageName 包名
     * @return true 有安装，false 未安装
     */
    public static boolean containAppByPackage(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info : installedPackages) {
            if (packageName.equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过应用名称检查手机是否安装了某一款app
     *
     * @param context 当前上下文
     * @param appName app名字
     * @return true表示有安装，false 表示未安装
     */
    public static boolean containAppByName(Context context, String appName) {
        if (context == null || TextUtils.isEmpty(appName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        for (ApplicationInfo info : installedApplications) {
            String label = packageManager.getApplicationLabel(info).toString();
            if (appName.equals(label)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过app名称查找第一个符合的应用包名，应用多开或者包名不同的相同应用只查找第一个找到的
     *
     * @param context 当前上下文
     * @param appName app名称
     * @return 包名
     */
    public static String getPackageByPackage(Context context, String appName) {
        if (context == null || TextUtils.isEmpty(appName)) {
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        String packageName = "";
        for (ApplicationInfo info : installedApplications) {
            String label = packageManager.getApplicationLabel(info).toString();
            if (appName.equals(label)) {
                packageName = info.packageName;
                break;
            }
        }
        return packageName;
    }


    /**
     * 通过包名进行app的启动
     *
     * @param context     当前上下文
     * @param packageName 包名
     */

    public static void startApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
//        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
//        context.startActivity(launchIntentForPackage);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        if (resolveInfos.size() > 0) {
            ResolveInfo resolveInfo = resolveInfos.get(0);
            String actPackageName = resolveInfo.activityInfo.packageName;
            String actClassName = resolveInfo.activityInfo.name;
            ComponentName componentName = new ComponentName(packageName, actClassName);
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }
        Toast.makeText(context, "启动APP失败，请重试", Toast.LENGTH_LONG).show();
        context.startActivity(intent);
    }

    /**
     * 通过包名进行app的启动
     *
     * @param context 当前上下文
     * @param appName app名字
     */
    public static void startAppByName(Context context, String appName) {
        if (containAppByName(context, appName)) {
            String packageName = getPackageByPackage(context, appName);
            startApp(context, packageName);
            return;
        }
        Toast.makeText(context, "启动APP失败，请重试", Toast.LENGTH_LONG).show();
    }

    /**
     * 根据url调用浏览器下载
     *
     * @param url 下载链接
     */
    public static void downloadByBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 通过下载链接对app文件进行下载
     *
     * @param context     上下文
     * @param url         下载链接
     * @param storagePath 存储目录
     * @return 下载任务ID
     */
    public static long downloadAppBySystem(Context context, String url, String storagePath) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(storagePath)) {
            return 0;
        }
        return SystemDownload.downloadBySystem(context, url,
                "application/vnd.android.package-archive", storagePath);
    }

    public static long downloadBySystem(Context context, String url,
                                        String mimeType, String storagePath) {
        return SystemDownload.downloadBySystem(context, url, mimeType, storagePath);
    }

    /**
     * 根据文件路径安装一个App
     *
     * @param context  上下文
     * @param filepath 文件路径
     */
    public static void installAppByFile(Context context, String filepath) {
        openAndroidFile(context, filepath, "application/vnd.android.package-archive");
    }

    /**
     * 打开路径中存在的文件
     * {@code  // 这是比较流氓的方法，绕过7.0的文件权限检查
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
     * StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
     * StrictMode.setVmPolicy(builder.build());
     * }
     * }
     *
     * @param context  上下文
     * @param filepath 文件路径
     */
    public static void openAndroidFile(Context context, String filepath, String mimeType) {
        if (TextUtils.isEmpty(filepath) || TextUtils.isEmpty(mimeType)) {
            return;
        }
        Intent intent = new Intent();
        final Uri uri;
        File file = new File(filepath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        //设置标记，非Activity的上下文井下启动时，需要添加此Flag，不然报错
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //动作，查看
        intent.setAction(Intent.ACTION_VIEW);
        //设置类型
        intent.setDataAndType(uri, mimeType);
        context.startActivity(intent);
    }

    /**
     * 系统下载辅助工具类
     */
    public static final class SystemDownload {
        /**
         * 系统下载程序的包名
         */
        private static final String SYSTEM_DOWNLOAD_PACKAGE_NAME = "com.android.providers.downloads";

        /**
         * 判断系统的下载服务是否可用
         *
         * @param context 上下文
         * @return true 标识可用，false 标识不可用
         */
        public static boolean isEnableSystemDownload(Context context) {
            PackageInfo packageInfo;
            boolean isExists = true;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(SYSTEM_DOWNLOAD_PACKAGE_NAME, 0);
                isExists = packageInfo != null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                isExists = false;
            }
            if (!isExists) {
                return false;
            }

            int state = context.getPackageManager()
                    .getApplicationEnabledSetting(SYSTEM_DOWNLOAD_PACKAGE_NAME);
            return (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        }

        public static void startSystemDownload(Context context) {

        }

        /**
         * 调用系统的下载程序进行下载
         *
         * @param url 下载链接
         * @return 下载任务的id
         */
        public static long downloadBySystem(final Context context, String url, String mimeType, String storagePath) {
            if (context == null || TextUtils.isEmpty(url) || TextUtils.isEmpty(storagePath)) {
                return 0;
            }
            if (TextUtils.isEmpty(mimeType)) {
                mimeType = "*/*";
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            /*
             * 设置下载完成进行通知
             * {@link DownloadManager.Request.VISIBILTY_HIDDEN} 设置此模式需要
             *      android.Manifest.permission.DOWNLOAD_WITHOUT_NOTIFICATION 权限
             * {@link DownloadManager.Request.VISIBILITY_VISIBLE}下载显示，下载完成自动消失
             * {@link DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED} 始终显示
             * @{link DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION} 任务完成时显示
             *
             */
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 允许流量下载
            request.setAllowedOverMetered(true);

            // 允许漫游下载
            request.setAllowedOverRoaming(true);
            // 允许下载的类型,wifi 和流量
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            String fileName = URLUtil.guessFileName(url, "", mimeType);
            File saveFile = new File(storagePath, fileName);
            request.setDestinationUri(Uri.fromFile(saveFile));
            /*
             * 存放在应用专属目录，应用卸载内容跟着消失
             * 目录： Android/data/package-name/files/Download/fileName
             */
            // request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,fileName);
            /*
             * 存放在公共目录， 也就是Download文件夹
             * 目录：SDCard/Download/fileDir/filename
             */
            // request.setDestinationInExternalPublicDir("/test/",fileName);
            LogUtils.debug("下载的文件名为： fileName = " + fileName);
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
            LogUtils.debug("downloadId: " + downloadId);
            // 测试
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctxt, Intent intent) {
                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                }
            };
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            return downloadId;
        }

        /**
         * 调用系统下载服务进行下载，并且回调下载状态
         *
         * @param context          上下文
         * @param url              下载链接
         * @param mimeType         文件mime类型
         * @param storagePath      存储目录
         * @param progressCallback 回调函数
         * @return 下载任务id
         */
        public static long downloadBySystem(Context context, String url, String mimeType,
                                            String storagePath, ProgressCallback progressCallback) {
            long downloadId = downloadBySystem(context, url, mimeType, storagePath);
            ThreadManager.getInstance().execSchedule(new QueryDownloadProgress(context, downloadId, progressCallback),
                    1000, 1000, TimeUnit.SECONDS);
            return downloadId;
        }

        /**
         * 查询下载进度工具方法
         */
        public static final class QueryDownloadProgress implements Runnable {

            /**
             * 下载管理器
             */
            private final DownloadManager mDownloadManager;
            /**
             * 下载任务查询器
             */
            private final DownloadManager.Query mQuery;
            /**
             * 全局上下文
             */
            private final Context mContext;
            /**
             * 下载任务的ID
             */
            private final long mDownloadId;

            private final ProgressCallback mProgressCallback;

            public QueryDownloadProgress(Context context, long downloadId, ProgressCallback progressCallback) {
                this.mContext = context.getApplicationContext();
                this.mDownloadId = downloadId;
                this.mProgressCallback = progressCallback;
                mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                // 创建一个查询对象
                mQuery = new DownloadManager.Query();
                // 根据 下载ID 过滤结果
                mQuery.setFilterById(mDownloadId);
            }

            /**
             * 使用定时器/调度线程/Handler进行调用
             */
            @Override
            public void run() {
                // 执行查询, 返回一个 Cursor (相当于查询数据库)
                Cursor cursor = mDownloadManager.query(mQuery);
                if (!cursor.moveToFirst()) {
                    cursor.close();
                    return;
                }
                // 下载请求的状态
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                // 下载文件在本地保存的路径（Android 7.0 以后 COLUMN_LOCAL_FILENAME 字段被弃用, 需要用 COLUMN_LOCAL_URI 字段来获取本地文件路径的 Uri）
                String localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                // 下载文件的uri
                String localFileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                // 已下载的字节大小
                long downloadedSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                // 下载文件的总字节大小
                long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                cursor.close();
                // 计算进度
                float progress = (downloadedSoFar * 1.0f / totalSize) * 100;

                LogUtils.debug("下载进度: " + downloadedSoFar + "/" + totalSize + " ==> " + progress + "%");

                /*
                 * 判断是否下载成功，其中状态 status 的值有 5 种:
                 *     DownloadManager.STATUS_SUCCESSFUL:   下载成功
                 *     DownloadManager.STATUS_FAILED:       下载失败
                 *     DownloadManager.STATUS_PENDING:      等待下载
                 *     DownloadManager.STATUS_RUNNING:      正在下载
                 *     DownloadManager.STATUS_PAUSED:       下载暂停
                 */
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    /*
                     * 特别注意: 查询获取到的 localFilename 才是下载文件真正的保存路径，在创建
                     * 请求时设置的保存路径不一定是最终的保存路径，因为当设置的路径已是存在的文件时，
                     * 下载器会自动重命名保存路径，例如: .../demo-1.apk, .../demo-2.apk
                     */
                    System.out.println("下载成功, 打开文件, 文件路径: " + localFilename + "uri = " + localFileUri);
                    mProgressCallback.onProgress(100f);
                    mProgressCallback.onSuccess(Uri.parse(localFileUri));
                    // 取消调度线程重复执行的任务
                    ThreadManager.getInstance().cancelSchedule(this);
                } else if (status == DownloadManager.STATUS_FAILED) {
                    mProgressCallback.onFailure();
                } else if (status == DownloadManager.STATUS_PAUSED) {
                    mProgressCallback.onPause();
                } else if (status == DownloadManager.STATUS_RUNNING) {
                    mProgressCallback.onProgress(progress);
                }
            }
        }

        public interface ProgressCallback {
            /**
             * 回调下载进度
             *
             * @param progress 进度信息：99.01
             */
            void onProgress(float progress);

            /**
             * 回调成功
             */
            void onSuccess(Uri uri);

            /**
             * 回调暂停
             */
            void onPause();

            /**
             * 回调失败
             */
            void onFailure();
        }
        /**
         * AppUtils.openAndroidFile(localFilename);
         * BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         *
         * @Override public void onReceive(Context context, Intent intent) {
         * LogUtils.debug("收到安装广播 intent = " + intent + ", action = " + intent.getAction() );
         * if (INSTALLED.equals(intent.getAction())) {
         * // TODO 安装成功
         * mInstalledSuccess.setValue(true);
         * GameApplication.appContext.unregisterReceiver(this);
         * }
         * }
         * };
         * IntentFilter intentFilter = new IntentFilter();
         * intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
         * intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
         * intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
         * intentFilter.addDataScheme("package");
         * GameApplication.appContext.registerReceiver(broadcastReceiver,intentFilter);
         */

    }
}


