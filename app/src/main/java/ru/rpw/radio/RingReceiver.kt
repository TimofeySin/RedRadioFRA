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
                        MediaPlayerControl().setMonoVolume(0.4F)
                    }
                    "IDLE" -> {
                        //SingletonMediaPlayer.recoverStatusPlayer()
                        MediaPlayerControl().setMonoVolume(1.0F)
                    }
                    "OFFHOOK" -> {
                        MediaPlayerControl().pause()
                    }
                }
            }
        }
    }
}



