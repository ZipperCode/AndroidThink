package com.think.core.util.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import com.think.core.util.ReflectUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PhoneUtils {
    private final static String TAG = "TelephoneUtil";

    public static String getVersion() { return Build.VERSION.RELEASE; }

    public static String getDeviceModel(){ return Build.MODEL; }

    /**
     * 获取手机序列号
     * @return 手机序列号
     */
    public static String getSerialNumber(){
        String serial = "null";
        try {
            Class<?> clazz = ReflectUtils.loadHideForName("android.os.SystemProperties");
            Method get = clazz.getMethod("get",String.class);
            serial = (String) get.invoke(clazz, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取手机 IMEI
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getIMEI(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (!TextUtils.isEmpty(tm.getDeviceId())) {
                return tm.getDeviceId();
            }
        }
        //获取设备Mac地址
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 判断是否有root权限
     * @return true 表示有root权限
     */
    public static boolean checkRootPermission(){
        boolean rooted = false;
        String [] suFiles = new String[]{"/system/bin/su","/system/xbin/su"};
        for (String su : suFiles) {
            if(new File(su).exists()){
                rooted = true;
                break;
            }
        }
        return rooted;
    }
}