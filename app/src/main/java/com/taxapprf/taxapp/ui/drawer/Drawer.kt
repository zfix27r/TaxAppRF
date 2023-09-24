package com.taxapprf.taxapp.ui.drawer

import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.UserModel
import com.taxapprf.domain.user.UserWithAccountsModel
import com.taxapprf.taxapp.R

class Drawer(
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private val callback: DrawerCallback,
) {
    private val header = navigationView.getHeaderView(HEADER_POSITION)
    private val userAvatar = header.findViewById<ImageView>(R.id.imageNavHeaderUserAvatar)
    private val userName = header.findViewById<TextView>(R.id.textNavHeaderUserName)
    private val userEmail = header.findViewById<TextView>(R.id.textNavHeaderUserEmail)
    private val expand
        get() = navigationView.findViewById<ImageView>(R.id.imageNavHeaderUserAccountExpand)
    private val recycler = header.findViewById<RecyclerView>(R.id.recyclerNavHeaderAccounts)
    private val accounts = header.findViewById<Layer>(R.id.layerNavHeaderAccounts)
    private val account: TextView = header.findViewById(R.id.textNavHeaderUserAccount)

    private val accountsAdapterCallback =
        object : DrawerAccountsAdapterCallback {
            override fun switchAccount(accountModel: AccountModel) {
                close()
                callback.switchAccount(accountModel)
            }

            override fun navToAddAccount() {
                close()
                callback.navToAccountAdd()
            }
        }

    private val accountsAdapter = MainAccountsAdapter(accountsAdapterCallback)

    init {
        recycler.adapter = accountsAdapter

        accounts.setOnClickListener { expandAccounts() }

        navigationView.setNavigationItemSelectedListener {
            close()
            it.onItemClick()
        }
    }

    fun updateUser(
        user: UserModel?,
        defaultUserEmail: String,
    ) {
        user?.let { showWithAuth() }
            ?: run { hideWithoutAuth() }

        user?.avatar?.let { userAvatar.setImageURI(it) }
            ?: run { userAvatar.setImageResource(R.drawable.free_icon_tax_10994810) }

        userName.text = user?.name ?: ""
        userEmail.text = user?.email ?: defaultUserEmail
    }

    fun updateActiveAccount(activeAccount: AccountModel?) {
        account.text = activeAccount?.name
    }

    fun updateOtherAccounts(accounts: List<AccountModel>?) {
        accountsAdapter.submitList(accounts)
    }

    private fun showWithAuth() {
        accounts.isVisible = true
        recycler.isVisible = false
        expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        navigationView.menu.findItem(R.id.sign).isVisible = false
        navigationView.menu.findItem(R.id.sign_out).isVisible = true
    }

    private fun hideWithoutAuth() {
        accounts.isVisible = false
        recycler.isVisible = false
        navigationView.menu.findItem(R.id.sign_out).isVisible = false
        navigationView.menu.findItem(R.id.sign).isVisible = true
    }

    private fun expandAccounts() {
        recycler.isVisible = !recycler.isVisible
        expand.setImageResource(
            if (recycler.isVisible) R.drawable.ic_baseline_expand_less_24
            else R.drawable.ic_baseline_expand_more_24
        )
    }

    private fun MenuItem.onItemClick() =
        when (itemId) {
            R.id.reports -> {
                callback.navToReports()
                true
            }

            R.id.currency_rates_today -> {
                callback.navToCurrencyRatesToday()
                true
            }

            R.id.currency_converter -> {
                callback.navToCurrencyConverter()
                true
            }

            R.id.sign -> {
                callback.navToSign()
                true
            }

            R.id.sign_out -> {
                callback.navToSignOut()
                true
            }

            else -> false
        }

    private fun close() = drawerLayout.close()

    companion object {
        private const val HEADER_POSITION = 0
    }
}