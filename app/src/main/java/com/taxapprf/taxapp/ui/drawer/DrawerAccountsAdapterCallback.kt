package com.taxapprf.taxapp.ui.drawer

import com.taxapprf.domain.main.account.AccountModel

interface DrawerAccountsAdapterCallback {
    fun switchAccount(accountModel: AccountModel)
    fun navToAddAccount()
}