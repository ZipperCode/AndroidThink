package com.think.jetpack.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingFragment<VDB : ViewDataBinding>(@LayoutRes val layoutRes: Int) : BaseFragment() {

    private lateinit var mBinding: VDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return mBinding.root
    }

    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }
}