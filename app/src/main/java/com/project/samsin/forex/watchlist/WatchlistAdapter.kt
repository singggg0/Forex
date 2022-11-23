package com.project.samsin.forex.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.samsin.forex.R
import com.project.samsin.forex.databinding.ItemWatchlistBinding
import com.project.samsin.forex.extension.getTrendColor
import com.project.samsin.forex.extension.toFormatDecimal
import java.math.BigDecimal

class WatchlistAdapter :
    ListAdapter<FxQuotationResult, WatchlistAdapter.ViewHolder>(WatchlistDiffCallback()) {

    class ViewHolder(val binding: ItemWatchlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWatchlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvSymbol.text = item.symbol
            updateBuyValue(this, item.buy)
            updateSellValue(this, item.sell)
            updateChangeValue(this, item.change)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val payload = payloads.firstOrNull() as? WatchlistDiffCallback.Payload ?: return
            updateItemView(holder.binding, payload)
        }
    }

    private fun updateItemView(binding: ItemWatchlistBinding, payload: WatchlistDiffCallback.Payload) {
        payload.changes.forEach {
            when (it) {
                is WatchlistDiffCallback.FieldChange.Buy -> updateBuyValue(binding, it.value)
                is WatchlistDiffCallback.FieldChange.Sell -> updateSellValue(binding, it.value)
                is WatchlistDiffCallback.FieldChange.Change -> updateChangeValue(binding, it.value)
            }
        }
    }

    private fun updateBuyValue(binding: ItemWatchlistBinding, value: BigDecimal?) {
        binding.tvBuy.text = value.toFormatDecimal(2)
    }

    private fun updateSellValue(binding: ItemWatchlistBinding, value: BigDecimal?) {
        binding.tvSell.text = value.toFormatDecimal(2)
    }

    private fun updateChangeValue(binding: ItemWatchlistBinding, value: BigDecimal?) {
        binding.tvChange.let {
            var change = "-"
            var color = ContextCompat.getColor(it.context, R.color.white)
            if (value != null) {
                change = "${value.toFormatDecimal(2)}%"
                color = value.getTrendColor(it.context, defaultColor = color)
            }
            it.text = change
            it.setTextColor(color)
        }
    }
}