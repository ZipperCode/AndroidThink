package com.think.jetpack.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.think.jetpack.R
import com.think.jetpack.databinding.LayoutSettingMenuBinding
import com.think.jetpack.databinding.LayoutSettingSwitchBinding

class SettingAdapter<DATA : BaseData>(private val mContext: Context,
                     private var mData: MutableList<DATA>
) : RecyclerView.Adapter<SettingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        val binding = when(viewType){
            VIEW_TYPE_MENU -> {
                DataBindingUtil.inflate<LayoutSettingMenuBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_menu, parent, false)
            }
            VIEW_TYPE_SWITCH -> {
                DataBindingUtil.inflate<LayoutSettingSwitchBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_menu, parent, false)
            }
            else -> {
                DataBindingUtil.inflate<LayoutSettingMenuBinding>(LayoutInflater.from(mContext),
                        R.layout.layout_setting_menu, parent, false)
            }
        }

        return SettingHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        if (holder.viewType == VIEW_TYPE_MENU) {
            val binding = holder.binding as LayoutSettingMenuBinding
            binding.rowData = mData[position] as MenuData
        } else if(holder.viewType == VIEW_TYPE_SWITCH){
            val binding = holder.binding as LayoutSettingSwitchBinding
            binding.rowData = mData[position] as SwitchData
        }
    }

    override fun getItemViewType(position: Int): Int {
        val baseData = mData[position]
        return if(baseData is MenuData){
            VIEW_TYPE_MENU
        }else if(baseData is SwitchData){
            VIEW_TYPE_SWITCH
        }else{
            0
        }
    }

    override fun onViewRecycled(holder: SettingHolder) {
        holder.binding.unbind()
    }

    override fun getItemCount(): Int = mData.size

    companion object {
        const val VIEW_TYPE_MENU = 1
        const val VIEW_TYPE_SWITCH = 2

    }
}

class SettingHolder(var binding: ViewDataBinding, val viewType: Int)
    : RecyclerView.ViewHolder(binding.root)