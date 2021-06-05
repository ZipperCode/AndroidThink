package com.think.jetpack.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    val imm get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    protected fun showKeyboard(){
        if(currentFocus != null){
            imm.showSoftInput(currentFocus, 0)
        }else{
            window?.decorView?.apply {
                requestFocus()
                requestFocusFromTouch()
                imm.showSoftInput(this, 0)
            }
        }
    }

    protected fun showKeyboard(focusView: View?){
        focusView?.apply {
            if(!this.isFocused){
                requestFocus()
                requestFocusFromTouch()
            }
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun hideKeyboard(){
        window?.decorView?.apply {
            val focusView = findFocus()
            if(focusView == null){
                imm.hideSoftInputFromWindow(this.windowToken, 0)
            }else{
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            }
        }
    }

    protected fun showToast(msg: String, isLong: Boolean = false){
        Toast.makeText(this, msg, if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}