package com.taxapprf.taxapp.ui.drawer

import com.taxapprf.domain.user.AccountModel

interface DrawerAccountsAdapterCallback {
    fun switchAccount(accountModel: AccountModel)
    fun navToAddAccount()
}