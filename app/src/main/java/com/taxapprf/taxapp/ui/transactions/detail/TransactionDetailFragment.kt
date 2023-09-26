package com.taxapprf.taxapp.ui.transactions.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailBinding
import com.taxapprf.taxapp.ui.BottomSheetBaseFragment
import com.taxapprf.taxapp.ui.formatDate
import com.taxapprf.taxapp.ui.getTransactionName
import com.taxapprf.taxapp.ui.getTransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TransactionDetailFragment : BottomSheetBaseFragment(R.layout.fragment_transaction_detail) {
    private val binding by viewBinding(FragmentTransactionDetailBinding::bind)
    private val viewModel by viewModels<TransactionDetailViewModel>()

    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachWithAccount()

        prepCurrencies()
        prepTypes()
        prepListeners()
    }

    override fun onAuthReady() {
        super.onAuthReady()
        viewModel.setFromReportModel(mainViewModel.report)
        viewModel.setFromTransactionModel(mainViewModel.transaction)
        updateUI()
    }

    private fun prepCurrencies() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currencies.collectLatest { currencies ->
                    currencies?.let {
                        val charCodes = it.map { it.charCode }

                        currenciesAdapter =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                charCodes
                            )
                        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerTransactionDetailCurrencies.adapter = currenciesAdapter
                        binding.spinnerTransactionDetailCurrencies.setSelection(viewModel.currencyPosition)
                    }
                }
            }
        }
    }

    private fun prepTypes() {
        val types = resources.getStringArray(R.array.transaction_types)
        typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionDetailType.adapter = typeAdapter
    }

    private fun prepListeners() {
        binding.buttonTransactionDetailDatePicker.setOnClickListener {
            showDatePicker {
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.checkDate(year, month, dayOfMonth)
                    updateDateText()
                }
            }
        }

        binding.buttonTransactionDetailSave.setOnClickListener {
            with(binding) {
                val updateNameResult = viewModel
                    .checkName(editTextTransactionDetailName.text)
                    .updateEditError(editTextTransactionDetailName)
                val updateDateResult = viewModel
                    .checkDate(editTextTransactionDetailDate.text)
                    .updateEditError(editTextTransactionDetailDate)
                val updateSumResult = viewModel
                    .checkSum(editTextTransactionDetailSum.text)
                    .updateEditError(editTextTransactionDetailSum)

                if (updateNameResult && updateDateResult && updateSumResult) {
                    mainViewModel.saveTransaction(viewModel.getSaveTransactionModel())
                    findNavController().popBackStack()
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
                    typeAdapter.getItem(position)?.let {
                        viewModel.type = requireActivity().getTransactionType(it)
                    }
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
                    viewModel.currencyId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun onSuccess() {
        super.onSuccess()
        findNavController().popBackStack()
    }

    private fun updateUI() {
        with(viewModel) {
            binding.editTextTransactionDetailName.setText(name)
            updateDateText()
            updateSumText()
            updateCurrency()
            updateType(type)
        }
    }

    private fun updateDateText() {
        binding.editTextTransactionDetailDate.setText(viewModel.date.formatDate())
    }

    private fun updateSumText() {
        if (viewModel.sum > 0)
            binding.editTextTransactionDetailSum.setText(viewModel.sum.toString())
    }

    private fun updateCurrency() {
        binding.spinnerTransactionDetailCurrencies.setSelection(viewModel.currencyPosition)
    }

    private fun updateType(type: Int) {
        binding.spinnerTransactionDetailType.setSelection(typeAdapter.getPosition(getString(type.getTransactionName())))
    }

    private fun showDatePicker(listener: () -> DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), listener.invoke(),
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }
}