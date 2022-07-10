package com.think.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater

abstract class BaseLazyFrameLayout @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    styleDef: Int = 0
) : FrameLayout(context, attr, styleDef), AsyncLayoutInflater.OnInflateFinishedListener {

    private val asyncLayoutInflater: AsyncLayoutInflater = AsyncLayoutInflater(context)

    init {
        initSet()
    }

    private fun initSet(){
        asyncLayoutInflater.inflate(layoutResId(), this, this)

    }

    protected open fun onViewLazyCreated(){

    }

    abstract fun layoutResId(): Int


    override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
        onViewLazyCreated()
    }

}