package com.think.core.util.device;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.think.core.util.device.DeviceUtil;
import com.think.core.util.shell.ShellUtils;

import java.util.List;

/**
 * 打开应用商店工具类
 */
public final class MarketUtils {

    /**
     * 打开三星市场
     * （有三星市场会跳转三星市场下载）
     *
     * @param packageName 要下载的报名
     */
    public static void openSamsungMarket(Context context, String packageName) {

        try {
            Uri uri = Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openVivoMarket(Context context, String packageName){
        openMarket(context, packageName, "com.bbk.appstore");
    }

    public static void openXiaomiMarket(Context context, String packageName) {
        openMarket(context, packageName, "com.xiaomi.market");
    }

    /**
     * 通过包名打开应用市场相关的详情页
     */
    public static void openMarket(Context context, String packageName, String marketPackageName) {

        try {
            packageName = packageName.trim();
            Uri uri = Uri.parse("market://details?id=" + packageName + "&caller=" + context.getPackageName());
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N && DeviceUtil.isMiui()) {
                StringBuilder sb = new StringBuilder().append("am start --user 0")
                        .append(" -a ").append(Intent.ACTION_VIEW)
                        .append(" -f ").append(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .append(" -d ").append(uri.toString())
                        .append(" -p ").append(marketPackageName);
                ShellUtils.exec(sb.toString());
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(uri);
            intent.setPackage(marketPackageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过华为市场打开app
     */
    static void openHuaweiMarket(Context context, String packageName) {

        try {
            Intent intent = new Intent();
            intent.setAction("com.huawei.appmarket.intent.action.AppDetail");
            intent.setPackage("com.huawei.appmarket");
            Bundle bundle = new Bundle();
            bundle.putString("APP_PACKAGENAME", packageName);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 步步高早教机打开市场
     * 跳转应用市场后自动下载：1.应用商店要升级到5.4.0.1.H以上 2.参数添加is_auto_download
     *
     */
    public static void openBBGMarket(Context context, String appKey) {
        try {
            String callerPackageName = context.getPackageName();
            String url = "market://details?id=" + appKey + "&caller=" + callerPackageName + "&th_name=is_auto_down";
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.eebbk.bbkmiddlemarket");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openOtherMarket(context, appKey);
        }
    }


    /**
     * 是否有市场安装
     */
    private static boolean hasAnyMarketInstalled(Context context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List<ResolveInfo> list = context.getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return 0 != list.size();
    }

    /**
     * 打开其他市场
     */
    public static void openOtherMarket(Context context, String appKey) {

        if (TextUtils.isEmpty(appKey)) {
            return;
        }

        // 打开第三方市场
        try {
            Uri uri = Uri.parse("market://details?id=" + appKey.trim());

            if (hasAnyMarketInstalled(context)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Intent it = new Intent();
                it.setAction(Intent.ACTION_VIEW);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setData(uri);
                context.startActivity(it);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
