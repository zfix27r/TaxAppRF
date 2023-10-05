package com.taxapprf.taxapp.ui.currency.rate

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.cbr.CurrencyRateModel
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding

class CurrencyRateAdapterViewHolder(
    private val binding: FragmentCurrencyRateAdapterItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currencyRate: CurrencyRateModel

    fun bind(currencyRateModel: CurrencyRateModel) {
        currencyRate = currencyRateModel

        currencyRate.localCurrencyName?.let { binding.textCurrencyRateName.text = it }
        binding.textCurrencyRateNumCode.text = currencyRate.currency.numCode
        binding.textCurrencyRateCharCode.text = currencyRate.currency.name
        binding.textCurrencyRateRate.text = currencyRate.rate.toString()
    }
}