package com.taxapprf.taxapp.ui.drawer

import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.UserModel
import com.taxapprf.taxapp.R

class Drawer(
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private val callback: DrawerCallback,
) {
    private val header = navigationView.getHeaderView(HEADER_POSITION)
    private val userAvatar = header.findViewById<ImageView>(R.id.image_drawer_header_user_avatar)
    private val userName = header.findViewById<TextView>(R.id.text_drawer_header_user_name)
    private val userEmail = header.findViewById<TextView>(R.id.text_drawer_header_user_email)
    private val expandAccounts
        get() = navigationView.findViewById<ImageView>(R.id.image_drawer_header_user_account_expand)
    private val accountsRecycler =
        header.findViewById<RecyclerView>(R.id.recycler_drawer_header_accounts)
    private val activeAccountLayer =
        header.findViewById<Layer>(R.id.layer_drawer_header_active_account)
    private val activeAccount: TextView =
        header.findViewById(R.id.text_drawer_header_user_active_account)

    private val accountsAdapterCallback =
        object : DrawerAccountsAdapterCallback {
            override fun switchAccount(accountModel: AccountModel) {
                closeDrawer()
                callback.switchAccount(accountModel)
            }

            override fun navToAddAccount() {
                closeDrawer()
                callback.navToAccountAdd()
            }
        }

    private val accountsAdapter = MainAccountsAdapter(accountsAdapterCallback)

    init {
        accountsRecycler.adapter = accountsAdapter
        activeAccountLayer.setOnClickListener { expandAccounts() }
        navigationView.setNavigationItemSelectedListener {
            closeDrawer()
            it.onNavigationItemClick()
        }
    }

    fun updateUser(
        user: UserModel?,
        defaultUserName: String,
    ) {
        if (user?.email != UserRepositoryImpl.LOCAL_USER_EMAIL) {
            showWithAuth()
            userAvatar.setImageURI(user?.avatar)
            userName.text = user?.name
            userEmail.text = user?.email
        } else {
            hideWithoutAuth()
            userAvatar.setImageResource(R.drawable.ic_tax_app_logo)
            userName.text = defaultUserName
            userEmail.text = ""
        }
    }

    fun updateActiveAccount(activeAccount: AccountModel?) {
        this.activeAccount.text = activeAccount?.name
    }

    fun updateOtherAccounts(accounts: List<AccountModel>?) {
        accountsAdapter.submitList(accounts)
    }

    private fun showWithAuth() {
        navigationView.menu.findItem(R.id.sign).isVisible = false
        navigationView.menu.findItem(R.id.sign_out).isVisible = true
    }

    private fun hideWithoutAuth() {
        navigationView.menu.findItem(R.id.sign).isVisible = true
        navigationView.menu.findItem(R.id.sign_out).isVisible = false
    }

    private fun expandAccounts() {
        accountsRecycler.isVisible = !accountsRecycler.isVisible
        expandAccounts.setImageResource(
            if (accountsRecycler.isVisible) R.drawable.ic_baseline_expand_less_24
            else R.drawable.ic_baseline_expand_more_24
        )
    }

    private fun MenuItem.onNavigationItemClick() =
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

    private fun closeDrawer() = drawerLayout.close()

    companion object {
        private const val HEADER_POSITION = 0
    }
}