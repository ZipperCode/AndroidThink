package com.think.ui.state

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.think.ui.R

class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val btnRetry: Button by lazy {
        findViewById<Button>(R.id.btn_retry)
    }

    init {
        inflate(context, R.layout.layout_state_error, this)

    }

    fun setOnRetryListener(listener: OnClickListener) {
        btnRetry.setOnClickListener {
            listener.onClick(it)
        }
    }
}

class StateLayoutCustomViewActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLayout: StateLayout by lazy {
        findViewById<StateLayout>(R.id.sl_container)
    }
    private val errorView: ErrorView by lazy {
        ErrorView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_layout_custom_view)

        stateLayout.setContent(R.layout.layout_state_content)
        stateLayout.setError(errorView)
        stateLayout.setLoading(R.layout.layout_state_loading)
        stateLayout.setEmpty(R.layout.layout_state_empty)

        errorView.setOnRetryListener {
            stateLayout.showLoading()
            handler.postDelayed({
                stateLayout.showContent()
            }, 4000)
        }
    }

    fun showLoading(view: View) {
        stateLayout.showLoading()
    }

    fun showContent(view: View) {
        stateLayout.showContent()
    }

    fun showEmpty(view: View) {
        stateLayout.showEmpty()
    }

    fun showError(view: View) {
        stateLayout.showError()
    }
}