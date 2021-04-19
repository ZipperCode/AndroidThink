package com.think.jetpack.demo

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.think.jetpack.preference.DataStore
import kotlinx.coroutines.delay

class LiveObservableWrapper<T>(
        val liveData: LiveData<T>,
        val observable: ObservableField<T>
) {
    init {
        observable.set(liveData.value)
        observable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val value = (sender as ObservableField<*>).get() as T
                Log.d(TAG, "onPropertyChanged value = $value")
                if(value is Boolean){
                    DataStore.instance().putBoolean("123",value)
                }
            }
        })
    }

    fun setValue(lifecycleOwner: LifecycleOwner){
        liveData.observe(lifecycleOwner, Observer {
            observable.set(it)
        })
    }

    companion object{
        const val TAG = "LiveObservableWrapper"
    }
}