package com.think.demo;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


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
        setContentView(R.layout.activity_main);
//        System.out.println(BarUtils.getNavigationBarHeight(this));
//        System.out.println(BarUtils.getStatusBarHeight(this));
//        System.out.println("是否全屏 = " + ScreenUtils.isFullScreen(this));
//        System.out.println(ViewScreenHelper.getInstance().toString());
//        Runtime.getRuntime().exec("adb")
//        if(isAccessibilitySettingsOn(this,MyAccessibilityService.class)){
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//        }
//        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//        startService(new Intent(this,MyAccessibilityService.class));

//        if(!isAccessibilitySettingsOn(this, CustomAccessibilityService.class)){
////            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//        }
//
//        String s = MessageUtil.md5Crypt("哈哈，我是原文");
//
//        System.out.println("密文为：" + s);
//
//        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG,"点击了");
//
//                try {
//                    Context packageContext = createPackageContext("com.think", CONTEXT_RESTRICTED);
//                    System.out.println(packageContext);
//                    if(packageContext != null){
//                        int resId = packageContext.getResources().getIdentifier("activity_vpn","layout",packageContext.getPackageName());
//                        System.out.println("resId = " + resId);
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static boolean isAccessibilitySettingsOn(Context mContext, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
