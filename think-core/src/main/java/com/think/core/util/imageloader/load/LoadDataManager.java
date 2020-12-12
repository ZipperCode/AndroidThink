package com.think.core.util.imageloader.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.think.core.util.ThreadManager;
import com.think.core.util.imageloader.Key;
import com.think.core.util.imageloader.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadDataManager implements ILoadData, Runnable {

    private String path;
    private ResponseListener responseListener;
    private Context context;

    @Override
    public Value loadResources(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;
        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            ThreadManager.getInstance().execPool(this);
        }

        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Value value = Value.getInstance();
                value.setmBitmap(bitmap);
                value.setKey(new Key(path));
                responseListener.onSuccess(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseListener.onFailure(e);
        } finally {


            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
