package com.taxapprf.taxapp.ui.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.main.account.AccountModel
import com.taxapprf.taxapp.app.databinding.ActivityMainDrawerHeaderAddBinding
import com.taxapprf.taxapp.app.databinding.ActivityMainDrawerHeaderItemBinding

class MainAccountsAdapter(
    private val callback: DrawerAccountsAdapterCallback,
) : ListAdapter<AccountModel, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position.isLast()) VIEW_TYPE_ADD
        else VIEW_TYPE_ITEM
    }

    private fun Int.isLast() = this + 1 == itemCount

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD -> parent.getAddViewHolder()
            else -> parent.getItemViewHolder()
        }
    }

    private fun ViewGroup.getItemViewHolder(): MainAccountsAdapterViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ActivityMainDrawerHeaderItemBinding.inflate(inflater, this, false)
        return MainAccountsAdapterViewHolder(binding, callback)
    }

    private fun ViewGroup.getAddViewHolder(): MainAccountsAdapterAddViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ActivityMainDrawerHeaderAddBinding.inflate(inflater, this, false)
        return MainAccountsAdapterAddViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainAccountsAdapterViewHolder -> holder.bind(getItem(position))
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AccountModel>() {
        override fun areItemsTheSame(
            oldItem: AccountModel,
            newItem: AccountModel
        ) = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: AccountModel,
            newItem: AccountModel
        ) = oldItem == newItem
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD = 1
    }
}