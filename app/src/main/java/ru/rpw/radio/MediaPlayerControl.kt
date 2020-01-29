package ru.rpw.radio

import android.content.Context
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager

class MediaPlayerControl : MediaPlayer() {
    //  var mMedia = MediaPlayer()
    private var stream = "https://myradio24.org/zuek1917"
    var state = StatePlayer.NOTREADY

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        try {
            this.setDataSource(stream)
            this.prepareAsync()
        } catch (e: IllegalStateException) {

        }

    }

//    override fun prepareAsync() {
//        this.setOnPreparedListener {
//            this.start()
//        }
////        this.setDataSource(stream)
////        super.prepareAsync()
//    }

    fun setMonoVolume(value: Float) {
        this.setVolume(value, value)
    }

    fun setWakeMode(context: Context, lock: Boolean) {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyWifiLock")

        if (lock) {
            this.let {
                it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                wifiLock.acquire()
            }
        } else {
            if (wifiLock.isHeld) {
                wifiLock.release()
            }
        }
    }

    fun getMediaPlayer(): MediaPlayer {
//        mMedia?.let { return mMedia as MediaPlayer }
//        mMedia = MediaPlayer()
//        initMediaPlayer()
        return this
    }

    override fun start() {
        initMediaPlayer()
        super.start()
        state = StatePlayer.PLAY
    }

    override fun stop() {
        //super.stop()
        this.reset()
//        this.release()
        // initMediaPlayer()
        state = StatePlayer.PAUSE
    }

    override fun pause() {
        if (isPlaying) {
            state = StatePlayer.PAUSE
            super.pause()
        }
    }


    enum class StatePlayer {
        PAUSE, RESET, NOTREADY, READY, PLAY
    }
}