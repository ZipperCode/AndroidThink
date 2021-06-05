package com.think.jetpack.core

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType

abstract class BaseBindingActivity<VDB : ViewDataBinding>() : BaseActivity() {

    private lateinit var mBinding: VDB

    private fun initBinding(){
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        val vdmClass = parameterizedType.actualTypeArguments[0] as Class<*>
        val inflaterMethod = vdmClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        mBinding = inflaterMethod.invoke(null,layoutInflater) as VDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    override fun onDestroy() {
        if(::mBinding.isInitialized){
            mBinding.unbind()
        }
        super.onDestroy()
    }
}