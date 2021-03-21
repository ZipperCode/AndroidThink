package com.think.accessibility.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.think.accessibility.bean.AppInfo
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

/**
 * @author : zzp
 * @date : 2020/8/5
 */
object AppUtils {
    /**
     * 检查无障碍服务是否开启
     *
     * @param context     当前上下文
     * @param serviceName 服务名：包名/类名 可能包含全类名也可能不包含
     * @return 开启为true
     */
    fun checkAccessibilityOn1(context: Context?, serviceName: String?): Boolean {
        if (context == null || serviceName == null) {
            return false
        }
        var isStarted = false
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledAccessibilityServiceList = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (i in enabledAccessibilityServiceList.indices) {
            if (enabledAccessibilityServiceList[i].id == serviceName) {
                isStarted = true
                break
            }
        }
        return isStarted
    }

    fun checkAccessibilityOn2(context: Context?, serviceName: String?): Boolean {
        if (context == null || serviceName == null) {
            return false
        }
        var ok = 0
        try {
            ok = Settings.Secure.getInt(context.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            // TODO Ignore
        }
        val ms = TextUtils.SimpleStringSplitter(':')
        if (ok == 1) {
            val settingValue = Settings.Secure.getString(context.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                ms.setString(settingValue)
                while (ms.hasNext()) {
                    val accessibilityService = ms.next()
                    if (accessibilityService.equals(serviceName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }


    fun isAccessibilitySettingsOn(mContext: Context, clazz: Class<out AccessibilityService?>): Boolean {
        var accessibilityEnabled = 0
        val service = mContext.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 判断Activity是否存活
     *
     * @param activity Activity对象
     * @return true为存活
     */
    fun isActivityAlive(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    fun getPackages(context: Context): List<PackageInfo> {
        val packageManager = context.packageManager
        return packageManager.getInstalledPackages(0)
    }

    fun getPackageNames(context: Context): Set<String> {
        val packages = getPackages(context)
        val packageNames: MutableSet<String> = HashSet()
        for (info in packages) {
            packageNames.add(info.packageName)
        }
        return packageNames
    }

    fun getLaunchActivity(context: Context): List<PackageInfo> {
        val pks = getPackages(context)
        return pks.filter {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(it.packageName)
            val aaa = context.packageManager.resolveActivity(intent,0)
            val resolveInfo = context.packageManager.queryIntentActivities(intent, 0)
            resolveInfo.size > 0
        }.toList()
    }

    fun getLaunch(context: Context, list: MutableList<AppInfo>){
        val pks = getPackages(context)
        pks.forEach {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(it.packageName)
            val resolveInfo = context.packageManager.queryIntentActivities(intent, 0)
            if(resolveInfo.size > 0){
                val icon = it.applicationInfo.loadIcon(context.packageManager)
                val name = context.packageManager.getApplicationLabel(it.applicationInfo)
                val launchActivity = resolveInfo[0].activityInfo.targetActivity
                if (!TextUtils.isEmpty(name) and (icon != null)){
                    list.add(AppInfo(icon, name.toString(),it.packageName))
                }
            }
        }
    }


    fun launchActivity(context: Context, map: HashMap<String,String>){
        val pks = getPackageNames(context)
        pks.forEach {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(it)
            val resolveInfo = context.packageManager.queryIntentActivities(intent, 0)
            if(resolveInfo.size > 0 ){
                map[it] = resolveInfo[0]?.activityInfo?.name ?: ""
            }
        }
    }
    /**
     * 通过包名进行app的启动
     *
     * @param context     当前上下文
     * @param packageName 包名
     */
    fun startApp(context: Context, packageName: String?) {
        val packageManager = context.packageManager
        //        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
//        context.startActivity(launchIntentForPackage);
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(packageName)
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        if (resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            val actClassName = resolveInfo.activityInfo.name
            val componentName = ComponentName(packageName!!, actClassName)
            intent.component = componentName
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }
        Toast.makeText(context, "启动APP失败，请重试", Toast.LENGTH_LONG).show()
    }

    fun checkAppInstall(context: Context, packageName: String): Boolean{
        return try {
            return context.packageManager.getPackageInfo(packageName,0) != null
        }catch (e: Exception){
            false
        }
    }

    /**
     * 打开路径中存在的文件
     * `// 这是比较流氓的方法，绕过7.0的文件权限检查
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
     * StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
     * StrictMode.setVmPolicy(builder.build());
     * }
    ` *
     *
     * @param context  上下文
     * @param filepath 文件路径
     */
    fun openAndroidFile(context: Context, filepath: String?, mimeType: String?) {
        if (TextUtils.isEmpty(filepath) || TextUtils.isEmpty(mimeType)) {
            return
        }
        val intent = Intent()
        val uri: Uri
        val file = File(filepath)
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
        } else {
            Uri.fromFile(file)
        }
        //设置标记，非Activity的上下文井下启动时，需要添加此Flag，不然报错
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //动作，查看
        intent.action = Intent.ACTION_VIEW
        //设置类型
        intent.setDataAndType(uri, mimeType)
        context.startActivity(intent)
    }

    /**
     * 忽略电池优化
     */
    @SuppressLint("BatteryLife")
    fun ignoreBatteryOptimization(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            try{
                val systemService = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                if(!systemService.isIgnoringBatteryOptimizations(context.packageName)){
                    context.startActivity(Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * 唤醒屏幕
     */
    fun wakeUpScreen(context: Context, timeout: Long = 60 * 1000L){
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
                AppUtils::class.java.simpleName
        )
        wakeLock.acquire(timeout)
    }


    fun unLock(context: Context, service: AccessibilityService): Boolean{
        val wh = ScreenUtils.getSysScreenWH(context)
        val w = wh[0]
        val h = wh[1]
        if((w <= 0) or (h <= 0)){
            return false
        }
        // 起始位置
        val startX = w / 2
        val startY = h / 10 * 8
        // 要移动到的位置
        val toY = h / 10 * 6
        val path = Path()
        path.moveTo(startX.toFloat(),startY.toFloat())
        path.lineTo(startX.toFloat(), toY.toFloat())
        if(Build.VERSION.SDK_INT >= 24){
            AccessibilityUtil.gestureScroll(service, path, 1000, 500,
                    object : AccessibilityService.GestureResultCallback() {
                        override fun onCompleted(gestureDescription: GestureDescription?) {
                            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                            val newKeyguardLock = keyguardManager.newKeyguardLock(AppUtils::class.java.simpleName)
                            newKeyguardLock.disableKeyguard()
                        }
                    })
        }
        return true
    }
}