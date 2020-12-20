package com.think.core.util.network;

public enum NetworkState {
    /**
     * 没有网络
     */
    NETWORK_NONE,
    /**
     * 移动网络
     */
    NETWORK_MOBILE,
    /**
     * wifi网络
     */
    NETWORK_WIFI,
    /**
     * 未知网络
     */
    NETWORK_UNKNOWN,
    /**
     * 网络已经连接
     */
    NETWORK_CONNECT,
    /**
     * 网络断开
     */
    NETWORK_LOSE;
}
