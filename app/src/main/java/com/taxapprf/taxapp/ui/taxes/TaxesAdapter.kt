package com.taxapprf.taxapp.ui.taxes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.taxes.ReportAdapterModel
import com.taxapprf.taxapp.databinding.FragmentTaxesAdapterItemBinding

class TaxesAdapter(
    private val callback: () -> TaxesAdapterCallback,
) : ListAdapter<ReportAdapterModel, TaxesAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxesAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentTaxesAdapterItemBinding.inflate(inflater, parent, false)
        return TaxesAdapterViewHolder(binding, callback.invoke())
    }

    override fun onBindViewHolder(holder: TaxesAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReportAdapterModel>() {
        override fun areItemsTheSame(
            oldItem: ReportAdapterModel,
            newItem: ReportAdapterModel
        ) = oldItem.year == newItem.year

        override fun areContentsTheSame(
            oldItem: ReportAdapterModel,
            newItem: ReportAdapterModel
        ) = oldItem == newItem
    }
}