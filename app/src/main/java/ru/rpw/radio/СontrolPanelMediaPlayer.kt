package ru.rpw.radio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getColor
import androidx.media.session.MediaButtonReceiver
import kotlin.math.roundToInt

class ControlPanelMediaPlayer(private var context: Context, mMedia: SingletonMediaPlayer) {

    private val tagNotification = (Math.random() * 9999).roundToInt()


    init {
        createNotification(mMedia)
    }

    private fun createNotification(mMedia: SingletonMediaPlayer) {
        val builder = initNotificationBuilder(mMedia)
        createNotificationChannel()
        with(NotificationManagerCompat.from(context)) {
            notify(tagNotification, builder!!.build())
        }
    }

    private fun initNotificationBuilder(mMedia: SingletonMediaPlayer): NotificationCompat.Builder? {
        val mNotification = NotificationCompat.Builder(context, "MM")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setColor(getColor(context, R.color.textForNav))

            .setSmallIcon(R.drawable.big_logo)
            .setContentTitle(context.getString(R.string.menu_nav_red_radio))
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_LOW)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            when (mMedia.state) {
                SingletonMediaPlayer.StatePlayer.PLAY -> mNotification.addAction(
                    R.drawable.ic_pause_button,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
                SingletonMediaPlayer.StatePlayer.PAUSE -> mNotification.addAction(
                    R.drawable.ic_play_button,
                    "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )

                )
                else -> mNotification.addAction(
                    R.drawable.ic_play_button,
                    "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            }
            mNotification.addAction(
                R.drawable.ic_stop_button,
                "Stop",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
        }
        return mNotification
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.menu_nav_red_radio) + " Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MM", name, importance).apply {

            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}