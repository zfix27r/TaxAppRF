package com.taxapprf.taxapp.ui.currency.today

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.cbr.CurrencyWithRateModel
import com.taxapprf.taxapp.databinding.FragmentRatesTodayAdapterItemBinding

class CurrencyRatesTodayAdapterViewHolder(
    private val binding: FragmentRatesTodayAdapterItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currency: CurrencyWithRateModel

    fun bind(currencyRateModel: CurrencyWithRateModel) {
        currency = currencyRateModel

        binding.textViewCurrencyName.text = currency.name
        binding.textViewCurrencyCode.text = currency.charCode
        binding.textViewCurrencyRate.text = currency.rate.toString()
    }
}