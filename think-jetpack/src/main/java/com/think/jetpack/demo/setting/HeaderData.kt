package com.think.jetpack.demo.setting

import com.think.jetpack.demo.setting.data.Gender

class HeaderData(
        layoutHeight: Int = 0,
        visibleCategorySeparator: Boolean = false,
        val imageUrl: String,
        val username: String,
        val gender: Gender = Gender.UNKNOWN
) : BaseData(
        layoutHeight = layoutHeight,
        visibleCategorySeparator = visibleCategorySeparator
)