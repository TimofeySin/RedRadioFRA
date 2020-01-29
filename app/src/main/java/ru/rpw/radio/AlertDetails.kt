package ru.rpw.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlertDetails : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val intAction = intent!!.getIntExtra("redRadioControl",0)

        val mMediaPlayer = MediaPlayerControl()
        when (intAction) {
            2 -> mMediaPlayer.pause()
            1 -> mMediaPlayer.start()
            3 -> mMediaPlayer.stop()
        }

    }

}
