package com.taxapprf.taxapp.ui.drawer

import androidx.recyclerview.widget.RecyclerView
import com.taxapprf.taxapp.databinding.ActivityMainDrawerHeaderAddBinding

class MainAccountsAdapterAddViewHolder(
    binding: ActivityMainDrawerHeaderAddBinding,
    private val callback: DrawerAccountsAdapterCallback,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            callback.navToAddAccount()
        }
    }
}