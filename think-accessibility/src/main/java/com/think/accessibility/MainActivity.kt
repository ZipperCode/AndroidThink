package com.think.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Flowable
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    private var mAppInfoList: MutableList<AppInfo> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private lateinit var mAppInAdapter: AppInfoAdapter

    private lateinit var flControl: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rv_list)
        flControl = findViewById(R.id.fb_control)

        mAppInAdapter = AppInfoAdapter(this, mAppInfoList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAppInAdapter

        flControl.setOnClickListener {
            if (!isAccessibilitySettingsOn(this, MyAccessibilityService::class.java)) {
                AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("检测到无障碍服务未打开，请前往设置打开")
                        .setTitle("提示")
                        .setNegativeButton("去设置") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        }.show()
            }else{
                Toast.makeText(this,"您已经拥有权限了",Toast.LENGTH_LONG).show()
            }
        }

        runOnUiThread{
            AppUtils.getLaunch(this,mAppInfoList)
            mAppInAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this,"点击浮动按钮检测无障碍权限",Toast.LENGTH_LONG).show()
    }

    private fun isAccessibilitySettingsOn(mContext: Context, clazz: Class<out AccessibilityService?>): Boolean {
        var accessibilityEnabled = 0
        val service = mContext.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }
}