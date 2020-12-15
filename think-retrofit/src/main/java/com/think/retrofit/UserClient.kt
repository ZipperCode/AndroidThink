package com.think.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserClient {
    @GET("think-retrofit/data.json")
    fun getUserInfo(): Call<UserResponse<User>>
}