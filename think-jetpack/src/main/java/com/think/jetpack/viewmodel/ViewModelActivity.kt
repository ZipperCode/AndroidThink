package com.think.jetpack.viewmodel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.think.jetpack.R

class ViewModelActivity : AppCompatActivity() {

    var liveData: MyViewModelAndLiveData ? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model)
        liveData = ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory() as ViewModelProvider.Factory
        ).get(MyViewModelAndLiveData::class.java)
        val tvText = findViewById<TextView>(R.id.num)
        val btn = findViewById<Button>(R.id.btn)

        liveData!!.number?.observe(this, Observer { num ->
            tvText.text = "变成${num}了"
        })


        btn.setOnClickListener(View.OnClickListener {
            liveData!!.addNum()
        })
    }
}