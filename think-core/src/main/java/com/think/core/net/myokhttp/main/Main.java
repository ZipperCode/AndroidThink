package com.think.core.net.myokhttp.main;

import com.think.core.net.myokhttp.Call;
import com.think.core.net.myokhttp.Callback;
import com.think.core.net.myokhttp.HttpClient;
import com.think.core.net.myokhttp.body.Request;
import com.think.core.net.myokhttp.body.Response;
import com.think.core.net.myokhttp.body.ResponseBody;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient.Builder().build();

        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .build();

//        try {
//            Response response = httpClient.newCall(request).execute();
//            ResponseBody body = response.body();
//            if(body != null){
//                System.out.println(body.string());
//            }
//            System.out.println(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onSuccess(Call call, Response response) {
                ResponseBody body = response.body();
                if(body != null){
                    System.out.println(body.string());
                }
                System.out.println(response.requestAtMillis());
            }

            @Override
            public void onFailure(Call call, Exception e) {
                e.printStackTrace();
            }
        });

    }
}
