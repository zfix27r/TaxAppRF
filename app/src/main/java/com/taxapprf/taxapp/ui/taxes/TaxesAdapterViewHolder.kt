package com.taxapprf.taxapp.ui.taxes

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.taxes.TaxAdapterModel
import com.taxapprf.taxapp.databinding.FragmentTaxesAdapterItemBinding

class TaxesAdapterViewHolder(
    private val binding: FragmentTaxesAdapterItemBinding,
    private val callback: TaxesAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var taxes: TaxAdapterModel

    init {
        binding.root.setOnClickListener {
            callback.onClick(taxes.year)
        }
    }

    fun bind(taxesAdapterModel: TaxAdapterModel) {
        taxes = taxesAdapterModel

        binding.textViewYear.text = taxes.year
        binding.textViewYearSum.text = taxes.sum
    }
}