package cn.think.eventbus

import android.app.Application
import org.greenrobot.eventbus.EventBus

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        EventBus.builder().addIndex(EventBusIndex()).installDefaultEventBus()
    }
}