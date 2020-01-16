package ru.rpw.radio

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataModelStatus {

    @SerializedName("song")
    @Expose
    private val song: String? = ""

    fun getSong(): String? {
        return song
    }
}