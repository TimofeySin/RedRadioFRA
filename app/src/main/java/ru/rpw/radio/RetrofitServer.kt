package ru.rpw.radio

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServer {

    @GET("users/zuek1917/status.json")
    fun getStatus(): Call<DataModelStatus>

}

