package com.think.core.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

/**
 * @date : 2020/8/5
 * @author : zzp
 **/
public class AppUtils {

    /**
     * 通过包名检查手机是否安装了某一款app
     * @param context 当前上下文
     * @param packageName 包名
     * @return true 有安装，false 未安装
     */
    public static boolean containAppByPackage(Context context, String packageName){
        if(context == null || TextUtils.isEmpty(packageName)){
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info: installedPackages) {
            if(packageName.equals(info.packageName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过应用名称检查手机是否安装了某一款app
     * @param context 当前上下文
     * @param appName app名字
     * @return true表示有安装，false 表示未安装
     */
    public static boolean containAppByName(Context context, String appName){
        if(context == null || TextUtils.isEmpty(appName)){
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        for (ApplicationInfo info: installedApplications) {
            String label = packageManager.getApplicationLabel(info).toString();
            if(appName.equals(label)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过app名称查找第一个符合的应用包名，应用多开或者包名不同的相同应用只查找第一个找到的
     * @param context 当前上下文
     * @param appName app名称
     * @return 包名
     */
    public static String getPackageByPackage(Context context, String appName){
        if(context == null || TextUtils.isEmpty(appName)){
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        String packageName = "";
        for (ApplicationInfo info: installedApplications) {
            String label = packageManager.getApplicationLabel(info).toString();
            if(appName.equals(label)){
                packageName =  info.packageName;
                break;
            }
        }
        return packageName;
    }

    /**
     * 通过包名进行app的启动
     * @param context 当前上下文
     * @param packageName 包名
     */
    public static void startApp(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();

//        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
//        context.startActivity(launchIntentForPackage);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        if(resolveInfos.size() > 0){
            ResolveInfo resolveInfo = resolveInfos.get(0);
            String actPackageName = resolveInfo.activityInfo.packageName;
            String actClassName = resolveInfo.activityInfo.name;
            ComponentName componentName = new ComponentName(packageName, actClassName);
            intent.setComponent(componentName);
            context.startActivity(intent);
            return;
        }
        Toast.makeText(context,"启动APP失败，请重试",Toast.LENGTH_LONG).show();
    }

    /**
     * 通过包名进行app的启动
     * @param context 当前上下文
     * @param appName app名字
     */
    public static void startAppByName(Context context, String appName){
        if(containAppByName(context, appName)){
            String packageName = getPackageByPackage(context, appName);
            startApp(context,packageName);
            return;
        }
        Toast.makeText(context,"启动APP失败，请重试",Toast.LENGTH_LONG).show();
    }
}
