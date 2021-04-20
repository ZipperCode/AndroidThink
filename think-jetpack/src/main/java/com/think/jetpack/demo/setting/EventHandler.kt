package com.think.jetpack.demo.setting

import android.util.Log

class EventHandler {

    companion object{
        val TAG: String = EventHandler::class.java.simpleName
    }

    fun action(actionType: ActionType){
        println("actionType = $actionType")
        when(actionType){
            ActionType.ACTION_1 ->{
                Log.d(TAG,"view doAction 1")
            }
            ActionType.ACTION_2 ->{
                Log.d(TAG,"view doAction 2")
            }
            ActionType.ACTION_3 -> {
                Log.d(TAG, "view doAction 3")
            }
            else ->{
                Log.d(TAG,"not implement")
            }
        }
    }
}