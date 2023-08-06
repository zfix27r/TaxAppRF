package com.taxapprf.taxapp.ui.taxes

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.report.ReportAdapterModel
import com.taxapprf.taxapp.databinding.FragmentTaxesAdapterItemBinding

class ReportsAdapterViewHolder(
    private val binding: FragmentTaxesAdapterItemBinding,
    private val callback: ReportsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var report: ReportAdapterModel

    init {
        binding.root.setOnClickListener {
            callback.onClick(report.name)
        }
    }

    fun bind(reportAdapterModel: ReportAdapterModel) {
        report = reportAdapterModel

        binding.textViewYear.text = report.name
        binding.textViewYearSum.text = report.sum
    }
}