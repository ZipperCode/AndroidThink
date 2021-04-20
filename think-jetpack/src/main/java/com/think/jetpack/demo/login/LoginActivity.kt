package com.think.jetpack.demo.login

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.think.jetpack.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction().add(LoginFragment(),"").commit()
    }
}