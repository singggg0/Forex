package com.project.samsin.network.retrofit.models

import com.google.gson.annotations.SerializedName

data class GetCurrencyPairsResponse(
    @SerializedName("supportedPairs")
    val supportedPairs: List<String> = emptyList(),

    @SerializedName("message")
    val message: String = "",

    @SerializedName("code")
    val code: Int = 0
)
