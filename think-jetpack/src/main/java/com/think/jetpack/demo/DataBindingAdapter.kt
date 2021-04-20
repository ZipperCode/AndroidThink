package com.think.jetpack.demo

import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.think.jetpack.R
import com.think.jetpack.demo.setting.HeaderData
import com.think.jetpack.demo.setting.data.Gender


object DataBindingAdapter {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageViewResource(imageView: ImageView, @DrawableRes resource: Int) {
        imageView.setImageResource(resource)
    }

    @BindingAdapter("loadCircleImage")
    @JvmStatic
    fun loadCircleImageView(imageView: ImageView, headerData: HeaderData) {
        val imageUrl = headerData.imageUrl
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(if (headerData.gender == Gender.MALE)
                R.drawable.icon_male_head
            else R.drawable.icon_female_head)
        } else {
            Glide.with(imageView)
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .placeholder(R.drawable.icon_male_head)
                    .error(R.drawable.icon_male_head)
                    .into(imageView)
        }
    }
}