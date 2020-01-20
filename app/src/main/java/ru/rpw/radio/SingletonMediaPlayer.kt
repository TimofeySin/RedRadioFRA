package ru.rpw.radio

import android.media.MediaPlayer

object SingletonMediaPlayer {

    private var mediaDone = false
    private var mMedia: MediaPlayer? = null
    private var stream = "https://myradio24.org/zuek1917"
    private var lastStatusPlay = false

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

    fun isPlaying(): Boolean {
        mMedia?.let { return it.isPlaying } ?: run { return false }
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

    fun setVolume(value: Float) {
        mMedia?.setVolume(value, value)
    }

    fun pauseMediaPlayer() {
        mMedia?.let {
            if (it.isPlaying) {
                it.pause()
                lastStatusPlay = true
            }
        }
    }

    fun recoverStatusPlayer() {
        if (lastStatusPlay) {
            mMedia?.start()
        }
    }
}
