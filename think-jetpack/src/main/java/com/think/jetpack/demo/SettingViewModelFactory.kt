package com.think.jetpack.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.think.jetpack.preference.data.LoginDataSource
import com.think.jetpack.preference.data.LoginRepository

class SettingViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}