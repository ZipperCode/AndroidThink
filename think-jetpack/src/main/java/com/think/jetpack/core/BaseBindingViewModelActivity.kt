package com.think.jetpack.core

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType

abstract class BaseBindingViewModelActivity<VDB : ViewDataBinding, VM : ViewModel>()
    : BaseBindingActivity<VDB>() {

    protected lateinit var mViewModel: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    @CallSuper
    protected fun initViewModel(){
        val  parameterizedType = javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = parameterizedType.actualTypeArguments
        if(actualTypeArguments.size <= 1){
            throw IllegalArgumentException("argument Type num <= 1, num should be 2")
        }
        val vmClass = actualTypeArguments[1] as Class<VM>
        mViewModel = getViewModel(vmClass)
    }

    protected fun <T: ViewModel> getViewModel(viewModelClass: Class<T>): T{
        return ViewModelProvider(this).get(viewModelClass)
    }
}