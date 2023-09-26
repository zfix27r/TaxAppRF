package com.taxapprf.taxapp.ui.transactions.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailBinding
import com.taxapprf.taxapp.ui.BottomSheetBaseFragment
import com.taxapprf.taxapp.ui.getTransactionName
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

        viewModel.attachWithAccount()

        prepCurrencies()
        prepTypes()
        prepListeners()
    }

    override fun onAuthReady() {
        super.onAuthReady()
        viewModel.report = mainViewModel.report
        viewModel.transaction = mainViewModel.transaction
        viewModel.currency = resources.getString(R.string.transaction_currency_usd)

        mainViewModel.report = null
        mainViewModel.transaction = null

        updateUI()
    }

    private fun prepCurrencies() {
        val currencies = resources.getStringArray(R.array.transaction_currencies)
        currenciesAdapter = ArrayAdapter(requireContext(), R.layout.list_item, currencies)
        (binding.spinnerTransactionDetailCurrencies as? AutoCompleteTextView)?.setAdapter(currenciesAdapter)
    }

    private fun prepTypes() {
        val types = resources.getStringArray(R.array.transaction_types)
        typeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
        (binding.spinnerTransactionDetailType as? AutoCompleteTextView)?.setAdapter(typeAdapter)
        binding.spinnerTransactionDetailType.setText(resources.getString(R.string.transaction_type_trade), false)
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
                    // TODO переместить во вьюмодел фрагмента? Привязка к жизненному циклу. Запускать глобал флоу?
                    mainViewModel.saveTransaction(viewModel.getSaveTransactionModel())
                    findNavController().popBackStack()
                }
            }
        }

        binding.spinnerTransactionDetailType.onItemClickListener =
            AdapterView.OnItemClickListener {
                    adapterView, view, position, id ->

                adapterView.getItemAtPosition(position)?.let {
                    val typeRus = adapterView.getItemAtPosition(position).toString()
                    viewModel.type = requireActivity().getTransactionType(typeRus)
                }
            }

        binding.spinnerTransactionDetailCurrencies.onItemClickListener =
            AdapterView.OnItemClickListener {
                    adapterView, view, position, id ->

                adapterView.getItemAtPosition(position)?.let {
                    currenciesAdapter.getItem(position)?.let {
                        viewModel.currency = it
                    }
                }
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
            updateCurrency(currency)
            updateType(type)
        }
    }

    private fun updateDateText() {
        binding.editTextTransactionDetailDate.setText(viewModel.date)
    }

    private fun updateSumText() {
        if (viewModel.sum > 0)
            binding.editTextTransactionDetailSum.setText(viewModel.sum.toString())
    }

    private fun updateCurrency(currency: String) {
        Log.d("OLGA", "currency: String: $currency")
        val positionCurrency = currenciesAdapter.getPosition(currency)
        Log.d("OLGA", "positionCurrency: $positionCurrency")
        Log.d("OLGA", "currenciesAdapter.getItem: ${currenciesAdapter.getItem(positionCurrency)}")
        binding.spinnerTransactionDetailCurrencies.setText(currenciesAdapter.getItem(positionCurrency), false)
    }

    private fun updateType(typeEnum: String) {
        val typeRus = typeEnum.getTransactionName()
        val positionType = typeAdapter.getPosition(resources.getString(typeRus))
        binding.spinnerTransactionDetailType.setText(typeAdapter.getItem(positionType), false)
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