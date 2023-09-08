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
    private lateinit var transaction: TransactionModel

    init {
        binding.root.setOnClickListener {
            callback.onClick(transaction)
        }

        binding.buttonTransactionsAdapterItemMore.setOnClickListener {
            callback.onClickMore(transaction)
        }
    }

    fun bind(transactionModel: TransactionModel) {
        transaction = transactionModel

        transaction.name?.let {
            with(binding.textViewTransactionsAdapterItemName) {
                if (it.isEmpty()) isVisible = false
                else {
                    text = transaction.name
                    isVisible = true
                }
            }
        }
        binding.textViewTransactionsAdapterItemType.setText(transaction.type.getTransactionName())
        binding.textViewTransactionsAdapterItemDate.text = transaction.date
        binding.textViewTransactionsAdapterItemSum.text = transaction.sum.toString()
        binding.textViewTransactionsAdapterItemCurrency.text = transaction.currency
        binding.textViewTransactionsAdapterItemRateCbr.text = transaction.rateCBRF.toString()
        binding.textViewTransactionsAdapterItemTax.text = transaction.tax.toString()
    }
}