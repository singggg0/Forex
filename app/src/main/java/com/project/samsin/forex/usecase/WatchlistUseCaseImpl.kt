package com.project.samsin.forex.usecase

import com.project.samsin.forex.repo.ForexPairRepo
import com.project.samsin.forex.watchlist.FxQuotationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WatchlistUseCaseImpl @Inject constructor(private val repo: ForexPairRepo) : WatchlistUseCase {

    override suspend fun fetchPairs(): Result<List<String>> {
        return withContext(Dispatchers.IO) {
            repo.getPairs()
        }
    }

    override suspend fun quotePairs(pairs: List<String>): Result<List<FxQuotationResult>> {
        return withContext(Dispatchers.IO) {
            repo.quotePairs(pairs)
        }
    }

    override fun subscribePairs(pairs: List<String>): Flow<FxQuotationResult> {
        return repo.subscribe(pairs)
    }
}