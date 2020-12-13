package com.think.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//
//        try {
//            Field field = ReflectUtils.loadHideField(Class.class,"dexCache");
//            Object dexCache = field.get(Class.class);
//
//            Field pathListField = ReflectUtils.loadHideField(BaseDexClassLoader.class, "pathList");
//
//            Object pathList = pathListField.get(getBaseContext().getClassLoader());
//
////            Class<?> eleClass = ReflectUtils.loadHideForName("dalvik.system.DexPathList$Element");
//
//            Field dexElementField = ReflectUtils.loadHideField(pathList.getClass(), "dexElements");
//
//            Object[] dexElements = (Object[]) dexElementField.get(pathList);
//
//            Class<?> dexFileClass = DexFile.class;
//            File file = new File(getFilesDir(),"app.dex");
//            FileWriter fileWriter = new FileWriter(file);
//
//            ReflectUtils.printMethod(dexFileClass);
//
//            Method dexFieldToDexField = ReflectUtils.loadHideMethod(dexFileClass,"toDex",Writer.class,Boolean.TYPE);
//
//            if(dexElements != null && dexElements.length > 0){
//                for (int i = 0; i < dexElements.length; i++) {
//                    Object dexElement = dexElements[i];
//                    Field eleDexFileField = ReflectUtils.loadHideField(dexElement.getClass(), "dexFile");
//                    Object dexFile = eleDexFileField.get(dexElement);
//                    dexFieldToDexField.invoke(dexFile,fileWriter,true);
//                }
//            }
//            fileWriter.flush();
//            fileWriter.close();
//            System.out.println(dexCache);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void print(Class<?> cls) throws IllegalAccessException {

    }
}
