package com.think.core.util.network;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.READ_PHONE_STATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

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
     *
     * @param context 当前上下文
     * @return true 标识可用，false 表示不可用
     */
    public static boolean networkIsConn(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = con.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = con.getNetworkCapabilities(activeNetwork);
                return (networkCapabilities != null
                        && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            }
        } else {
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isConnected();
        }
        return false;
    }

    /**
     * 获取本机的mac地址
     *
     * @return mac
     */
    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
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

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is4G(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ctx.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = manager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
                    if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return getNetType(ctx) == NetworkType.NETWORK_4G;
                    }
                }

            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isAvailable() && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
            }
        }
        return false;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is5G(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ctx.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = manager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
                    if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        NetworkType netState = getNetType(ctx);
                        return netState == NetworkType.NETWORK_5G;
                    }
                }
            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isAvailable() && networkInfo.getSubtype() == 20;
            }
        }
        return false;
    }


    @SuppressLint("MissingPermission")
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkType getNetType(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ctx.checkSelfPermission(ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            return NetworkType.NETWORK_UNKNOWN;
        }
        NetworkType networkType = NetworkType.NETWORK_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return networkType;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni == null) {
                return networkType;
            }
            if (!ni.isConnectedOrConnecting()) {
                return NetworkType.NETWORK_NO;
            }
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.NETWORK_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return getMobileStatus(ni.getSubtype());
                case ConnectivityManager.TYPE_ETHERNET:
                    return NetworkType.NETWORK_ETHER;
                default:
                    return NetworkType.NETWORK_UNKNOWN;
            }
        }else {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return NetworkType.NETWORK_UNKNOWN;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null){
                return NetworkType.NETWORK_UNKNOWN;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkType.NETWORK_WIFI;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                int networkTypeCode = TelephonyManager.NETWORK_TYPE_UNKNOWN;

                if (telephonyManager == null){
                    return NetworkType.NETWORK_UNKNOWN;
                }

                if (ctx.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        networkTypeCode = telephonyManager.getDataNetworkType();
                    } else {
                        networkTypeCode = telephonyManager.getNetworkType();
                    }
                }
                int code = 0;
                if (networkTypeCode == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                    //在这边重写获取一次，到这边几乎没成功的机会，使用networkinfo实现
                    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return getMobileStatus(info.getSubtype());
                        }
                    }
                } else {
                    return getMobileStatus(networkTypeCode);
                }

            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return NetworkType.NETWORK_ETHER;
            }
        }
        return NetworkType.NETWORK_UNKNOWN;
    }


    /**
     * @param systemNetworkType
     * @return 0:未知 1：2G 2:3G 3:4G 4:5G
     */
    private static NetworkType getMobileStatus(int systemNetworkType) {
        int maxNetworkType = 20;
        if (systemNetworkType >= maxNetworkType) {
            return NetworkType.NETWORK_5G;
        } else if (systemNetworkType == TelephonyManager.NETWORK_TYPE_IWLAN
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_LTE) {
            return NetworkType.NETWORK_4G;
        } else if (systemNetworkType == TelephonyManager.NETWORK_TYPE_TD_SCDMA
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_A
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_UMTS
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_0
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_HSDPA
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_HSUPA
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_HSPA
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_B
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_EHRPD
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
            return NetworkType.NETWORK_3G;
        } else if (systemNetworkType == TelephonyManager.NETWORK_TYPE_GSM
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_GPRS
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_EDGE
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_CDMA
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_1xRTT
                || systemNetworkType == TelephonyManager.NETWORK_TYPE_IDEN) {
            return NetworkType.NETWORK_2G;
        }
        return NetworkType.NETWORK_UNKNOWN;
    }

}
