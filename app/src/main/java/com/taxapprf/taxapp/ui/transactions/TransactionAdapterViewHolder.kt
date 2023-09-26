package com.taxapprf.taxapp.ui.transactions

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.round
import com.taxapprf.taxapp.ui.formatDate
import com.taxapprf.taxapp.ui.getTransactionName

class TransactionAdapterViewHolder(
    private val binding: FragmentTransactionsAdapterItemBinding,
    private val callback: TransactionsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context
    private lateinit var _transaction: TransactionModel
    val transaction
        get() = _transaction

    init {
        binding.root.setOnClickListener {
            callback.onClick(_transaction)
        }

        binding.buttonTransactionsAdapterItemMore.setOnClickListener {
            callback.onClickMore(_transaction)
        }
    }

    fun bind(transactionModel: TransactionModel) {
        _transaction = transactionModel

        _transaction.name?.let {
            with(binding.textViewTransactionsAdapterItemName) {
                if (it.isEmpty()) isVisible = false
                else {
                    text = _transaction.name
                    isVisible = true
                }
            }
        }
        binding.textViewTransactionsAdapterItemType.setText(_transaction.type.getTransactionName())
        binding.textViewTransactionsAdapterItemDate.text = _transaction.date.formatDate()
        binding.textViewTransactionsAdapterItemSum.text = _transaction.sum.round().toString()
        binding.textViewTransactionsAdapterItemCurrency.text = _transaction.currencyCharCode

        _transaction.tax?.let {
            binding.textViewTransactionsAdapterItemRateCbr.text =
                _transaction.currencyRate?.round().toString()
            binding.textViewTransactionsAdapterItemTax.text = _transaction.tax?.round().toString()

            binding.textViewTransactionsAdapterItemRateCbr.isVisible = true
            binding.textViewTransactionsAdapterItemTaxSymbol.isVisible = true
            binding.textViewTransactionsAdapterItemTaxSymbol2.isVisible = true
        } ?: run {
            binding.textViewTransactionsAdapterItemTax.text =
                if (_transaction.isTaxUpdate) context.getString(R.string.transaction_tax_is_update)
                else context.getString(R.string.transaction_tax_is_connection_lost)

            binding.textViewTransactionsAdapterItemRateCbr.isVisible = false
            binding.textViewTransactionsAdapterItemTaxSymbol.isVisible = false
            binding.textViewTransactionsAdapterItemTaxSymbol2.isVisible = false
        }
    }
}