package com.think.accessibility.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.think.accessibility.R
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.utils.AccessibilityUtil
import com.think.accessibility.utils.ThreadManager

class ViewInfoAdapter(context: Context, data: MutableList<ViewInfo>)
    : RecyclerView.Adapter<ViewInfoAdapter.Companion.ViewInfoHolder>(),
    RecycleItemTouchCallback.Companion.OnItemTouchListener
{

    private val mContext: Context = context

    private val mData: MutableList<ViewInfo> = data


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewInfoHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.layout_view_info_item, parent, false)
        return ViewInfoHolder(view)
    }
    override fun onBindViewHolder(holder: ViewInfoHolder, position: Int) {
        holder.tvViewId.text = mData[position].viewId
        holder.tvViewRect.text = mData[position].screenRect.toString()
    }

    override fun getItemCount() = mData.size


    companion object{
        class ViewInfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val tvViewId: TextView = itemView.findViewById(R.id.tv_view_id)
            val tvViewRect: TextView = itemView.findViewById(R.id.tv_view_rect)
        }
    }

    override fun onSwiped(position: Int) {
        val removeItem = mData.removeAt(position)
        ThreadManager.runOnSub{ AccessibilityUtil.deleteViewInfo(removeItem)}
        notifyDataSetChanged()
    }
}