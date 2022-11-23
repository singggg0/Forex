package com.project.samsin.forex.usecase

import com.project.samsin.forex.watchlist.FxQuotationResult
import kotlinx.coroutines.flow.Flow

interface WatchlistUseCase {
    suspend fun fetchPairs(): Result<List<String>>
    suspend fun quotePairs(pairs: List<String>): Result<List<FxQuotationResult>>
    fun subscribePairs(pairs: List<String>): Flow<FxQuotationResult>
}