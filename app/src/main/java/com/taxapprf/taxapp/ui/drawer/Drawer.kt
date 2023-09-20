package com.taxapprf.taxapp.ui.drawer

import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.AccountsModel
import com.taxapprf.domain.user.UserModel
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
    private val expand =
        navigationView.findViewById<ImageView>(R.id.imageNavHeaderUserAccountExpand)
    private val recycler = header.findViewById<RecyclerView>(R.id.recyclerNavHeaderAccounts)
    private val accounts = header.findViewById<Layer>(R.id.layerNavHeaderAccounts)
    private val account: TextView = header.findViewById(R.id.textNavHeaderUserAccount)

    private val expandLessIcon = R.drawable.ic_baseline_expand_less_24
    private val expandMoreIcon = R.drawable.ic_baseline_expand_more_24

    private val accountsAdapter by lazy { MainAccountsAdapter(accountsAdapterCallback) }

    init {
        accounts.setOnClickListener { expandAccounts() }

        navigationView.setNavigationItemSelectedListener {
            close()
            it.onItemClick()
        }
    }

    fun updateAuth(userModel: UserModel?) {
        userModel.updateUserInfo()

        userModel
            ?.let { showWithAuth() }
            ?: run { hideWithoutAuth() }
    }

    fun updateAccounts(accountsModel: AccountsModel?) {
        accountsModel
            ?.let { showWithAccounts(it) }
            ?: run { hideWithoutAccounts() }
    }

    private fun UserModel?.updateUserInfo() {
        this
            ?.avatar
            ?.let { userAvatar.setImageURI(it) }
            ?: run { userAvatar.setImageResource(R.drawable.free_icon_tax_10994810) }

        userName.text = this?.name ?: ""
        userEmail.text = this?.email ?: ""
    }

    private fun showWithAccounts(accountsModel: AccountsModel) {
        accountsAdapter.submitList(accountsModel.inactive)
        account.text = accountsModel.active.accountKey

        accounts.isVisible = true
        recycler.isVisible = true
    }

    private fun hideWithoutAccounts() {
        accounts.isVisible = false
        recycler.isVisible = false
    }

    private fun showWithAuth() {
        navigationView.menu.findItem(R.id.sign).isVisible = false
        navigationView.menu.findItem(R.id.sign_out).isVisible = true
    }

    private fun hideWithoutAuth() {
        navigationView.menu.findItem(R.id.sign_out).isVisible = false
        navigationView.menu.findItem(R.id.sign).isVisible = true
    }

    private fun expandAccounts() {
        expand.setImageResource(if (recycler.isVisible) expandLessIcon else expandMoreIcon)
        recycler.isVisible = !recycler.isVisible
    }

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