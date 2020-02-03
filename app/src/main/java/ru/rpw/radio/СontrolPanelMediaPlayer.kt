package ru.rpw.radio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ControlPanelMediaPlayer(private var context: Context) {

    init {
        createNotification()
    }

    private fun createNotification() {
        val builder = initNotificationBuilder()
        createNotificationChannel()
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder!!.build())
        }
    }

    private fun getPending(value :Int): PendingIntent {
        val intent = Intent(context, Receiver::class.java).apply {
            when (value){
                  1->{action = "redRadio.intent.action.PLAY"}
                  2->{action = "redRadio.intent.action.STOP"}
            }
        }
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    private fun initNotificationBuilder(): NotificationCompat.Builder? {
        val mNotification =  NotificationCompat.Builder(context, "MM")
            .setSmallIcon(R.drawable.big_logo)
            .setContentTitle(context.getString(R.string.menu_nav_red_radio))
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotification
                .addAction(R.drawable.ic_play_button,  "Play", getPending(1))
                .addAction(R.drawable.ic_stop_button,  "Stop", getPending(2))
        }
        return mNotification
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.menu_nav_red_radio)+" Channel"
           // val descriptionText = "Name descriptionText"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel("MM", name, importance).apply {
              //  description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }





}