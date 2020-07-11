package com.example.trackersinall.interfaces

import retrofit2.Call
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface  IAlterarNumeroSerie {
    @Multipart
    @POST("apiAlterarserie.php")

    fun getSeparados(
        @Part("idcontrato") idcontrato: RequestBody,
        @Part("protocolo") protocolo: RequestBody,
        @Part("serieanterior") serieanterior: RequestBody,
        @Part("serieatual") serieatual: RequestBody
    ): Call<String>

    companion object {

        var BASE_URL = "http://201.20.54.187:3128/apiAndroid/"

        fun create() : IAlterarNumeroSerie {
            //timeout
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(IAlterarNumeroSerie::class.java)
        }
    }
}