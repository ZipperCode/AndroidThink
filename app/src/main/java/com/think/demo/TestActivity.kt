package com.think.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class TestActivity : AppCompatActivity() {

    private val mFreeFlowAppList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    private fun parseInputData(intent: Intent) {
        fullPks()
    }


    private fun fullPks(){
        Log.e("Hook-Pks", "before pks = $mFreeFlowAppList")
        mFreeFlowAppList.addAll(AppUtils.getPackageNames(this))
        Log.e("Hook-Pks", "after pks = $mFreeFlowAppList")
    }
}