package com.project.samsin.forex.watchlist

import org.junit.Test
import java.math.BigDecimal

class FxQuotationResultUnitTest {

    @Test
    fun `change is null when open & rate are null`() {
        val change = FxQuotationResult("", timestamp = 1).change
        assert(change == null)
    }

    @Test
    fun `change is null when open or rate is null`() {
        val changeWhenOpenIsNull = FxQuotationResult("", rate = BigDecimal.TEN, timestamp = 1).change
        val changeWhenRateIsNull = FxQuotationResult("", open = BigDecimal.TEN, timestamp = 1).change
        assert(changeWhenOpenIsNull == null)
        assert(changeWhenRateIsNull == null)
    }

    @Test
    fun `change is correct when open & rate is not null`() {
        val change = FxQuotationResult("", open = BigDecimal("100"), rate = BigDecimal("200"), timestamp = 1).change
        assert(change?.compareTo(BigDecimal.ONE) == 0)
    }
}