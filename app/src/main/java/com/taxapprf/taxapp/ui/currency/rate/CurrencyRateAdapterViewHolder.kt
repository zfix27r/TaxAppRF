package com.taxapprf.taxapp.ui.currency.rate

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.currency.CurrencyRateModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding
import com.taxapprf.taxapp.ui.toAppDouble

class CurrencyRateAdapterViewHolder(
    private val binding: FragmentCurrencyRateAdapterItemBinding,
    private val currencyNames: List<String>,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currencyRate: CurrencyRateModel
    fun bind(currencyRateModel: CurrencyRateModel) {
        currencyRate = currencyRateModel

        binding.textViewCurrencyRateName.text = binding.root.context.getString(
            R.string.currency_rate_title_with_num_code,
            currencyNames[currencyRate.currency.ordinal],
            currencyRate.currency.numCode
        )
        binding.textViewCurrencyRateCharCode.text = currencyRate.currency.name
        binding.textViewCurrencyRateRate.text = currencyRate.rate?.toAppDouble() ?: "0.00"
    }
}