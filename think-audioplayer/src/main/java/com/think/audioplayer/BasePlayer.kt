package com.think.audioplayer

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL

abstract class BasePlayer(
        val playerId: String,
        protected val mPlayerPlugin: AudioPlayerPlugin
) {

    /**
     * 是否处于播放状态
     */
    var isPlaying: Boolean = false;
    /**
     * 是否处于暂停
     */
    protected var isPaused: Boolean = false

    /**
     * 是否释放了资源
     */
    protected var isRelease: Boolean = false

    /**
     * 速率
     */
    protected var rate: Double = 1.0
    /**
     * 声道
     */
    var volume: Float = 1.0F
        set(value) {
            if ((value >= 0.0F) and (value <= 1.0F)) {
                field = value
            }
        }

    abstract fun play(url: String,
                      isParallel: Boolean = false,
                      mode: LoadAudioMode,
                      rate: Double = 1.0,
                      isLoop: Boolean = false,
                      position: Int)

    abstract fun resume()

    abstract fun pause()

    abstract fun stop()

    abstract fun release()

    abstract fun destroy()

    abstract fun duration(): Int

    abstract fun currentDuration(): Int

    open fun setPlayerRate(rate: Double) {
        this.rate = rate
    }

    companion object {
        fun loadByAssets(url: String, context: Context): AssetFileDescriptor {
            return context.assets.openFd("$url")
        }

        fun loadTempFileFromNetwork(url: String?): File {
            val bytes = downloadUrl(URI.create(url).toURL())
            val tempFile = File.createTempFile("sound", "")
            FileOutputStream(tempFile).use {
                it.write(bytes)
                tempFile.deleteOnExit()
            }
            return tempFile
        }

        private fun downloadUrl(url: URL): ByteArray {
            val outputStream = ByteArrayOutputStream()
            url.openStream().use { stream ->
                val chunk = ByteArray(4096)
                while (true) {
                    val bytesRead = stream.read(chunk).takeIf { it > 0 } ?: break
                    outputStream.write(chunk, 0, bytesRead)
                }
            }
            return outputStream.toByteArray()
        }


    }
}