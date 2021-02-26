package com.think.audioplayer

import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WrappedSoundPool {

    private var mShortPlayer: SoundPool = if (Build.VERSION.SDK_INT > 21) {
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

    /**
     * 需要播放的音频队列
     */
    private val audioOrder = Collections.synchronizedMap(mutableMapOf<Int,WrappedSoundPool >())

    private var isPlay: Boolean = false

    private var isPaused: Boolean = false

    private var isLoading: Boolean = false

    /**
     * 声道
     */
    private var volume: Float = 1.0F

    /**
     * 播放音频的流id
     */
    private var streamId: Int = 0

    /**
     * 当前需要播放的音频
     */
    private var soundId: Int = 0

    init {
        mShortPlayer.setOnLoadCompleteListener { _, soundId, _ ->
            Log.d(TAG, "加载id =  $soundId 的音频文件成功")
            if(audioOrder[soundId] != null){
                audioOrder.remove(soundId)?.start()
            }
        }
    }


    fun play() {

    }

    fun start() {
        if (isPaused) {
            mShortPlayer.resume(streamId)
            isPaused = false
        } else {
            if (soundId <= 0) return
            streamId = mShortPlayer.play(
                    soundId,
                    volume,
                    volume,
                    0,
                    loopModeInteger(),
                    1.0f
            )
        }
    }


    private fun loadByAsset(assetPath: String) {
        if ((App.context == null) or TextUtils.isEmpty(assetPath)) {
            return
        }
        audioOrder[mShortPlayer.load(App.context!!.assets.openFd(assetPath), 1)] = this
    }

    companion object {
        val TAG: String = WrappedSoundPool::class.java.simpleName
    }
}