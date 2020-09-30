package com.think.study.context

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.Method

class ProxyContext(base: Context?) : ContextWrapper(base) {

    private companion object {
        private val TAG = ProxyContext::class.simpleName

        private val dexFileName = "res.apk";
    }

    private var pluginAssetManager: AssetManager? = null
    private var pluginResource: Resources? = null
    private var pluginResourceTheme: Resources.Theme? = null

    private var dexClassLoader: DexClassLoader? = null;

    init {
        var dexPath = applicationContext.filesDir.absolutePath + File.separator + dexFileName
        val file = File(dexPath)
        if (file.exists()) {
            dexClassLoader = DexClassLoader(
                    dexPath,
                    applicationContext.filesDir.absolutePath,
                    null,
                    baseContext.classLoader
            )
            pluginAssetManager = AssetManager::class.java.getConstructor().newInstance()
            val addAssetMethod: Method = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            addAssetMethod.isAccessible = true;
            if (addAssetMethod.invoke(pluginAssetManager, dexPath) == 0) {
                throw IllegalStateException("无法加载 Assets 资源")
            }
            pluginResource = Resources(pluginAssetManager,
                    applicationContext.resources.displayMetrics, applicationContext.resources.configuration)
            pluginResourceTheme = if(baseContext.theme != null){
                pluginResource!!.newTheme().setTo(baseContext.theme)
                pluginResource!!.newTheme()
            } else{
                pluginResource!!.newTheme()
            }

        }
    }

    @SuppressLint("PrivateApi")
    override fun getSystemService(name: String): Any? {
        if (Context.LAYOUT_INFLATER_SERVICE == name) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                return LayoutInflater.from(baseContext).cloneInContext(this);
            }
            try{
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    val layoutInflaterClass = Class.forName("com.android.internal.policy.PhoneLayoutInflater")
                    val constructor = layoutInflaterClass.getConstructor(Context::class.java)
                    val layoutInflater = constructor.newInstance(this);
//                  return PhoneLayoutInflater(this)
                    return layoutInflater
                }else{
                    val policyClass = Class.forName("com.android.internal.policy.PolicyManager")
                    val addAssetMethod = policyClass.getDeclaredMethod("makeNewLayoutInflater",Context::class.java)
                    return addAssetMethod.invoke(null,this)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return super.getSystemService(name)
    }

    override fun getAssets(): AssetManager {
        return pluginAssetManager!!
    }

    override fun getClassLoader(): ClassLoader {
        return dexClassLoader!!
    }


    override fun getResources(): Resources {
        return pluginResource!!
    }

    override fun getTheme(): Resources.Theme {
        return pluginResourceTheme!!
    }

    override fun getPackageName(): String {
        return "com.think.res"
    }

}