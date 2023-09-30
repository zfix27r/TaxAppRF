package com.taxapprf.taxapp.ui.currency.today

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.cbr.RateWithCurrencyModel
import com.taxapprf.taxapp.databinding.FragmentRatesTodayAdapterItemBinding


class CurrencyRatesTodayAdapter(
) : ListAdapter<RateWithCurrencyModel, CurrencyRatesTodayAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyRatesTodayAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentRatesTodayAdapterItemBinding.inflate(inflater, parent, false)
        return CurrencyRatesTodayAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyRatesTodayAdapterViewHolder, position: Int) {
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