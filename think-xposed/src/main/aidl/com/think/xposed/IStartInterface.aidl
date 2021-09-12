// IStartInterface.aidl
package com.think.xposed;
import com.think.xposed.ILocalToServer;

interface IStartInterface {

    void startActivity(String packageName, String compentName);

    void callback(ILocalToServer localToServer);
}