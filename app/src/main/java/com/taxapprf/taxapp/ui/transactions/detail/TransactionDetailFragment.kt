package com.taxapprf.taxapp.ui.transactions.detail

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.currency.Currencies
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailBinding
import com.taxapprf.taxapp.ui.BaseBottomSheetFragment
import com.taxapprf.taxapp.ui.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionDetailFragment : BaseBottomSheetFragment(R.layout.fragment_transaction_detail) {
    private val binding by viewBinding(FragmentTransactionDetailBinding::bind)
    private val viewModel by viewModels<TransactionDetailViewModel>()

    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>
    private lateinit var transactionTypes: List<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog.wrapHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attach()
        observeTransactionDetail()

        prepTransactionTypes()
        prepCurrencies()
        prepListeners()
    }

    override fun onPause() {
        super.onPause()
        viewModel.name = binding.editTextTransactionDetailName.text.toString()
        viewModel.date = binding.editTextTransactionDetailDate.text.toString()
        viewModel.sum = binding.editTextTransactionDetailSum.text.toString()
    }

    private fun observeTransactionDetail() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionDetailModel.collectLatest {
                    updateUI()
                }
            }
        }
    }

    private fun prepTransactionTypes() {
        transactionTypes = resources.getStringArray(R.array.transaction_types).toList()
        typeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, transactionTypes)
        binding.spinnerTransactionDetailType.setAdapter(typeAdapter)
        updateSelectedTransactionTypeSpinner()
    }

    private fun prepCurrencies() {
        val currencies = Currencies.values().map { it.name }
        currenciesAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, currencies)
        binding.spinnerTransactionDetailCurrencies.setAdapter(currenciesAdapter)
        updateSelectedCurrencySpinner()
    }

    private fun prepListeners() {
        binding.buttonTransactionDetailDatePicker.setOnClickListener {
            viewModel.date.showDatePickerDialog(requireContext(), datePickerListener)
        }

        binding.buttonTransactionDetailSave.setOnClickListener {
            with(binding) {
                viewModel.name = binding.editTextTransactionDetailName.text.toString()
                viewModel.date = binding.editTextTransactionDetailDate.text.toString()
                viewModel.sum = binding.editTextTransactionDetailSum.text.toString()

                val updateNameResult = viewModel.checkName()
                    .updateEditError(editTextTransactionDetailName)
                val updateDateResult = viewModel.checkDate()
                    .updateEditError(editTextTransactionDetailDate)
                val updateSumResult = viewModel.checkSum()
                    .updateEditError(editTextTransactionDetailSum)

                if (updateNameResult && updateDateResult && updateSumResult) {
                    viewModel.getSaveTransactionModel()?.let {
                        mainViewModel.saveTransaction(it)
                        dismiss()
                    }
                }
            }
        }

        binding.spinnerTransactionDetailType.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.transactionTypeOrdinal = position
            }

        binding.spinnerTransactionDetailCurrencies.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.currencyOrdinal = position
            }

        binding.buttonTransactionDetailDismiss.setOnClickListener {
            dismiss()
        }
    }

    private val datePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.checkDate(year, month, dayOfMonth)
            updateDateText()
        }

    private fun updateUI() {
        binding.editTextTransactionDetailName.setText(viewModel.name)
        binding.editTextTransactionDetailSum.setText(viewModel.sum)
        updateDateText()
        updateSelectedTransactionTypeSpinner()
        updateSelectedCurrencySpinner()
    }

    private fun updateSelectedTransactionTypeSpinner() {
        binding.spinnerTransactionDetailType.setText(transactionTypes[viewModel.transactionTypeOrdinal], false)
    }

    private fun updateSelectedCurrencySpinner() {
        binding.spinnerTransactionDetailCurrencies.setText(viewModel.currencyCharCode, false)
    }

    private fun updateDateText() {
        binding.editTextTransactionDetailDate.setText(viewModel.date)
    }
}