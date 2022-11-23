package com.project.samsin.network.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private val retrofit = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(FREE_FOREX_API_BASE_URL)
        .build()

    val apiServices = retrofit.create(ApiServices::class.java)
}

