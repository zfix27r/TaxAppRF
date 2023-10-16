package com.taxapprf.taxapp.ui.drawer

import com.taxapprf.domain.main.account.AccountModel

interface DrawerCallback {
    fun signOut()
    fun switchAccount(accountModel: AccountModel)
}