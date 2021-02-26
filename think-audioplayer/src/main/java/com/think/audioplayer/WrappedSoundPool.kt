package com.think.audioplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class WrappedSoundPool(audioPlayerPlugin: AudioPlayerPlugin) {

    private var mShortPlayer: SoundPool = if (Build.VERSION.SDK_INT > 21) {
        SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(
                        AudioAttributes.Builder()
                                .setLegacyStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
                                .build()
                ).build()
    } else {
        SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
    }

    private var mOrderPlayer: SoundPool = if (Build.VERSION.SDK_INT > 21) {
        SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(
                        AudioAttributes.Builder()
                                .setLegacyStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
                                .build()
                ).build()
    } else {
        SoundPool(1, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
    }

    private val mPlayerPlugin: AudioPlayerPlugin = audioPlayerPlugin

    /**
     * 需要播放的音频队列
     */
    private val audioOrder = Collections.synchronizedMap(mutableMapOf<Int, WrappedSoundPool>())

    /**
     * 正在播放中的队列
     */
    private val playerStream: MutableList<Int> = ArrayList()

    /**
     * 播放队列中是否有未完成的任务
     */
    private val hasPlay get() = (playerStream.size > 0)

    /**
     * 是否处于暂停
     */
    private var isPaused: Boolean = false

    /**
     * 是否释放了资源
     */
    private var isRelease: Boolean = false

    /**
     * 单独播放的音频id，只能同时播放一个，下一个会把上一个音频覆盖
     */
    private var soundId: Int = 0

    /**
     * 正在播放的音频，只能同时播放一个，下一个会把上一个音频覆盖
     */
    private var streamId: Int = 0

    /**
     * 声道
     */
    private var volume: Float = 1.0F
    set(value){
        if((value >= 0.0F) and (value <= 1.0F)){
            field = value
        }
    }
    init {
        mShortPlayer.setOnLoadCompleteListener { _, sampleId, _ ->
            // 释放后将队列清空
            if (this.isRelease) {
                unload()
                return@setOnLoadCompleteListener
            }
            // 不为空且非暂停状态下，才继续播放
            if ((audioOrder[sampleId] != null) and !isPaused) {
                audioOrder.remove(sampleId)?.start(sampleId)
            }
        }
        mOrderPlayer.setOnLoadCompleteListener{ _, _, _ ->
            streamId = mOrderPlayer.play(soundId, volume, volume, 0, 0, 1.0f)
        }
    }

    fun play(assetPath: String, type: AudioPlayType = AudioPlayType.PARALLEL) {
        this.isRelease = false
        this.isPaused = false
        if (type == AudioPlayType.PARALLEL) {
            loadByAsset(assetPath)
        } else if (type == AudioPlayType.ORDER) {
            soundId = mOrderPlayer.load(mPlayerPlugin.context!!.assets.openFd(assetPath), 1)
        }
    }

    /**
     * 恢复被暂停的音频，经测试，只能恢复第一个
     */
    fun resume() {
        // 是否有未完成的任务，且处于暂停
        Log.d(TAG, "[resume] isRelease = ${this.isRelease} this.hasPlay = $hasPlay isPaused = $isPaused playerStream = $playerStream")
        if((streamId > 0) and this.isPaused){
            mOrderPlayer.resume(streamId)
        }

        if (!this.isRelease and this.hasPlay and this.isPaused) {
            playerStream.forEach {
                mShortPlayer.resume(it)
            }
        }
        this.isPaused = false
    }

    /**
     * 暂停播放中的音频，由于短音频同时播放，需要循环遍历暂停
     */
    fun pause() {
        Log.d(TAG, "pause isPlay = ${this.hasPlay} isPaused = $isPaused playerStream = ${playerStream.size}")
        if(streamId > 0){
            mOrderPlayer.pause(streamId)
        }

        if (this.hasPlay and !this.isPaused) {
            playerStream.forEach {
                mShortPlayer.pause(it)
            }
        }
        this.isPaused = true
    }

    /**
     * 释放所有音频，不释放播放器
     */
    fun release() {
        this.stop()
        playerStream.clear()
        this.unload()
    }

    /**
     * 释放播放器中加载的音频，减小内存压力
     */
    private fun unload() {
        audioOrder.forEach {
            mShortPlayer.unload(it.key)
        }
        audioOrder.clear()
    }

    /**
     * 启动音频播放
     */
    private fun start(soundId: Int) {
        if (soundId <= 0) return
        val streamId = mShortPlayer.play(soundId, volume, volume, 0, 0, 1.0f)
        synchronized(playerStream) {
            playerStream.add(streamId)
        }
    }

    /**
     * 停止音频播放
     */
    private fun stop() {
        Log.d(TAG, "pause isPlay = ${this.hasPlay} playerStream = ${playerStream.size}")
        if(streamId > 0){
            mOrderPlayer.stop(streamId)
        }
        soundId = 0
        streamId = 0

        if (this.hasPlay) {
            // isRelease 设置为true，阻止音频继续加载
            isRelease = true
            playerStream.forEach {
                mShortPlayer.stop(it)
            }
            synchronized(playerStream) {
                playerStream.clear()
            }
        }
    }

    private fun loadByAsset(assetPath: String) {
        if (TextUtils.isEmpty(assetPath)) {
            return
        }
        audioOrder[mShortPlayer.load(mPlayerPlugin.context.assets.openFd(assetPath), 1)] = this
    }

    companion object {
        val TAG: String = WrappedSoundPool::class.java.simpleName
    }
}


