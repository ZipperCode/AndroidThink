package com.think.bsdiff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BsPatcher {
    static {
        System.loadLibrary("patch");
    }

    public static void pullPatch(final Context context){
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"patch.apk");
                File outFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"marge_patch.apk");
                try {
                    URL url = new URL("");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == 200){
                        inputStream = connection.getInputStream();
                        if(file.exists()){
                            file.delete();
                        }
                        file.createNewFile();
                        if(outFile.exists()){
                            outFile.delete();
                        }
                        outFile.createNewFile();
                        outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1){
                            outputStream.write(buffer,0,len);
                        }
                        outputStream.flush();
                    }
                    inputStream.close();
                    String oldPath = context.getApplicationContext().getPackageResourcePath();
                    margeParch(oldPath,file.getAbsolutePath(),outFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try{
                        if(inputStream != null){
                            inputStream.close();
                        }
                        if(outputStream != null){
                            outputStream.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if(file.exists()){
                    installApk(context,file);
                }
            }
        });
    }

    public static void test(Context context){
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"patch.apk");
        File outFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"marge_patch.apk");
        System.out.println("file = " + file.getAbsolutePath());

        if(!file.exists() ){
            return;
        }
        if(outFile.exists()){
           outFile.delete();
        }
        try{
            outFile.createNewFile();
        }catch (IOException e){}
        System.out.println("outFile = " + file.getAbsolutePath());
        String oldPath = context.getApplicationContext().getPackageResourcePath();
        System.out.println("oldPath = " +oldPath);
        int ret = margeParch(oldPath,file.getAbsolutePath(),outFile.getAbsolutePath());
        System.out.println("ret = " + ret);
        installApk(context,outFile);
    }

    public static native int margeParch(String oldApk, String patch, String outputApk);

    public static void installApk(Context context, File apkFile){
        if(!apkFile.exists()){
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if(Build.VERSION.SDK_INT > 23){
            Uri fileUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + "fileprovider", apkFile) ;
            intent.setDataAndType(fileUri,"application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

}
