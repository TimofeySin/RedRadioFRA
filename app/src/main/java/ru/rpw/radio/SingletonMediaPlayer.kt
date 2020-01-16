package ru.rpw.radio

import android.media.MediaPlayer

object SingletonMediaPlayer {

    private var mediaDone = false
    private var mMedia: MediaPlayer? = null
    private var stream = "https://myradio24.org/zuek1917"

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

    fun getMediaPlayer(): MediaPlayer {
        return mMedia!!
    }

    fun getMediaDone(): Boolean {
        return mediaDone
    }

    fun setMediaDone(newValue: Boolean) {
        mediaDone = newValue
    }
}
