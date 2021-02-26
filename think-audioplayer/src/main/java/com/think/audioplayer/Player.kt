package com.think.audioplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build

class Player {

    private var isLoading: Boolean = false

    private var isPlaying: Boolean = false

    private var isPause: Boolean = false

    private var isStop: Boolean = false

    private var isRelease: Boolean = false

    private var mShortPlayer: SoundPool

    private var mLongPlayer: MediaPlayer

    init {
        mShortPlayer = if (Build.VERSION.SDK_INT > 21) {
            SoundPool.Builder()
                    .setMaxStreams(100)
                    .setAudioAttributes(
                            AudioAttributes.Builder()
                                    .setLegacyStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
                                    .build()
                    ).build()
        } else {
            SoundPool(50, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
        }
    }





}