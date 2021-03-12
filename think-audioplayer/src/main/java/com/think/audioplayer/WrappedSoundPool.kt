package com.think.audioplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.think.audioplayer.AudioPlayerPlugin.Companion.debug
import java.util.*
import kotlin.collections.ArrayList

class WrappedSoundPool(
        playerId: String,
        audioPlayerPlugin: AudioPlayerPlugin
) :BasePlayer(playerId, audioPlayerPlugin) {

    private var mOrderPlayer: SoundPool

    private var mPlayer: SoundPool

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
     * 单独播放的音频id，只能同时播放一个，下一个会把上一个音频覆盖
     */
    private var soundId: Int = 0

    /**
     * 正在播放的音频，只能同时播放一个，下一个会把上一个音频覆盖
     */
    private var streamId: Int = 0

    private var isLoop: Boolean = false

    init {
        mOrderPlayer = createSoundPool(1)
        mPlayer = createSoundPool(10)
        mOrderPlayer.setOnLoadCompleteListener{ _, _, _ -> run(soundId) }

        mPlayer.setOnLoadCompleteListener { _, sampleId, _ ->
            // 释放后将队列清空
            if (this.isRelease) {
                unload()
                return@setOnLoadCompleteListener
            }
            // 不为空且非暂停状态下，才继续播放
            if ((audioOrder[sampleId] != null) and !isPaused) {
                audioOrder.remove(sampleId)?.run(sampleId,true)
            }
        }
    }

    override fun play(url: String, isParallel: Boolean, mode: LoadAudioMode,rate: Double, isLoop: Boolean, position: Int) {
        debug(TAG,"[play] url = $url isParallel = $isParallel mode = $mode rate = $rate isLoop = $isLoop position = $position")
        this.isPaused = false
        this.isRelease = false
        setPlayerRate(rate)
        this.isLoop = isLoop
        when(mode){
            LoadAudioMode.LOCAL ->{
                debug(TAG,"本地文件加载")
                if(isParallel){
                    audioOrder[mPlayer.load(url,0)] = this
                }else{
                    soundId = mOrderPlayer.load(url,0)
                }
            }
            LoadAudioMode.NETWORK ->{
                debug(TAG,"网络加载")
                if(isParallel){
                    audioOrder[mPlayer.load(loadTempFileFromNetwork(url).absolutePath,0)] = this
                }else{
                    soundId = mOrderPlayer.load(loadTempFileFromNetwork(url).absolutePath,0)
                }
            }
            LoadAudioMode.ASSETS ->{
                debug(TAG,"Assets加载")
                if(isParallel){
                    audioOrder[mPlayer.load(loadByAssets(url, mPlayerPlugin.mAppContext),0)] = this
                }else{
                    soundId = mOrderPlayer.load(loadByAssets(url, mPlayerPlugin.mAppContext),0)
                }
            }
        }

    }

    override fun resume() {
        debug(TAG,"resume streamId = $streamId")
        if(streamId > 0){
            mOrderPlayer.resume(streamId)
        }
        if(hasPlay){
            playerStream.forEach {
                mPlayer.resume(it)
            }
        }

        this.isPaused = false
    }

    /**
     * 暂停播放中的音频
     */
    override fun pause() {
        debug(TAG,"pause streamId = $streamId")
        if(streamId > 0){
            mOrderPlayer.pause(streamId)
        }
        if(hasPlay){
            playerStream.forEach {
                mPlayer.pause(it)
            }
        }
        this.isPaused = true
    }

    /**
     * 停止音频播放
     */
    override fun stop() {
        if(streamId > 0){
            mOrderPlayer.stop(streamId)
        }
        soundId = 0
        streamId = 0

        playerStream.forEach {
            mPlayer.stop(it)
        }
        playerStream.clear()
    }


    /**
     * 释放所有音频，不释放播放器
     */
    override fun release() {
        this.unload()
        this.stop()
        this.isRelease = true
        audioOrder.clear()
    }

    override fun destroy() {
        this.release()
        mPlayer.release()
        mOrderPlayer.release()
    }

    override fun duration(): Int  = 0

    override fun currentDuration(): Int  = 0

    override fun setPlayerRate(rate: Double) {
        super.setPlayerRate(rate)
        if(streamId > 0){
            mOrderPlayer.setRate(this.streamId, this.rate.toFloat())
        }
    }

    /**
     * 释放播放器中加载的音频，减小内存压力
     */
    private fun unload() {
        audioOrder.keys.forEach{
            mPlayer.unload(it)
        }
        audioOrder.clear()
    }

    private fun createSoundPool(parallelNum: Int): SoundPool{
        return if (Build.VERSION.SDK_INT > 21) {
            SoundPool.Builder()
                    .setMaxStreams(parallelNum)
                    .setAudioAttributes(
                            AudioAttributes.Builder()
                                    .setLegacyStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
                                    .build()
                    ).build()
        } else {
            SoundPool(parallelNum, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
        }
    }

    /**
     * 启动音频播放
     */
    private fun run(soundId: Int, isParallel: Boolean = false) {
        debug(TAG,"run soundId = $soundId isParallel = $isParallel")
        if(isParallel){
            val streamId = mPlayer.play(soundId, volume, volume, 0, 0, 1.0f)
            playerStream.add(streamId)
            mPlayer.setRate(streamId,rate.toFloat())
        }else{
            if (soundId <= 0) return
            streamId = mOrderPlayer.play(soundId, volume, volume, 0, 0, 1.0f)
            mOrderPlayer.setRate(streamId,rate.toFloat())
            mOrderPlayer.setLoop(streamId, if(isLoop) -1 else 0)

            debug(TAG,"run playing streamId = $streamId")
        }
    }


    companion object {
        val TAG: String = WrappedSoundPool::class.java.simpleName
    }
}


