package com.think.ui.state

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import com.think.ui.R
import java.util.*

class StateLayoutActivity : AppCompatActivity(), OnStateChangedListener {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLayout: StateLayout by lazy {
        findViewById<StateLayout>(R.id.sl_container)
    }

    private var errorStateView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_layout)
        stateLayout.onStateChangedListener = this
        loadData()
    }

    private fun loadData() {
        stateLayout.showLoading()
        handler.postDelayed({
            if (Random().nextBoolean()) {
                stateLayout.showContent()
            }
        }, 5000)
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

    override fun onStateChanged(uiState: UiState, stateView: View, firstInflate: Boolean) {
        if (uiState == UiState.Error) {
            errorStateView = stateView
            stateView.findViewById<Button>(R.id.btn_retry).setOnClickListener {
                stateLayout.showLoading()
                handler.postDelayed({
                    stateLayout.showContent()
                }, 3000)
            }
        }
    }
}