package com.taxapprf.taxapp.ui.currency.today

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.currency.CurrencyModel
import com.taxapprf.taxapp.databinding.FragmentRatesTodayAdapterItemBinding


class CurrencyRatesTodayAdapter(
) : ListAdapter<CurrencyModel, CurrencyRatesTodayAdapterViewHolder>(DiffCallback()) {
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

    private class DiffCallback : DiffUtil.ItemCallback<CurrencyModel>() {
        override fun areItemsTheSame(
            oldItem: CurrencyModel,
            newItem: CurrencyModel
        ) = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: CurrencyModel,
            newItem: CurrencyModel
        ) = oldItem == newItem
    }
}