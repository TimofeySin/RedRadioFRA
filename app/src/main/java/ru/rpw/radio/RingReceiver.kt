package ru.rpw.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let { itIntent ->
            if (itIntent.action == "android.intent.action.PHONE_STATE") {
                itIntent.extras?.let {
                    val mModelMedia = SingletonMediaPlayer
                    when (it.getString("state")) {
                        "RINGING" -> {
                            mModelMedia.setVolume(0.4F)
                        }
                        "IDLE" -> {
                            mModelMedia.recoverStatusPlayer()
                            mModelMedia.setVolume(1.0F)
                        }
                        "OFFHOOK" -> {
                            mModelMedia.pauseMediaPlayer()
                        }
                    }
                }
            }
        }
    }
}



