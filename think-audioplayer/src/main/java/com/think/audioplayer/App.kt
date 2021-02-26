package com.think.audioplayer

import android.app.Application
import android.content.Context

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        plugin = AudioPlayerPlugin(this)
    }

    companion object{
        lateinit var plugin: AudioPlayerPlugin
    }
}