package com.taxapprf.taxapp.ui.transaction.detail

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.hideKeyboard
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class TransactionDetailFragment : BaseFragment(R.layout.fragment_transaction_detail) {
    private val binding by viewBinding(FragmentTransactionDetailBinding::bind)
    private val viewModel by viewModels<TransactionDetailViewModel>()

    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var typeTransactionAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepCurrencies()
        prepTypeTransaction()

        viewModel.attachToBaseFragment()
        viewModel.transactionLiveData.observe(viewLifecycleOwner) { updateUI(it) }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.SuccessEdit -> navToTaxes()
                is BaseState.SuccessDelete -> {
                    binding.root.showSnackBar(R.string.transaction_detail_delete_success)
                    popBackStack()
                }

                else -> {}
            }
        }

        binding.imageDetailDate.setOnClickListener {
            showDatePicker {
                OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.saveDate(year, month, dayOfMonth)
                }
            }
        }

        binding.buttonDetailDelete.setOnClickListener {
/*            val dialog =
                TransactionsDeleteDialogFragment()
            dialog.show(childFragmentManager, "deleteTransDialog")
            dialog.verificationStatus.observe(viewLifecycleOwner) {
                viewModel.deleteTransaction()
            }*/
        }

        binding.buttonDetailUpdate.setOnClickListener {
            viewModel.saveTransaction()
        }

        binding.spinnerDetailType.setOnItemClickListener { _, _, position, _ ->
            typeTransactionAdapter.getItem(position)?.let {
                viewModel.transactionType = it
            }
        }

        binding.spinnerDetailCurrencies.setOnItemClickListener { _, _, position, _ ->
            currenciesAdapter.getItem(position)?.let {
                viewModel.transactionCurrency = it
            }
        }

        binding.editDetailSum.doOnTextChanged { text, _, _, _ ->
            viewModel.transactionSum = text.toString().toDouble()
        }

        binding.editDetailId.doOnTextChanged { text, _, _, _ ->
            viewModel.transactionId = text.toString()
        }
    }

    private fun updateUI(transaction: TransactionModel) {
        binding.editDetailId.setText(transaction.id)
        binding.editDetailDate.setText(transaction.date)
        binding.editDetailSum.setText(transaction.sum.toString())
        updateCurrencies(transaction.currency)
        updateTypeTransaction(transaction.type)
    }

    private fun updateCurrencies(currency: String) {
        binding.spinnerDetailCurrencies.setSelection(currenciesAdapter.getPosition(currency))
    }

    private fun updateTypeTransaction(type: String) {
        binding.spinnerDetailType.setSelection(typeTransactionAdapter.getPosition(type))
    }

    private fun prepCurrencies() {
        val arrayCurrencies = resources.getStringArray(R.array.transaction_currencies)
        currenciesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCurrencies)
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetailCurrencies.adapter = currenciesAdapter
    }

    private fun prepTypeTransaction() {
        val typeTransactionArray = resources.getStringArray(R.array.transaction_types)
        typeTransactionAdapter = object :
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                typeTransactionArray
            ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) tv.setTextColor(Color.GRAY)
                else tv.setTextColor(Color.BLACK)

                return view
            }
        }
        typeTransactionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetailType.adapter = typeTransactionAdapter
    }


    /*            val dateCheck = DateCheck(date!!.text.toString())
                val currentYear: String
                currentYear = if (!dateCheck.check()) {
                    Snackbar.make(v, "Неправильный формат даты!", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                } else {
                    dateCheck.year
                }*/
    /*            val doubleCheck = DoubleCheck(sum.text.toString())
                val sumDouble: Double
                sumDouble = if (!doubleCheck.isCheck) {
                    Snackbar.make(v, "Неправильно введена сумма!", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                } else {
                    doubleCheck.numDouble
                }*/

    override fun onStop() {
        super.onStop()
        binding.root.hideKeyboard()
    }

    private fun showDatePicker(listener: () -> OnDateSetListener) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), listener.invoke(),
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun navToTaxes() {
        findNavController().navigate(R.id.action_transactionDetailsFragment_to_taxesFragment)
    }

    companion object {
        const val TRANSACTION_KEY = "transaction_key"
    }
}