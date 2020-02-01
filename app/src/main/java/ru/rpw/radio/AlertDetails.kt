package ru.rpw.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.rpw.radio.ui.HomeFragment

class AlertDetails : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val intAction = intent!!.extras?.get("redRadioControl")//("redRadioControl",0)
        val intActionTest = intent!!.extras?.get("T")
        val mAction = intent.action

        val mMediaPlayer = SingletonMediaPlayer
        when (intAction) {
            3 -> mMediaPlayer.playPauseMediaPlayer()
            4 -> mMediaPlayer.playPauseMediaPlayer()
            5 -> mMediaPlayer.stopMediaPlayer()
        }

    }

}
