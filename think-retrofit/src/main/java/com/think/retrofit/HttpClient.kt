package com.think.retrofit

import retrofit2.Call
import retrofit2.http.*

interface HttpClient {

    @GET("/get")
    fun get(): Call<HttpResponse>

    @Headers("Custom: Custom")
    @FormUrlEncoded
    @POST("/{method}")
    fun post(@Path("method") method: String = "post",
             @Query("a") a: String,
             @Field("username")username:String,
             @Field("password")password:String
    ): Call<HttpResponse>

    @POST("/post")
    fun post(@Body user: User): Call<HttpResponse>
}