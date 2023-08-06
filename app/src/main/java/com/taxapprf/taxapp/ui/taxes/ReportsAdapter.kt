package com.taxapprf.taxapp.ui.taxes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.report.ReportAdapterModel
import com.taxapprf.taxapp.databinding.FragmentTaxesAdapterItemBinding

class ReportsAdapter(
    private val callback: () -> ReportsAdapterCallback,
) : ListAdapter<ReportAdapterModel, ReportsAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentTaxesAdapterItemBinding.inflate(inflater, parent, false)
        return ReportsAdapterViewHolder(binding, callback.invoke())
    }

    override fun onBindViewHolder(holder: ReportsAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReportAdapterModel>() {
        override fun areItemsTheSame(
            oldItem: ReportAdapterModel,
            newItem: ReportAdapterModel
        ) = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: ReportAdapterModel,
            newItem: ReportAdapterModel
        ) = oldItem == newItem
    }
}