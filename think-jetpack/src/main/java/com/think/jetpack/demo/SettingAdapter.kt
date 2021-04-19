package com.think.jetpack.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.think.jetpack.R
import com.think.jetpack.databinding.LayoutSettingMenuBinding
import com.think.jetpack.databinding.LayoutSettingSelectBinding
import com.think.jetpack.databinding.LayoutSettingSwitchBinding

class SettingAdapter<DATA : BaseData>(private val mContext: Context,
                                      private var mData: MutableList<DATA>
) : RecyclerView.Adapter<SettingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        val binding = when (viewType) {
            VIEW_TYPE_SWITCH -> {
                DataBindingUtil.inflate<LayoutSettingSwitchBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_switch, parent, false)
            }
            VIEW_TYPE_SELECT -> {
                DataBindingUtil.inflate<LayoutSettingSelectBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_select, parent, false)
            }
            else -> {
                DataBindingUtil.inflate<LayoutSettingMenuBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_menu, parent, false)
            }
        }

        return SettingHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        when(holder.viewType){
            VIEW_TYPE_MENU -> {
                val binding = holder.binding as LayoutSettingMenuBinding
                binding.rowData = mData[position] as MenuData
            }
            VIEW_TYPE_SWITCH -> {
                val binding = holder.binding as LayoutSettingSwitchBinding
                val rowData = mData[position] as SwitchData
                binding.rowData = rowData
            }
            VIEW_TYPE_SELECT -> {
                val binding = holder.binding as LayoutSettingSelectBinding
                val rowData = mData[position] as SelectData
                binding.rowData = rowData
            }
        }
        val layoutHeight: Int = mData[position].layoutHeight
//        if(layoutHeight != 0){
//            holder.itemView.layoutParams.height = mContext.resources.getDimensionPixelSize(mData[position].layoutHeight)
//        }

    }

    override fun getItemViewType(position: Int): Int {
        val baseData = mData[position]
        return if (baseData is SwitchData) {
            VIEW_TYPE_SWITCH
        } else if(baseData is SelectData){
            VIEW_TYPE_SELECT
        } else {
            VIEW_TYPE_MENU
        }
    }

    override fun onViewRecycled(holder: SettingHolder) {
        holder.binding.unbind()
    }

    override fun getItemCount(): Int = mData.size

    companion object {
        const val VIEW_TYPE_MENU = 1
        const val VIEW_TYPE_SWITCH = 2
        const val VIEW_TYPE_SELECT = 3
        const val TAG = "SettingAdapter"
    }
}
