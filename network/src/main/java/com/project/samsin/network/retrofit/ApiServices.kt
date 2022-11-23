package com.project.samsin.network.retrofit

import com.project.samsin.network.retrofit.models.GetCurrencyPairsResponse
import com.project.samsin.network.retrofit.models.GetRatesDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("api/live")
    fun getFxParis(): Call<GetCurrencyPairsResponse>

    @GET("api/live")
    fun getRates(@Query("pairs") pairs: String): Call<GetRatesDataResponse>
}