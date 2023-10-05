package com.taxapprf.taxapp.ui.currency.rate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.cbr.CurrencyRateModel
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding

class CurrencyRateAdapter
    : ListAdapter<CurrencyRateModel, CurrencyRateAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyRateAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentCurrencyRateAdapterItemBinding.inflate(inflater, parent, false)
        return CurrencyRateAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyRateAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<CurrencyRateModel>() {
        override fun areItemsTheSame(
            oldItem: CurrencyRateModel,
            newItem: CurrencyRateModel
        ) = oldItem.currency.ordinal == newItem.currency.ordinal

        override fun areContentsTheSame(
            oldItem: CurrencyRateModel,
            newItem: CurrencyRateModel
        ) = oldItem == newItem
    }
}