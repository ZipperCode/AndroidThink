package com.think.jetpack.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.think.jetpack.R
import com.think.jetpack.preference.DataStore
import com.think.jetpack.preference.SettingsActivity
import com.think.jetpack.preference.ui.login.LoginViewModel
import com.think.jetpack.preference.ui.login.LoginViewModelFactory

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