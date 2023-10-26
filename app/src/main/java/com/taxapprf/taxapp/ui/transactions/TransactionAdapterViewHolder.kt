package com.taxapprf.taxapp.ui.transactions

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transactions.TransactionModel
import com.taxapprf.taxapp.app.R
import com.taxapprf.taxapp.app.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.toAppDouble

class TransactionAdapterViewHolder(
    private val binding: FragmentTransactionsAdapterItemBinding,
    private val callback: TransactionsAdapterCallback,
    private val localTypeNames: List<String>,
) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context
    private lateinit var _transaction: TransactionModel
    private val rateNotFound = context.getString(R.string.transactions_rate_not_found)
    private val rateNotLoaded = context.getString(R.string.transactions_rate_not_loaded)
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
        if (_transaction.currencyRate == null) {
            binding.textviewTransactionsAdapterItemTax.text = rateNotLoaded
            binding.textviewTransactionsAdapterItemCurrencyRate.isVisible = false
            binding.textviewTransactionsAdapterItemTaxSymbol.isVisible = false
        } else if (_transaction.currencyRate!! < 0.0) {
            binding.textviewTransactionsAdapterItemTax.text = rateNotFound
            binding.textviewTransactionsAdapterItemCurrencyRate.isVisible = false
            binding.textviewTransactionsAdapterItemTaxSymbol.isVisible = false
        } else {
            binding.textviewTransactionsAdapterItemCurrencyRate.text =
                _transaction.currencyRate?.toAppDouble()
            binding.textviewTransactionsAdapterItemTax.text =
                _transaction.taxRUB?.toAppDouble()
            binding.textviewTransactionsAdapterItemCurrencyRate.isVisible = true
            binding.textviewTransactionsAdapterItemTaxSymbol.isVisible = true
        }
    }
}