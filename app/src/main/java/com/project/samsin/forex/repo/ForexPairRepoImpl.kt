package com.project.samsin.forex.repo

import com.project.samsin.forex.watchlist.FxQuotationResult
import com.project.samsin.network.retrofit.RetrofitManager
import com.project.samsin.network.retrofit.models.CurrencyPairRate
import com.project.samsin.network.retrofit.models.GetCurrencyPairsResponse
import com.project.samsin.network.retrofit.models.GetRatesDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class ForexPairRepoImpl @Inject constructor() : ForexPairRepo {
    private val apiServices = RetrofitManager.apiServices

    //  for mocking changes
    private var latestResults = mutableListOf<FxQuotationResult>()

    override suspend fun getPairs(): Result<List<String>> {
        return try {
            val result = suspendCancellableCoroutine { continuation ->
                val call = apiServices.getFxParis()
                call.enqueue(object :
                    Callback<GetCurrencyPairsResponse> {
                    override fun onResponse(call: Call<GetCurrencyPairsResponse>, response: Response<GetCurrencyPairsResponse>) {
                        response.body()?.let {
                            if (it.code != 1001) {
                                onFailure(call, Throwable("code is not 1001"))
                                return
                            }

                            if (continuation.isActive) {
                                continuation.resumeWith(Result.success(it.supportedPairs.sorted()))
                            }
                        } ?: run {
                            onFailure(call, Throwable("null body"))
                        }
                    }

                    override fun onFailure(call: Call<GetCurrencyPairsResponse>, t: Throwable) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(t.message))
                        }
                    }
                })
                continuation.invokeOnCancellation { call.cancel() }
            }
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun quotePairs(pairs: List<String>): Result<List<FxQuotationResult>> {
        return try {
            val result = suspendCancellableCoroutine { continuation ->
                val call = apiServices.getRates(pairs.joinToString())
                call.enqueue(object :
                    Callback<GetRatesDataResponse> {
                    override fun onResponse(call: Call<GetRatesDataResponse>, response: Response<GetRatesDataResponse>) {
                        response.body()?.let {
                            if (it.code != 200) {
                                onFailure(call, Throwable("code is not 200"))
                                return
                            }

                            val list = it.rates.toFxQuotationResults().sortedBy { it.symbol }
                            if (continuation.isActive) {
                                continuation.resumeWith(Result.success(list))
                            }

                            latestResults = list.toMutableList()
                        } ?: run {
                            onFailure(call, Throwable("null body"))
                        }
                    }

                    override fun onFailure(call: Call<GetRatesDataResponse>, t: Throwable) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(t.message))
                        }
                    }
                })
                continuation.invokeOnCancellation {
                    call.cancel()
                }
            }
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun Map<String, CurrencyPairRate>.toFxQuotationResults(): List<FxQuotationResult> {
        return map {
            val symbol = it.key
            val rate = it.value.rate
            val open = rate
            val buy = rate.multiply(Random.nextDouble(0.9, 0.99).toBigDecimal())
            val sell = rate.multiply(Random.nextDouble(1.01, 1.1).toBigDecimal())
            val timestamp = it.value.timestamp
            FxQuotationResult(symbol = symbol, rate = rate, open = open, buy = buy, sell = sell, timestamp = timestamp)
        }
    }

    override fun subscribe(pairs: List<String>): Flow<FxQuotationResult> {
        return flow {
            while (currentCoroutineContext().isActive) {
                pairs.forEach { symbol ->
                    val index = latestResults.indexOfFirst { it.symbol == symbol }
                    if (index != -1) {
                        //  mock new result
                        val old = latestResults[index]
                        val newRate = old.rate?.multiply(Random.nextDouble(0.9, 1.1).toBigDecimal())
                        val newBuy = newRate?.multiply(Random.nextDouble(0.9, 0.99).toBigDecimal())
                        val newSell = newRate?.multiply(Random.nextDouble(1.01, 1.1).toBigDecimal())
                        val new = old.copy(rate = newRate, buy = newBuy, sell = newSell, timestamp = System.currentTimeMillis())

                        //  emit updated result
                        emit(new)

                        //  store updated result
                        latestResults[index] = new
                    }
                }
                kotlinx.coroutines.delay(1000)
            }
        }.flowOn(Dispatchers.IO)
    }
}