package com.think.core.dialogfragment

abstract class BaseLoadingDialog: BaseCommonDialog<BaseCommonDialog.Setting>() {

    private val setting: Setting = Setting()

    override fun requireSetting(): Setting = setting

    abstract class Builder: BaseCommonDialog.Builder<Setting>(){

        private val setting: Setting = Setting()

        override fun requireSetting(): Setting = setting

    }
}