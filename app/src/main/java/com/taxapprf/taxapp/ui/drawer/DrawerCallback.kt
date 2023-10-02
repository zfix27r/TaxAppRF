package com.taxapprf.taxapp.ui.drawer

import com.taxapprf.domain.user.AccountModel

interface DrawerCallback {
    fun signOut()
    fun switchAccount(accountModel: AccountModel)
}