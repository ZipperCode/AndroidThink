package com.think.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {

    companion object{
        private val retrofitHelper = RetrofitHelper()

        public fun getInstance(): RetrofitHelper = retrofitHelper
    }

    private val retrofit:Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl("https://httpbin.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build()
    }


    fun <T> userClient(clazz: Class<T>):T = retrofit.create(clazz)


    fun <T> create(clazz: Class<T>): T = retrofit.create(clazz)
}