package ru.intensiv.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ru.intensiv.player.R


class PlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        return PlayerServiceBinder()
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    var mediaPlayer: MediaPlayer? = null
    var notificationManager: NotificationManager? = null

    val tracks = listOf(
        R.raw.track_04,
        R.raw.track_05,
        R.raw.track_06,
        R.raw.track_07,
        R.raw.track_08,
        R.raw.track_09,
        R.raw.track_10,
        R.raw.track_11,
        R.raw.track_12
    )

    var currentTrack = 0

    override fun onCreate() {
        mediaPlayer = MediaPlayer.create(this, tracks[0])
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PLAY -> {
                    play()
                }
                ACTION_NEXT -> {
                    next()
                }
                ACTION_PREVIOUS-> {
                    previous()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification() {

        val playIntent = PendingIntent.getService(this, 0, Intent(this, PlayerService::class.java).apply {
            action = ACTION_PLAY
        }, PendingIntent.FLAG_MUTABLE)

        val pauseIntent = PendingIntent.getService(this, 0, Intent(this, PlayerService::class.java).apply {
            action = "ACTION_PAUSE"
        }, PendingIntent.FLAG_MUTABLE)

        val nextIntent = PendingIntent.getService(this, 0, Intent(this, PlayerService::class.java).apply {
            action = ACTION_NEXT
        }, PendingIntent.FLAG_MUTABLE)

        val previousIntent = PendingIntent.getService(this, 0, Intent(this, PlayerService::class.java).apply {
            action = ACTION_PREVIOUS
        }, PendingIntent.FLAG_MUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "MUSIC_CHANNEL_ID"
            val channelName: CharSequence = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, "MUSIC_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Media Player")
            .setContentText("Playing")
            .addAction(R.drawable.ic_previous, "Previous", previousIntent)
            .addAction(R.drawable.ic_play, "Play", playIntent)
            .addAction(R.drawable.ic_next, "Next", nextIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        notificationManager?.notify(1, notification)
        startForeground(1, notification)
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun previous() {
        currentTrack = (currentTrack - 1) % tracks.size
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(this, tracks[currentTrack])
        play()
    }

    fun next() {
        currentTrack = (currentTrack + 1) % tracks.size
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(this, tracks[currentTrack])
        play()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    companion object {
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
    }
}
