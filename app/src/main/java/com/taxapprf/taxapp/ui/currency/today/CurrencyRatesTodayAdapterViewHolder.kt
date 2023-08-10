package com.taxapprf.taxapp.ui.currency.today

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.currency.CurrencyModel
import com.taxapprf.taxapp.databinding.FragmentRatesTodayAdapterItemBinding

class CurrencyRatesTodayAdapterViewHolder(
    private val binding: FragmentRatesTodayAdapterItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currency: CurrencyModel

    fun bind(currencyRateModel: CurrencyModel) {
        currency = currencyRateModel

        binding.textViewCurrencyName.text = currency.name
        binding.textViewCurrencyCode.text = currency.code
        binding.textViewCurrencyRate.text = currency.rate.toString()
    }
}