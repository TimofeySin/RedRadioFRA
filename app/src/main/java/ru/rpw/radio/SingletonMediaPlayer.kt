package ru.rpw.radio

import android.content.Context
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager


object SingletonMediaPlayer {

    private var mMedia: MediaPlayer? = null
    private var stream = "https://myradio24.org/zuek1917"
    var state = StatePlayer.NOTREADY

    init {
        if (mMedia == null) {
            mMedia = MediaPlayer()
            initMediaPlayer()
        }
    }

    private fun initMediaPlayer() {
        mMedia?.let {
            it.setDataSource(stream)
            it.prepareAsync()
        }
    }

    fun prepareAsync() {
        mMedia = null
        mMedia = MediaPlayer()
        initMediaPlayer()
        mMedia?.setOnPreparedListener {
            mMedia?.let {
                it.start()
                state = StatePlayer.PLAY
            }
        }
    }

    fun setWakeMode(context: Context, lock: Boolean) {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyWifiLock")

        if (lock) {
            mMedia?.let {
                it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)

                wifiLock.acquire()
            }
        } else {
            if (wifiLock.isHeld) {
                wifiLock.release()
            }
        }
    }

    fun isPlaying(): Boolean {
        return state == StatePlayer.PLAY
    }

    fun getMediaPlayer(): MediaPlayer {
        return mMedia!!
    }

    fun setVolume(value: Float) {
        mMedia?.setVolume(value, value)
    }

    fun pauseMediaPlayer() {
        mMedia?.let {
            if (it.isPlaying) {
                it.pause()
                state = StatePlayer.PAUSE
            }
        }
    }

    fun playMediaPlayer() {
        if (state == StatePlayer.PAUSE) {
            mMedia?.start()
            state = StatePlayer.PLAY
        }
    }

    enum class StatePlayer {
        PAUSE, RESET, NOTREADY, READY, PLAY
    }

}
