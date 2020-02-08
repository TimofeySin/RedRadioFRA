package ru.rpw.radio

import android.content.Context
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.fragment_home.view.*


object SingletonMediaPlayer : MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnCompletionListener{
    private var stream = "https://myradio24.org/zuek1917"
    var root: View? = null
    private val mMedia: MediaPlayer = MediaPlayer()
    var state = StatePlayer.NOTREADY
    var kbps = 0
    val mediaSessionToken = null

    init {

        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        mMedia.setDataSource(stream)
        mMedia.setOnCompletionListener(this)
        mMedia.setOnBufferingUpdateListener(this)
        mMedia.setOnInfoListener(this)
        mMedia.setOnPreparedListener(this)
        mMedia.prepareAsync()
        MediaPlaybackService()
    }

    fun prepareAsync() {
        initMediaPlayer()
        state = StatePlayer.NOTREADY

    }

    private fun setWakeMode(context: Context, lock: Boolean) {
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
        setWakeMode(root!!.context, false)
        state = StatePlayer.STOP
        mMedia.prepareAsync()
        viewButtonMediaPlayer()
        //root?.let{ ControlPanelMediaPlayer(it.context,this)}
    }

    fun playPauseMediaPlayer() {
        when (state) {
            StatePlayer.PAUSE, StatePlayer.READY -> {
                mMedia.start()
                state = StatePlayer.PLAY
            }
            StatePlayer.STOP -> {
                mMedia.start()
                state = StatePlayer.PLAY
            }
            StatePlayer.PLAY -> {
                mMedia.pause()
                state = StatePlayer.PAUSE
            }
            else -> {
            }
        }
        viewButtonMediaPlayer()
        setAirRecText("pos:" + mMedia.currentPosition + " dur:" + mMedia.duration)
        //root?.let{ ControlPanelMediaPlayer(it.context,this)}
    }

    enum class StatePlayer {
        PAUSE, RESET, NOTREADY, READY, PLAY, STOP
    }

    fun viewButtonMediaPlayer() {
        val transparency = 0.5f
        root?.let {
            when (state) {
                StatePlayer.PLAY -> {
                    it.imageStopButton.alpha = 1f
                    it.imagePlayButton.alpha = 1f
                    it.imagePlayButton.setImageResource(R.drawable.ic_pause_button)
                    it.progressBar.visibility = ProgressBar.INVISIBLE
                    setAirRecText("")
                }
                StatePlayer.PAUSE -> {
                    it.imageStopButton.alpha = 1f
                    it.imagePlayButton.alpha = 1f
                    it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                    it.progressBar.visibility = ProgressBar.INVISIBLE
                    setAirRecText(it.context.getString(R.string.air_rec))
                }
                StatePlayer.RESET -> {
                    it.imageStopButton.alpha = transparency
                    it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                    it.progressBar.visibility = ProgressBar.INVISIBLE
                    setAirRecText("")
                }
                StatePlayer.NOTREADY, StatePlayer.STOP -> {
                    it.imageStopButton.alpha = transparency
                    it.imagePlayButton.alpha = 1f
                    it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                    //it.progressBar.visibility = ProgressBar.VISIBLE
                    setAirRecText("")
                }
                StatePlayer.READY -> {
                    it.imageStopButton.alpha = transparency
                    it.imagePlayButton.alpha = 1f
                    it.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                    it.progressBar.visibility = ProgressBar.INVISIBLE
                    setAirRecText("")
                }
            }
        }
    }

    private fun setAirRecText(text: String) {
        root?.airRec?.let {
            if (text != it.text) {
                it.text = text
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        state = StatePlayer.READY
        viewButtonMediaPlayer()
        setWakeMode(root!!.context, true)

        Log.d(
            "PLAYER",
            "onPrepared: done pos:" + mMedia.currentPosition + " dur:" + mMedia.duration  +" info:" + mp!!.trackInfo
        )
        root?.let{ ControlPanelMediaPlayer(it.context,this)}

    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("PLAYER", "onInfo: what:$what, extra:$extra")
        if (what == 703) kbps = extra
        return true
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("PLAYER", "onError: percent:$what, extra:$extra")
        return true
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        Log.d("PLAYER", "onBufferingUpdate: percent:$percent")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d("PLAYER", "onCompletion: done")
    }



}
