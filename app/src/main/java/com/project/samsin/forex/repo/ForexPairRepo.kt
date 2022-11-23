package com.project.samsin.forex.repo

import com.project.samsin.forex.watchlist.FxQuotationResult
import kotlinx.coroutines.flow.Flow

interface ForexPairRepo {
    suspend fun getPairs(): Result<List<String>>

    suspend fun quotePairs(pairs: List<String>): Result<List<FxQuotationResult>>

    fun subscribe(pairs: List<String>): Flow<FxQuotationResult>
}