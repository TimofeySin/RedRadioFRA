package ru.rpw.radio

import android.content.Context
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.fragment_home.view.*
import ru.rpw.radio.ui.HomeFragment


object SingletonMediaPlayer {
    private var stream = "https://myradio24.org/zuek1917"
    var root : View? = null
    val mMedia: MediaPlayer = MediaPlayer()
    var state = StatePlayer.NOTREADY

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        mMedia.setDataSource(stream)
        mMedia.prepareAsync()
    }

    fun prepareAsync() {
        initMediaPlayer()
        state = StatePlayer.NOTREADY

    }

    fun setWakeMode(context: Context, lock: Boolean) {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyWifiLock")

        if (lock) {
            mMedia.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            wifiLock.acquire()
        } else {
            if (wifiLock.isHeld) {
                wifiLock.release()
            }
        }
    }

    fun setVolume(value: Float) {
        mMedia.setVolume(value, value)
    }

    fun stopMediaPlayer() {
        mMedia.stop()
        state = StatePlayer.STOP
        viewButtonMediaPlayer()
    }

    fun playPauseMediaPlayer() {
        when (state) {
            StatePlayer.PAUSE, StatePlayer.READY -> {
                mMedia.start()
                state = StatePlayer.PLAY
            }
            StatePlayer.STOP -> {
                mMedia.seekTo(0)
                mMedia.start()
                state = StatePlayer.PLAY
            }
            StatePlayer.PLAY -> {
                mMedia.pause()
                state = StatePlayer.PAUSE
            }
            else -> { }
        }
        viewButtonMediaPlayer()
    }

    enum class StatePlayer {
        PAUSE, RESET, NOTREADY, READY, PLAY, STOP
    }
    private fun viewButtonMediaPlayer() {
        val transpAlfa = 0.5f
        root?.let {
        when (state) {
            StatePlayer.PLAY -> {
                it.imageStopButton.alpha = 1f
                it.imagePlayButton.alpha = 1f
                it.imagePlayButton.setImageResource(R.drawable.ic_pause_button)
                it.progressBar.visibility = ProgressBar.INVISIBLE
                //setAirRecText("")
            }
            StatePlayer.PAUSE -> {
                it.imageStopButton.alpha = 1f
                it.imagePlayButton.alpha = 1f
                it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                it.progressBar.visibility = ProgressBar.INVISIBLE
               // setAirRecText(getString(R.string.air_rec))
            }
            StatePlayer.RESET -> {
                it.imageStopButton.alpha = transpAlfa
                it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                it.progressBar.visibility = ProgressBar.INVISIBLE
                //setAirRecText("")
            }
            StatePlayer.NOTREADY,StatePlayer.STOP -> {
                it.imageStopButton.alpha = transpAlfa
                it.imagePlayButton.alpha = transpAlfa
                it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                it.progressBar.visibility = ProgressBar.VISIBLE
               // setAirRecText("")
            }
            StatePlayer.READY -> {
                it.imageStopButton.alpha = transpAlfa
                it.imagePlayButton.alpha = 1f
                it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                it.progressBar.visibility = ProgressBar.INVISIBLE
               // setAirRecText("")
            }
        }
        }
    }

}
