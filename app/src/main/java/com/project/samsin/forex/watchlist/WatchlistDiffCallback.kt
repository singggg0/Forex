package com.project.samsin.forex.watchlist

import androidx.recyclerview.widget.DiffUtil
import java.math.BigDecimal

class WatchlistDiffCallback : DiffUtil.ItemCallback<FxQuotationResult>() {
    override fun areItemsTheSame(oldItem: FxQuotationResult, newItem: FxQuotationResult): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: FxQuotationResult, newItem: FxQuotationResult): Boolean {
        return getChanges(oldItem, newItem).isEmpty()
    }

    override fun getChangePayload(oldItem: FxQuotationResult, newItem: FxQuotationResult): Any {
        return Payload(getChanges(oldItem, newItem))
    }

    private fun getChanges(oldItem: FxQuotationResult, newItem: FxQuotationResult): MutableList<FieldChange> {
        val changes = mutableListOf<FieldChange>()
        if (oldItem.change != newItem.change) changes.add(FieldChange.Change(newItem.change))
        if (oldItem.buy != newItem.buy) changes.add(FieldChange.Buy(newItem.buy))
        if (oldItem.sell != newItem.sell) changes.add(FieldChange.Sell(newItem.sell))
        return changes
    }

    data class Payload(val changes: List<FieldChange>)

    sealed class FieldChange {
        data class Buy(val value: BigDecimal?) : FieldChange()
        data class Sell(val value: BigDecimal?) : FieldChange()
        data class Change(val value: BigDecimal?) : FieldChange()
    }
}