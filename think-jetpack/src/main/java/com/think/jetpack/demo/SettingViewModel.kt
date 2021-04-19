package com.think.jetpack.demo

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.think.jetpack.R
import com.think.jetpack.demo.data.SettingRepository
import com.think.jetpack.demo.data.SettingSource
import com.think.jetpack.preference.DataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingViewModel: ViewModel() {

    private val repository: SettingRepository = SettingRepository(SettingSource())


    val mList: MutableList<BaseData>

    init {
        mList = arrayListOf(
                SelectData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "家长中心",
                        layoutHeight = R.dimen.setting_row_layout_height,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleArrow = true,
                        visibleIcon = true,
                        visibleDivider = true,
                        selectValue = repository.mSelectValue
                ),
                MenuData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "学习中心",
                        layoutHeight = R.dimen.setting_row_layout_height,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleDivider = true,
                        visibleIcon = true
                ),
                MenuData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "家长中心",
                        layoutHeight = R.dimen.setting_row_layout_height,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleArrow = true,
                        visibleIcon = true,
                        visibleDivider = true
                ),
                MenuData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "学习中心",
                        layoutHeight = R.dimen.setting_row_layout_height,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleDivider = true,
                        visibleIcon = true
                ),
                SwitchData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "学习中心",
                        summary = "我是摘要",
                        visibleSummary = true,
                        layoutHeight = R.dimen.setting_row_layout_height_2,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleDivider = true,
                        visibleIcon = false,
                        switchValue = repository.mSwitchData
                ),
                SwitchData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "学习中心",
                        summary = "我是摘要",
                        visibleSummary = true,
                        layoutHeight = R.dimen.setting_row_layout_height_2,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleDivider = true,
                        visibleIcon = false,
                        switchValue =  repository.mSwitchData1
                )
        )

        DataStore.instance().sharedPreferences.registerOnSharedPreferenceChangeListener(repository)

    }

    companion object{
        const val TAG = "SettingViewModel"
    }
}