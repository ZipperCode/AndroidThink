package com.think.accessibility.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.think.accessibility.AppHelper
import com.think.accessibility.bean.AppInfo
import com.think.accessibility.adapter.AppInfoAdapter
import com.think.accessibility.utils.AppUtils
import com.think.accessibility.R
import com.think.accessibility.service.MyAccessibilityService
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.ThreadManager

class MainActivity : AppCompatActivity() {
    private var mAppInfoList: MutableList<AppInfo> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private lateinit var mAppInAdapter: AppInfoAdapter

    private lateinit var flControl: FloatingActionButton

    private lateinit var searchView: SearchView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rv_list)
        flControl = findViewById(R.id.fb_control)

        mAppInAdapter = AppInfoAdapter(this, mAppInfoList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAppInAdapter

        flControl.setOnClickListener {

        }

        runOnUiThread{
            AccessibilityUtil.pksInit(this)
            val appList = ArrayList<AppInfo>()
            AppUtils.getLaunch(this, appList)
            mAppInAdapter.setData(appList)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!AppUtils.isAccessibilitySettingsOn(this, MyAccessibilityService::class.java)) {
            Toast.makeText(this,"您还未开启无障碍权限",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"您已经拥有权限了",Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_act_menu,menu)
        menu?.also {
            val item = it.findItem(R.id.menu_search)
            searchView = item.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    mAppInAdapter.filter.filter(newText)
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_setting -> {
                Toast.makeText(this,"Setting",Toast.LENGTH_LONG).show()
                if (!AppUtils.isAccessibilitySettingsOn(this, MyAccessibilityService::class.java)) {
//                AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("检测到无障碍服务未打开，请前往设置打开")
//                        .setTitle("提示")
//                        .setNegativeButton("去设置") { dialogInterface, _ ->
//                            dialogInterface.dismiss()
//
//                        }.show()
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }else{
                    Toast.makeText(this,"您已经拥有权限了",Toast.LENGTH_LONG).show()
                }
            }
            R.id.menu_help ->{
                Toast.makeText(this,"Help",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,TestWebActivity::class.java))
            }
        }
        return true
    }



    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }
}