package com.taxapprf.taxapp.ui.transactions.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailBinding
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BottomSheetBaseFragment
import com.taxapprf.taxapp.ui.activity.MainViewModel
import com.taxapprf.taxapp.ui.getTransactionType
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TransactionDetailFragment : BottomSheetBaseFragment(R.layout.fragment_transaction_detail) {
    private val binding by viewBinding(FragmentTransactionDetailBinding::bind)
    private val viewModel by viewModels<TransactionDetailViewModel>()

    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel.observeAccount()

        prepCurrencies()
        prepTypes()
        prepListeners()

        viewModel.attachToBaseFragment()
        viewModel.observeState()
    }

    private fun MainViewModel.observeAccount() {
        account.observe(viewLifecycleOwner) { account ->
            viewModel.saveTransaction.accountKey = account.name

            with(activityViewModel) {
                if (report != null && transaction != null) {
                    viewModel.saveTransaction.update(report!!)
                    viewModel.saveTransaction.update(transaction!!)

                    report = null
                    transaction = null
                }
            }

            updateUI()
        }
    }

    private fun prepCurrencies() {
        val currencies = resources.getStringArray(R.array.transaction_currencies)
        currenciesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionDetailCurrencies.adapter = currenciesAdapter
        binding.spinnerTransactionDetailCurrencies.setSelection(
            currencies.indexOf(resources.getString(R.string.transaction_currency_usd))
        )
        viewModel.saveTransaction.currency = resources.getString(R.string.transaction_currency_usd)
    }

    private fun prepTypes() {
        val types = resources.getStringArray(R.array.transaction_types)
        typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionDetailType.adapter = typeAdapter
        binding.spinnerTransactionDetailType.setSelection(
            types.indexOf(resources.getString(R.string.transaction_type_trade))
        )
    }

    private fun prepListeners() {
        binding.buttonTransactionDetailDatePicker.setOnClickListener {
            showDatePicker {
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.saveTransaction.update(year, month, dayOfMonth)
                    binding.editTextTransactionDetailDate.setText(viewModel.saveTransaction.date)
                }
            }
        }

        binding.buttonTransactionDetailSave.setOnClickListener {
            activityViewModel.saveTransaction(viewModel.saveTransaction)
            popBackStack()
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
                        viewModel.saveTransaction.type = requireActivity().getTransactionType(it)
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
                    currenciesAdapter.getItem(position)?.let {
                        viewModel.saveTransaction.currency = it
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.editTextTransactionDetailSum.doOnTextChanged { text, _, _, _ ->
            var sum = 0.0

            text?.let {
                if (it.isNotEmpty()) {
                    sum = it.toString().toDouble()
                }
            }

            viewModel.saveTransaction.sum = sum
        }

        binding.editTextTransactionDetailName.doOnTextChanged { text, _, _, _ ->
            text?.let { viewModel.saveTransaction.name = it.toString() }
        }

        binding.editTextTransactionDetailDate.doOnTextChanged { text, _, _, _ ->
            text?.let { viewModel.saveTransaction.date = it.toString() }
        }
    }

    private fun TransactionDetailViewModel.observeState() {
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Success -> {
                    popBackStack()
                }

                else -> {}
            }
        }
    }

    private fun updateUI() {
        with(viewModel.saveTransaction) {
            binding.editTextTransactionDetailName.setText(name)
            binding.editTextTransactionDetailDate.setText(date)
            if (sum > 0) binding.editTextTransactionDetailSum.setText(sum.toString())
            updateCurrency(currency)
            updateType(type)
        }
    }

    private fun updateCurrency(currency: String) {
        binding.spinnerTransactionDetailCurrencies.setSelection(
            currenciesAdapter.getPosition(currency)
        )
    }

    private fun updateType(type: String) {
        binding.spinnerTransactionDetailType.setSelection(typeAdapter.getPosition(type))
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