package com.taxapprf.taxapp.ui.currency.today

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentCurrencyRatesBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyRatesFragment : BaseFragment(R.layout.fragment_currency_rates) {
    private val binding by viewBinding(FragmentCurrencyRatesBinding::bind)
    private val viewModel by viewModels<CurrencyRatesViewModel>()
    private val adapter = CurrencyRatesTodayAdapter()

    private val datePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.date = viewModel.toEpochDay(year, month, dayOfMonth)
            observeRateWithCurrencies()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.attach()

        toolbar.updateMenu(R.menu.toolbar_currency_rate) {
            when (it.itemId) {
                R.id.toolbar_calendar -> {
                    showDatePickerDialog(requireContext(), datePickerListener)
                    true
                }

                else -> false
            }
        }

        binding.recyclerviewCurrencies.adapter = adapter
        observeRateWithCurrencies()
    }

    private fun observeRateWithCurrencies() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ratesWithCurrency().collectLatest { rateWithCurrencies ->
                    rateWithCurrencies?.let {
                        adapter.submitList(it)
                    }
                }
            }
        }
    }
}