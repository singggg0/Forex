package com.project.samsin.forex.watchlist

import java.math.BigDecimal

data class FxQuotationResult(val symbol: String,
    val rate: BigDecimal? = null,
    val open: BigDecimal? = null,
    val buy: BigDecimal? = null,
    val sell: BigDecimal? = null,
    val timestamp: Long) {
    val change: BigDecimal?
        get() {
            return if (rate != null && open != null) {
                (rate - open).div(open)
            } else {
                null
            }
        }
}