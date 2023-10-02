package com.taxapprf.taxapp.ui.transactions.detail

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.transaction.TransactionType
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attach()

        prepTypes()
        prepListeners()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currencies.collectLatest { currencies ->
                    currencies?.prepCurrencies()
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transaction?.collectLatest {
                    updateUI()
                } ?: updateUI()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.report?.collectLatest { updateUI() }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.name = binding.editTextTransactionDetailName.text.toString()
        viewModel.date = binding.editTextTransactionDetailDate.text.toString()
        viewModel.sum = binding.editTextTransactionDetailSum.text.toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        return dialog.wrapHeight()
    }

    private fun List<CurrencyModel>.prepCurrencies() {
        val charCodes = map { it.charCode }

        currenciesAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                charCodes
            )
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionDetailCurrencies.adapter = currenciesAdapter
        binding.spinnerTransactionDetailCurrencies.setSelection(
            currenciesAdapter.getPosition(viewModel.currency)
        )
    }

    private fun prepTypes() {
        val transactionTypes = resources.getStringArray(R.array.transaction_types)
        typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, transactionTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionDetailType.adapter = typeAdapter
    }

    private val datePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.checkDate(year, month, dayOfMonth)
            updateDateText()
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

        binding.spinnerTransactionDetailType.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    typeAdapter.getItem(position)
                        ?.toTransactionTypeK()?.let { viewModel.typeK = it }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerTransactionDetailCurrencies.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currenciesAdapter.getItem(position)?.let { viewModel.currency = it }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.buttonTransactionDetailDismiss.setOnClickListener {
            dismiss()
        }
    }

    private fun updateUI() {
        binding.editTextTransactionDetailName.setText(viewModel.name)
        binding.editTextTransactionDetailSum.setText(viewModel.sum)
        updateDateText()
        binding.spinnerTransactionDetailType.setSelection(
            typeAdapter.getPosition(viewModel.typeK.toTransactionTypeName())
        )
    }

    private fun updateDateText() {
        binding.editTextTransactionDetailDate.setText(viewModel.date)
    }

    private fun Int.toTransactionTypeName() =
        when (this) {
            TransactionType.TRADE.k -> getString(R.string.transaction_type_trade)
            TransactionType.COMMISSION.k -> getString(R.string.transaction_type_commission)
            TransactionType.FUNDING_WITHDRAWAL.k -> getString(R.string.transaction_type_funding_withdrawal)
            else -> null
        }

    private fun String.toTransactionTypeK() =
        when (this) {
            getString(R.string.transaction_type_trade) -> TransactionType.TRADE.k
            getString(R.string.transaction_type_commission) -> TransactionType.COMMISSION.k
            getString(R.string.transaction_type_funding_withdrawal) -> TransactionType.FUNDING_WITHDRAWAL.k
            else -> null
        }
}