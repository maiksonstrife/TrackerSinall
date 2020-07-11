package com.example.trackersinall.interfaces

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface  ILogin {
    @Multipart
    @POST("apiLogin.php")

    fun getSeparados(
        @Part("celular") celular: RequestBody,
        @Part("senha") senha: RequestBody
    ): Call<String>

    companion object {

        var BASE_URL = "http://201.20.54.187:3128/apiAndroid/"

        fun create() : ILogin {
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

            return retrofit.create(ILogin::class.java)
        }
    }
}