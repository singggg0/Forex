package com.project.samsin.network.retrofit.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class GetRatesDataResponse(
    @SerializedName("rates")
    val rates: Map<String, CurrencyPairRate> = emptyMap(),

    @SerializedName("code")
    val code: Int = 0
)

data class CurrencyPairRate(
    @SerializedName("rate")
    val rate: BigDecimal,
    @SerializedName("timestamp")
    val timestamp: Long
)