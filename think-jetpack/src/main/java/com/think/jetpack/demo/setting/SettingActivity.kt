package com.think.jetpack.demo.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.think.jetpack.R
import com.think.jetpack.demo.setting.MenuFragment
import com.think.jetpack.preference.DataStore

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        DataStore.init(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, MenuFragment())
                    .commit()
        }
    }
}