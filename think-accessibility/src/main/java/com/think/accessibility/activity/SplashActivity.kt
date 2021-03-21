package com.think.accessibility.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.think.accessibility.R
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.AppUtils
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {

            val result1 = async(Dispatchers.IO) {
                AccessibilityUtil.init(this@SplashActivity)
            }
            val result2 = async(Dispatchers.IO){
                AppUtils.getLaunch(this@SplashActivity,AccessibilityUtil.mMainAppInfo)
            }
            result1.await()
            result2.await()

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}