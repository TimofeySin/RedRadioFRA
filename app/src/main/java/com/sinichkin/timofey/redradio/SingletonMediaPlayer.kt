package com.sinichkin.timofey.redradio

import android.content.res.Resources
import android.media.MediaPlayer


object SingletonMediaPlayer {
    private var mediaDone = false
    private var mMedia: MediaPlayer? = null
    private var stream = Resources.getSystem().getString(R.string.path_to_stream)

    init {
        if (mMedia == null) {
            mMedia = MediaPlayer()
            initMediaPlayer()
        }
    }

    private fun initMediaPlayer() {
        mMedia!!.setDataSource(stream)
        mMedia!!.prepareAsync()
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
