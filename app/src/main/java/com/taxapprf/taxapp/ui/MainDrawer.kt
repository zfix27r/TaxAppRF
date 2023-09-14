package com.taxapprf.taxapp.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.taxapprf.domain.user.UserModel
import com.taxapprf.taxapp.R

class MainDrawer(
    private val navView: NavigationView,
    private val reports: () -> Unit,
    private val currencies: () -> Unit,
    private val converter: () -> Unit,
    private val sign: () -> Unit,
    private val signOut: () -> Unit,
) {
    private val header
        get() = navView.getHeaderView(HEADER_POSITION)
    private val userAvatar
        get() = header.findViewById<ImageView>(R.id.imageNavHeaderUserAvatar)
    private val userName
        get() = header.findViewById<TextView>(R.id.textNavHeaderUserName)
    private val userEmail
        get() = header.findViewById<TextView>(R.id.textNavHeaderUserEmail)
    private val expand
        get() = navView.findViewById<ImageView>(R.id.imageNavHeaderUserAccountExpand)
    val recycler: RecyclerView
        get() = header.findViewById(R.id.recyclerNavHeaderAccounts)
    private val accounts
        get() = header.findViewById<Layer>(R.id.layerNavHeaderAccounts)
    val account: TextView
        get() = header.findViewById(R.id.textNavHeaderUserAccount)

    private var isAccountsExpand = false

    private fun expandMoreAccounts() {
        expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        recycler.isVisible = true
    }

    private fun expandLessAccounts() {
        expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        recycler.isVisible = false
    }

    init {
        accounts.setOnClickListener {
            if (isAccountsExpand) expandLessAccounts()
            else expandMoreAccounts()
            isAccountsExpand = !isAccountsExpand
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.reports -> {
                    reports()
                    true
                }

                R.id.currency_rates_today -> {
                    currencies()
                    true
                }

                R.id.currency_converter -> {
                    converter()
                    true
                }

                R.id.sign -> {
                    sign()
                    true
                }

                R.id.sign_out -> {
                    signOut()
                    true
                }

                else -> false
            }
        }
    }

    fun auth(userModel: UserModel?) {
        userModel.updateUserInfo()

        userModel?.let { showWithAuth() }
            ?: run { hideWithoutAuth() }
    }

    private fun UserModel?.updateUserInfo() {
        this?.avatar?.let { userAvatar.setImageURI(it) }
        userName.text = this?.name ?: ""
        userEmail.text = this?.email ?: ""
    }

    fun showWithAuth() {
        accounts.isVisible = true
        recycler.isVisible = true

        navView.menu.findItem(R.id.sign).isVisible = false
        navView.menu.findItem(R.id.sign_out).isVisible = true
    }

    fun hideWithoutAuth() {
        userAvatar.setImageResource(R.drawable.free_icon_tax_10994810)
        accounts.isVisible = false
        recycler.isVisible = false

        navView.menu.findItem(R.id.sign_out).isVisible = false
        navView.menu.findItem(R.id.sign).isVisible = true
    }

    companion object {
        private const val HEADER_POSITION = 0
    }
}