package com.taxapprf.taxapp.ui.transactions

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.toAppDouble

class TransactionAdapterViewHolder(
    private val binding: FragmentTransactionsAdapterItemBinding,
    private val callback: TransactionsAdapterCallback,
    private val localTypeNames: List<String>,
) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context
    private lateinit var _transaction: TransactionModel
    val transaction
        get() = _transaction

    init {
        binding.root.setOnClickListener {
            callback.onItemClick(_transaction)
        }

        binding.buttonTransactionsAdapterItemMore.setOnClickListener {
            callback.onMoreClick(_transaction)
        }
    }

    fun bind(transactionModel: TransactionModel) {
        _transaction = transactionModel

        updateName()
        updateTaxAndCurrencyRate()

        binding.textviewTransactionsAdapterItemType.text = localTypeNames[_transaction.type.ordinal]
        binding.textviewTransactionsAdapterItemDate.text = _transaction.date.toAppDate()
        binding.textViewTransactionsAdapterItemSum.text = _transaction.sum.toAppDouble()
        binding.textViewTransactionsAdapterItemCurrency.text = _transaction.currency.name
    }

    private fun updateName() {
        binding.textviewTransactionsAdapterItemName.text = _transaction.name
        binding.textviewTransactionsAdapterItemName.isVisible = _transaction.name != ""
    }

    private fun updateTaxAndCurrencyRate() {
        _transaction.tax?.let {
            binding.textviewTransactionsAdapterItemCurrencyRate.text =
                _transaction.currencyRate?.toAppDouble()
            binding.textviewTransactionsAdapterItemTax.text =
                _transaction.tax?.toAppDouble()

            binding.textviewTransactionsAdapterItemCurrencyRate.isVisible = true
            binding.textviewTransactionsAdapterItemCurrencyRateSymbol.isVisible = true
            binding.textviewTransactionsAdapterItemTaxSymbol.isVisible = true
        } ?: run {
            binding.textviewTransactionsAdapterItemTax.text =
                context.getString(R.string.transaction_tax_is_empty)

            binding.textviewTransactionsAdapterItemCurrencyRate.isVisible = false
            binding.textviewTransactionsAdapterItemCurrencyRateSymbol.isVisible = false
            binding.textviewTransactionsAdapterItemTaxSymbol.isVisible = false
        }
    }
}