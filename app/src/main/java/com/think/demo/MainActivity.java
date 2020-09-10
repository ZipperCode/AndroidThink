package com.think.demo;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.adjustDensity(getApplication(),this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ViewScreenHelper.FULL_SCREEN_ACTION);
//        intentFilter.addAction(ViewScreenHelper.LAND_SCREEN_ACTION);
//        registerReceiver(ViewScreenHelper.getInstance(this),intentFilter);
//        //去除标题栏
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //去除状态栏
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
////                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main);
//        System.out.println(BarUtils.getNavigationBarHeight(this));
//        System.out.println(BarUtils.getStatusBarHeight(this));
//        System.out.println("是否全屏 = " + ScreenUtils.isFullScreen(this));
//        System.out.println(ViewScreenHelper.getInstance().toString());
//
        new AsyncTask<String, String, String>() {

            private int i = 0;

            @Override
            protected void onPreExecute() {
                // 1
                Log.d(TAG,"onPreExecute");
                super.onPreExecute();
            }


            @Override
            protected String doInBackground(String... strings) {
                // 2
                Log.d(TAG,"doInBackground");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress("123123123123123");

                return String.valueOf(i++);
            }

            @Override
            protected void onProgressUpdate(String... values) {
                Log.d(TAG,"onProgressUpdate");
                super.onProgressUpdate(values);
            }
            @Override
            protected void onPostExecute(String s) {
                // 3
                Log.d(TAG,"onPostExecute");
                super.onPostExecute(s);
            }

        }.execute();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
