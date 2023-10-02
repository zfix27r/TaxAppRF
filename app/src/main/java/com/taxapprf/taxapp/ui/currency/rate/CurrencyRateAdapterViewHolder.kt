package com.taxapprf.taxapp.ui.currency.rate

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.cbr.RateWithCurrencyModel
import com.taxapprf.taxapp.databinding.FragmentCurrencyRateAdapterItemBinding
import com.taxapprf.taxapp.ui.ROUND_SIX
import com.taxapprf.taxapp.ui.round

class CurrencyRateAdapterViewHolder(
    private val binding: FragmentCurrencyRateAdapterItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currency: RateWithCurrencyModel

    fun bind(currencyRateModel: RateWithCurrencyModel) {
        currency = currencyRateModel

        binding.textCurrencyRateName.text = currency.name
        binding.textCurrencyRateNumCode.text = currency.numCode.toString()
        binding.textCurrencyRateCharCode.text = currency.charCode
        binding.textCurrencyRateRate.text = currency.rate?.round(ROUND_SIX).toString()
    }
}