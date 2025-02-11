package com.taxapprf.taxapp.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.databinding.FragmentTransactionsAdapterItemBinding
import com.taxapprf.taxapp.ui.toDateFormat


class TransactionsAdapter(
    private val callback: () -> TransactionsAdapterCallback,
) : ListAdapter<TransactionModel, TransactionAdapterViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentTransactionsAdapterItemBinding.inflate(inflater, parent, false)
        return TransactionAdapterViewHolder(binding, callback.invoke())
    }

    override fun onBindViewHolder(holder: TransactionAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<TransactionModel>?) {
        super.submitList(sortListByData(list))
    }

    private class DiffCallback : DiffUtil.ItemCallback<TransactionModel>() {
        override fun areItemsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ) = oldItem.key == newItem.key

        override fun areContentsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ) = oldItem == newItem
    }

    private fun sortListByData(list: List<TransactionModel>?):  List<TransactionModel>? {
        list?.toMutableList()?.let {
            it.sortWith { item1, item2 ->
                val dateItem1 = item1.date.toDateFormat()
                val dateItem2 = item2.date.toDateFormat()
                dateItem1?.compareTo(dateItem2) ?: 0
            }
            return it
        }
        return list
    }
}