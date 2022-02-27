package com.think.xposed.dex;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Environment;

import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;
import com.think.xposed.ReflectUtils;
import com.think.xposed.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

@TargetApi(25)
public class Fdex {

    public Class<?> dexClass;

    private Method dexClassGetBytes;

    private Method dexClassGetDex;

    private Method dexClassWriteTo;

    public Fdex() {
        try {
            dexClass = ReflectUtils.loadHideForName("com.android.dex.Dex");
            dexClassGetBytes = ReflectUtils.loadHideMethod(dexClass, "getBytes");
            dexClassGetDex = ReflectUtils.loadHideMethod(Class.class, "getDex");
            dexClassWriteTo = ReflectUtils.loadHideMethod(dexClass, "writeTo", File.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hook(Application application) {
        if (application != null) {
            String packageName = application.getPackageName();
            try {
                Class<?> clazz = application.getBaseContext().getClassLoader().loadClass("java.lang.ClassLoader");

                DexposedBridge.findAndHookMethod(clazz, "loadClass", String.class, Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 程序加载到了类
                        Class<?> cls = (Class<?>) param.getResult();
                        if (cls == null) {
                            DexposedBridge.log("cls == null");
                            return;
                        }
                        String name = cls.getName();
                        DexposedBridge.log("获取到加载的类名：" + name);
                        Object dexObject = dexClassGetDex.invoke(cls); // getDex 无需参数
                        String dexPath = Utils.rename(Environment.getExternalStorageDirectory().getPath() + "/FDex/" + packageName  + ".dex");
                        File file = new File(dexPath);
                        DexposedBridge.log("开始写数据：" + dexPath);
                        try {
                            dexClassWriteTo.invoke(dexObject, file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            byte[] bArr = (byte[]) dexClassGetDex.invoke(dexObject);
                            byte[] dexArr = (byte[]) dexClassGetBytes.invoke(dexObject);
                            if (bArr == null) {
                                DexposedBridge.log("获取到的dex数据为空：返回");
                                return;
                            }
                            writeByte(dexArr, file);
                            DexposedBridge.log("数据写入失败：" + dexPath);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeByte(byte[] bArr, File file) throws Exception {
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bArr);
        outputStream.close();

    }
}
