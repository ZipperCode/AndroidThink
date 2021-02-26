package com.think.audioplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build

abstract class Player {

    /**
     * 是否处于暂停
     */
    protected var isPaused: Boolean = false

    /**
     * 是否释放了资源
     */
    protected var isRelease: Boolean = false

    /**
     * 声道
     */
    protected var volume: Float = 1.0F
        set(value){
            if((value >= 0.0F) and (value <= 1.0F)){
                field = value
            }
        }

    abstract fun play(assetPath: String)

    abstract fun resume()

    abstract fun pause()

    abstract fun release()
}