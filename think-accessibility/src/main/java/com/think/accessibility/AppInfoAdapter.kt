package com.think.accessibility

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppInfoAdapter(context: Context, appList: List<AppInfo>): RecyclerView.Adapter<AppInfoAdapter.Companion.AppHolder>() {

    private var mContext: Context = context

    private var mAppList: List<AppInfo> = appList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_app_info, parent, false)
        return AppHolder(view)
    }

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        holder.ivAppIcon.setImageDrawable(mAppList[position].icon)

        holder.tvAppName.text = mAppList[position].appName
    }

    override fun getItemCount(): Int {
        return mAppList.size
    }

    companion object{
        class AppHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var ivAppIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
            var tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)

        }
    }
}

