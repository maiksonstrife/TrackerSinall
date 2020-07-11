package com.example.trackersinall.interfaces

import com.example.trackersinall.model.PecasModelModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface  IObterPecas {

    @Headers("Content-Type: application/json")
    @GET("apiPecas.php")
    fun getSeparados() : Call<List<PecasModelModel>>

    companion object {
        var BASE_URL = "http://201.20.54.187:3128/apiAndroid/"
        fun create() : IObterPecas {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(IObterPecas::class.java)
        }
    }
}