package com.think.study.context

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import java.lang.reflect.Constructor
import java.lang.reflect.Method

object ReflectHelper {

    private val RES_STYLEABLE = "styleable"
    private val RES_DRAWABLE = "drawable"
    private val RES_LAYOUT = "layout"

    private lateinit var proxyContext: ProxyContext

    private var isInit = false


    @JvmStatic
    public fun init(base: Context){
        proxyContext = ProxyContext(base)
        isInit = true
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            val forName = Class::class.java.getDeclaredMethod("forName", String::class.java)
            val getDeclaredMethod = Class::class.java.getDeclaredMethod(
                    "getDeclaredMethod",
                    String::class.java,
                    arrayOf<Class<*>>()::class.java
            )

            val vmRuntimeClass = forName.invoke(
                    null,
                    "dalvik.system.VMRuntime"
            ) as Class<*>
            val getRuntime = getDeclaredMethod.invoke(
                    vmRuntimeClass,
                    "getRuntime",
                    null
            ) as Method
            val setHiddenApiExemptions = getDeclaredMethod.invoke(
                    vmRuntimeClass,
                    "setHiddenApiExemptions",
                    arrayOf(arrayOf<String>()::class.java)
            ) as Method

            val vmRuntime = getRuntime.invoke(null)

            setHiddenApiExemptions.invoke(vmRuntime, arrayOf("L"))
        }
    }

    public fun getProxyContext(): ProxyContext{
        return proxyContext
    }

    public fun getIdentifier(type: String,styleName:String):Int{
        if(!isInit){
            throw IllegalAccessException("尚未调用init方法进行初始化")
        }
        var id = proxyContext.resources.getIdentifier(styleName,type, proxyContext.packageName)
        if(id == 0){
            id = reflect(type,styleName) as Int
        }
        return id;
    }

    public fun getDrawableId(drawableName: String):Int{
        val id = getIdentifier(RES_DRAWABLE,drawableName)
        return if(id == 0) reflect(RES_DRAWABLE,drawableName) as Int else id
    }

    public fun getDrawable(drawableName: String): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            proxyContext.resources.getDrawable(getDrawableId(drawableName), proxyContext.theme)
        }else{
            proxyContext.resources.getDrawable(getDrawableId(drawableName))
        }
    }

    public fun getLayoutId(layoutName: String) : Int{
        val id = getIdentifier(RES_LAYOUT,layoutName)
        return if(id == 0) reflect(RES_LAYOUT,layoutName) as Int else id
    }

    public fun getLayoutView(layoutName:String) : View? {
        return LayoutInflater.from(proxyContext)
                .inflate(getLayoutId(layoutName),null,false)
    }

    public fun getLayoutView2(layoutName:String) : View? {
        val layoutInflater:LayoutInflater =
                proxyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return layoutInflater.inflate(getLayoutId(layoutName),null,false)
    }

    private fun reflect(type: String, name: String):Any{
        val className =  "${proxyContext.packageName}.R${'$'}${type}"
        val clazz = proxyContext.classLoader.loadClass(className)
        val field = clazz?.getField(name);
        field?.isAccessible = true
        var id = 0
        id = field?.get(clazz) as Int
        return id
    }


    @Throws(ClassNotFoundException::class,NoSuchMethodException::class)
    public fun getHiddenMethod(className:String, methodName:String, vararg args: Class<*>):Method?{
        val forNameMethod = Class::class.java.getDeclaredMethod("forName",String::class.java)
        val hiddenClass:Class<*> = forNameMethod.invoke(null,className) as Class<*>
        val classGetClassMethod = Class::class.java.getDeclaredMethod(
                "getDeclaredMethod",
                String::class.java,
                arrayOf<Class<*>>()::class.java
        )
        val forName = Class.forName(className)
        forName.declaredMethods.iterator().forEach {
            println(it.name)
        }
        println("-----------------------")
        forName.declaredFields.iterator().forEach {
            println(it.name)
        }
        println("-----------------------")
        hiddenClass.declaredMethods.iterator().forEach {
            println(it.name)
        }
        println("-----------------------")
        hiddenClass.declaredFields.iterator().forEach {
            println(it.name)
        }
        val hiddenMethod = classGetClassMethod.invoke(hiddenClass,methodName,args) as Method
        return hiddenMethod
    }

    @Throws(ClassNotFoundException::class,NoSuchMethodException::class)
    public fun getHiddenMethod(target: Any, className:String, methodName:String, vararg args: Class<*>):Method?{
        val forNameMethod = Class::class.java.getDeclaredMethod("forName",String::class.java)
        val hiddenClass = forNameMethod.invoke(null,className)
        val classGetClassMethod = Class::class.java.getDeclaredMethod(
                "getDeclaredMethod",
                String::class.java,
                arrayOf<Class<*>>()::class.java
        )
        val hiddenMethod = classGetClassMethod.invoke(target,methodName,args) as Method
        return hiddenMethod
    }

    public fun getHiddenConstructor(className:String, vararg args: Class<*>) : Constructor<*>{
        val hiddenClass = Class.forName(className)

        val classGetClassConstructorMethod
                = Class::class.java.getDeclaredMethod("getDeclaredConstructor")

        val hiddenConstructor = classGetClassConstructorMethod.invoke(hiddenClass,*args) as Constructor<*>

        return hiddenConstructor;

    }

}