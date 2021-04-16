package com.think.jetpack.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.think.jetpack.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val list = arrayListOf(
                MenuData(
                        icon = AppCompatResources.getDrawable(this, android.R.drawable.ic_lock_idle_lock)!!,
                        title = "家长中心",
                        arrow = AppCompatResources.getDrawable(this, R.drawable.button_arrow_right_yellow)!!,
                        visibleArrow = true,
                        visibleIcon = true,
                        visibleDivider = true
                ),
                MenuData(
                        icon = AppCompatResources.getDrawable(this, android.R.drawable.ic_lock_idle_lock)!!,
                        title = "学习中心",
                        arrow = AppCompatResources.getDrawable(this, R.drawable.button_arrow_right_yellow)!!,
                        visibleDivider = true,
                        visibleIcon = true
                ),
                SwitchData(
                        icon = AppCompatResources.getDrawable(this, android.R.drawable.ic_lock_idle_lock)!!,
                        title = "学习中心",
                        arrow = AppCompatResources.getDrawable(this, R.drawable.button_arrow_right_yellow)!!,
                        visibleDivider = true,
                        visibleIcon = true,
                        switchValue = ObservableBoolean(false)
                        
                )
        )

        val adapter = SettingAdapter(this, list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }
}