package com.think.jetpack.preference

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.think.jetpack.R
import com.think.jetpack.databinding.LayoutSettingRowBinding

class RowPreference : Preference {

    companion object {
        private const val TAG = "RowPreference"
        const val MENU: Int = 1
        const val SWITCH: Int = 2
    }

    /**
     * Row布局的高度
     */
    private var mLayoutHeight: Int = 0

    /**
     * 右边箭头资源id
     */
    private var mArrowResId: Int = 0

    private var mShowArrow: Boolean = false

    private var mShowSummary: Boolean = false

    private var mShowUnderDivider: Boolean = false

    private var mRowType: Int = MENU

    private var mSwitchKey: String = ""

    private var mSelectValueKey: String = ""

    private lateinit var mSettingRowData: SettingRowData

    private var mRowBinding: LayoutSettingRowBinding? = null

    private val switchStoreKey: String get() = "${key}_$mSwitchKey"

    private val selectStoreKey: String get() = "${key}_$mSelectValueKey"

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RowPreference);
        mArrowResId = typedArray.getResourceId(R.styleable.RowPreference_arrow, R.drawable.button_arrow_right_yellow)
        mShowArrow = typedArray.getBoolean(R.styleable.RowPreference_showArrow, false)
        mShowSummary = typedArray.getBoolean(R.styleable.RowPreference_showSummary, false)
        mShowUnderDivider = typedArray.getBoolean(R.styleable.RowPreference_showUnderDivider, false)
        mLayoutHeight = typedArray.getDimensionPixelSize(R.styleable.RowPreference_layoutHeight,
                context.resources.getDimensionPixelSize(R.dimen.setting_row_layout_height))
        mRowType = typedArray.getInt(R.styleable.RowPreference_rowType, 1);
        mSwitchKey = typedArray.getString(R.styleable.RowPreference_switchKey) ?: ""
        mSelectValueKey = typedArray.getString(R.styleable.RowPreference_selectValueKey) ?: ""
        typedArray.recycle()

        init()
    }

    constructor(context: Context) : super(context) {

    }

    private fun init() {
        initData()
        initListener()
    }

    private fun initData() {
        var switchValue: ObservableBoolean? = null
        var selectValue: ObservableField<String>? = null
        preferenceDataStore?.run {
            switchValue = ObservableBoolean(getBoolean("${key}_$mSwitchKey", false))
            selectValue = ObservableField(getString("${key}_$mSelectValueKey", "")!!)
        }

        mSettingRowData = SettingRowData(
                key = key ?: "",
                icon = icon
                        ?: AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background)!!,
                title = title?.toString() ?: "",
                summary = summary?.toString() ?: "",
                arrow = AppCompatResources.getDrawable(context, mArrowResId)!!,
                showSummary = mShowSummary,
                visibleIcon = isIconSpaceReserved,
                visibleDivider = mShowUnderDivider,
                showArrow = mShowArrow,
                switchValue = switchValue ?: ObservableBoolean(false),
                selectValue = selectValue ?: ObservableField("")
        )
    }

    override fun onClick() {
        super.onClick()
        Log.d(TAG,"onClick")
    }

    private fun initListener() {
        mSettingRowData.apply {
            switchValue.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if(!TextUtils.isEmpty(mSwitchKey)){
                        preferenceDataStore?.putBoolean(switchStoreKey, (sender as ObservableBoolean).get())
                    }
                }
            })

            selectValue.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if(!TextUtils.isEmpty(mSelectValueKey)){
                        preferenceDataStore?.putString(switchStoreKey, (sender as ObservableField<*>).get() as String)
                    }
                }
            })
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        Log.d(TAG, "onBindViewHolder")
        if (layoutResource != 0 && mRowBinding == null) {
            mRowBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    layoutResource, holder?.itemView as ViewGroup, true)
            holder.itemView.layoutParams?.height = mLayoutHeight
        }

        mRowBinding!!.run {
            rowData = mSettingRowData
            clContainer.layoutParams?.height = mLayoutHeight
            clContainer.apply {
                background = AppCompatResources.getDrawable(context,R.drawable.while_ripple)
                isClickable = true
                isFocusable = true
            }
            if (mRowType == SWITCH) {
                scValue.visibility = View.VISIBLE
                ivArrow.visibility = View.GONE
                tvSelectValue.visibility = View.GONE
            } else {
                scValue.visibility = View.GONE
                ivArrow.visibility = View.VISIBLE
                tvSelectValue.visibility = View.VISIBLE
            }
        }
    }

}