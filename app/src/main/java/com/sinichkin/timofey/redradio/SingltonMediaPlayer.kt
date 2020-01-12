package com.sinichkin.timofey.redradio

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer


object SingltonMediaPlayer {
    private var mediaDone = false
    private var mMedia: MediaPlayer? = null
    private const val stream = "https://myradio24.org/zuek1917"

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
  //  fun getStreamMetaData():String {
//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(stream)
//        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
  //  }
}
