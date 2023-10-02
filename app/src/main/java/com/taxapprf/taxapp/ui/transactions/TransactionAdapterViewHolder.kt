package com.taxapprf.taxapp.ui.transactions

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.convertToTransactionTypeName
import com.taxapprf.taxapp.ui.round

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
            callback.onItemClick(_transaction)
        }

        binding.buttonTransactionsAdapterItemMore.setOnClickListener {
            callback.onMoreClick(_transaction)
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
        binding.textViewTransactionsAdapterItemType.text =
            context.convertToTransactionTypeName(_transaction.typeK)
        binding.textViewTransactionsAdapterItemDate.text = _transaction.date.toAppDate()
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
                context.getString(R.string.transaction_tax_is_empty)

            binding.textViewTransactionsAdapterItemRateCbr.isVisible = false
            binding.textViewTransactionsAdapterItemTaxSymbol.isVisible = false
            binding.textViewTransactionsAdapterItemTaxSymbol2.isVisible = false
        }
    }
}