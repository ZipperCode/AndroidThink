package com.think.hook

import dalvik.system.PathClassLoader
import java.lang.reflect.Field

class ApkClassLoader(
        dexPath: String,
        librarySearchPath: String,
        parent: ClassLoader
) : PathClassLoader(dexPath, librarySearchPath, parent.parent) {

    private val mOriginClassLoader: ClassLoader = parent

    init {
        val parentField = ClassLoader::class.java.getDeclaredField("parent") as Field
        parentField.isAccessible = true
        parentField.set(mOriginClassLoader,this)
    }

}