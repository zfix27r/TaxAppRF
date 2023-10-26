package com.taxapprf.taxapp.ui.reports

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.transactions.ReportModel
import com.taxapprf.taxapp.app.databinding.FragmentReportsAdapterItemBinding
import com.taxapprf.taxapp.ui.toAppDouble

class ReportsAdapterViewHolder(
    private val binding: FragmentReportsAdapterItemBinding,
    private val callback: ReportsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var _report: ReportModel
    val report
        get() = _report

    init {
        binding.root.setOnClickListener {
            callback.onItemClick(_report)
        }

        binding.buttonReportsAdapterItemMore.setOnClickListener {
            callback.onMoreClick(_report)
        }
    }

    fun bind(reportAdapterModel: ReportModel) {
        _report = reportAdapterModel

        binding.textReportsAdapterItemYear.text = _report.name
        binding.textReportsAdapterItemTax.text = _report.taxRUB.toAppDouble()
    }
}