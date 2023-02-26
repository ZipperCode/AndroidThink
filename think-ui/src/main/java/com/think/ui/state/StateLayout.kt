package com.think.ui.state

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.think.ui.R

sealed class UiState (@IdRes val viewId: Int){
    object Empty : UiState(R.id.state_layout_empty_id)
    object Loading: UiState(R.id.state_layout_loading_id)
    object Content: UiState(R.id.state_layout_empty_id)
    object Error: UiState(R.id.state_layout_loading_id)
}

const val NONE_STATE_ID = View.NO_ID


interface OnStateChangedListener{
    fun onStateChanged(uiState: UiState, stateView: View, firstInflate: Boolean)
}

class StateLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), ViewStub.OnInflateListener {

    private val stateMap: MutableMap<UiState, View> = mutableMapOf()

    var onStateChangedListener: OnStateChangedListener? = null

    var currentState: UiState = UiState.Empty

    val isLoading: Boolean get() = currentState == UiState.Loading

    val isError: Boolean get() = currentState == UiState.Error

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        val contentId = typedArray.getResourceId(R.styleable.StateLayout_state_layout_content_ref, NONE_STATE_ID)
        val loadingId = typedArray.getResourceId(R.styleable.StateLayout_state_layout_loading_ref, NONE_STATE_ID)
        val emptyId = typedArray.getResourceId(R.styleable.StateLayout_state_layout_empty_ref, NONE_STATE_ID)
        val errorId = typedArray.getResourceId(R.styleable.StateLayout_state_layout_error_ref, NONE_STATE_ID)

        if (contentId != NONE_STATE_ID){
            stateMap[UiState.Content] = ViewStub(context, contentId)
        }

        if (loadingId != NONE_STATE_ID){
            stateMap[UiState.Loading] = ViewStub(context, loadingId)
        }

        if (emptyId != NONE_STATE_ID){
            stateMap[UiState.Empty] = ViewStub(context, emptyId)
        }

        if (errorId != NONE_STATE_ID){
            stateMap[UiState.Error] = ViewStub(context, errorId)
        }
        setState(UiState.Empty)
        typedArray.recycle()
    }

    fun register(state: UiState, view: View){
        stateMap[state] = view
    }

    fun setState(state: UiState){
        val view = stateMap[state] ?: return
        if (currentState == state){
            return
        }
        var realView = view
        var firstInflate = false
        if (view is ViewStub){
            view.setOnInflateListener(this)
            if (view.parent == null){
                addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
            val inflateView = view.inflate()
            stateMap[state] = inflateView
            realView = inflateView
            firstInflate = true
        }
        removeAllViews()
        currentState = state
        addView(realView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        realView.visibility = View.VISIBLE
        onStateChangedListener?.onStateChanged(state, realView, firstInflate)
    }

    override fun onInflate(stub: ViewStub, inflated: View) {
        Log.d("StateLayout", "onInflate View stub = $stub inflate = $inflated")
    }

    fun setContent(view: View){
        register(UiState.Content, view)
    }

    fun setContent(@LayoutRes layoutId: Int){
        register(UiState.Content, ViewStub(context, layoutId))
    }

    fun setLoading(view: View){
        register(UiState.Loading, view)
    }

    fun setLoading(@LayoutRes layoutId: Int){
        register(UiState.Loading, ViewStub(context, layoutId))
    }

    fun setEmpty(view: View){
        register(UiState.Empty, view)
    }

    fun setEmpty(@LayoutRes layoutId: Int){
        register(UiState.Empty, ViewStub(context, layoutId))
    }

    fun setError(view: View){
        register(UiState.Error, view)
    }

    fun setError(@LayoutRes layoutId: Int){
        register(UiState.Error, ViewStub(context, layoutId))
    }

    fun showContent(){
        setState(UiState.Content)
    }

    fun showLoading(){
        setState(UiState.Loading)
    }

    fun showEmpty(){
        setState(UiState.Empty)
    }

    fun showError(){
        setState(UiState.Error)
    }

    fun<T: View> findContentView():T?{
        return findViewById<T>(R.id.state_layout_content_id)
    }

    fun<T: View> findLoadingView():T?{
        return findViewById<T>(R.id.state_layout_loading_id)
    }

    fun<T: View> findEmptyView():T?{
        return findViewById<T>(R.id.state_layout_empty_id)
    }

    fun<T: View> findErrorView():T?{
        return findViewById<T>(R.id.state_layout_error_id)
    }

    fun<T: View> getInflateContentView(): T?{
        val view = stateMap[UiState.Content]
        if (view == null || view is ViewStub){
            return null
        }
        return view as T
    }
}