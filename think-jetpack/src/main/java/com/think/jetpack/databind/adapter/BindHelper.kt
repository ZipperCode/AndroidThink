package com.think.jetpack.databind.adapter

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindHelper {

    private const val TAG = "BindHelper"

    @BindingAdapter("loadImage")
    fun ImageView.loadImage(url: String){
        Log.d(TAG,"ImageView.loadImage ==> url = $url")
    }


}