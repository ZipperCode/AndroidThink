package com.think.study.live

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.widget.FrameLayout
import com.think.study.PluginActivity
import com.think.study.R
import com.think.study.context.ReflectHelper
import java.io.File
import java.io.FileOutputStream

class LiveCycleActivity : AppCompatActivity() {

    companion object{
        val TAG = LiveCycleActivity::class.java.simpleName;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"onCreate")
        setContentView(R.layout.activity_live_cycle)
//        var liveCycleFragment = LiveCycleFragment();
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.add(liveCycleFragment,liveCycleFragment::class.simpleName)
//        transaction.commit()
//        var liveCycleDialogFragment = LiveCycleDialogFragment()
//        liveCycleDialogFragment.show(supportFragmentManager,LiveCycleDialogFragment::class.simpleName)
//        ReflectHelper.init(this)
//        val layoutInflater = ReflectHelper.getProxyContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)
//        Log.e(TAG,"layoutInflater = $layoutInflater")
//        val c = ReflectHelper.getHiddenConstructor("com.android.internal.policy.PhoneLayoutInflater",Context::class.java)
        Log.e(TAG,"method  = ${ReflectHelper.getHiddenMethod(
                "android.content.pm.ApplicationInfo",
                "setHiddenApiEnforcementPolicy",
                Int::class.java
        )}")
        println()
//        Thread(Runnable {
//            copy()
//            runOnUiThread(Runnable {
//                val drawable = ReflectHelper.getDrawable("lq_icon_logo");
//                val view: View = ReflectHelper.getLayoutView2("floatview_logo") as View
//                Log.e("PluginActivity","view = $view")
//                findViewById<FrameLayout>(R.id.frameLayout).background = drawable
////                startActivity(Intent(
////                        this,
////                        PluginActivity::class.java
////                ))
//            })
//        }).start()
    }

    private fun copy(){
        val path = filesDir.absolutePath + File.separator + "res.apk"
        val file = File(path)
        if(!file.exists()){
            file.createNewFile()
        }

        val open = assets.open("res.apk")
        val ouput = FileOutputStream(path)
        val buffer = ByteArray(1024)
        var len = open.read(buffer)
        while (len > 0){
            ouput.write(buffer,0,len)
            ouput.flush()
            len = open.read(buffer)
        }
        ouput.close()
        open.close()

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        Log.i(TAG,"onCreateContextMenu")
        super.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Log.i(TAG,"onCreateView name = $name")
        return super.onCreateView(name, context, attrs)
    }


    override fun onStart() {
        Log.i(TAG,"onStart")
        super.onStart()
    }


    override fun onResume() {
        Log.i(TAG,"onResume")
        super.onResume()
    }

    override fun onAttachedToWindow() {
        Log.i(TAG,"onAttachedToWindow")
        super.onAttachedToWindow()
    }

    // 屏幕关闭 onPause -> onCreateDescription -> onStop
    // 窗口退出 onPause -> onStop -> onDestroy -> onDetachedFromWindow
    override fun onPause() {
        Log.i(TAG,"onPause")
        super.onPause()
    }

    override fun onCreateDescription(): CharSequence? {
        Log.i(TAG,"onCreateDescription")
        return super.onCreateDescription()
    }

    override fun onStop() {
        Log.i(TAG,"onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG,"onDestroy")
        super.onDestroy()
    }

    override fun onDetachedFromWindow() {
        Log.i(TAG,"onDetachedFromWindow")
        super.onDetachedFromWindow()
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.i(TAG,"onSaveInstanceState")
        super.onSaveInstanceState(outState, outPersistentState)
    }


}
