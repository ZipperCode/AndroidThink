package com.think.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val client = RetrofitHelper.getInstance().userClient(UserClient::class.java);
//        // 不是标准的json，无法解析，但方法是没错的
//        client.getUserInfo()
//                .also {
//            it.enqueue(object : Callback<UserResponse<User>>{
//                override fun onFailure(call: Call<UserResponse<User>>?, t: Throwable?) {
//                    t?.message.let { it1 -> Log.e(TAG, it1!!) }
//                }
//
//                override fun onResponse(call: Call<UserResponse<User>>?, response: Response<UserResponse<User>>?) {
//                    println(response)
//                    response?.body().run { println(it.toString()) }
//                }
//
//            })
//        }
        val httpclient = RetrofitHelper.getInstance().create(HttpClient::class.java)
        val call = httpclient.get()
        call.also {
            it.enqueue(object : Callback<HttpResponse> {
                override fun onResponse(call: Call<HttpResponse>?, response: Response<HttpResponse>?) {
                    response?.body().run {
                        println(toString())
                        Log.i(TAG,toString())
                    }
                }

                override fun onFailure(call: Call<HttpResponse>?, t: Throwable?) {
                    t?.message.let { it1 -> Log.e(TAG, it1!!) }
                }
            });
           Handler()
        }
        httpclient.post("post", "abc","admin","admin").also {
            it.enqueue(object : Callback<HttpResponse>{
                override fun onResponse(call: Call<HttpResponse>?, response: Response<HttpResponse>?) {
                    response?.body().run { println(toString()) }
                }

                override fun onFailure(call: Call<HttpResponse>?, t: Throwable?) {
                    t?.message.let { it1 -> Log.e(TAG, it1!!) }
                }
            })
        }
        val user = User()
        user.apply {
            id = "10000"
            username = "username"
            password = "123"
            age = 10
        }
        httpclient.post(user).also {
            it.enqueue(object : Callback<HttpResponse>{
                override fun onResponse(call: Call<HttpResponse>?, response: Response<HttpResponse>?) {
                    response?.body().run { println(toString()) }
                }

                override fun onFailure(call: Call<HttpResponse>?, t: Throwable?) {
                    t?.message.let { it1 -> Log.e(TAG, it1!!) }
                }
            })
        }

    }
}