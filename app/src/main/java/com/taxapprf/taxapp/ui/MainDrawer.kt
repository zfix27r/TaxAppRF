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
    private val logOutCallback: () -> Unit,
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
    private val logOut
        get() = header.findViewById<ImageView>(R.id.imageNavHeaderUserLogOut)

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

        logOut.setOnClickListener {
            logOutCallback()
        }
    }

    fun auth(userModel: UserModel?) {
        userModel?.let {
            it.updateUserInfo()
            showWithAuth()
        } ?: run {
            hideWithoutAuth()
        }
    }

    private fun UserModel.updateUserInfo() {
        avatar?.let { userAvatar.setImageURI(it) }
        userName.text = name
        userEmail.text = email
    }

    fun showWithAuth() {
        userName.isVisible = true
        userEmail.isVisible = true
        logOut.isVisible = true
        accounts.isVisible = true

        navView.menu.clear()
        navView.inflateMenu(R.menu.auth_drawer)
    }

    fun hideWithoutAuth() {
        userAvatar.setImageResource(R.drawable.free_icon_tax_10994810)
        userName.isVisible = false
        userEmail.isVisible = false
        logOut.isVisible = false
        accounts.isVisible = false

        navView.menu.clear()
        navView.inflateMenu(R.menu.not_auth_drawer)
    }

    companion object {
        private const val HEADER_POSITION = 0
    }
}