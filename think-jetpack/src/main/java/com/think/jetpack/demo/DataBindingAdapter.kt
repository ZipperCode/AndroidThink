package com.think.jetpack.demo

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter


object DataBindingAdapter {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageViewResource(imageView: ImageView, @DrawableRes resource: Int) {
        imageView.setImageResource(resource)
    }
}