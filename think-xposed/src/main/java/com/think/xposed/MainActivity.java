package com.think.xposed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences config = getSharedPreferences("config", Context.MODE_PRIVATE);
        config.edit().putString("nativeDir", getApplication().getApplicationInfo().nativeLibraryDir).apply();
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                a();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AHook", "MainActivity TargetService.iLocalToServer = " + TargetService.iLocalToServer);
                if (TargetService.iLocalToServer != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ACTIVITY", 100);
                    bundle.putString("PKS", "com.kugou.android");
                    bundle.putString("NAME", "com.kugou.networktest.NetworkTestActivity");
                    try {
                        TargetService.iLocalToServer.callback(bundle);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
//                getFilesDir().mkdirs();
//                Log.d("TAG", " lib = " + getApplication().getApplicationInfo().nativeLibraryDir);
////                test(getPackageName(), getClassLoader());
//
//                try {
//                    List<ApplicationInfo> list = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
//                    for (ApplicationInfo info : list) {
//                        if (info.packageName.equals(BuildConfig.APPLICATION_ID)) {
//                            Log.i(TAG, info.nativeLibraryDir);
//                        }
//                    }
//                } catch (Throwable throwable) {
//                    Log.e(TAG, "JNILoadHelper load library error:", throwable);
//                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        Toast.makeText(this, "权限授予成功", Toast.LENGTH_LONG).show();
        a();
    }


    private void a() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "__share");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(getApplication().getApplicationInfo().nativeLibraryDir.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                su();
            }
        }).start();
    }

    public static void test(String packageName, ClassLoader classLoader) {
        Set<Object> dexCookie = new HashSet<>();
        ClassLoader loader = null, originLoader = null;
        loader = classLoader;
        do {
            if (loader instanceof BaseDexClassLoader) {
                dexCookie.addAll(getElementObject((BaseDexClassLoader) loader));
            }
            originLoader = loader;
            loader = loader.getParent();
        } while (loader != null && originLoader != loader);

        Log.i("AHook", "查找到的DexFile Cookie 数量为：" + dexCookie);
        if (dexCookie.size() > 0) {
            JniHelper.dexFileByCookie(packageName, dexCookie.toArray());
        }
    }

    private static void su() {
        BufferedReader bufferedReader = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "__share");
            Process process = Runtime.getRuntime().exec("su cat " + file.getAbsolutePath());
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Log.d("AHook", "" + bufferedReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

    private static Set<Object> getElementObject(BaseDexClassLoader classLoader) {
        Set<Object> dexCookie = new HashSet<>();
        try {
            Field pathListField = ReflectUtils.loadHideField(BaseDexClassLoader.class, "pathList");
            Object pathList = pathListField.get(classLoader);
            Field dexElementField = ReflectUtils.loadHideField(pathList.getClass(), "dexElements");
            Object[] dexElements = (Object[]) dexElementField.get(pathList);
            for (Object dexElement : dexElements) {
                Field dexFileField = ReflectUtils.loadHideField(dexElement.getClass(), "dexFile");
                DexFile dexFile = (DexFile) dexFileField.get(dexElement);
                if (dexFile != null) {
                    Field cookieField = ReflectUtils.loadHideField(DexFile.class, "mInternalCookie");
                    Object cookie = cookieField.get(dexFile);
                    if (cookie != null) {
                        dexCookie.add(cookie);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("AHook", "Hook DexCookie = " + dexCookie);
        return dexCookie;
    }
}