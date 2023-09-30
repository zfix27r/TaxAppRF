package com.taxapprf.taxapp.ui.currency.rate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.cbr.RateWithCurrencyModel
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding

class CurrencyRateAdapter
    : ListAdapter<RateWithCurrencyModel, CurrencyRateAdapterViewHolder>(DiffCallback()) {
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

    private class DiffCallback : DiffUtil.ItemCallback<RateWithCurrencyModel>() {
        override fun areItemsTheSame(
            oldItem: RateWithCurrencyModel,
            newItem: RateWithCurrencyModel
        ) = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: RateWithCurrencyModel,
            newItem: RateWithCurrencyModel
        ) = oldItem == newItem
    }
}