package ru.intensiv.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import ru.intensiv.player.service.PlayerService
import ru.intensiv.player.service.PlayerService.PlayerServiceBinder


class MainActivity : ComponentActivity() {

    var playerService: PlayerService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val playerServiceBinder = service as PlayerServiceBinder
            playerService = playerServiceBinder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            playerService = null
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, PlayerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        val playPauseButton: ImageButton = findViewById(R.id.play_pause)
        val previousButton: ImageButton = findViewById(R.id.previous)
        val nextButton: ImageButton = findViewById(R.id.next)

        fun updatePlayPauseButton() {
            if (playerService?.isPlaying() == true) {
                playPauseButton.setImageResource(R.drawable.ic_pause)
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play)
            }
        }

        updatePlayPauseButton()

        playPauseButton.setOnClickListener {
            if (playerService?.isPlaying() == true) {
                playerService?.pause()
                updatePlayPauseButton()
            } else {
                playerService?.play()
                updatePlayPauseButton()
            }
        }

        previousButton.setOnClickListener {
            playerService?.previous()
        }

        nextButton.setOnClickListener {
            playerService?.next()
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}
