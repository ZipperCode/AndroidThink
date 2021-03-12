package com.think.hook

import android.app.Application
import android.content.ContentProvider
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import java.lang.Exception
import java.lang.reflect.Method

object Hook {


    private val atCls: Class<*> = ReflectHelper.deepForName("android.app.ActivityThread")!!

    private var at: Any? = null

    init {
        val field = ReflectHelper.deepFindField(atCls, "sCurrentActivityThread")
        at = field?.get(null)
    }


    fun hookClassLoader(context: Context, dexPath: String, librarySearchPath: String) {
        val apkClassLoader = ApkClassLoader(dexPath, librarySearchPath, context.classLoader)
    }

    fun getPluginLoadApk(context: Context, apkPath: String): Any? {
        val packageArchiveInfo = context.packageManager.getPackageArchiveInfo(apkPath, 0)
        return packageArchiveInfo?.let {
            val cls = ReflectHelper.deepForName("android.content.res.CompatibilityInfo")!!
            val field = ReflectHelper.deepFindField(cls, "DEFAULT_COMPATIBILITY_INFO")
            val getPackageInfoNoCheckMethod = ReflectHelper.deepFindMethod(atCls,
                    "getPackageInfoNoCheck",
                    ApplicationInfo::class.java,
                    cls
            )
            getPackageInfoNoCheckMethod?.invoke(atCls, it.applicationInfo, field?.get(null))
        }
    }


    object ApplicationHook {

        fun replaceDelegateApplication(application: Application, targetAppClassName: String) {
            try {
                /* 模拟 handleBindApplication 中App的创建执行过程 */
                // 模拟Instrumentation中newApplication的过程
                val attachMethod = ReflectHelper.deepFindMethod(
                        Application::class.java,
                        "attach",
                        Context::class.java
                ) as Method
                val newAppCls = Class.forName(targetAppClassName, true, application.classLoader)
                val app = newAppCls.newInstance() as Application
                attachMethod.invoke(app, application.baseContext)

                // LoadedApk中的makeApplication中app创建好后
                // appContext.setOuterContext(app);

                val setOuterContextMethod = ReflectHelper.deepFindMethod(
                        application.baseContext.javaClass,
                        "setOuterContext",
                        Context::class.java
                )!!
                // 反射替换成新创建的Application
                setOuterContextMethod.invoke(application.baseContext, app)

                // 接下来是  mActivityThread.mAllApplications.add(app);

                val mAllApplicationsField = ReflectHelper.deepFindField(atCls, "mAllApplications")!!
                val apps: MutableList<Application> = mAllApplicationsField.get(at) as ArrayList<Application>
                apps.remove(application)
                apps.add(app)

                // LoadedApk中mApplication属性的替换 [mApplication = app;]

                // 首先要获取LoadedApk对象
                val mPackageInfoField = ReflectHelper.deepFindField(
                        application.baseContext.javaClass,
                        "mPackageInfo"
                )!!
                val loadedApk = mPackageInfoField.get(application.baseContext)
                val loadedApkCls = ReflectHelper.deepForName("android.app.LoadedApk")!!
                val mApplicationField = ReflectHelper.deepFindField(loadedApkCls, "mApplication")!!
                mApplicationField.set(loadedApk, app)

                // 在Application创建过程中，onCreate调用之前，安装了ContentProvider
                // 而ContentProvider中的context属于Application，需要替换
                val mProviderMapField = ReflectHelper.deepFindField(atCls,"mProviderMap")!!
                val mProviderMap = mProviderMapField.get(at) as Map<*, *>

                for (entry in mProviderMap){
                    val localField = ReflectHelper.deepFindField(entry.value!!.javaClass,"mLocalProvider")
                    val contentProvider = localField?.get(entry.value)
                    val mContextField = ReflectHelper.deepFindField(ContentProvider::class.java, "mContext")
                    contentProvider?.run {
                     mContextField!!.set(this,app)
                    }
                }


                // 调用Application的onCreate
                app.onCreate()

                Log.d("ApplicationHook","replace Application success")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
