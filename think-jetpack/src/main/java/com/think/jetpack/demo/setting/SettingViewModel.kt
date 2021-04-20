package com.think.jetpack.demo.setting

import androidx.lifecycle.ViewModel
import com.think.jetpack.R
import com.think.jetpack.demo.setting.data.SettingRepository
import com.think.jetpack.demo.setting.data.SettingSource
import com.think.jetpack.demo.setting.BaseData
import com.think.jetpack.demo.setting.MenuData
import com.think.jetpack.demo.setting.SelectData
import com.think.jetpack.demo.setting.SwitchData
import com.think.jetpack.demo.setting.data.Gender
import com.think.jetpack.preference.DataStore

class SettingViewModel : ViewModel() {

    private val repository: SettingRepository = SettingRepository(SettingSource())


    val mList: MutableList<BaseData>

    init {
        mList = arrayListOf(
                HeaderData(
                        imageUrl = "",
                        username = "咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜咕噜",
                        gender = Gender.MALE,
                        visibleCategorySeparator = true
                ),
                SelectData(
                        icon = android.R.drawable.ic_lock_idle_lock,
                        title = "家长中心",
                        visibleSummary = false,
                        layoutHeight = R.dimen.setting_row_layout_height,
                        arrow = R.drawable.button_arrow_right_yellow,
                        visibleArrow = true,
                        visibleIcon = true,
                        visibleDivider = true,
                        selectValue = repository.mSelectValue,
                        actionType = ActionType.ACTION_1,
                        visibleCategorySeparator = true
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
                        visibleIcon = true,
                        visibleCategorySeparator = true
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
                        switchValue = repository.mSwitchData1
                )
        )

        DataStore.instance().sharedPreferences.registerOnSharedPreferenceChangeListener(repository)

    }

    companion object {
        const val TAG = "SettingViewModel"
    }
}