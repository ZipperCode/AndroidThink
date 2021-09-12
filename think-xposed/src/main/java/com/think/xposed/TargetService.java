package com.think.xposed;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class TargetService extends Service {

    private static final String TAG = "TargetService";

    public static ILocalToServer iLocalToServer;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("AHook", TAG + " ===> 收到客户端的绑定请求 intent = " + intent);
        Log.d("AHook", TAG+ " onBind 当前进程为 ： " + getProcessName(TargetService.this));

        return new IStartInterface.Stub() {
            @Override
            public void startActivity(String packageName, String componentName) throws RemoteException {
                try{
                    Log.d("AHook", TAG + " ===> startActivity packageName = " + packageName + ", componentName " + componentName);
                    Log.d("AHook", TAG+ " IStartInterface.Stub#startActivity 当前进程为 ： " + getProcessName(TargetService.this));
                    ComponentName cm = new ComponentName(packageName, componentName);
                    Intent startIntent = new Intent();
                    startIntent.setComponent(cm);
                    TargetService.this.startActivity(startIntent);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("AHook",e.getMessage());
                }
            }

            @Override
            public void callback(ILocalToServer localToServer) throws RemoteException {
                iLocalToServer = localToServer;
            }
        };
    }

    public static String getProcessName(Context cxt) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}