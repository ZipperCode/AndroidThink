package com.think.jetpack.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    val imm get() = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    protected fun showKeyboard() {
        val currentFocus = view?.findFocus()
        if (currentFocus != null) {
            imm.showSoftInput(currentFocus, 0)
        } else {
            view?.apply {
                requestFocus()
                requestFocusFromTouch()
                imm.showSoftInput(this, 0)
            }
        }
    }

    protected fun showKeyboard(focusView: View?) {
        focusView?.apply {
            if (!this.isFocused) {
                requestFocus()
                requestFocusFromTouch()
            }
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun hideKeyboard() {
        view?.apply {
            val focusView = findFocus()
            if (focusView == null) {
                imm.hideSoftInputFromWindow(this.windowToken, 0)
            } else {
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            }
        }
    }

    protected fun showToast(msg: String, isLong: Boolean = false) {
        Toast.makeText(requireContext(), msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}