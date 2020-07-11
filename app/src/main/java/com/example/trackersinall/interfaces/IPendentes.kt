package com.example.trackersinall.interfaces

import com.example.trackersinall.model.ChamadosModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface  IPendentes {

    @Headers("Content-Type: application/json")
    @GET("apiPendentes.php")
    fun getSeparados(@Query("idfuncionario") idfuncionario: String) : Call<List<ChamadosModel>>

    companion object {
        var BASE_URL = "http://201.20.54.187:3128/apiAndroid/"
        fun create() : IPendentes {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(IPendentes::class.java)
        }
    }
}