package com.think.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var pool: WrappedSoundPool

    private lateinit var player: WrappedMediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pool = WrappedSoundPool(App.plugin)
        player = WrappedMediaPlayer(App.plugin)

        findViewById<Button>(R.id.play).setOnClickListener{
            player.play("BGM1.mp3")
        }
        findViewById<Button>(R.id.resume).setOnClickListener{
            player.resume()
        }

        findViewById<Button>(R.id.pause).setOnClickListener{
            player.pause()
        }

        findViewById<Button>(R.id.release).setOnClickListener{
            player.release()
        }

        findViewById<Button>(R.id.click).setOnClickListener{
            player.play("BGM1.mp3")
            player.play("BGM2.mp3")
        }
    }
}