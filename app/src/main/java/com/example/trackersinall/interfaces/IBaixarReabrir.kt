package com.example.trackersinall.interfaces

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface  IBaixarReabrir {

    @Headers("Content-Type: application/json")
    @GET("apiIniciarchamado.php")
    fun getSeparados(@Query("idchamado") idchamado: String) : Call<String>

    companion object {
        var BASE_URL = "http://201.20.54.187:3128/apiAndroid/"
        fun create() : IBaixarReabrir {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(IBaixarReabrir::class.java)
        }
    }
}