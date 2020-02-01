package ru.rpw.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            if (it.action == "android.intent.action.PHONE_STATE") {
                when (it.extras?.getString("state")) {
                    "RINGING" -> {
                        SingletonMediaPlayer.setVolume(0.4F)
                    }
                    "IDLE" -> {
                        //SingletonMediaPlayer.recoverStatusPlayer()
                        SingletonMediaPlayer.setVolume(1.0F)
                    }
                    "OFFHOOK" -> {
                        SingletonMediaPlayer.playPauseMediaPlayer()
                    }
                }
            }
        }
    }
}



