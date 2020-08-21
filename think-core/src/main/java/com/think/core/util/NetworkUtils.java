package com.think.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author zzp
 * @date 2020-7-29
 */
public class NetworkUtils {

    /**
     * 检查网络是否可用
     * @param context 当前上下文
     * @return true 标识可用，false 表示不可用
     */
    public static boolean networkIsConn(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Network activeNetwork = con.getActiveNetwork();
            if(activeNetwork != null){
                NetworkCapabilities networkCapabilities = con.getNetworkCapabilities(activeNetwork);
                return (networkCapabilities != null
                        && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            }
        }else{
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isConnected();
        }
        return false;
    }

    /**
     * 获取本机的mac地址
     * @return mac
     */
    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")){
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }
}
