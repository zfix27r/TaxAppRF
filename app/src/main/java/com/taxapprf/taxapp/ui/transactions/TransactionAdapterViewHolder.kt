package com.taxapprf.taxapp.ui.transactions

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.getTransactionName

class TransactionAdapterViewHolder(
    private val binding: FragmentTransactionsAdapterItemBinding,
    private val callback: TransactionsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
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
        binding.textViewTransactionsAdapterItemDate.text = _transaction.date
        binding.textViewTransactionsAdapterItemSum.text = _transaction.sum.toString()
        binding.textViewTransactionsAdapterItemCurrency.text = _transaction.currency
        binding.textViewTransactionsAdapterItemRateCbr.text = _transaction.rateCBRF.toString()
        binding.textViewTransactionsAdapterItemTax.text = _transaction.tax.toString()
    }
}