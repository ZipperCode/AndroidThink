package com.think.core.fragment2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.Fragment
import java.lang.Exception

/**
 * 延迟加载的view
 */
abstract class BaseLazyFragment : Fragment(), AsyncLayoutInflater.OnInflateFinishedListener {

    private lateinit var mContainer: ViewGroup

    private lateinit var asyncLayoutInflater: AsyncLayoutInflater

    protected open fun generateContainer(): ViewGroup {
        return FrameLayout(requireContext())
    }

    abstract fun layoutResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::asyncLayoutInflater.isInitialized){
            asyncLayoutInflater = AsyncLayoutInflater(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mContainer.isInitialized) {
            mContainer = generateContainer()
        }

        val layoutId = layoutResId()
        mContainer.tag = savedInstanceState
        if (layoutId > 0) {
            asyncLayoutInflater.inflate(layoutId, mContainer, this)
        }
        onViewLazyCreatedBefore()
        return mContainer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mContainer.removeAllViews()
    }

    @Deprecated(
        "", ReplaceWith(
            "super.onViewCreated(view, savedInstanceState)",
            "androidx.fragment.app.Fragment"
        )
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 异步创建view之前的操作
     * 可以进行数据的初始化操作
     */
    protected open fun onViewLazyCreatedBefore(){

    }

    /**
     * 异步view创建成功之后的回调
     * 可填充数据到view或者设置view的监听
     */
    protected open fun onViewLazyCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
        val data = mContainer.tag
        var savedInstanceState: Bundle? = null
        try {
            if (data != null){
                savedInstanceState = data as? Bundle
                mContainer.tag = null
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        onViewLazyCreated(view, savedInstanceState)
    }
}