package com.think.audioplayer

import android.media.MediaPlayer
import android.util.Log
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

class WrappedMediaPlayer(audioPlayerPlugin: AudioPlayerPlugin) : MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    /**
     * 单独播放
     */
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    private var mMediaPlayerMap: MutableMap<Int, BabyBusMediaPlayer> = HashMap()

    private var mAudioPlayerPlugin: AudioPlayerPlugin = audioPlayerPlugin

    /**
     * 是否处于暂停
     */
    private var isPaused: Boolean = false

    /**
     * 是否释放了资源
     */
    private var isRelease: Boolean = false

    private var isPrepared: AtomicBoolean = AtomicBoolean(false)

    /**
     * 声道
     */
    var volume: Float = 1.0F
        set(value) {
            if ((value >= 0.0F) and (value <= 1.0F)) {
                field = value
            }
        }


    val duration: Int get() = mMediaPlayer.duration

    val currentDuration: Int get() =  mMediaPlayer.currentPosition

    val prepared: Boolean get() = isPrepared.get()

    /**
     * 在音频流准备好之前，如果需要将游标移动到指定位置，则可以通过此变量进行占位
     */
    private var shouldSeekToPosition: Int = -1

    fun play(assetPath: String) {
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnErrorListener(this)

        try {
            val fd = mAudioPlayerPlugin.context.assets.openFd(assetPath)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mMediaPlayer.setDataSource(fd)
            }else{
                mMediaPlayer.setDataSource(fd.fileDescriptor)
            }
            mMediaPlayer.prepareAsync()
            mMediaPlayer.isLooping = false
        } catch (e: Exception) {
            Log.e(TAG, "长声音播放异常")
            e.printStackTrace()
        }
    }

    fun resume() {
        if (mMediaPlayer.isPlaying) return
        mMediaPlayer.start()
    }

    fun pause() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
    }

    fun release() {
        stop()
    }

    fun seekTo(position: Int){
        Log.d(TAG,"要移动的位置为: $position 当前位置为: $currentDuration 总长度为: $duration")
        if(isPrepared.get()){
            mMediaPlayer.seekTo(position)
        }else{
            this.shouldSeekToPosition = position
        }
    }

    private fun start() {
        if(mMediaPlayer.isPlaying){
            mMediaPlayer.stop()
        }
        mMediaPlayer.start()
    }

    private fun stop() {
        if (isRelease) {
            return
        }
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }
        isPrepared.set(false)
        mMediaPlayer.reset()
    }

    /**
     * 播放完成后的监听
     */
    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
        mMediaPlayer.reset()
    }

    /**
     * 媒体流是否准备好监听
     */
    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared")
        // 获取媒体总时长
        isPrepared.set(true)
        start()
        if(shouldSeekToPosition >= 0){
            mMediaPlayer.seekTo(shouldSeekToPosition)
            shouldSeekToPosition = -1
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onError code = $what , extra code = $extra")
        when(what){
            MediaPlayer.MEDIA_ERROR_IO -> Log.d(TAG,"MEDIA_ERROR_IO")
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> Log.d(TAG,"MEDIA_ERROR_TIMED_OUT")
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> Log.d(TAG,"MEDIA_ERROR_UNSUPPORTED")
            else -> Log.d(TAG,"MEDIA_ERROR_UNKNOWN")
        }
        return false
    }


    companion object {
        val TAG: String = WrappedMediaPlayer::class.java.simpleName
    }
}