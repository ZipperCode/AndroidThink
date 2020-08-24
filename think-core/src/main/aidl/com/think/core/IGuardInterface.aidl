// IGuardInterface.aidl
package com.think.core;

// 连接主进程和守护进程的桥梁
interface IGuardInterface {
    void onAlive();
}
