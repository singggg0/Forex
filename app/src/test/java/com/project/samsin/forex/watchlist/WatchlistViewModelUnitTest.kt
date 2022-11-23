package com.project.samsin.forex.watchlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.samsin.forex.MainCoroutineRule
import com.project.samsin.forex.getOrAwaitValue
import com.project.samsin.forex.usecase.WatchlistUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class WatchlistViewModelUnitTest {
    private val mockPairs = listOf("AUDUSD", "EURGBP", "EURUSD")
    private val mockException = Exception("test")
    private val mockQuotationResults: List<FxQuotationResult> = mockPairs.map {
        FxQuotationResult(it, rate = BigDecimal.TEN, open = BigDecimal.TEN, buy = BigDecimal.TEN, sell = BigDecimal.TEN, timestamp = 1000)
    }

    @MockK(relaxUnitFun = true)
    private lateinit var useCase: WatchlistUseCase
    private val viewModel: WatchlistViewModel by lazy {
        WatchlistViewModel(useCase)
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init when fetch pairs fail`() {
        coEvery { useCase.fetchPairs() } returns Result.failure(mockException)

        viewModel.onCreate()
        val result = viewModel.showError.getOrAwaitValue()
        assert(result == mockException.message)
    }

    @Test
    fun `init when quote pairs fail`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.failure(mockException)

        viewModel.onCreate()

        val showLoading = viewModel.showLoadingDialog.getOrAwaitValue()
        val quotations = viewModel.quotationResults.getOrAwaitValue()
        val areSymbolsMatch = areSymbolMatch(mockPairs, quotations)
        val hasNoQuotationData = hasNoQuotationData(quotations)

        assert(!showLoading)
        assert(areSymbolsMatch)
        assert(hasNoQuotationData)
    }

    @Test
    fun `init when quote pairs success`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.success(mockQuotationResults)
        coEvery { useCase.subscribePairs(any()) } returns emptyFlow()

        viewModel.onCreate()

        val showLoading = viewModel.showLoadingDialog.getOrAwaitValue()
        val quotations = viewModel.quotationResults.getOrAwaitValue()
        val symbolsAreMatch = areSymbolMatch(mockPairs, quotations)
        val hasQuotationData = !hasNoQuotationData(quotations)

        assert(!showLoading)
        assert(symbolsAreMatch)
        assert(hasQuotationData)
        verify(exactly = 1) { useCase.subscribePairs(any()) }
    }

    @Test
    fun `test equity`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.success(mockQuotationResults)
        coEvery { useCase.subscribePairs(any()) } returns emptyFlow()

        viewModel.onCreate()

        val equity = viewModel.equity.getOrAwaitValue()
        val expected = mockQuotationResults.mapNotNull { it.rate }.reduce { acc, bigDecimal ->
            acc + bigDecimal
        }
        assert(equity?.compareTo(expected) == 0)
    }

    @Test
    fun `test used`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.success(mockQuotationResults)
        coEvery { useCase.subscribePairs(any()) } returns emptyFlow()

        viewModel.onCreate()

        val equity = viewModel.used.getOrAwaitValue()
        val expected = BigDecimal("12345")
        assert(equity?.compareTo(expected) == 0)
    }

    @Test
    fun `test margin`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.success(mockQuotationResults)
        coEvery { useCase.subscribePairs(any()) } returns emptyFlow()

        viewModel.onCreate()

        val margin = viewModel.margin.getOrAwaitValue()
        val expected = BigDecimal("12345")
        assert(margin?.compareTo(expected) == 0)
    }

    @Test
    fun `test balance`() {
        coEvery { useCase.fetchPairs() } returns Result.success(mockPairs)
        coEvery { useCase.quotePairs(any()) } returns Result.success(mockQuotationResults)
        coEvery { useCase.subscribePairs(any()) } returns emptyFlow()

        viewModel.onCreate()

        val balance = viewModel.balance.getOrAwaitValue()
        val expected = BigDecimal("10000").multiply(mockQuotationResults.size.toBigDecimal())
        assert(balance?.compareTo(expected) == 0)
    }

    private fun areSymbolMatch(symbol: List<String>, quotationResults: List<FxQuotationResult>?): Boolean {
        var match = true
        symbol.forEach { syb ->
            if (quotationResults == null) {
                match = false
                return@forEach
            }

            if (quotationResults.firstOrNull { it.symbol == syb } == null) {
                match = false
                return@forEach
            }
        }
        return match
    }

    private fun hasNoQuotationData(quotationResults: List<FxQuotationResult>?): Boolean {
        if (quotationResults == null) return false
        var hasData = false
        quotationResults.forEach {
            if (it.buy != null || it.sell != null || it.rate != null || it.open != null) {
                hasData = true
            }
        }
        return !hasData
    }
}