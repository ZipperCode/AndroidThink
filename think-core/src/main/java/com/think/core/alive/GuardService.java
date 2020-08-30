//package com.think.core.alive;
//
//import android.app.Service;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.util.Log;
//
//
//public class GuardService extends Service {
//
//    private static final String TAG = GuardService.class.getSimpleName();
//    public GuardService() {
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        bindMainService();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mIGuardInterface;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//
//    private void bindMainService(){
//        Intent intent = new Intent(this,MainService.class);
//        bindService(intent,mServiceConnect, Context.BIND_AUTO_CREATE);
//    }
//
//    private IGuardInterface.Stub mIGuardInterface = new IGuardInterface.Stub() {
//        @Override
//        public void onAlive() throws RemoteException {
//            Log.i(TAG,"Main Service call onAlive");
//        }
//    };
//
//    private ServiceConnection mServiceConnect = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            IGuardInterface guardInterface = IGuardInterface.Stub.asInterface(service);
//            try {
//                guardInterface.onAlive();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            bindMainService();
//        }
//    };
//}
