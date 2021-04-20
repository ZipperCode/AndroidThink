package com.think.jetpack.demo.setting.data

import android.content.SharedPreferences
import android.util.Log
import androidx.collection.arrayMapOf
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingRepository(val settingSource: SettingSource): SharedPreferences.OnSharedPreferenceChangeListener {

    val mSwitchData: ObservableField<Boolean> = ObservableField(false)
    val mSwitchData1: ObservableField<Boolean> = ObservableField(false)

    val mSelectValue: ObservableField<String> = ObservableField("")

    val mapTable: Map<String, ObservableField<Boolean>> = arrayMapOf(
            "123" to mSwitchData,
            "456" to mSwitchData1
    )

    init {
        loadData()
        mSwitchData.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val value = (sender as ObservableField<*>).get() as Boolean
                Log.d(TAG, "onPropertyChanged value = $value")
                settingSource.switchValue("123",value)
                settingSource.switchValue("456",!value)
                settingSource.selectValue("time",5)
            }
        })
        mSwitchData1.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val value = (sender as ObservableField<*>).get() as Boolean
                Log.d(TAG, "onPropertyChanged value = $value")
                settingSource.switchValue("123",!value)
                settingSource.switchValue("456",value)
                settingSource.selectValue("time",0)
            }
        })

        mSelectValue.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val value = (sender as ObservableField<*>).get() as String
                Log.d(TAG, "onPropertyChanged value = $value")
                val time = MapTable.getTime(value)
                settingSource.selectValue("time",time)
            }
        })
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG,"onSharedPreferenceChanged key = $key")
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.Default).launch {
            settingSource.switchValue("123").collect { value -> mSwitchData.set(value) }
            settingSource.switchValue("456").collect { value -> mSwitchData1.set(value) }
            settingSource.selectValue("time").collect { value -> mSelectValue.set(value) }
        }
    }

    companion object{
        val TAG: String = SettingRepository::class.java.simpleName
    }
}