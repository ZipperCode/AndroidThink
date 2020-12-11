package com.think.jetpack.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModelAndLiveData : ViewModel() {

    public var number: MutableLiveData<Int>? = null
    get() {
        if(field == null){
            field = MutableLiveData<Int>()
        }
        return field
    }

    fun addNum(){
        if(number?.value == null){
            number?.value = 0
        }
        number?.value = number?.value as Int + 1
    }




}