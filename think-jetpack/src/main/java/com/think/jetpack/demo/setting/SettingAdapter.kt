package com.think.jetpack.demo.setting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.think.jetpack.R
import com.think.jetpack.databinding.LayoutSettingMenuBinding
import com.think.jetpack.databinding.LayoutSettingSelectBinding
import com.think.jetpack.databinding.LayoutSettingSwitchBinding
import com.think.jetpack.databinding.LayoutSettingUserHeaderBinding

class SettingAdapter<DATA : BaseData>(private val mContext: Context,
                                      private var mData: MutableList<DATA>
) : RecyclerView.Adapter<SettingHolder>() {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        val binding = when (viewType) {
            VIEW_TYPE_HEADER -> {
                DataBindingUtil.inflate<LayoutSettingUserHeaderBinding>(mLayoutInflater,
                        R.layout.layout_setting_user_header, parent, false)
            }
            VIEW_TYPE_SWITCH -> {
                DataBindingUtil.inflate<LayoutSettingSwitchBinding>(mLayoutInflater,
                        R.layout.layout_setting_switch, parent, false)
            }
            VIEW_TYPE_SELECT -> {
                DataBindingUtil.inflate<LayoutSettingSelectBinding>(mLayoutInflater,
                        R.layout.layout_setting_select, parent, false)
            }
            else -> {
                DataBindingUtil.inflate<LayoutSettingMenuBinding>(mLayoutInflater,
                        R.layout.layout_setting_menu, parent, false)
            }
        }

        return SettingHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        when (holder.viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = holder.binding as LayoutSettingUserHeaderBinding
                binding.apply {
                    rowData = mData[position] as HeaderData
                }
            }
            VIEW_TYPE_MENU -> {
                val binding = holder.binding as LayoutSettingMenuBinding
                binding.apply {
                    rowData = mData[position] as MenuData
                    handler = EventHandler()
                }
            }
            VIEW_TYPE_SWITCH -> {
                val binding = holder.binding as LayoutSettingSwitchBinding
                binding.apply {
                    rowData = mData[position] as SwitchData
                    handler = EventHandler()
                }
            }
            VIEW_TYPE_SELECT -> {
                val binding = holder.binding as LayoutSettingSelectBinding
                binding.apply {
                    rowData = mData[position] as SelectData
                    binding.handler = EventHandler()
                }
            }
        }

        val layoutHeight: Int = mData[position].layoutHeight
//        if(layoutHeight != 0){
//            holder.itemView.layoutParams.height = mContext.resources.getDimensionPixelSize(mData[position].layoutHeight)
//        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (mData[position]) {
            is HeaderData -> {
                VIEW_TYPE_HEADER
            }
            is SwitchData -> {
                VIEW_TYPE_SWITCH
            }
            is SelectData -> {
                VIEW_TYPE_SELECT
            }
            else -> {
                VIEW_TYPE_MENU
            }
        }
    }

    override fun onViewRecycled(holder: SettingHolder) {
        holder.binding.unbind()
    }

    override fun getItemCount(): Int = mData.size

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_MENU = 1
        const val VIEW_TYPE_SWITCH = 2
        const val VIEW_TYPE_SELECT = 3
        const val TAG = "SettingAdapter"
    }
}
