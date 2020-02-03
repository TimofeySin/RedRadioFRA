package ru.rpw.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val mMedia = SingletonMediaPlayer
        intent?.let {
            when (it.action) {
                "android.intent.action.PHONE_STATE" -> {
                    when (it.extras?.get("state")) {
                        "RINGING" -> mMedia.setVolume(0.4F)
                        "IDLE" -> mMedia.setVolume(1.0F)
                        "OFFHOOK" -> mMedia.playPauseMediaPlayer()
                    }
                }
                "redRadio.intent.action.PLAY" -> mMedia.playPauseMediaPlayer()
                "redRadio.intent.action.STOP" -> mMedia.stopMediaPlayer()
            }
        }
    }
}




