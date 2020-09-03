package com.think.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.think.core.util.BarUtils;
import com.think.core.util.ScreenUtils;
import com.think.core.util.ViewScreenHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.adjustDensity(getApplication(),this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ViewScreenHelper.FULL_SCREEN_ACTION);
        intentFilter.addAction(ViewScreenHelper.LAND_SCREEN_ACTION);
        registerReceiver(ViewScreenHelper.getInstance(this),intentFilter);
        //去除标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        System.out.println(BarUtils.getNavigationBarHeight(this));
        System.out.println(BarUtils.getStatusBarHeight(this));
        System.out.println("是否全屏 = " + ScreenUtils.isFullScreen(this));
        System.out.println(ViewScreenHelper.getInstance().toString());


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("onConfigurationChanged ===> " + newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Intent intent = new Intent(ViewScreenHelper.LAND_SCREEN_ACTION);
            intent.setComponent(new ComponentName("com.think.core.util","com.think.core.util.ViewScreenHelper"));
            sendBroadcast(intent);
        }else{
            Intent intent = new Intent(ViewScreenHelper.PORT_SCREEN_ACTION);
            intent.setComponent(new ComponentName("com.think.core.util","com.think.core.util.ViewScreenHelper"));
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ViewScreenHelper.getInstance());
    }
}
