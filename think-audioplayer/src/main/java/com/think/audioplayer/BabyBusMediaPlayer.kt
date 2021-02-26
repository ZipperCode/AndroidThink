package com.think.audioplayer

import android.media.MediaPlayer

class BabyBusMediaPlayer(playerId: Int) : MediaPlayer() {

    /**
     * 播放器id
     */
    private var playerId: Int = playerId

    /**
     * 播放的声音标识
     */
    private var sn: String? = null
    set(value){
        field = value
    }
    /**
     * 是否人为暂停，人为暂停不自动恢复播放
     */
    private var isPeoplePause:Boolean = false
    set(value) {
        field = value
    }
    get() = field
}