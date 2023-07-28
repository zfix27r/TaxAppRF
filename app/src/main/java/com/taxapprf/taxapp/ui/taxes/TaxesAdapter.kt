package com.taxapprf.taxapp.ui.taxes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.taxes.TaxesAdapterModel
import com.taxapprf.taxapp.databinding.FragmentTaxesAdapterItemBinding

class TaxesAdapter(
    private val callback: () -> TaxesAdapterCallback,
) : ListAdapter<TaxesAdapterModel, TaxesAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxesAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentTaxesAdapterItemBinding.inflate(inflater, parent, false)
        return TaxesAdapterViewHolder(binding, callback.invoke())
    }

    override fun onBindViewHolder(holder: TaxesAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<TaxesAdapterModel>() {
        override fun areItemsTheSame(
            oldItem: TaxesAdapterModel,
            newItem: TaxesAdapterModel
        ) = oldItem.year == newItem.year

        override fun areContentsTheSame(
            oldItem: TaxesAdapterModel,
            newItem: TaxesAdapterModel
        ) = oldItem == newItem
    }
}