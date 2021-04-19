package com.think.jetpack.demo

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class SettingHolder(var binding: ViewDataBinding, val viewType: Int)
    : RecyclerView.ViewHolder(binding.root)