package com.think.accessibility.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.think.accessibility.R
import com.think.accessibility.activity.AppDetailActivity
import com.think.accessibility.bean.AppInfo
import com.think.accessibility.service.MyAccessibilityService
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.AppUtils
import com.think.accessibility.utils.ThreadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppInfoAdapter(context: Context, appList: MutableList<AppInfo>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        Filterable {


    private var mContext: Context = context

    private var mAppList: MutableList<AppInfo> = appList

    private val mFilterList: MutableList<AppInfo> = ArrayList()

    init {
        mFilterList.addAll(mAppList)
    }

    fun setData(data: List<AppInfo>) {
        mAppList.clear()
        mFilterList.clear()
        mAppList.addAll(data)
        mFilterList.addAll(mAppList)
        ThreadManager.runOnMain(Runnable { notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_app_info_header, parent, false)
            AppHeaderHolder(view)
        } else {
            val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_app_info, parent, false)
            AppHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, itemPosition: Int) {
        val position = itemPosition - 1
        if (holder is AppHeaderHolder) {
            holder.tvAsStatus.run {
                if (AppUtils.isAccessibilitySettingsOn(mContext, MyAccessibilityService::class.java)) {
                    text = resources.getText(R.string.text_accessibility_status_success)
                    setTextColor(resources.getColor(R.color.text_success))
                } else {
                    text = resources.getText(R.string.text_accessibility_status_failure)
                    setTextColor(resources.getColor(R.color.text_failure))
                }
            }
            val allPks = mFilterList.map { it.pks }
            val enableStatus = AccessibilityUtil.pksContainsAll(allPks)
            holder.swDump.isChecked = enableStatus
            holder.swDump.setOnClickListener { view ->
                if (view is SwitchCompat) {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            if (view.isChecked) {
                                AccessibilityUtil.addPks(allPks)
                            } else {
                                AccessibilityUtil.clearPks()
                            }
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        } else if (holder is AppHolder) {
            holder.ivAppIcon.setImageDrawable(mFilterList[position].icon)
            holder.tvAppName.text = mFilterList[position].appName
            val enableStatus = AccessibilityUtil.pksContains(mFilterList[position].pks)
            holder.swDump.isChecked = enableStatus
            holder.tvStatus.text = if (enableStatus) "Dump运行中" else "未运行"

            holder.swDump.setOnClickListener { view ->
                if (view is SwitchCompat) {
                    holder.tvStatus.text = if (view.isChecked) "Dump运行中" else "未运行"
                    if (view.isChecked) {
                        AccessibilityUtil.addPks(mFilterList[position].pks)
                    } else {
                        AccessibilityUtil.delPks(mFilterList[position].pks)
                    }
                }
            }
            holder.itemView.setOnClickListener {
                Toast.makeText(mContext, "列表项点击", Toast.LENGTH_LONG).show()
                val intent = Intent(mContext, AppDetailActivity::class.java)
                intent.putExtra("title", mFilterList[position].appName)
                intent.putExtra("packageName", mFilterList[position].pks)
                mContext.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener {
                Toast.makeText(mContext, "列表项长按", Toast.LENGTH_LONG).show()
                true
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_CONTENT
    }

    override fun getItemCount(): Int {
        return mFilterList.size + 1
    }

    companion object {

        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1

        class AppHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivAppIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
            var tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)

            var tvStatus: TextView = itemView.findViewById(R.id.tv_status)
            var swDump: SwitchCompat = itemView.findViewById(R.id.sw_dump)
        }

        class AppHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvAsStatus: TextView = itemView.findViewById(R.id.tv_as_status)
            var swDump: SwitchCompat = itemView.findViewById(R.id.sw_dump)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                mFilterList.clear()
                val str = constraint?.toString() ?: ""
                if (TextUtils.isEmpty(str)) {
                    mFilterList.addAll(mAppList)
                } else {
                    for (app in mAppList) {
                        if (app.appName.contains(str)) {
                            mFilterList.add(app)
                        }
                    }
                }
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}

