package com.project.samsin.forex.watchlist

import androidx.lifecycle.*
import com.project.samsin.forex.usecase.WatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(private val useCase: WatchlistUseCase) : ViewModel() {
    private val goLong = BigDecimal("10000")

    private val streamingFlow: MutableSharedFlow<FxQuotationResult> = MutableSharedFlow()
    private var streamingJob: Job? = null
    private val _quotationResults: MediatorLiveData<List<FxQuotationResult>?> = MediatorLiveData()
    private val _used: MutableLiveData<BigDecimal?> = MutableLiveData()
    private val _margin: MutableLiveData<BigDecimal?> = MutableLiveData()
    private val _showLoadingDialog: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _showError: MutableLiveData<String> = MutableLiveData()

    val quotationResults: LiveData<List<FxQuotationResult>?> = _quotationResults
    val equity: LiveData<BigDecimal?> = _quotationResults.map { computeEquity(it) }
    val used: LiveData<BigDecimal?> = _used
    val margin: LiveData<BigDecimal?> = _margin
    val balance: LiveData<BigDecimal?> = _quotationResults.map { computeBalance(it) }
    val showLoadingDialog: LiveData<Boolean> = _showLoadingDialog
    val showError: LiveData<String> = _showError

    init {
        _quotationResults.addSource(streamingFlow.asLiveData(timeoutInMs = 0)) {
            val updated = updateQuotationsResults(_quotationResults.value, it)
            _quotationResults.value = updated
        }
    }

    fun onCreate(){
        _margin.postValue(BigDecimal("12345"))
        _used.postValue(BigDecimal("12345"))
        fetchAndQuotePairs()
    }

    fun onResume() {
        _quotationResults.value?.map { it.symbol }?.let {
            subscribePairs(it)
        }
    }

    fun onPause() {
        streamingJob?.cancel()
        streamingJob = null
    }

    private fun fetchAndQuotePairs() {
        viewModelScope.launch {
            val defaultResults: List<FxQuotationResult>

            //  get available paris
            val pairs = useCase.fetchPairs().fold(
                onSuccess = { it },
                onFailure = {
                    _showError.postValue(it.message)
                    return@launch
                }
            )
            defaultResults = pairs.map { FxQuotationResult(symbol = it, timestamp = System.currentTimeMillis()) }

            _showLoadingDialog.postValue(false)

            //  post pairs without quotation data
            _quotationResults.postValue(defaultResults)

            //  quote pairs
            val results = useCase.quotePairs(pairs).fold(
                onSuccess = {
                    subscribePairs(pairs)
                    it
                },
                onFailure = { defaultResults }
            )
            _quotationResults.postValue(results)
        }
    }

    private fun subscribePairs(pairs: List<String>) {
        if (streamingJob?.isActive == true) {
            return
        }
        streamingJob = viewModelScope.launch {
            useCase.subscribePairs(pairs).collect {
                streamingFlow.emit(it)
            }
        }
    }

    private fun updateQuotationsResults(currentResults: List<FxQuotationResult>?, newData: FxQuotationResult): List<FxQuotationResult>? {
        val updated = currentResults?.toMutableList()?.let { results ->
            val index = results.indexOfFirst { it.symbol == newData.symbol }
            if (index != -1) {
                results[index] = newData
            }
            results
        }
        return updated
    }

    private fun computeEquity(results: List<FxQuotationResult>?): BigDecimal? {
        val mapped = results?.mapNotNull { it.rate }
        return if (mapped.isNullOrEmpty()) {
            null
        } else {
            mapped.reduce { acc, bigDecimal ->
                acc + bigDecimal
            }
        }
    }

    private fun computeBalance(results: List<FxQuotationResult>?): BigDecimal? {
        if (results == null) return null
        return goLong.multiply(results.size.toBigDecimal())
    }
}