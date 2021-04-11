package com.think.demo;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : zzp
 * @date : 2020/8/5
 */

public class AppUtils {

    public static List<PackageInfo> getPackages(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        return installedPackages;
    }

    public static Set<String> getPackageNames(Context context) {
        List<PackageInfo> packages = getPackages(context);
        Set<String> packageNames = new HashSet<>();
        for (PackageInfo info : packages) {
            packageNames.add(info.packageName);
        }
        return packageNames;
    }
}


