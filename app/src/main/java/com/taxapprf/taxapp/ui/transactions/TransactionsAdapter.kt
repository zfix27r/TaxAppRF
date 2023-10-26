package com.taxapprf.taxapp.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.transactions.TransactionModel
import com.taxapprf.taxapp.app.databinding.FragmentTransactionsAdapterItemBinding

class TransactionsAdapter(
    private val callback: TransactionsAdapterCallback,
) : ListAdapter<TransactionModel, TransactionAdapterViewHolder>(DiffCallback()) {
    var localTransactionTypes: List<String> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentTransactionsAdapterItemBinding.inflate(inflater, parent, false)
        return TransactionAdapterViewHolder(binding, callback, localTransactionTypes)
    }

    override fun onBindViewHolder(holder: TransactionAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<TransactionModel>() {
        override fun areItemsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ) = oldItem == newItem
    }
}