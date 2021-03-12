package com.think.audioplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.think.audioplayer.AudioPlayerPlugin.Companion.debug
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class WrappedMediaPlayer(playerId: String, audioPlayerPlugin: AudioPlayerPlugin) : BasePlayer(playerId, audioPlayerPlugin),
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    /**
     * 单独播放
     */
    var mMediaPlayer: MediaPlayer = MediaPlayer()

    private var mAudioPlayerPlugin: AudioPlayerPlugin = audioPlayerPlugin

    private var isPrepared: AtomicBoolean = AtomicBoolean(false)

    val prepared: Boolean get() = isPrepared.get()

    /**
     * 在音频流准备好之前，如果需要将游标移动到指定位置，则可以通过此变量进行占位
     */
    private var shouldSeekToPosition: Int = -1


    override fun play(url: String, isParallel: Boolean, mode: LoadAudioMode, rate: Double, isLoop: Boolean, position: Int) {
        debug(TAG, "id = $playerId url = $url isParallel = $isParallel mode = $mode rate = $rate isLoop = $isLoop")
        if (isRelease) {
            mMediaPlayer = MediaPlayer()
        }else{
            mMediaPlayer.reset()
        }
        isRelease = false
        isPaused = false
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnErrorListener(this)
        setPlayerRate(rate)
        this.shouldSeekToPosition = position
        try {
            when (mode) {
                LoadAudioMode.LOCAL -> {
                    debug(TAG, "本地文件加载")
                    mMediaPlayer.setDataSource(url)
                }
                LoadAudioMode.NETWORK -> {
                    debug(TAG, "网络加载")
                    mMediaPlayer.setDataSource(loadTempFileFromNetwork(url).absolutePath)
                }
                LoadAudioMode.ASSETS -> {
                    debug(TAG, "Assets加载")
                    val fd = loadByAssets(url, mAudioPlayerPlugin.mAppContext)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        mMediaPlayer.setDataSource(fd)
                    } else {
                        mMediaPlayer.setDataSource(fd.fileDescriptor)
                    }
                }
            }
            mMediaPlayer.isLooping = isLoop
            mMediaPlayer.prepareAsync()

            /* 此处进行start的话 会引发[-38]错误 link:[https://www.cnblogs.com/getherBlog/p/3939033.html]*/
        } catch (e: Exception) {
            Log.e(TAG, "长声音播放异常")
            e.printStackTrace()
        }
    }

    override fun resume() {
        debug(TAG, "id = $playerId resume")
        if (mMediaPlayer.isPlaying and isPlaying) return
        mMediaPlayer.start()
        isPaused = false
        isPlaying = true
    }

    override fun pause() {
        debug(TAG, "id = $playerId pause")
        if (isRelease) {
            return
        }
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
        isPaused = true
        isPlaying = false
    }

    override fun stop() {
        debug(TAG, "id = $playerId stop")
        if (isRelease) {
            return
        }

        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }

        isPlaying = false
        isPrepared.set(false)
        mMediaPlayer.reset()

    }

    override fun release() {
        debug(TAG, "id = $playerId release")
        this.stop()
        this.isRelease = true
    }

    override fun destroy() {
        debug(TAG, "id = $playerId destroy")
        release()
        mMediaPlayer.release()
    }

    override fun duration(): Int {
        return try {
            if(mMediaPlayer.isPlaying)
                mMediaPlayer.duration
            else
                0
        } catch (e: Exception) {
            isPlaying = false
            0
        }
    }

    override fun currentDuration(): Int {
        return try {
            if(mMediaPlayer.isPlaying)
                mMediaPlayer.currentPosition
            else
                0
        } catch (e: Exception) {
            isPlaying = false
            0
        }
    }

    fun seekTo(position: Int) {
        debug(TAG, "id = $playerId seekTo 要移动的位置为: $position 当前位置为: ${currentDuration()} 总长度为: ${duration()}")
        if (isPrepared.get()) {
            mMediaPlayer.seekTo(position)
        } else {
            this.shouldSeekToPosition = position
        }
    }

    private fun run() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }
        if (Build.VERSION.SDK_INT > 23) {
            mMediaPlayer.playbackParams.speed = rate.toFloat()
        }
        isPlaying = true
        try {
            mMediaPlayer.start()
            if ((mMediaPlayer.duration > 0) and (shouldSeekToPosition >= 0) and (shouldSeekToPosition <= mMediaPlayer.duration)) {
                mMediaPlayer.seekTo(shouldSeekToPosition)
                shouldSeekToPosition = -1
            }
        } catch (e: Exception) {
            mMediaPlayer.start()
        }
    }

    /**
     * 播放完成后的监听
     */
    override fun onCompletion(mp: MediaPlayer?) {
        debug(TAG, "onCompletion")
        isPlaying = false
    }

    /**
     * 媒体流是否准备好监听
     */
    override fun onPrepared(mp: MediaPlayer?) {
        debug(TAG, "onPrepared")
        // 获取媒体总时长
        isPrepared.set(true)
        this.run()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isPlaying = false
        debug(TAG, "onError code = $what , extra code = $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                debug(TAG, "MEDIA_ERROR_IO")
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                debug(TAG, "MEDIA_ERROR_UNSUPPORTED")
            }
            else -> {
                debug(TAG, "MEDIA_ERROR_UNKNOWN")
            }
        }
        return false
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WrappedMediaPlayer

        if (mMediaPlayer != other.mMediaPlayer) return false

        if (playerId != other.playerId) return false

        return true
    }

    override fun hashCode(): Int {
        return mMediaPlayer.hashCode()
    }

    private fun createMediaPlayer(): MediaPlayer {
        val player = MediaPlayer()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        return player
    }

    companion object {
        val TAG: String = WrappedMediaPlayer::class.java.simpleName
    }
}