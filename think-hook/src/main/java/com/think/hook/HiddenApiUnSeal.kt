package com.think.hook

import android.os.Build
import android.util.Log
import java.lang.reflect.Method

/**
 * @link https://github.com/tiann/FreeReflection
 */
object HiddenApiUnSeal {

    private val TAG: String = HiddenApiUnSeal::class.java.simpleName

    private var mVmRuntime: Any? = null

    private var mSetHiddenApiExemptions: Method? = null

    init {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            try {
                val forNameMethod = Class::class.java.getDeclaredMethod("forName", String::class.java)
                val getDeclaredMethod = Class::class.java.getDeclaredMethod(
                        "getDeclaredMethod",
                        String::class.java,
                        *emptyArray<Class<*>>()
                )

                val vmRuntimeClass = forNameMethod.invoke(null, "dalvik.system.VMRuntime")

                val getRuntimeMethod = getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime") as Method

                mVmRuntime = getRuntimeMethod.invoke(null)

                mSetHiddenApiExemptions = getDeclaredMethod.invoke(
                        vmRuntimeClass,
                        "setHiddenApiExemptions",
                        arrayOf<Class<*>>(Array<String>::class.java)
                ) as Method
            }catch (e: Throwable){
                Log.e(TAG, "无法反射隐藏信息类")
            }
        }
    }


    fun unseal(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            mSetHiddenApiExemptions?.takeIf { mVmRuntime != null }?.apply {
                try {
                    invoke(mVmRuntime, arrayOf("L"))
                }catch (e: Throwable){
                    Log.e(TAG, "无法解除反射")
                }
            }
        }
    }
}