package com.think.hook

import android.os.Build
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectHelper {

    val forNameMethod: Method = Class::class.java.getDeclaredMethod("forName", String::class.java)

    val getDeclaredMethod: Method = Class::class.java.getDeclaredMethod(
            "getDeclaredMethod",
            String::class.java,
            arrayOf<Class<*>>()::class.java
    )
    val getDeclaredFieldMethod: Method = Class::class.java.getDeclaredMethod(
            "getDeclaredField",
            String::class.java
    )


    init {

//        HiddenApiUnSeal.unseal()
    }


    fun forName(className: String): Class<*>?{
        var clazz: Class<*>? = null
        try{
            clazz = Class.forName(className)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return clazz
    }

    fun deepForName(className: String): Class<*>?{
        var clazz: Class<*>? = null
        try{
            clazz = forNameMethod.invoke(null,className) as Class<*>
        }catch (e:Exception){
            e.printStackTrace()
        }
        return clazz
    }

    fun findMethod(targetClass: Class<*>, name: String, vararg args:Class<*>): Method?{
        var result: Method? = null
        try {
            var clazz: Class<*>? = targetClass
            while (clazz != null) {
                try {
                    result = clazz.getDeclaredMethod(name, *args)
                    if (!result.isAccessible) {
                        result.isAccessible = true
                    }
                    return result
                } catch (e: NoSuchMethodException) {
                    // ignore and search next
                }
                clazz = clazz.superclass
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return result
    }

    fun deepFindMethod(targetClass: Class<*>, name: String, vararg args:Class<*>): Method?{
        var result: Method? = null
        try {
            var clazz: Class<*>? = targetClass
            while (clazz != null) {
                try {
                    result = getDeclaredMethod.invoke(clazz, name, args) as Method
                    if (!result.isAccessible) {
                        result.isAccessible = true
                    }
                    return result
                } catch (e: NoSuchMethodException) {
                    // ignore and search next
                }
                clazz = clazz.superclass
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return result
    }


    fun findField(targetClass: Class<*>, name: String): Field?{
        var result: Field? = null
        try {
            var clazz: Class<*>? = targetClass
            while (clazz != null) {
                try {
                    result = clazz.getDeclaredField(name) as Field
                    if (!result.isAccessible) {
                        result.isAccessible = true
                    }
                    return result
                } catch (e: NoSuchMethodException) {
                    // ignore and search next
                }
                clazz = clazz.superclass
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
        return result
    }


    fun deepFindField(targetClass: Class<*>, name: String): Field?{
        var result: Field? = null
        try {
            var clazz: Class<*>? = targetClass
            while (clazz != null) {
                try {
                    result = getDeclaredFieldMethod.invoke(clazz, name) as Field
                    if (!result.isAccessible) {
                        result.isAccessible = true
                    }
                    return result
                } catch (e: NoSuchMethodException) {
                    // ignore and search next
                }
                clazz = clazz.superclass
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return result
    }

    private fun checkVersionUp(): Boolean = Build.VERSION.SDK_INT > Build.VERSION_CODES.P

}
