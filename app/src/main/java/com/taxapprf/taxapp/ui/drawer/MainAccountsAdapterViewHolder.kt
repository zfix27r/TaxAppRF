package com.taxapprf.taxapp.ui.drawer

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.taxapp.databinding.ActivityMainDrawerHeaderItemBinding

class MainAccountsAdapterViewHolder(
    private val binding: ActivityMainDrawerHeaderItemBinding,
    private val callback: DrawerAccountsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var account: AccountModel

    init {
        binding.drawerHeaderItem.setOnClickListener {
            callback.switchAccount(account)
        }
    }

    fun bind(accountModel: AccountModel) {
        account = accountModel

        binding.drawerHeaderItemTitle.text = account.name
    }
}