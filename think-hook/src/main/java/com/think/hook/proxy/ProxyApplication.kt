package com.think.hook.proxy

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.think.hook.Hook
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.reflect.Type

class ProxyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        Hook.ApplicationHook.replaceDelegateApplication(this, "com.think.hook.RealApplication")

        hook()
    }

    private fun hook(){
        try{
            val inputStream = assets.open("cls.dex")
            val outputFile = File(filesDir,"cls.dex")
            if(!outputFile.exists()){
                outputFile.createNewFile()
            }
            val fos = FileOutputStream(outputFile)
            var byteArray = ByteArray(1024 * 1024)
            var length = inputStream.read(byteArray)
            while (length > 0){
                fos.write(byteArray,0,length)
                fos.flush()
                length = inputStream.read(byteArray)
            }
            fos.close()
            inputStream.close()
            Hook.hookClassLoader(this,outputFile.absolutePath,"")
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
