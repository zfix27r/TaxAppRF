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

    fun updateUserProfile(userModel: UserModel) {
        userModel.avatar?.let {
            userAvatar.setImageURI(it)
        }
        userName.text = userModel.name
        userEmail.text = userModel.email
    }

    fun showAuth() {
        userName.isVisible = true
        userEmail.isVisible = true
        logOut.isVisible = true
        accounts.isVisible = true
        recycler.isVisible = true

        navView.menu.clear()
        navView.inflateMenu(R.menu.auth_drawer)
    }

    fun hideAuth() {
        userName.isVisible = false
        userEmail.isVisible = false
        logOut.isVisible = false
        accounts.isVisible = false
        recycler.isVisible = false

        navView.menu.clear()
        navView.inflateMenu(R.menu.not_auth_drawer)
    }

    companion object {
        private const val HEADER_POSITION = 0
    }
}