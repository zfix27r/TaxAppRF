package com.taxapprf.taxapp.ui.currency.rate

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.currency.CurrencyRateModel
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding

class CurrencyRateAdapterViewHolder(
    private val binding: FragmentCurrencyRateAdapterItemBinding,
    private val currencyNames: List<String>,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currencyRate: CurrencyRateModel

    fun bind(currencyRateModel: CurrencyRateModel) {
        currencyRate = currencyRateModel

        binding.textCurrencyRateName.text = currencyNames[currencyRate.currency.ordinal]
        binding.textCurrencyRateNumCode.text = currencyRate.currency.numCode
        binding.textCurrencyRateCharCode.text = currencyRate.currency.name
        binding.textCurrencyRateRate.text = currencyRate.rate.toString()
    }
}