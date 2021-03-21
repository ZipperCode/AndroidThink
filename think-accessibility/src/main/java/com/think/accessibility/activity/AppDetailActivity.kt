package com.think.accessibility.activity

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.think.accessibility.R
import com.think.accessibility.adapter.RecycleItemTouchCallback
import com.think.accessibility.adapter.ViewInfoAdapter
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.room.DBHelper
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.SpHelper
import com.think.accessibility.utils.ThreadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppDetailActivity : AppCompatActivity() {

    private lateinit var mViewInfoListView: RecyclerView

    private lateinit var mViewInfoAdapter: ViewInfoAdapter

    private val mPackageViewInfo: MutableList<ViewInfo> = ArrayList()

    private lateinit var mPackageName: String

    private lateinit var mEtDumpName: EditText
    private lateinit var mBtnSetDumpName: Button

    private var mSettingLastTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        val toolBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        mViewInfoListView = findViewById(R.id.rv_view_info_list)
        mViewInfoAdapter = ViewInfoAdapter(this, mPackageViewInfo)
        mViewInfoListView.layoutManager = LinearLayoutManager(this)
        mViewInfoListView.adapter = mViewInfoAdapter
        mViewInfoListView.addItemDecoration(DividerItemDecoration(this, 1))

        val touchCallback = RecycleItemTouchCallback(mViewInfoAdapter)
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(mViewInfoListView)

        mBtnSetDumpName = findViewById(R.id.btn_set_dump_name)
        mEtDumpName = findViewById(R.id.et_dump_name)

        mBtnSetDumpName.setOnClickListener {
            if (TextUtils.isEmpty(mBtnSetDumpName.text)) {
                return@setOnClickListener
            }
            val nowTime = System.nanoTime()
            if (nowTime - mSettingLastTime < 2000) {
                return@setOnClickListener
            }
            mSettingLastTime = nowTime
            // 保存设置的文字
            CoroutineScope(Dispatchers.IO).launch {
                SpHelper.saveString(mPackageName,mEtDumpName.text.toString())
            }
        }

        val bundle = intent.extras
        if (bundle != null) {
            val title = bundle.getString("title", "")
            mPackageName = bundle.getString("packageName", "")
            supportActionBar?.title = "$title:$mPackageName"
            mPackageViewInfo.clear()

            CoroutineScope(Dispatchers.Main).launch {
                val dumpName = withContext(Dispatchers.IO){
                    mPackageViewInfo.addAll(AccessibilityUtil.packageViewInfoList(mPackageName))
                    return@withContext SpHelper.loadString(mPackageName)
                }
                mEtDumpName.setText(if(TextUtils.isEmpty(dumpName)) "跳过" else dumpName)
                mViewInfoAdapter.notifyDataSetChanged()
            }

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}