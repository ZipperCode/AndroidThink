package com.think.hook

import android.app.Application
import dalvik.system.BaseDexClassLoader

class RealApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mApplicationContext = this
        hook()
    }

    companion object{
        private  var mApplicationContext: Application? = null
    }

    private fun hook(){
        val libPaths: Collection<String> = listOf(packageResourcePath)
        val pathListField = ReflectHelper.deepFindField(BaseDexClassLoader::class.java, "pathList")
        val pathListSelf = pathListField?.get(classLoader)
        if(pathListSelf != null){
            ReflectHelper.deepFindMethod(pathListSelf.javaClass, "addNativePath",Collection::class.java)
                    ?.invoke(pathListSelf,libPaths)
        }
    }
//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(base)
//        Log.d(TAG,"attachBaseContext base = $base")
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.d(TAG,"onCreate")
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        Log.d(TAG,"onConfigurationChanged")
//    }
//
//    override fun onTerminate() {
//        super.onTerminate()
//        Log.d(TAG,"onTerminate")
//    }
//
//
//    override fun onTrimMemory(level: Int) {
//        super.onTrimMemory(level)
//        Log.d(TAG,"onTrimMemory")
//    }
//
//    companion object{
//        private val TAG: String = RealApplication::class.java.simpleName
//    }
}