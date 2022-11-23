package com.project.samsin.forex.watchlist

import com.project.samsin.forex.repo.ForexPairRepoImpl
import com.project.samsin.forex.usecase.WatchlistUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class WatchlistUseCaseImplUnitTest {
    @MockK
    private lateinit var repo: ForexPairRepoImpl

    private val useCase: WatchlistUseCaseImpl by lazy {
        WatchlistUseCaseImpl(repo)
    }

    private val mockPairs = listOf("AUDUSD", "EURGBP", "EURUSD")
    private val mockException = Exception("test")
    private val mockQuotationResults: List<FxQuotationResult> = mockPairs.map {
        FxQuotationResult(it, rate = BigDecimal.TEN, open = BigDecimal.TEN, buy = BigDecimal.TEN, sell = BigDecimal.TEN, timestamp = 1000)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `fetch pairs success`() {
        coEvery { repo.getPairs() } returns Result.success(mockPairs)
        runTest {
            val result = useCase.fetchPairs()
            assert(result.getOrNull() == mockPairs)
        }
    }

    @Test
    fun `fetch pairs failure`() {
        coEvery { repo.getPairs() } returns Result.failure(mockException)
        runTest {
            val result = useCase.fetchPairs()
            assert(result.exceptionOrNull() == mockException)
        }
    }

    @Test
    fun `quote pairs success`() {
        coEvery { repo.quotePairs(any()) } returns Result.success(mockQuotationResults)
        runTest {
            val quotations = useCase.quotePairs(mockPairs)
            assert(quotations.isSuccess)

            quotations.getOrNull()?.forEach { result ->
                val symbolMatch = mockPairs.firstOrNull { it == result.symbol } != null
                assert(symbolMatch)
            }
        }
    }

    @Test
    fun `quote pairs failure`() {
        coEvery { repo.quotePairs(any()) } returns Result.failure(mockException)
        runTest {
            val quotations = useCase.quotePairs(mockPairs)
            assert(quotations.exceptionOrNull() == mockException)
        }
    }

    @Test
    fun `subscribe pairs`() {
        coEvery { repo.subscribe(any()) } returns mockQuotationResults.asFlow()
        val streamingFlow = repo.subscribe(mockPairs)
        runTest {
            streamingFlow.collect { result ->
                val symbolMatch = mockPairs.firstOrNull { it == result.symbol } != null
                assert(symbolMatch)
            }
        }
    }
}