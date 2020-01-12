package com.sinichkin.timofey.redradio


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


public class DataModelStatus {
    @SerializedName("port")
     @Expose
     private val port: String? = ""

     @SerializedName("server")
     @Expose
     private val server: String? = ""

     @SerializedName("kbps")
     @Expose
     private val kbps: Int? = 0

     @SerializedName("title")
     @Expose
     private val title: String? = ""

     @SerializedName("song")
     @Expose
     private val song: String? = ""

     @SerializedName("genre")
     @Expose
     private val genre: String? = ""

     @SerializedName("artist")
     @Expose
     private val artist: String? = ""

     @SerializedName("songtitle")
     @Expose
     private val songtitle: String? = ""

 fun getTitle(): String? {
  return title
 }
 fun getSong(): String? {
  return song
 }
}